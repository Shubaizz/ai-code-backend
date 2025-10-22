-- 创建库
create database if not exists ai_code;

-- 切换库
use ai_code;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/vip',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;

create table if not exists userVip
(
    id           bigint auto_increment comment 'id' primary key,
    userId      bigint                                not null comment '关联用户id（外键）',
    vipExpireTime datetime                           null comment '会员过期时间',
    vipCode     varchar(128)                          null comment '会员兑换码',
    vipNumber   bigint                                null comment '会员编号',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除',
    -- 外键约束：关联user表的id
    constraint fk_user_vip_user_id foreign key (userId) references user (id) on delete cascade,
    -- 索引：加速通过用户id查询会员信息
    index idx_user_id (userId),
    -- 索引：会员编号唯一（如果需要唯一标识会员）
    unique key uk_vip_number (vipNumber)
) comment '用户会员信息' collate = utf8mb4_unicode_ci;


create table if not exists userInvitation
(
    id           bigint auto_increment comment 'id' primary key,
    userId      bigint                                not null comment '关联用户id（外键）',
    shareCode   varchar(20)                           null comment '分享码（用户用于邀请他人）',
    inviteUser  bigint                                null comment '邀请者用户id（关联被邀请者的邀请人）',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除',
    -- 外键约束：关联user表的id（当前用户）
    constraint fk_invitation_user_id foreign key (userId) references user (id) on delete cascade,
    -- 外键约束：关联邀请者的user.id（允许为null，即用户可能未被邀请）
    constraint fk_invitation_invite_user foreign key (inviteUser) references user (id) on delete set null,
    -- 索引：加速通过用户id查询邀请信息
    index idx_user_id (userId),
    -- 索引：加速通过邀请者id查询被邀请的用户
    index idx_invite_user (inviteUser),
    -- 唯一索引：分享码全局唯一（避免重复）
    unique key uk_share_code (shareCode)
) comment '用户邀请信息' collate = utf8mb4_unicode_ci;