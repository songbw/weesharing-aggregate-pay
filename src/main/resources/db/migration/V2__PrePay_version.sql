DROP TABLE IF EXISTS wspay_pre_consume ;
CREATE TABLE `wspay_pre_consume` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  
  `pay_type` varchar(100) NOT NULL default '' COMMENT '支付方式',
  `payer` varchar(100) NOT NULL default '' COMMENT '付款方',
  `payee` varchar(100) NOT NULL default ''  COMMENT '收款方',
  
  `order_no` varchar(100) NOT NULL COMMENT '支付订单号',
  `out_trade_no` varchar(100) NOT NULL COMMENT '订单号',
  `trade_no` varchar(100) NOT NULL default '' COMMENT '联机账户订单号',
  
  `body` varchar(150) NOT NULL default '' COMMENT '商品描述',
  `remark` varchar(256) NOT NULL default '' COMMENT '用户自定义',
  
  `total_fee` varchar(11) NOT NULL default '0' COMMENT '交易总金额',
  `act_pay_fee` varchar(11) NOT NULL default '0' COMMENT '交易实际金额',
  
  `status` tinyint(3) NOT NULL default 0 COMMENT '交易状态: 1: 成功, 2: 失败, 0: 新创建',
  `trade_date` varchar(20) NOT NULL default '' COMMENT '交易时间',
  
  `create_date` datetime NOT NULL COMMENT '创建时间',
  
  `limit_pay` varchar(32) NOT NULL default ''  COMMENT '支付限制',
  
  `return_url` varchar(200) NOT NULL default '' comment '前端返回地址',
  `notify_url` varchar(200) NOT NULL default ''comment '异步通知地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
