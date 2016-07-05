INSERT INTO `t_product` VALUES (5, '凹凸租车', '一分钱免费租车', 1, 1, '同城租车半小时送达', 'http://api.caitu99.com/apppage/o2o/aotu.html', 2, 2, 'http://api.caitu99.com/apppage/o2o/images/atzc.png');

UPDATE `t_product` set url = SUBSTR(url,23) where url LIKE 'http://api.caitu99.com%' ;
UPDATE `t_product` set imageurl = SUBSTR(imageurl,23) where imageurl LIKE 'http://api.caitu99.com%' ;

UPDATE `t_product` set price = 10;

DROP TABLE IF EXISTS `t_recharge`;
CREATE TABLE `t_recharge` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自动增长',
  `USER_ID` bigint(20) NOT NULL COMMENT '用户ID',
  `RECHARGE_TYPE` int(11) NOT NULL COMMENT '充值类型(1.购买 2 兑换 3 营销赠送)',
  `GIFT_WAY` int(11) NOT NULL COMMENT '赠送方式（0 非赠送模型 1.及时到帐）',
  `GIFT_TYPE` int(11) NOT NULL COMMENT '赠送类型（0.非赠送模型 1.首次邮箱导入 2 首次手动查询）',
  `TOTAL_AMOUNT` bigint(20) NOT NULL DEFAULT '0' COMMENT '充值财币（单位：分）',
  `RECHARGE_TIME` datetime NOT NULL COMMENT '充值时间/兑换时间/赠送时间',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='财币充值记录表';



#手动查询建表和初始化数据语句_2015114
/*==============================================================*/
/* Table: t_manual                                              */
/*==============================================================*/
create table t_manual
(
   id                   bigint(20) not null auto_increment,
   name                 varchar(10) not null comment '名称',
   type                 int(1) not null comment '商户类型(0:银行;1:商旅;2:购物)',
   icon                 varchar(100) not null comment '图标',
   status               int(1) default 0 comment '状态(1:正常;-1:删除)',
   sort					varchar(1) not null comment '排序字母',
   url                  varchar(100) not null comment '登录地址',
   gmt_create           datetime not null comment '创建时间',
   gmt_modify           datetime not null comment '修改时间',
   primary key (id)
);

alter table t_manual comment '手动积分账户列表';

/*==============================================================*/
/* Table: t_future                                              */
/*==============================================================*/
create table t_future
(
   id                   bigint(10) not null auto_increment,
   code                 varchar(20) not null comment 'code',
   name                 varchar(20) not null comment '名称',
   gmt_create           datetime not null comment '创建时间',
   gmt_modify           datetime not null comment '修改时间',
   primary key (id)
);

alter table t_future comment '手动查询登录配置表'; 

/*==============================================================*/
/* Table: t_manual_future_relation                              */
/*==============================================================*/
create table t_manual_future_relation
(
   id                   bigint(10) not null auto_increment,
   manual_id            bigint(10) not null comment '手动账户列表id',
   future_id            bigint(10) not null comment '登录配置id',
   gmt_create           datetime not null comment '创建时间',
   gmt_modify           datetime not null comment '修改时间',
   primary key (id)
);

alter table t_manual_future_relation comment '手动查询登录配置关联表';

alter table t_manual_future_relation add constraint FK_Reference_1 foreign key (manual_id) references t_manual (id) on delete restrict on update restrict;

alter table t_manual_future_relation add constraint FK_Reference_2 foreign key (future_id) references t_future (id) on delete restrict on update restrict;
	  

/*==============================================================*/
/* Table: t_manual_login                                        */
/*==============================================================*/
create table t_manual_login
(
   id                   bigint(20) not null auto_increment,
   user_id              bigint(20) not null,
   manual_id            bigint(20) not null comment '手动账户查询id',
   login_account        varchar(50) not null comment '登录名',
   password             varchar(50) comment '密码',
   is_password          int(1) not null default 0 comment '是否传递密码(0:传递;1:不传递)',
   type                 int(1) not null comment '账户类型(1:银行卡号;2:身份证;3:用户名;4:手机)',
   gmt_create           datetime not null comment '创建时间',
   gmt_modify           datetime not null comment '修改时间',
   primary key (id)
);

