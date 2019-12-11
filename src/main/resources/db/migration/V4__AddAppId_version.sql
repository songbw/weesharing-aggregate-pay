ALTER TABLE `wspay_pre_consume` ADD `app_id` VARCHAR(10) DEFAULT '11' ;
ALTER TABLE `wspay_consume`     ADD `app_id` VARCHAR(10) DEFAULT '11' ;
ALTER TABLE `wspay_pre_refund`  ADD `app_id` VARCHAR(10) DEFAULT '11' ;
ALTER TABLE `wspay_refund`      ADD `app_id` VARCHAR(10) DEFAULT '11' ;