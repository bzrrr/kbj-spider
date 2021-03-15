package com.bzrrr.kbjspider.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class KbjHtmlSpider {
    private static Set<String> fileUrls = new HashSet<>();
    private static int index = 0;

    public static void main(String[] args) {
        initFileNames();
        int startPage = (index * 5) + 1;
        int endPage = startPage + 4;
        for (int i = startPage; i <= endPage; i++) {
            printLinks(i);
        }
    }

    private static void printLinks(int page) {
        try {
            HashSet<String> links = new HashSet<>();
            Document doc = Jsoup.connect("http://www.kav1004.com/page/" + page + "/").get();
            Elements elements = doc.select(".aligncenter > a");
            for (Element e : elements) {
                String href = e.attr("href");
                if (href.contains("uploadgig")) {
                    links.add(href);
                }
            }
//            Elements titleElements = doc.select(".entry-title > a");
//            for (Element titleElement : titleElements) {
//                System.out.println(titleElement.text());
//            }
            for (String link : links) {
                if (!checkFile(link)) {
                    System.out.println(link);
                }
            }
            System.out.println(links.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkFile(String name) {
        return fileUrls.contains(name);
    }

    private static void initFileNames() {
        try (FileInputStream inputStream = new FileInputStream("D:\\pic\\kbj\\downloadFiles.txt");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                fileUrls.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
