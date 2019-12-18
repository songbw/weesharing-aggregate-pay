DROP TABLE IF EXISTS wspay_paytype ;
CREATE TABLE `wspay_paytype` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  
  `appid` varchar(100) NOT NULL default '' COMMENT 'APPID',
  `name` varchar(100) NOT NULL default '' COMMENT '支付方式',
  `description` varchar(100) NOT NULL default ''  COMMENT '描述',
  
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
