package com.shubai.shubaiaicode.controller;

import com.shubai.shubaiaicode.common.BaseResponse;
import com.shubai.shubaiaicode.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: HealthController
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/10/20 14:41
 * Version: 1.0
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("ok");
    }
}

