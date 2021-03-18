//package com.bzrrr.kbjspider.spider;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.bzrrr.kbjspider.domain.EdgeOwner;
//import com.bzrrr.kbjspider.domain.Ins;
//import com.bzrrr.kbjspider.domain.InsEdge;
//import com.bzrrr.kbjspider.domain.InsNode;
//import com.bzrrr.kbjspider.model.dto.InsDto;
//import com.bzrrr.kbjspider.model.dto.InsUserDto;
//import com.bzrrr.kbjspider.service.InsPersistService;
//import com.bzrrr.kbjspider.service.InsUserPersistService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * @Author: wangziheng
// * @Date: 2021/3/18
// */
//@Slf4j
//public class InsSpiderTask implements Runnable {
//    private int count = 0;
//    private int total = 0;
//    private String insUrl = "https://www.instagram.com/";
//    private String startApi = "?__a=1";
//    private String userId;
//    private String cookie;
//    private String queryHash = "003056d32c2554def87228bc3fd9668a";
//    private String queryApi = "https://www.instagram.com/graphql/query/?query_hash=%s&variables={\"id\":\"%s\",\"first\":12,\"after\":\"%s\"}";
//    private long sleepTime = 3000L;
//    private Random random = new Random();
//
//    private static Pattern imgNameReg = Pattern.compile("/\\d.*\\.(jpg|mp4|jpeg|gif|flv)");
//
//    private List<InsDto> dataList = new ArrayList<>();
//
//    @Autowired
//    private InsPersistService persistService;
//
//    public void task(String realname) {
//        log.info("start: " + realname);
//        total = 0;
//        String username = realname.endsWith("/") ? realname : realname + "/";
//        String api = insUrl + username + startApi;
//
//        EdgeOwner currentEdgeOwner = next(api, realname);
//        while (currentEdgeOwner != null && currentEdgeOwner.getPage_info().isHas_next_page()) {
//            count = 0;
//            String endCursor = currentEdgeOwner.getPage_info().getEnd_cursor();
//            String nextPageApi = String.format(queryApi, queryHash, userId, endCursor);
//            currentEdgeOwner = next(nextPageApi, realname);
//        }
//        log.info("total: " + total);
////        QueryWrapper<InsUserDto> userWrapper = new QueryWrapper<>();
////        userWrapper.eq("username", realname);
////        InsUserDto userDto = userPersistService.getOne(userWrapper);
////        userDto.setUpdateTime(new Date());
////        userDto.setSpider(false);
////        userPersistService.updateById(userDto);
//        log.info("end!");
//    }
//
//    private EdgeOwner next(String api, String username) {
//        try {
//            String json = getApiJson(api, cookie, "", "https://www.instagram.com/" + username + "/");
//            Ins insCur = JSON.parseObject(json, Ins.class);
//            if (!StringUtils.isEmpty(insCur.getLogging_page_id())) {
//                userId = insCur.getLogging_page_id().replace("profilePage_", "");
//            }
//            EdgeOwner currentEdgeOwner = null;
//            if (total == 0) {
//                currentEdgeOwner = insCur.getGraphql().getUser().getEdge_owner_to_timeline_media();
//            } else {
//                currentEdgeOwner = insCur.getData().getUser().getEdge_owner_to_timeline_media();
//            }
//            List<InsEdge> edges = currentEdgeOwner.getEdges();
////            dataList.clear();
//            recur(edges, username);
////            persistService.saveList(dataList);
//            System.out.println(Thread.currentThread().getName() + ": ins: " + username + " --- " + count);
//            long suspendTime = sleepTime + random.nextInt(4000);
//            System.out.println("suspendTime: " + suspendTime / 1000d + "s");
//            Thread.sleep(suspendTime);
//            return currentEdgeOwner;
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private String getApiJson(String api, String cookie, String sessionId, String referer) throws IOException {
//        String proxyHost = "0.0.0.0";//代理IP
//        String proxyPort = "1080";//代理端口
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
//        URL url = new URL(api);
//        URLConnection conn = url.openConnection(proxy);
//        conn.setRequestProperty("cookie", cookie);
//        conn.setRequestProperty("referer", referer);
//        conn.setRequestProperty("sessionid", sessionId);
//        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
//        InputStream inStream = conn.getInputStream();
//        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
//        StringBuilder bs = new StringBuilder();
//        String l = null;
//        while ((l = buffer.readLine()) != null) {
//            bs.append(l);
//        }
//        return bs.toString();
//    }
//
//    private void recur(List<InsEdge> edges, String username) {
//        for (InsEdge edge : edges) {
//            InsNode node = edge.getNode();
//            InsDto dto = new InsDto();
//            dto.setUsername(username);
//            dto.setSaved(false);
//            if (node.is_video()) {
//                String videoUrl = node.getVideo_url();
////                System.out.println(videoUrl);
//                dto.setLink(videoUrl);
//                Matcher videoNameMatcher = imgNameReg.matcher(videoUrl);
//                if (videoNameMatcher.find()) {
//                    String fileName = videoNameMatcher.group().substring(1);
////                    System.out.println(fileName);
//                    dto.setFilename(fileName);
//                }
//                count++;
//                total++;
//            }
//            if (node.getEdge_sidecar_to_children() != null) {
//                recur(node.getEdge_sidecar_to_children().getEdges(), username);
//            } else {
//                if (!node.is_video()) {
//                    String imgUrl = node.getDisplay_url();
////                    System.out.println(imgUrl);
//                    dto.setLink(imgUrl);
//                    Matcher imgNameMatcher = imgNameReg.matcher(imgUrl);
//                    if (imgNameMatcher.find()) {
//                        String fileName = imgNameMatcher.group().substring(1);
////                        System.out.println(fileName);
//                        dto.setFilename(fileName);
//                    }
//                    count++;
//                    total++;
//                }
//            }
//            if (!StringUtils.isEmpty(dto.getLink())) {
//                persistService.sendAsync(dto);
//            }
//        }
//    }
//
//    @Override
//    public void run() {
//        task();
//    }
//}
