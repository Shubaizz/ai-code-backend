package com.shubai.shubaiaicode.common;

import lombok.Data;

/**
 * ClassName: PageRequest
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/10/20 14:47
 * Version: 1.0
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}

