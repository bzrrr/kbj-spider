package com.bzrrr.kbjspider.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: wangziheng
 * @Date: 2021/3/15
 */
@Data
@TableName(value = "ins")
public class InsDto {
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    private String username;
    private String link;
    private String filename;
    private Boolean saved;
}
