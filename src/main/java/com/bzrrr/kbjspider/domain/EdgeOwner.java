package com.bzrrr.kbjspider.domain;

import lombok.Data;

import java.util.List;

@Data
public class EdgeOwner {
	private List<InsEdge> edges;
	private InsPageInfo page_info;
}
