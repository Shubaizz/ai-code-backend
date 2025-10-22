package com.shubai.shubaiaicode.aop;

import com.shubai.shubaiaicode.annotation.AuthCheck;
import com.shubai.shubaiaicode.exception.BusinessException;
import com.shubai.shubaiaicode.exception.ErrorCode;
import com.shubai.shubaiaicode.model.entity.User;
import com.shubai.shubaiaicode.model.enums.UserRoleEnum;
import com.shubai.shubaiaicode.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ClassName: AuthInteceptor
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/10/21 16:51
 * Version: 1.0
 */
@Component
@Aspect
public class AuthInterceptor {

    @Autowired
    private UserService userService;

    /**
     * 拦截所有有@AuthCheck注解的方法
     * @param proceedingJoinPoint 切点
     * @param authCheck 注解
     * @return  方法返回值
     * @throws Throwable 抛出异常
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint proceedingJoinPoint, AuthCheck authCheck) throws Throwable{
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不要校验权限，放行
        if (mustUserRoleEnum == null) {
            return proceedingJoinPoint.proceed();
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return proceedingJoinPoint.proceed();
    }
}