alter table t_manual_login comment '手动账户登录记录表';

alter table t_manual_login add constraint FK_Reference_3 foreign key (manual_id) references t_manual (id) on delete restrict on update restrict;


/*==============================================================*/
/* Table: t_user_card_manual                                    */
/*==============================================================*/
create table t_user_card_manual
(
   id                   bigint(20) not null auto_increment comment '主键',
   user_id              bigint(20) not null comment '用户ID',
   card_type_id         bigint(20) not null comment '卡片类型id',
   user_name            varchar(50) comment '用户名',
   card_no              varchar(50) comment '卡号',
   integral             int(11) not null comment '积分(金币;里程)',
   expiration_integral  int(11) comment '本月即将过期积分(国航本月里程;即将过期的淘金币;即将过期的天猫积分;即将过期的京豆)',
   next_expiration_integral char(10) comment '下月即将过期积分',
   expiration_time      datetime comment '过期时间',
   status               int(1) not null default 1 comment '状态(1:正常;-1删除)',
   gmt_create           datetime not null comment '创建时间',
   gmt_modify           datetime not null comment '修改时间',
   primary key (id)
);

alter table t_user_card_manual comment '用户手动查询积分表';

ALTER TABLE 't_card_type' MODIFY COLUMN 'type_id'  int(11) UNSIGNED NULL DEFAULT NULL COMMENT '1信用卡；2购物卡；3商旅卡' AFTER 'scale';


#初始化手动积分账户数据 
insert into t_manual (id,name,type,icon,status,gmt_create,gmt_modify,url) values(1,'淘宝网',2,'http://static.caitu99.com/apppage/picture/manual/tb.png',-1,now(),now(),"http");
insert into t_manual (id,name,type,icon,status,gmt_create,gmt_modify,url) values(2,'招商银行',0,'http://static.caitu99.com/apppage/picture/manual/zs.png',-1,now(),now(),"http");
insert into t_manual (id,name,type,icon,status,gmt_create,gmt_modify,url) values(3,'京东',2,'http://static.caitu99.com/apppage/picture/manual/jd.png',-1,now(),now(),"http");
insert into t_manual (id,name,type,icon,status,gmt_create,gmt_modify,url) values(4,'天翼',1,'http://static.caitu99.com/apppage/picture/manual/ty.png',1,now(),now(),"checkTianYiImageCode.1.0");
insert into t_manual (id,name,type,icon,status,gmt_create,gmt_modify,url) values(5,'国航',1,'http://static.caitu99.com/apppage/picture/manual/gh.png',1,now(),now(),"http");
	  
#初始化手动查询登录配置数据
insert into t_future (id,code,name,gmt_create,gmt_modify) values(1,'bankCard','银行卡号',now(),now());
insert into t_future (id,code,name,gmt_create,gmt_modify) values(2,'identityCard','身份证',now(),now());
insert into t_future (id,code,name,gmt_create,gmt_modify) values(3,'loginAccount','用户名',now(),now());
insert into t_future (id,code,name,gmt_create,gmt_modify) values(4,'phone','手机',now(),now());
insert into t_future (id,code,name,gmt_create,gmt_modify) values(5,'password','密码',now(),now());
insert into t_future (id,code,name,gmt_create,gmt_modify) values(6,'imageCode','图片验证码',now(),now());
insert into t_future (id,code,name,gmt_create,gmt_modify) values(7,'phoneCode','手机验证码',now(),now());

