package com.shubai.shubaiaicode.common;

import com.shubai.shubaiaicode.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: BaseResponse
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/10/20 14:46
 * Version: 1.0
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

