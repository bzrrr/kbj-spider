package com.bzrrr.kbjspider.domain;

import lombok.Data;

@Data
public class InsNode {
	private String display_url;
	private boolean is_video;
	private String video_url;
	private InsChildren edge_sidecar_to_children;
	private String id;
	private String username;
}
