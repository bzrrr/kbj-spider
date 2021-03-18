package com.bzrrr.kbjspider.domain;

import lombok.Data;

@Data
public class InsUser {
	private EdgeOwner edge_owner_to_timeline_media;
	private EdgeFollow edge_follow;
}
