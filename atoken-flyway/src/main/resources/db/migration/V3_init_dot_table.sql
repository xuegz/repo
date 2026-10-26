create database if not exists dot_query collate utf8_bin;
create table if not exists dot_query.address
(
	id bigint unsigned auto_increment comment '唯一编号'
		primary key,
	wallet_id char(64) not null comment '钱包编号',
	addr varchar(100) not null comment '地址',
	amount decimal(25,10) default 0.0000000000 not null comment '余额',
	delegate_amount decimal(25,10) default 0.0000000000 not null comment '质押金额',
	unbonding_amount decimal(25,10) default 0.0000000000 not null comment '赎回中的金额',
	rewards_amount decimal(25,10) default 0.0000000000 not null comment '质押收益',
	platform smallint(5) unsigned not null comment '平台 ios android',
	addr_type int unsigned not null comment '地址类型：4 创建， 16 导入',
	account_num varchar(50) null comment '账号id',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint unique_addr
		unique (addr),
	constraint unique_wallet_id
		unique (wallet_id)
)
comment '地址信息表' charset=utf8;

create table if not exists dot_query.block
(
	id bigint unsigned auto_increment comment '唯一编号'
		primary key,
	hash varchar(100) not null comment '区块hash',
	height int unsigned not null comment '区块高度',
	state smallint(5) unsigned default 0 not null comment '是否是孤块 0：不是孤块 1：是孤块',
	previous varchar(100) not null comment '父区块hash',
	tx_count int unsigned not null comment '交易数量',
	size int unsigned null comment '大小',
	timestamp bigint unsigned not null comment '打包区块时间',
	relative tinyint unsigned null comment '1 此区块有自己的地址交易信息；   0 此区块无自己的地址交易信息',
	synced tinyint unsigned default 0 not null comment '是否同步 0：未同步 1：已同步',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint idx_hash_height
		unique (hash, height)
)
comment 'dot区块信息';

create table if not exists dot_query.sync_error
(
	id bigint auto_increment comment '唯一编号'
		primary key,
	block_height int not null comment '区块高度',
	block_hash varchar(100) not null comment '区块hash',
	tx_hash char(64) null comment '交易hash',
	tx_num int null comment '交易编号',
	error_info mediumtext null comment '错误信息',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
comment '同步错误表' charset=utf8;

create table if not exists dot_query.tx
(
	id bigint auto_increment comment '唯一编号'
		primary key,
	hash varchar(100) not null comment '交易hash',
	from_addr varchar(100) null comment '出账地址',
	to_addr varchar(100) null comment '入账地址',
	fee decimal(30,15) null comment '消耗的dot',
	amount decimal(30,15) not null comment '转账金额',
	block_hash varchar(100) null comment '区块hash',
	block_height int null comment '区块高度',
	block_state smallint(5) unsigned default 0 not null comment '是否是孤块 0：不是孤块 1：是孤块',
	timestamp bigint null comment '交易打包时间',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	state int default 1 not null comment '交易状态，0表示执行失败，1表示执行成功',
	asset_short_name varchar(55) null comment '资产缩写',
	status int default 0 not null comment '状态，0：未上链 1：已上链 2:赎回成功',
	confirm smallint(6) default 0 not null comment '区块打包之后需要19个sr进行确认，也就是当前区块之后需要有18个以上的区块打包之后才算是上链了不会回退了',
	gas_limit decimal(65) null comment '交易指定的gas',
	gas_price decimal(30,15) null comment '交易指定gasPrice',
	tx_id varchar(100) not null comment '交易唯一编号',
	type varchar(100) null comment '交易类型',
	plat_type smallint(6) not null comment '是否是我们平台发送的交易 0：发送者是平台用户  1：接收者是平台用户  2:发送者和接收者都是平台用户',
	tx_num int null comment '交易下标',
	comment varchar(1000) default '' null comment '交易备注',
	account_num varchar(50) null comment '账号id',
	nonce int default 0 not null comment '交易编号 sequence',
	main_coin varchar(55) null comment '主网币种',
	constraint tx_id
		unique (tx_id)
)
comment '区块交易表' charset=utf8;

create index tx_from_addr_index
	on dot_query.tx (from_addr);

create index tx_to_addr_index
	on dot_query.tx (to_addr);

