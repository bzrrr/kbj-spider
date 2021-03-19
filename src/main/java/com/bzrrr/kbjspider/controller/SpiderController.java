package com.bzrrr.kbjspider.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bzrrr.kbjspider.domain.InsCookie;
import com.bzrrr.kbjspider.model.dto.InsUserDto;
import com.bzrrr.kbjspider.service.InsUserPersistService;
import com.bzrrr.kbjspider.spider.InsSpider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wangziheng
 * @Date: 2021/3/17
 */
@RestController
@RequestMapping("/spider")
@Slf4j
public class SpiderController {

    @Autowired
    private InsSpider insSpider;
    @Autowired
    private InsUserPersistService userService;

    @GetMapping("/start/all")
    public void startIns(InsCookie cookie) {
        insSpider.startTask(cookie);
    }

    @GetMapping("/stop/all")
    public void stopIns() {
        insSpider.stopTask();
    }

    @GetMapping("/start/one")
    public void startInsOne(@RequestParam String username, @RequestParam InsCookie cookie) {
        QueryWrapper<InsUserDto> userWrapper = new QueryWrapper<>();
        userWrapper.eq("username", username);
        userWrapper.last("limit 1");
        InsUserDto user = userService.getOne(userWrapper);
        insSpider.startSpider(username, user.getUserid(), cookie.getCookie());
    }
}