#初始化天翼手动查询登录配置关联数据
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(1,4,4,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(2,4,6,now(),now());

#初始化国航手动查询登录配置关联数据
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(3,5,4,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(4,5,5,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(5,5,6,now(),now());

#修改卡片类型表中南航,国航,东航,海航卡片类型为商旅卡
update t_card_type set type_id = 3 where id in (3,20,21,22);

#新增卡片类型表购物卡和商旅卡数据
insert into t_card_type (id,name,type_id) values(28,'花旗银行',1);
insert into t_card_type (id,name,type_id) values(29,'天翼积分',3);
insert into t_card_type (id,name,type_id) values(30,'电信积分',3);
insert into t_card_type (id,name,type_id) values(31,'淘宝积分',3);
insert into t_card_type (id,name,type_id) values(32,'天猫积分',3);
insert into t_card_type (id,name,type_id) values(33,'淘宝里程',3);
insert into t_card_type (id,name,type_id) values(34,'京东积分',3);
insert into t_card_type (id,name,type_id) values(35,'京东钢镚',3);

#新增积分兑换比例表购物卡和商旅卡数据
insert into t_exchange_rule (id,card_type_id,way,scale,gmt_create,gmt_modify,status,scaleStr) values(29,29,3,1,now(),now(),1,'100:1');
insert into t_exchange_rule (id,card_type_id,way,scale,gmt_create,gmt_modify,status,scaleStr) values(30,30,3,1,now(),now(),1,'100:1');
insert into t_exchange_rule (id,card_type_id,way,scale,gmt_create,gmt_modify,status,scaleStr) values(31,31,3,1,now(),now(),1,'100:1');
insert into t_exchange_rule (id,card_type_id,way,scale,gmt_create,gmt_modify,status,scaleStr) values(32,32,3,1,now(),now(),1,'100:1');
insert into t_exchange_rule (id,card_type_id,way,scale,gmt_create,gmt_modify,status,scaleStr) values(33,33,3,1,now(),now(),1,'100:1');
insert into t_exchange_rule (id,card_type_id,way,scale,gmt_create,gmt_modify,status,scaleStr) values(34,34,3,1,now(),now(),1,'100:1');
insert into t_exchange_rule (id,card_type_id,way,scale,gmt_create,gmt_modify,status,scaleStr) values(35,35,3,100,now(),now(),1,'1:1');




UPDATE `t_card_type` SET type_id = 2 where `id` in (23,24,25);
UPDATE `t_card_type` SET url = 'http://www.asiamiles.com/am/sc/offers' where `id` = 23;
UPDATE `t_card_type` SET url = 'http://jifen.ctrip.com/rewards/searchprize.aspx' where `id` = 24;
UPDATE `t_card_type` SET url = 'http://square.elong.com' where `id` = 25;



#国航手动查询登录配置关联数据修改
delete from t_manual_future_relation where manual_id = 5;
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(3,5,3,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(4,5,5,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(5,5,6,now(),now());


#修改卡片类型表中商旅卡卡片类型为type_id为2
update t_card_type set type_id = 2 where id in (3,20,21,22);
update t_card_type set type_id = 2 where id in (28,29,30,31,32,33,34,35);

UPDATE `t_product` SET `summary` = replace (`summary`,'一分钱','一毛钱');

#修改手动积分账户,天翼不上线
update t_manual set status = -1 where id = 4;

#修改手动积分账户,导入排序字母数据
update t_manual set sort = 'T' where id = 1;
update t_manual set sort = 'Z' where id = 2;
update t_manual set sort = 'J' where id = 3;
update t_manual set sort = 'T' where id = 4;
update t_manual set sort = 'G' where id = 5;

INSERT INTO `t_version` VALUES ('12', '1.5.0', null, '0', '7', '1');
INSERT INTO `t_version` VALUES ('13', '1.5.0', null, '0', '6', '0');



#2015-11-20	by 熊斌

#积分账户:京东上线
update t_manual set status = 1 where id = 3;
#积分账户:天翼上线
update t_manual set status = 1 where id = 4;

#初始化京东手动查询登录配置关联数据
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(6,3,3,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(7,3,5,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(8,3,6,now(),now());

#初始化招行手动查询登录配置关联数据
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(9,2,2,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(10,2,5,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(11,2,6,now(),now());

#修改手动查询积分账户表注释
ALTER TABLE `t_manual`
MODIFY COLUMN `url`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录地址(废弃)' AFTER `status`;

#清空手动查询积分账户表废弃字段数据
update t_manual set url = null;

#修改手动查询积分账户表商户类型
update t_manual set type = 1 where id = 1;
update t_manual set type = 0 where id = 2;
update t_manual set type = 1 where id = 3;
update t_manual set type = 1 where id = 4;
update t_manual set type = 1 where id = 5;

#新增手动查询积分账户数据.联通,南航,移动
insert into t_manual (id,name,type,icon,status,gmt_create,gmt_modify,sort) values(6,'联通',1,'http://static.caitu99.com/apppage/picture/manual/lt.png',1,now(),now(),"L");
insert into t_manual (id,name,type,icon,status,gmt_create,gmt_modify,sort) values(7,'南航',1,'http://static.caitu99.com/apppage/picture/manual/nh.png',1,now(),now(),"N");
insert into t_manual (id,name,type,icon,status,gmt_create,gmt_modify,sort) values(8,'移动',1,'http://static.caitu99.com/apppage/picture/manual/yd.png',1,now(),now(),"Y");

#积分账户:招商上线
update t_manual set status = 1 where id = 2;

#新增手动查询登录配置表数据,邮箱
insert into t_future (id,code,name,gmt_create,gmt_modify) values(8,'emaill','邮箱',now(),now());

#修改手动查询登录配置表数据,银行卡号统一叫卡号
update t_future set code = 'cardNo',name = '卡号' where id = 1;

#新增手动查询登录配置关联表字段
ALTER TABLE `t_manual_future_relation`
add COLUMN `type`  int(1) NULL DEFAULT 0 COMMENT '类型(0:登录页面配置;1:登录提示语配置配置;2:密码提示语配置)' AFTER `future_id`;

#初始化招行手动查询登录配置关联数据,登录名
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(12,2,2,now(),now(),1);

#初始化京东手动查询登录配置关联数据,登录名
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(13,3,8,now(),now(),1);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(14,3,4,now(),now(),1);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(15,3,3,now(),now(),1);

#初始化天翼手动查询登录配置关联数据,登录名
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(16,4,4,now(),now(),1);

#初始化国航手动查询登录配置关联数据,登录名
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(17,5,1,now(),now(),1);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(18,5,4,now(),now(),1);

#用户手动查询积分表新增字段
ALTER TABLE `t_user_card_manual` ADD COLUMN `login_account`  varchar(50) NULL COMMENT '登录名' AFTER `card_no`;

#初始化南航手动查询登录配置关联数据
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(19,7,3,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(20,7,5,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(21,7,6,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(22,7,4,now(),now(),1);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(23,7,8,now(),now(),1);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(24,7,1,now(),now(),1);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(25,7,2,now(),now(),1);

#初始化淘宝手动查询登录配置关联数据
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(26,1,3,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(27,1,5,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify) values(28,1,6,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(29,1,8,now(),now(),1);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(30,1,4,now(),now(),1);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(31,1,3,now(),now(),1);

#手动查询,淘宝,移动,联通,不上线
update t_manual set status = -1 where id in (1,6,8);

#配置国航、南航、京东、天翼的积分商城官网
#国航
update t_card_type set url = 'http://eshop.airchina.com.cn' where id = 20;
#南航
update t_card_type set url = 'http://czshop.csair.com/index' where id = 3;
#京东
update t_card_type set url = 'http://vip.jd.com/bean.html ' where id in (34,35);
#天翼
update t_card_type set url = 'http://jf.189.cn' where id in (29,30);

#新增花旗银行兑换比例
insert into t_exchange_rule (id,card_type_id,way,scale,gmt_create,gmt_modify,status,scaleStr) values(36,28,1,0.20,now(),now(),1,'500:1')

#新增手动查询登录配置表数据,提示语
insert into t_future (id,code,name,gmt_create,gmt_modify) values(9,'seepassword','查询密码',now(),now());

#初始化手动查询登录配置密码提示语
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(32,1,5,now(),now(),2);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(33,2,9,now(),now(),2);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(34,3,5,now(),now(),2);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(35,5,5,now(),now(),2);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(36,6,5,now(),now(),2);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(37,7,5,now(),now(),2);
insert into t_manual_future_relation (id,manual_id,future_id,gmt_create,gmt_modify,type) values(38,8,5,now(),now(),2);

#修改文案
update t_future set name = '手机号' where id = 4;

#配置淘宝,花旗银行的积分商城官网
update t_card_type set url='https://www.citibank.com.cn/CNGCB/ICARD/rewhom/entryPre.do',type_id = 1 where id = 28;
update t_card_type set url='https://taojinbi.taobao.com/half/list.htm' where id in (31,32,33);
update t_card_type set url='http://vip.tmall.com' where id = 32;

#积分兑换比例,天翼上线
update t_exchange_rule set status = 1 where id = 29;



#增加t_card_type的belong_to，保存该卡片属于哪个银行的信息
alter table `t_card_type` add belong_to varchar(128);

update `t_card_type` set belong_to = '中国银行' where id = 1; 
update `t_card_type` set belong_to = '招商银行' where id = 2; 
update `t_card_type` set belong_to = '南航' where id = 3; 
update `t_card_type` set belong_to = '包商银行' where id = 4; 
update `t_card_type` set belong_to = '北京银行' where id = 5; 
update `t_card_type` set belong_to = '光大银行' where id = 6; 
update `t_card_type` set belong_to = '平安银行' where id = 7; 
update `t_card_type` set belong_to = '招商银行' where id = 8; 
update `t_card_type` set belong_to = '工商银行' where id = 9; 
update `t_card_type` set belong_to = '广发银行' where id = 10; 
update `t_card_type` set belong_to = '浦发银行' where id = 11; 
update `t_card_type` set belong_to = '兴业银行' where id = 12; 
update `t_card_type` set belong_to = '中信银行' where id = 13; 
update `t_card_type` set belong_to = '华夏银行' where id = 14; 
update `t_card_type` set belong_to = '民生银行' where id = 15; 
update `t_card_type` set belong_to = '交通银行' where id = 16; 
update `t_card_type` set belong_to = '建设银行' where id = 17; 
update `t_card_type` set belong_to = '农业银行' where id = 18; 
update `t_card_type` set belong_to = '招商银行' where id = 19; 
update `t_card_type` set belong_to = '国航' where id = 20; 
update `t_card_type` set belong_to = '亚洲万里通' where id = 23; 
update `t_card_type` set belong_to = '携程' where id = 24; 
update `t_card_type` set belong_to = '艺龙' where id = 25; 
update `t_card_type` set belong_to = '招商银行' where id = 26; 
update `t_card_type` set belong_to = '花旗银行' where id = 28; 
update `t_card_type` set belong_to = '天翼' where id = 29; 
update `t_card_type` set belong_to = '电信' where id = 30; 
update `t_card_type` set belong_to = '淘宝' where id = 31; 
update `t_card_type` set belong_to = '淘宝' where id = 32; 
update `t_card_type` set belong_to = '淘宝' where id = 33; 
update `t_card_type` set belong_to = '京东' where id = 34; 
update `t_card_type` set belong_to = '京东' where id = 35; 
update `t_card_type` set belong_to = '移动' where id = 36; 
update `t_card_type` set belong_to = '联通' where id = 37; 
update `t_card_type` set belong_to = '东航' where id = 21; 
update `t_card_type` set belong_to = '海航' where id = 22; 


#增加t_bank表，存放银行和对应的图标地址
DROP TABLE IF EXISTS `t_bank`;  
CREATE TABLE `t_bank` (  
  `id` int(20) NOT NULL AUTO_INCREMENT,  
  `name` varchar(128) NOT NULL,  
  `pic_url`  varchar(128),   
  PRIMARY KEY (`id`)  
) ENGINE=MyISAM  DEFAULT CHARSET=utf8; 

INSERT INTO t_bank (NAME,pic_url) VALUES ('中国银行','/apppage/picture/manual/zgyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('招商银行','/apppage/picture/manual/zsyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('南航','/apppage/picture/manual/nh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('包商银行','/apppage/picture/manual/bsyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('北京银行','/apppage/picture/manual/bjyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('光大银行','/apppage/picture/manual/gdyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('平安银行','/apppage/picture/manual/payh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('工商银行','/apppage/picture/manual/gsyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('广发银行','/apppage/picture/manual/gfyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('浦发银行','/apppage/picture/manual/pfyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('兴业银行','/apppage/picture/manual/xyyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('中信银行','/apppage/picture/manual/zxyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('华夏银行','/apppage/picture/manual/hxyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('民生银行','/apppage/picture/manual/msyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('交通银行','/apppage/picture/manual/jtyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('建设银行','/apppage/picture/manual/jsyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('农业银行','/apppage/picture/manual/nyyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('国航','/apppage/picture/manual/gh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('东航','/apppage/picture/manual/dh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('海航','/apppage/picture/manual/hh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('亚洲万里通','/apppage/picture/manual/yzwlt.png');
INSERT INTO t_bank (name,pic_url) VALUES ('携程','/apppage/picture/manual/xc.png');
INSERT INTO t_bank (name,pic_url) VALUES ('艺龙','/apppage/picture/manual/yl.png');
INSERT INTO t_bank (name,pic_url) VALUES ('花旗银行','/apppage/picture/manual/hqyh.png');
INSERT INTO t_bank (name,pic_url) VALUES ('天翼','/apppage/picture/manual/ty.png');
INSERT INTO t_bank (name,pic_url) VALUES ('电信','/apppage/picture/manual/dx.png');
INSERT INTO t_bank (name,pic_url) VALUES ('淘宝','/apppage/picture/manual/tb.png');
INSERT INTO t_bank (name,pic_url) VALUES ('京东','/apppage/picture/manual/jd.png');
INSERT INTO t_bank (name,pic_url) VALUES ('移动','/apppage/picture/manual/yd.png');
INSERT INTO t_bank (name,pic_url) VALUES ('联通','/apppage/picture/manual/lt.png');

#增加手动查询 洲际,花旗,铂涛会
INSERT INTO `t_manual` (`id`,`name`,`type`,icon,`status`,sort,gmt_create,gmt_modify) VALUES (9,'IHG',1,'http://static.caitu99.com/apppage/picture/manual/zj.png',1,'I',NOW(),NOW());
INSERT INTO `t_manual` (`id`,`name`,`type`,icon,`status`,sort,gmt_create,gmt_modify) VALUES (10,'花旗银行',0,'http://static.caitu99.com/apppage/picture/manual/hqyh.png',1,'H',NOW(),NOW());
INSERT INTO `t_manual` (`id`,`name`,`type`,icon,`status`,sort,gmt_create,gmt_modify) VALUES (11,'铂涛会',1,'http://static.caitu99.com/apppage/picture/manual/bth.png',1,'B',NOW(),NOW());


#增加类型 洲际,铂涛会
INSERT INTO `t_card_type` (`id`,`name`,type_id,url,canexchange,belong_to,manual_id) VALUES (38,'IHG',2,'https://www.ihg.com',0,'IHG',9);
INSERT INTO `t_bank` (`name`,pic_url) VALUES ('IHG','/apppage/picture/manual/zj.png');

INSERT INTO `t_card_type` (`id`,`name`,type_id,url,canexchange,belong_to,manual_id) VALUES (39,'铂涛会',2,'http://www.plateno.com/',0,'铂涛会',11);
INSERT INTO `t_bank` (`name`,pic_url) VALUES ('铂涛会','/apppage/picture/manual/bth.png');

#增加天猫，淘里程，钢镚图标
insert into `t_bank` (`name`,pic_url) VALUES ('钢镚','/apppage/picture/manual/gb.png');
insert into `t_bank` (`name`,pic_url) VALUES ('淘里程','/apppage/picture/manual/tlc.png');
insert into `t_bank` (`name`,pic_url) VALUES ('天猫','/apppage/picture/manual/tm.png');
update `t_card_type` set belong_to = '钢镚' where `id` = 35;
update `t_card_type` set belong_to = '淘里程' where `id` = 33;
update `t_card_type` set belong_to = '天猫' where `id` = 32;

insert into t_future(id,code,name,gmt_create,gmt_modify) values(10,'fourpassword','密码(4位数字)',now(),now());

insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(46,9,3,0,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(47,9,5,0,now(),now());

insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(48,9,8,1,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(49,9,1,1,now(),now());

insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(50,9,10,2,now(),now());

insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(51,10,3,0,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(52,10,5,0,now(),now());

insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(53,10,3,1,now(),now());

insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(54,10,9,2,now(),now());

insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(55,11,4,0,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(56,11,6,0,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(57,11,7,2,now(),now());
insert into t_manual_future_relation (id,manual_id,future_id,type,gmt_create,gmt_modify) values(58,11,4,1,now(),now());


#2015/12/07
UPDATE `t_exchange_rule` SET `status` = 1 WHERE card_type_id IN (23,24,25);















