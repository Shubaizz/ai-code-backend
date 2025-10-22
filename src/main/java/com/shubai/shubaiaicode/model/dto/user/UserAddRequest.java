package com.shubai.shubaiaicode.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassName: UserAddRequest
 * Description:
 * <p>
 * Author: shubaizz
 * DateTime: 2025/10/22 13:46
 * Version: 1.0
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}

