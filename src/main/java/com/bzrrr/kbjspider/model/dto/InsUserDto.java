package com.bzrrr.kbjspider.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: wangziheng
 * @Date: 2021/3/16
 */
@Data
@TableName(value = "ins_user")
public class InsUserDto {
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    private String userid;
    private String username;
    private Date updateTime;
    private Boolean spider;
}
