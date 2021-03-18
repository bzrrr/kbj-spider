package com.bzrrr.kbjspider.spider;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bzrrr.kbjspider.domain.*;
import com.bzrrr.kbjspider.model.dto.InsDto;
import com.bzrrr.kbjspider.model.dto.InsUserDto;
import com.bzrrr.kbjspider.service.InsPersistService;
import com.bzrrr.kbjspider.service.InsUserPersistService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class InsSpider {
    private int count = 0;
    private int total = 0;
    private String insUrl = "https://www.instagram.com/";
    private String startApi = "?__a=1";
    private String queryHash = "003056d32c2554def87228bc3fd9668a";
    private String homeQueryHash = "3dec7e2c57367ef3da3d987d89f9dbc8";
    private String queryApi = "https://www.instagram.com/graphql/query/?query_hash=%s&variables={\"id\":\"%s\",\"first\":12,\"after\":\"%s\"}";
    private String homeApi = "https://www.instagram.com/graphql/query/?query_hash=%s&variables={\"id\":\"6031262926\",\"first\":12,\"after\":\"%s\",\"include_reel\":true,\"fetch_mutual\":false}";
    private long sleepTime = 3000L;
    private Random random = new Random();

    private static Pattern imgNameReg = Pattern.compile("/\\d.*\\.(jpg|mp4|jpeg|gif|flv)");

    private Map<String, Integer> currentSpiderMap = new ConcurrentHashMap<>();

    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("ins-pool-%d").build();
    ExecutorService singleThreadPool = new ThreadPoolExecutor(3, 6,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private InsPersistService persistService;
    @Autowired
    private InsUserPersistService userPersistService;

    public void startTask(InsCookie cookie) {
                QueryWrapper<InsUserDto> userWrapper = new QueryWrapper<>();
        userWrapper.orderByDesc("update_time");
        List<InsUserDto> users = userPersistService.list(userWrapper);
        singleThreadPool.execute(() -> {
            for (InsUserDto user : users) {
                if (user.getSpider()) {
                    if (currentSpiderMap.containsKey(user.getUsername())) {
                        continue;
                    } else {
                        currentSpiderMap.put(user.getUsername(), 1);
                    }
                    startSpider(user.getUsername(), user.getUserid(), cookie.getCookie());
                    try {
                        Thread.sleep(sleepTime * 5 + random.nextInt(10000));
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                    currentSpiderMap.remove(user.getUsername());
                }
            }
        });
    }

    public void stopTask() {
        singleThreadPool.shutdown();
    }

    public void startSpider(String realname, String userId, String cookie) {
        log.info("start: " + realname);
        total = 0;
        String username = realname.endsWith("/") ? realname : realname + "/";
        String api = insUrl + username + startApi;

        EdgeOwner currentEdgeOwner = next(api, realname, cookie);
        while (currentEdgeOwner != null && currentEdgeOwner.getPage_info().isHas_next_page()) {
            count = 0;
            String endCursor = currentEdgeOwner.getPage_info().getEnd_cursor();
            String nextPageApi = String.format(queryApi, queryHash, userId, endCursor);
            currentEdgeOwner = next(nextPageApi, realname, cookie);
        }
        log.info("total: " + total);
        QueryWrapper<InsUserDto> userWrapper = new QueryWrapper<>();
        userWrapper.eq("username", realname);
        InsUserDto userDto = userPersistService.getOne(userWrapper);
        userDto.setUpdateTime(new Date());
        userDto.setSpider(false);
        userPersistService.updateById(userDto);
        log.info("end!");
    }

    private EdgeOwner next(String api, String username, String cookie) {
        try {
            String json = getApiJson(api, cookie, "", "https://www.instagram.com/" + username + "/");
            Ins insCur = JSON.parseObject(json, Ins.class);
            EdgeOwner currentEdgeOwner = null;
            if (total == 0) {
                currentEdgeOwner = insCur.getGraphql().getUser().getEdge_owner_to_timeline_media();
            } else {
                currentEdgeOwner = insCur.getData().getUser().getEdge_owner_to_timeline_media();
            }
            List<InsEdge> edges = currentEdgeOwner.getEdges();
            List<InsDto> list = new ArrayList<>();
            recur(edges, username, list);
            persistService.saveList(list);
            System.out.println(Thread.currentThread().getName() + ": ins: " + username + " --- " + count);
            long suspendTime = sleepTime + random.nextInt(4000);
            System.out.println("suspendTime: " + suspendTime / 1000d + "s");
            Thread.sleep(suspendTime);
            return currentEdgeOwner;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getApiJson(String api, String cookie, String sessionId, String referer) throws IOException {
        String proxyHost = "0.0.0.0";//代理IP
        String proxyPort = "1080";//代理端口
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
        URL url = new URL(api);
        URLConnection conn = url.openConnection(proxy);
        conn.setRequestProperty("cookie", cookie);
        conn.setRequestProperty("referer", referer);
        conn.setRequestProperty("sessionid", sessionId);
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
        InputStream inStream = conn.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder bs = new StringBuilder();
        String l = null;
        while ((l = buffer.readLine()) != null) {
            bs.append(l);
        }
        return bs.toString();
    }

    private void recur(List<InsEdge> edges, String username, List<InsDto> list) {
        for (InsEdge edge : edges) {
            InsNode node = edge.getNode();
            InsDto dto = new InsDto();
            dto.setUsername(username);
            dto.setSaved(false);
            if (node.is_video()) {
                String videoUrl = node.getVideo_url();
//                System.out.println(videoUrl);
                dto.setLink(videoUrl);
                Matcher videoNameMatcher = imgNameReg.matcher(videoUrl);
                if (videoNameMatcher.find()) {
                    String fileName = videoNameMatcher.group().substring(1);
//                    System.out.println(fileName);
                    dto.setFilename(fileName);
                }
                count++;
                total++;
            }
            if (node.getEdge_sidecar_to_children() != null) {
                recur(node.getEdge_sidecar_to_children().getEdges(), username, list);
            } else {
                if (!node.is_video()) {
                    String imgUrl = node.getDisplay_url();
//                    System.out.println(imgUrl);
                    dto.setLink(imgUrl);
                    Matcher imgNameMatcher = imgNameReg.matcher(imgUrl);
                    if (imgNameMatcher.find()) {
                        String fileName = imgNameMatcher.group().substring(1);
//                        System.out.println(fileName);
                        dto.setFilename(fileName);
                    }
                    count++;
                    total++;
                }
            }
            if (!StringUtils.isEmpty(dto.getLink())) {
//                persistService.sendAsync(dto);
                list.add(dto);
            }
        }
    }

    private void homeFocus(String endCursor) {
        String api = String.format(homeApi, homeQueryHash, endCursor);
        try {
            String json = getApiJson(api, InsCookie.MAIN_COOKIE.getCookie(), "", "https://www.instagram.com/bzrrrw/following/");
            Ins insCur = JSON.parseObject(json, Ins.class);
            EdgeFollow curEdgeFollow = insCur.getData().getUser().getEdge_follow();
            List<InsEdge> edges = curEdgeFollow.getEdges();
            for (InsEdge edge : edges) {
                InsNode node = edge.getNode();
                String userid = node.getId();
                String username = node.getUsername();
                QueryWrapper<InsUserDto> userWrapper = new QueryWrapper<>();
                userWrapper.eq("username", username);
                InsUserDto dto = userPersistService.getOne(userWrapper);
                if (dto == null) {
                    dto = new InsUserDto();
                    dto.setSpider(true);
                }
                dto.setUserid(userid);
                dto.setUsername(username);
                QueryWrapper<InsDto> wrapper = new QueryWrapper<>();
                wrapper.eq("username", username);
                int count = persistService.count(wrapper);
                if (count == 0) {
                    dto.setUpdateTime(new Date());
                }
                userPersistService.saveOrUpdate(dto);
                System.out.println(username);
            }
            Thread.sleep(sleepTime + random.nextInt(4000));
            if (curEdgeFollow.getPage_info().isHas_next_page()) {
                homeFocus(curEdgeFollow.getPage_info().getEnd_cursor());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void downloadPic() {
        try {
            String path = "https://scontent-hkt1-2.cdninstagram.com/v/t51.2885-15/sh0.08/e35/c0.174.1440.1440a/s640x640/158953369_4064170063614803_4586640019575809031_n.jpg?tp=1&_nc_ht=scontent-hkt1-2.cdninstagram.com&_nc_cat=111&_nc_ohc=-C2yEe4P-mMAX_btYex&oh=92dac6ae72dfdde5c4733ef25891aad4&oe=60783388";
            File file = new File("D:\\dist\\test\\test.jpg");
            int bytesum = 0;
            int byteread = 0;

            URL url = new URL(path);

//			URLConnection conn = url.openConnection();
            String proxyHost = "0.0.0.0";//代理IP
            String proxyPort = "1080";//代理端口
            Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
            URLConnection conn = url.openConnection(proxy);
//			conn.setRequestProperty("fs_secure","b14b1D2Df7c0b5Bbd5b970ACc4405E2abc4e9dc63061490815c292030e4e9a7a");
//			conn.setRequestProperty("rn2","d3208d7140315ca6");

            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(file);
            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
