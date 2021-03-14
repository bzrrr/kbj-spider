package com.bzrrr.kbjspider.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

public class KbjHtmlSpider {
	public static void main(String[] args) {
		try {
			int page = 1;
			HashSet<String> links = new HashSet<>();
//			for (; page < 11; page++) {
				Document doc = Jsoup.connect("http://www.kav1004.com/page/" + page + "/").get();
				Elements elements = doc.select(".aligncenter > a");
				for (Element e : elements) {
					String href = e.attr("href");
					if (href.contains("uploadgig")) {
						links.add(href);
					}
				}
//			}
			System.out.println(links);
			System.out.println(links.size());
//			File file = File.createTempFile("test1", ".rar");
//			File file = new File("D:\\develop_tools\\apache-maven-3.5.2\\conf\\test1.rar");
////			String path=links.iterator().next();
//			String path = "https://uploadgig.com/file/download/991fd26197d8E205/ka2021031405.rar";
//
//			int bytesum = 0;
//			int byteread = 0;
//
//			URL url = new URL(path);
//
//			URLConnection conn = url.openConnection();
////			conn.setRequestProperty("PHPSESSID","vog1e4om9e9jd8rk39egrjve6n");
////			conn.setRequestProperty("firewall","25c8d430e786de6527798f31bffc70ef");
////			conn.setRequestProperty("_ga","GA1.2.287963222.1615703863");
////			conn.setRequestProperty("_gid","GA1.2.2049306957.1615703863");
//			conn.setRequestProperty("fs_secure","b14b1D2Df7c0b5Bbd5b970ACc4405E2abc4e9dc63061490815c292030e4e9a7a");
//			conn.setRequestProperty("rn2","d3208d7140315ca6");
//
//			InputStream inStream = conn.getInputStream();
//			FileOutputStream fs = new FileOutputStream(file);
//			byte[] buffer = new byte[1204];
//			int length;
//			while ((byteread = inStream.read(buffer)) != -1) {
//				bytesum += byteread;
//				fs.write(buffer, 0, byteread);
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
