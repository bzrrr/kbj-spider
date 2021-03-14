package com.bzrrr.kbjspider.spider;

import com.alibaba.fastjson.JSON;
import com.bzrrr.kbjspider.domain.EdgeOwner;
import com.bzrrr.kbjspider.domain.Ins;
import com.bzrrr.kbjspider.domain.InsEdge;
import com.bzrrr.kbjspider.domain.InsNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;

public class InsSpider {
	private static int count = 0;
	private static String nextPage;

	public static void main(String[] args) {
//		homePage();
//		downloadPic();
		nextPage = "QVFDQ3JINGo0cEw3RHY0M2Fzanc4WUFGX3RiUllGdjgyUlREUzJPN1M3YW02TFk0eVktdG9QYXpBNGs4UEtHTUUzRlI3S0Y1ZVR1TWs0dVhnTl9UNFNMWg==";
		test(nextPage);
	}

	private static void test(String nextPage) {
		try {
//			String api = "https://www.instagram.com/2km2km/?__a=1";
//			String nextPage = "QVFDQ3JINGo0cEw3RHY0M2Fzanc4WUFGX3RiUllGdjgyUlREUzJPN1M3YW02TFk0eVktdG9QYXpBNGs4UEtHTUUzRlI3S0Y1ZVR1TWs0dVhnTl9UNFNMWg==";
//			String nextPage = "QVFETUExRXdJNG9GZ2h2NXE3V2ZCS3NBNk5HNDhVUnV3TGhCNVVFX205MDdwRHdKLS1CeVpfR2xsclFYOGVpMFdRanRoTXVNUkpsMW54emc3TWtRSldmbA==";
//			String nextPage = "QVFCTExlTHZTR3BPaExjdjBvRmdmd2xPNTZaRWE2SHNZNG1fNDBjdWdNc1ZtWlF3Tk1NVndFbHc2S3VGdXluWmJKRTZVRVppbHZod0JWa3Zya09pOFRkTA==";
			String api = "https://www.instagram.com/graphql/query/?query_hash=003056d32c2554def87228bc3fd9668a&variables={\"id\":\"1261174072\",\"first\":12,\"after\":\"" + nextPage + "\"}";
			String proxyHost = "0.0.0.0";//代理IP
			String proxyPort = "1080";//代理端口
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
			URL url = new URL(api);
			URLConnection conn = url.openConnection(proxy);
			conn.setRequestProperty("cookie", "ig_did=CB63A1C7-F131-4D21-B6FA-A55505C54A0C; csrftoken=U9acvshicAmqOewAkiP8HFevZ88QajOf; mid=YE4frgALAAH2mYstMEGULbNVgKXv; ig_nrcb=1; rur=ATN; ds_user_id=1203194525; sessionid=1203194525%3AluGJbN7jHvyuWG%3A9; shbid=2849; shbts=1615732707.4266386");
			conn.setRequestProperty("referer", "https://www.instagram.com/");
			conn.setRequestProperty("sessionid", "1203194525:luGJbN7jHvyuWG:9");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
			InputStream inStream = conn.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
			StringBuilder bs = new StringBuilder();
			String l = null;
			while ((l = buffer.readLine()) != null) {
				bs.append(l);
			}
			String json = bs.toString();
			Ins ins = JSON.parseObject(json, Ins.class);
			EdgeOwner currentEdgeOwner = ins.getData().getUser().getEdge_owner_to_timeline_media();
			List<InsEdge> edges = currentEdgeOwner.getEdges();
			recur(edges);
			System.out.println(count);
			count = 0;
			if (currentEdgeOwner.getPage_info().isHas_next_page()) {
				test(currentEdgeOwner.getPage_info().getEnd_cursor());
			}
			Thread.sleep(2000L);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void recur(List<InsEdge> edges) {
		for (InsEdge edge : edges) {
			InsNode node = edge.getNode();
			if (node.is_video()) {
				System.out.println(node.getVideo_url());
				count++;
			}
			if (node.getEdge_sidecar_to_children() != null) {
				recur(node.getEdge_sidecar_to_children().getEdges());
			} else {
				if (!node.is_video()) {
					System.out.println(node.getDisplay_url());
					count++;
				}
			}
		}
	}

	private static void homePage() {
		try {
			String path = "https://www.instagram.com/2km2km" + "/";
			int bytesum = 0;
			int byteread = 0;
			HashSet<String> links = new HashSet<>();
			String proxyHost = "0.0.0.0";//代理IP
			String proxyPort = "1080";//代理端口
			Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
			URL url = new URL(path);
			URLConnection conn = url.openConnection(proxy);
			conn.setRequestProperty("sessionid", "1203194525:luGJbN7jHvyuWG:9");
			conn.setRequestProperty("cookie", "ig_did=CB63A1C7-F131-4D21-B6FA-A55505C54A0C; csrftoken=U9acvshicAmqOewAkiP8HFevZ88QajOf; mid=YE4frgALAAH2mYstMEGULbNVgKXv; ig_nrcb=1; rur=ATN; ds_user_id=1203194525; sessionid=1203194525%3AluGJbN7jHvyuWG%3A9; shbid=2849; shbts=1615732707.4266386");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
			conn.setRequestProperty("x-fb-trip-id", "1679558926");
			InputStream inStream = conn.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
			StringBuilder bs = new StringBuilder();
			String l = null;
			while ((l = buffer.readLine()) != null) {
				bs.append(l);
			}
//			Document doc = Jsoup.connect(path).get();
			String html = bs.toString();
			System.out.println(html);
			Document doc = Jsoup.parse(html);
//			Elements elements = doc.select("a");
			Elements elements = doc.getElementsByTag("a");
			for (Element e : elements) {
				String href = e.attr("href");
				links.add(href);
			}
			System.out.println(links);
			System.out.println(links.size());
		} catch (IOException e) {
			e.printStackTrace();
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
