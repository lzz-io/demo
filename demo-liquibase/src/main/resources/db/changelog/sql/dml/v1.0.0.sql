--liquibase formatted sql
--changeset q12199331697:dml

INSERT INTO `test_db`.`t_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `avatar`, `status`, `delete_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '1', NULL, NULL, NULL, NULL, NULL, '1', '0', NULL, '2026-04-22 17:27:58', NULL, '2026-04-22 17:27:58', NULL);
