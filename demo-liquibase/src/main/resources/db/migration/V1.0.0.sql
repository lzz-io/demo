-- 文件名必须是V(大写)开头，同一个版本号不能拆分ddl、dml
-- DROP DATABASE IF EXISTS test_db;
-- CREATE DATABASE test_db
--     CHARACTER SET utf8mb4
--     COLLATE utf8mb4_unicode_ci;
-- USE test_db;

CREATE TABLE t_user (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                        username VARCHAR(50) NOT NULL COMMENT '用户名',
                        password VARCHAR(100) COMMENT '密码',
                        real_name VARCHAR(50) COMMENT '真实姓名',
                        phone VARCHAR(20) COMMENT '手机号',
                        email VARCHAR(100) COMMENT '邮箱',
                        avatar VARCHAR(255) COMMENT '头像地址',
                        status VARCHAR(1) DEFAULT '1' COMMENT '状态 (1:正常, 0:停用)',
                        delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标记 (0:未删除, 1:已删除)',
                        create_by VARCHAR(50) COMMENT '创建人',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        update_by VARCHAR(50) COMMENT '更新人',
                        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        remark VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
CREATE INDEX idx_t_user_username ON t_user(username);
-- ALTER TABLE t_user ADD INDEX idx_t_user_username(username);

INSERT INTO `test_db`.`t_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `avatar`, `status`, `delete_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '1', NULL, NULL, NULL, NULL, NULL, '1', '0', NULL, '2026-04-22 17:27:58', NULL, '2026-04-22 17:27:58', NULL);
