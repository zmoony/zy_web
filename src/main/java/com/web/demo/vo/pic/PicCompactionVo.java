package com.web.demo.vo.pic;

import lombok.Data;

/**
 * @author yuez
 * @title: picCompaction
 * @projectName zy_web
 * @description: 图片压缩vo类
 * @date 2020/11/25 9:26
 */
@Data
public class PicCompactionVo {
    private String base64;
    private String imgUrl;
    private Integer width;
    private Integer height;
    private Double scale;
}
