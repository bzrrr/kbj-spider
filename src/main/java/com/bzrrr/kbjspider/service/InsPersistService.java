package com.bzrrr.kbjspider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bzrrr.kbjspider.dao.InsPersistDao;
import com.bzrrr.kbjspider.dao.InsUserPersistDao;
import com.bzrrr.kbjspider.model.dto.InsDto;
import com.bzrrr.kbjspider.model.dto.InsUserDto;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: wangziheng
 * @Date: 2021/3/16
 */
@Service
@Transactional
public class InsPersistService extends ServiceImpl<InsPersistDao, InsDto> {
    @Resource
    private InsUserPersistDao insUserPersistDao;

    public void saveList(List<InsDto> list) {
        QueryWrapper<InsDto> wrapper = new QueryWrapper<>();
        Iterator<InsDto> it = list.iterator();
        while (it.hasNext()) {
            InsDto dto = it.next();
            wrapper.clear();
            wrapper.eq("filename", dto.getFilename());
            int count = this.count(wrapper);
            if (count > 0) {
                it.remove();
            }
        }
        for (InsDto dto : list) {
            System.out.println(dto.getUsername() + " -- " + dto.getLink());
        }
        if (list.size() > 0) {
            this.saveBatch(list);
            QueryWrapper<InsUserDto> userWrapper = new QueryWrapper<>();
            userWrapper.eq("username", list.get(0).getUsername());
            InsUserDto userDto = insUserPersistDao.selectOne(userWrapper);
            if (userDto != null) {
                userDto.setUpdateTime(new Date());
                insUserPersistDao.updateById(userDto);
            } else {
                userDto = new InsUserDto();
                userDto.setUpdateTime(new Date());
                userDto.setUsername(list.get(0).getUsername());
                insUserPersistDao.insert(userDto);
            }
        }
    }
}
