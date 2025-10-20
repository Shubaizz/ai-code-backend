package com.shubai.shubaiaicode.exception;

import lombok.Getter;

/**
 * ClassName: BusinessException
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/10/20 14:45
 * Version: 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
