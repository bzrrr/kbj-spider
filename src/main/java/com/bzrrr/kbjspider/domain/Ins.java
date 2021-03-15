package com.bzrrr.kbjspider.domain;

import lombok.Data;

@Data
public class Ins {
	private Graphql graphql;
	private InsData data;
	private String logging_page_id;
}
