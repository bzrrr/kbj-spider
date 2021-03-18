package com.bzrrr.kbjspider.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: wangziheng
 * @Date: 2021/3/16
 */
@Data
@TableName(value = "kbj")
public class KbjDto {
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    private String link;
    private String filename;
    private String title;
    private Boolean saved;
}
