package com.bzrrr.kbjspider.domain;

import lombok.Data;

import java.util.List;

/**
 * @Author: wangziheng
 * @Date: 2021/3/16
 */
@Data
public class EdgeFollow {
    private int count;
    private List<InsEdge> edges;
    private InsPageInfo page_info;
}
