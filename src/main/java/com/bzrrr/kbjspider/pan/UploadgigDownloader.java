package com.bzrrr.kbjspider.pan;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class UploadgigDownloader {
	public static void main(String[] args) {
		RestTemplate rt = new RestTemplate();
		RequestEntity<Void> re = RequestEntity.get("https://uploadgig.com/file/download/411Ef8319dbbDA68/kbj2021031310.rar")
				.header(HttpHeaders.COOKIE, "PHPSESSID=vog1e4om9e9jd8rk39egrjve6n")
				.build();
		ResponseEntity<String> entity1 = rt.exchange(re, String.class);
//		ResponseEntity<String> entity = rt.getForEntity("https://uploadgig.com/file/download/411Ef8319dbbDA68/kbj2021031310.rar", String.class);
		System.out.println(entity1);
	}
}
