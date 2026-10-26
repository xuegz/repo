create database if not exists coin_account collate utf8_bin;
CREATE TABLE `coin_account`.`tomo_address` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '唯一编号',
  `wallet_id` char(64) NOT NULL COMMENT '钱包编号',
  `addr` varchar(100) NOT NULL COMMENT '地址',
  `amount` decimal(25,10) DEFAULT '0.0000000000' COMMENT '余额',
  `delegate_amount` decimal(25,10) DEFAULT '0.0000000000' COMMENT '质押金额',
  `unbonding_amount` decimal(25,10) DEFAULT '0.0000000000' COMMENT '赎回中的金额',
  `rewards_amount` decimal(25,10) DEFAULT '0.0000000000' COMMENT '质押收益',
  `platform` smallint(5) unsigned NOT NULL COMMENT '平台 ios android',
  `addr_type` int(10) unsigned NOT NULL COMMENT '地址类型：4 创建， 16 导入',
  `account_num` varchar(50) DEFAULT NULL COMMENT '账号id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tma_unq_addr` (`addr`),
  UNIQUE KEY `tma_unq_wallet_id` (`wallet_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='tomo地址信息表';

CREATE TABLE `coin_account`.`tomo_block` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '唯一编号',
  `hash` char(64) COLLATE utf8_bin NOT NULL COMMENT '区块hash',
  `height` int(10) unsigned NOT NULL COMMENT '区块高度',
  `state` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '是否是孤块 0：不是孤块 1：是孤块',
  `previous` char(64) COLLATE utf8_bin NOT NULL COMMENT '父区块hash',
  `tx_count` int(10) unsigned NOT NULL COMMENT '交易数量',
  `size` int(10) unsigned DEFAULT NULL COMMENT '大小',
  `timestamp` bigint(20) unsigned NOT NULL COMMENT '打包区块时间',
  `relative` tinyint(3) unsigned DEFAULT NULL COMMENT '1 此区块有自己的地址交易信息；   0 此区块无自己的地址交易信息',
  `synced` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否同步 0：未同步 1：已同步',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tmb_unq_hash_height` (`hash`,`height`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tomo区块信息';

CREATE TABLE `coin_account`.`tomo_delegate_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一编号',
  `wallet_id` char(64) NOT NULL COMMENT '钱包编号',
  `addr` varchar(100) NOT NULL COMMENT '地址',
  `validator_addr` varchar(100) NOT NULL COMMENT '验证人地址',
  `delegate_amount` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '委托的atom数量',
  `unbonding_amount` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '赎回中的金额',
  `rewards_amount` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '质押收益',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tmdd_unq_wallet_validator` (`wallet_id`,`validator_addr`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='tomo委托细节表';
--
-- CREATE TABLE `coin_account`.`gas_config` (
--   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
--   `coin_type` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '币种',
--   `name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '币种名称',
--   `alias` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '币种别名',
--   `gas_recommend_price` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '推荐gas price',
--   `gas_priority_price` decimal(25,10) NOT NULL DEFAULT '0.0000000000' COMMENT '优先gas price',
--   `gas_limit` bigint(20) NOT NULL COMMENT 'gas_limit',
--   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   PRIMARY KEY (`id`,`gas_recommend_price`,`gas_priority_price`,`gas_limit`)
-- ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `coin_account`.`tomo_sync_error` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一编号',
  `block_height` int(11) NOT NULL COMMENT '区块高度',
  `block_hash` char(64) NOT NULL COMMENT '区块hash',
  `tx_hash` char(64) DEFAULT NULL COMMENT '交易hash',
  `tx_num` int(11) DEFAULT NULL COMMENT '交易编号',
  `error_info` mediumtext COMMENT '错误信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='同步错误表';

CREATE TABLE `coin_account`.`tomo_tx` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一编号',
  `hash` char(64) NOT NULL COMMENT '交易hash',
  `from_addr` varchar(100) DEFAULT NULL COMMENT '出账地址',
  `to_addr` varchar(100) DEFAULT NULL COMMENT '入账地址',
  `fee` decimal(25,10) DEFAULT NULL COMMENT '消耗的atom',
  `amount` decimal(25,10) NOT NULL COMMENT '转账金额',
  `block_hash` char(64) DEFAULT NULL COMMENT '区块hash',
  `block_height` int(11) DEFAULT NULL COMMENT '区块高度',
  `block_state` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '是否是孤块 0：不是孤块 1：是孤块',
  `timestamp` bigint(20) DEFAULT NULL COMMENT '交易打包时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `state` int(11) NOT NULL DEFAULT '1' COMMENT '交易状态，0表示执行失败，1表示执行成功',
  `asset_short_name` varchar(55) DEFAULT NULL COMMENT '资产缩写',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态，0：未上链 1：已上链 2:赎回成功',
  `confirm` smallint(6) NOT NULL DEFAULT '0' COMMENT '区块打包之后需要19个sr进行确认，也就是当前区块之后需要有18个以上的区块打包之后才算是上链了不会回退了',
  `gas_limit` decimal(65,0) DEFAULT NULL COMMENT '交易指定的gas',
  `used_gas_limit` decimal(65,0) DEFAULT NULL COMMENT '被使用的gas limit',
  `gas_price` decimal(25,10) DEFAULT NULL COMMENT '交易指定gasPrice',
  `tx_id` varchar(100) NOT NULL COMMENT '交易唯一编号',
  `type` varchar(100) DEFAULT NULL COMMENT '交易类型 转账：cosmos-sdk/MsgSend 赎回收益：cosmos-sdk/MsgWithdrawDelegationReward 解质押：cosmos-sdk/MsgUndelegate 质押：cosmos-sdk/MsgDelegate ',
  `plat_type` smallint(6) NOT NULL COMMENT '是否是我们平台发送的交易 0：发送者是平台用户  1：接收者是平台用户  2:发送者和接收者都是平台用户 ',
  `tx_num` int(11) DEFAULT NULL COMMENT '交易下标',
  `comment` varchar(1000) DEFAULT '' COMMENT '交易备注',
  `account_num` varchar(50) DEFAULT NULL COMMENT '账号id',
  `nonce` int(11) NOT NULL DEFAULT '0' COMMENT '交易编号 sequence',
  `main_coin` varchar(55) DEFAULT NULL COMMENT '主网币种',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tmt_tx_id` (`tx_id`),
  KEY `tmt_from_addr_index` (`from_addr`),
  KEY `tmt_to_addr_index` (`to_addr`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='tomo区块交易表';

CREATE TABLE `coin_account`.`tomo_validator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一编号',
  `addr` varchar(100) NOT NULL COMMENT '验证人地址',
  `consensus_pubkey` varchar(100) DEFAULT NULL COMMENT '共识公钥',
  `jailed` smallint(6) NOT NULL COMMENT '是否监禁',
  `status` smallint(6) NOT NULL COMMENT '0: unbonded 1: unbonding 2: bonded',
  `tokens` decimal(25,10) NOT NULL COMMENT '委托的token数量',
  `delegator_shares` decimal(25,10) NOT NULL COMMENT '总权益量',
  `moniker` varchar(1000) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '姓名',
  `identity` varchar(1000) DEFAULT NULL COMMENT '身份签名',
  `website` varchar(1000) DEFAULT NULL COMMENT '网址链接',
  `details` varchar(5000) DEFAULT NULL COMMENT '验证人描述',
  `unbonding_height` int(11) DEFAULT NULL COMMENT '解绑区块高度',
  `unbonding_timestamp` bigint(20) DEFAULT NULL COMMENT '解绑打包时间',
  `rate` double DEFAULT NULL COMMENT '佣金比率',
  `max_rate` double DEFAULT NULL COMMENT '最大佣金比率',
  `max_change_rate` double DEFAULT NULL COMMENT '验证人佣金每日最大增长率',
  `rate_update_timestamp` bigint(20) DEFAULT NULL COMMENT '佣金比率改变时间',
  `year_yield` double DEFAULT NULL COMMENT '年华收益率',
  `all_delegator_num` int(11) DEFAULT NULL COMMENT '委托人的数量',
  `order_num` int(11) DEFAULT '0' COMMENT '排名 倒叙',
  `keybase_url` varchar(1000) DEFAULT NULL COMMENT '验证人logo',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tmv_addr_uindex` (`addr`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='tomo验证人信息表';

alter table `coin_account`.tomo_block modify hash varchar(100) not null comment '区块hash';

alter table `coin_account`.tomo_block modify previous varchar(100) not null comment '父区块hash';

alter table `coin_account`.tomo_tx modify hash varchar(100) not null comment '交易hash';

alter table `coin_account`.tomo_tx modify block_hash varchar(100) null comment '区块hash';

alter table `coin_account`.tomo_tx modify type varchar(100) null comment '普通交易：NORMAL 质押:VOTE，解除质押:UNVOTE 赎回:WITHDRAW';

alter table `coin_account`.tomo_delegate_detail
	add status varchar(50) default 'DELEGATE' null comment 'DELEGATE：质押中 UNDELEGATE：解除质押中';

alter table `coin_account`.tomo_address
	add pledged int default 0 null;

drop index tmb_unq_hash_height on tomo_block;

create unique index tomo_block_height_hash_uindex
	on tomo_block (height desc, hash asc);

CREATE TABLE `coin_account`.`tomo_block_history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '唯一编号',
  `hash` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '区块hash',
  `height` int(10) unsigned NOT ULL COMMENT '区块高度',
  `state` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '是否是孤块 0：不是孤块 1：是孤块',
  `previous` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '父区块hash',
  `tx_count` int(10) unsigned NOT NULL COMMENT '交易数量',
  `size` int(10) unsigned DEFAULT NULL COMMENT '大小',
  `timestamp` bigint(20) unsigned NOT NULL COMMENT '打包区块时间',
  `relative` tinyint(3) unsigned DEFAULT NULL COMMENT '1 此区块有自己的地址交易信息；   0 此区块无自己的地址交易信息',
  `synced` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否同步 0：未同步 1：已同步',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tmb_unq_height_hash` (`height`, `hash`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tomo区块信息历史表';
