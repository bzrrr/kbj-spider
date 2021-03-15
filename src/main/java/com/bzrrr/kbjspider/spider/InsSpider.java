package com.bzrrr.kbjspider.spider;

import com.alibaba.fastjson.JSON;
import com.bzrrr.kbjspider.domain.EdgeOwner;
import com.bzrrr.kbjspider.domain.Ins;
import com.bzrrr.kbjspider.domain.InsEdge;
import com.bzrrr.kbjspider.domain.InsNode;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class InsSpider {
    private static int count = 0;
    private static int total = 0;
    private static String insUrl = "https://www.instagram.com/";
    private static String startApi = "?__a=1";
    private static String userId;
    private static String queryHash = "003056d32c2554def87228bc3fd9668a";
    private static String queryApi = "https://www.instagram.com/graphql/query/?query_hash=%s&variables={\"id\":\"%s\",\"first\":12,\"after\":\"%s\"}";

    public static void main(String[] args) {
        start("2km2km");
//        start("wanna._b");

    }

    private static void start(String username) {
        if (!username.endsWith("/")) {
            username += "/";
        }
        String api = insUrl + username + startApi;

        EdgeOwner currentEdgeOwner = next(api);
        while (currentEdgeOwner != null && currentEdgeOwner.getPage_info().isHas_next_page()) {
            count = 0;
            String endCursor = currentEdgeOwner.getPage_info().getEnd_cursor();
            String nextPageApi = String.format(queryApi, queryHash, userId, endCursor);
            currentEdgeOwner = next(nextPageApi);
        }
        System.out.println(total);
    }

    private static EdgeOwner next(String api) {
        try {
            String json = getApiJson(api, "ig_did=CB63A1C7-F131-4D21-B6FA-A55505C54A0C; csrftoken=U9acvshicAmqOewAkiP8HFevZ88QajOf; mid=YE4frgALAAH2mYstMEGULbNVgKXv; ig_nrcb=1; rur=ATN; ds_user_id=1203194525; sessionid=1203194525%3AluGJbN7jHvyuWG%3A9; shbid=2849; shbts=1615732707.4266386", "1203194525:luGJbN7jHvyuWG:9");
            Ins insCur = JSON.parseObject(json, Ins.class);
            if (!StringUtils.isEmpty(insCur.getLogging_page_id())) {
                userId = insCur.getLogging_page_id().replace("profilePage_", "");
            }
            EdgeOwner currentEdgeOwner = null;
            if (total == 0) {
                currentEdgeOwner = insCur.getGraphql().getUser().getEdge_owner_to_timeline_media();
            } else {
                currentEdgeOwner = insCur.getData().getUser().getEdge_owner_to_timeline_media();
            }
            List<InsEdge> edges = currentEdgeOwner.getEdges();
            recur(edges);
            System.out.println(count);
            Thread.sleep(2000L);
            return currentEdgeOwner;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getApiJson(String api, String cookie, String sessionId) throws IOException {
        String proxyHost = "0.0.0.0";//代理IP
        String proxyPort = "1080";//代理端口
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
        URL url = new URL(api);
        URLConnection conn = url.openConnection(proxy);
        conn.setRequestProperty("cookie", cookie);
        conn.setRequestProperty("referer", "https://www.instagram.com/");
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

    private static void recur(List<InsEdge> edges) {
        for (InsEdge edge : edges) {
            InsNode node = edge.getNode();
            if (node.is_video()) {
                System.out.println(node.getVideo_url());
                count++;
                total++;
            }
            if (node.getEdge_sidecar_to_children() != null) {
                recur(node.getEdge_sidecar_to_children().getEdges());
            } else {
                if (!node.is_video()) {
                    System.out.println(node.getDisplay_url());
                    count++;
                    total++;
                }
            }
        }
    }

    private static void downloadPic() {
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
