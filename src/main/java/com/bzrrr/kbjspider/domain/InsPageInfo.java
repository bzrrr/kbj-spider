package com.bzrrr.kbjspider.domain;

import lombok.Data;

@Data
public class InsPageInfo {
	private boolean has_next_page;
	private String end_cursor;
}
