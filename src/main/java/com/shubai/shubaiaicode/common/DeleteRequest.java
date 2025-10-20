package com.shubai.shubaiaicode.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: DeleteRequest
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/10/20 14:47
 * Version: 1.0
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}

