-- 建表文件

drop table if exists TB_USER;

create table TB_USER
(
  ID         INTEGER auto_increment,
  USERNAME   VARCHAR(32),
  CREATETIME TIMESTAMP,
  constraint TB_USER_PK
    primary key (ID)
);

create unique index TB_USER_ID_UINDEX
	on TB_USER (ID);

create index TB_USER_USERNAME_INDEX
	on TB_USER (USERNAME);

create index TB_USER_CREATETIME_INDEX
	on TB_USER (CREATETIME desc);

