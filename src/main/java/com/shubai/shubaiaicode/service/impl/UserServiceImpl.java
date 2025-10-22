package com.shubai.shubaiaicode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.shubai.shubaiaicode.common.ResultUtils;
import com.shubai.shubaiaicode.exception.BusinessException;
import com.shubai.shubaiaicode.exception.ErrorCode;
import com.shubai.shubaiaicode.exception.ThrowUtils;
import com.shubai.shubaiaicode.model.dto.user.UserAddRequest;
import com.shubai.shubaiaicode.model.dto.user.UserQueryRequest;
import com.shubai.shubaiaicode.model.entity.User;
import com.shubai.shubaiaicode.mapper.UserMapper;
import com.shubai.shubaiaicode.model.enums.UserRoleEnum;
import com.shubai.shubaiaicode.model.vo.user.LoginUserVO;
import com.shubai.shubaiaicode.model.vo.user.UserVO;
import com.shubai.shubaiaicode.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.shubai.shubaiaicode.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author shubaizz
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2. 检查用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在");
        }
        // 3. 加密密码
        String encryptedPassword = getEncryptedPassword(userPassword);
        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        user.setUserName("未知用户" + RandomUtil.randomNumbers(4));
        user.setUserRole(UserRoleEnum.USER.getValue());
        int result = this.mapper.insert(user);
        if (result < 1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = getEncryptedPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 4. 获得脱敏后的用户信息
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }


    @Override
    public UserVO getUserVO(User user) {
        if (user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).toList();
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest){
        if (userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (StrUtil.isNotBlank(userRole)) {
            queryWrapper.eq("userRole", userRole);
        }
        if (StrUtil.isNotBlank(userName)) {
            queryWrapper.like("userName", userName);
        }
        if (StrUtil.isNotBlank(userAccount)) {
            queryWrapper.like("userAccount", userAccount);
        }
        if (StrUtil.isNotBlank(userProfile)) {
            queryWrapper.like("userProfile", userProfile);
        }
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        }
        return queryWrapper;
    }


    @Override
    public String getEncryptedPassword(String userPassword){
        // 盐值加密
        final String SALT = "shubai";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        if (userAddRequest.getUserAccount() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        final String DEFAULT_PASSWORD = "12345678";
        String encryptedPassword = this.getEncryptedPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptedPassword);
        if (StrUtil.isBlank(user.getUserName()) || user.getUserName() == null){
            user.setUserName("未知用户" + RandomUtil.randomNumbers(4));
        }
        if (StrUtil.isBlank(user.getUserName()) || user.getUserName() == null){
            user.setUserName("未知用户" + RandomUtil.randomNumbers(4));
        }
        if (StrUtil.isBlank(user.getUserRole()) || user.getUserRole() == null){
            user.setUserRole(UserRoleEnum.USER.getValue());
        }
        if (StrUtil.isBlank(user.getUserAccount()) || user.getUserAccount() == null){
            while (true){
                String userAccount = RandomUtil.randomNumbers(8);
                if (this.getById(userAccount) == null){
                    user.setUserAccount(userAccount);
                    break;
                }
            }
        }
        int result = this.mapper.insert(user);
        if (result != 1){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return user.getId();
    }
}
