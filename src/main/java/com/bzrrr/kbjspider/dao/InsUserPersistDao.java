package com.bzrrr.kbjspider.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bzrrr.kbjspider.model.dto.InsUserDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: wangziheng
 * @Date: 2021/3/16
 */
@Mapper
public interface InsUserPersistDao extends BaseMapper<InsUserDto> {
}
