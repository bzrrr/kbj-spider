package com.bzrrr.kbjspider.spider;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: wangziheng
 * @Date: 2021/3/15
 */
@Slf4j
public class KbjImgSpider {
    private static Set<String> imgUrls = new HashSet<>();
    private static int index = 1;//
    private static Pattern imgNameReg = Pattern.compile("\\d{11,}");
    private static Pattern imgDirReg = Pattern.compile("\\d{4}/\\d{2}");

    public static void main(String[] args) {
        initImgUrls();
        boolean hasNext = true;
        while (hasNext) {
            log.info("page -- " + index);
            hasNext = printLinks(index);
            index++;
        }

        log.info("end!");
    }

    private static boolean printLinks(int page) {
        try {
            Document doc = Jsoup.connect("http://www.kav1004.com/page/" + page + "/").get();
            HashSet<String> imgLinks = new HashSet<>();
            Elements imgElements = doc.select(".wp-block-image > img");
            Elements nextPageElement = doc.getElementsByClass("next page-numbers");
            for (Element imgElement : imgElements) {
                String imgLink = imgElement.attr("src");
                imgLinks.add(imgLink);
            }
            for (String imgLink : imgLinks) {
                if (!checkImage(imgLink)) {
                    System.out.println(imgLink);
                    saveImage(imgLink);
                }
            }
            System.out.println();
            return nextPageElement != null && nextPageElement.size() > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkImage(String imgLink) {
        return imgUrls.contains(imgLink);
    }

    private static void saveImage(String imgPath) {
        InputStream inputStream = null;
        OutputStreamWriter osw = null;
        FileOutputStream fos = null;
        try {
            String proxyHost = "0.0.0.0";
            String proxyPort = "1080";
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
            URL url = new URL(imgPath);
            URLConnection conn = url.openConnection(proxy);
            inputStream = conn.getInputStream();
            Matcher imgDirMatcher = imgDirReg.matcher(imgPath);
            String imgDir = "";
            if (imgDirMatcher.find()) {
                imgDir = imgDirMatcher.group().replace("/", "-") + "\\";
            }

            String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
            Matcher imgNameMatcher = imgNameReg.matcher(imgName);
            if (imgNameMatcher.find()) {
                String tName = imgNameMatcher.group();
                String pre = tName.substring(0, 10);
                String post = "(" + tName.substring(10) + ")";
                imgName = imgName.replace(tName, pre + post);
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream("D:\\pic\\kbj_img\\" + imgDir + imgName);
            } catch (FileNotFoundException e) {
                File dir = new File("D:\\pic\\kbj_img\\" + imgDir);
                dir.mkdir();
                out = new FileOutputStream("D:\\pic\\kbj_img\\" + imgDir + imgName);
            }
            int j = 0;
            while ((j = inputStream.read()) != -1) {
                out.write(j);
            }
            fos = new FileOutputStream(new File("D:\\pic\\kbj_img\\downloadImgs.txt"), true);
            osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            osw.write(imgPath + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                osw.close();
                fos.close();
                inputStream.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initImgUrls() {
        try (FileInputStream inputStream = new FileInputStream("D:\\pic\\kbj_img\\downloadImgs.txt");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                imgUrls.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
