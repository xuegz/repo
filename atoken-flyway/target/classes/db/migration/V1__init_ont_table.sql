create database if not exists ont_query collate utf8_bin;
create table if not exists ont_query.block (
  id bigint unsigned auto_increment primary key comment '唯一编号',
  hash char(64) not null comment '区块hash',
  height INT unsigned not null comment '区块高度',
  state smallint unsigned not null default 0 comment '是否是孤块 0：不是孤块 1：是孤块',
  previous char(64) not null comment '父区块hash',
  tx_count int unsigned not null comment '交易数量',
  size int unsigned not null comment '大小',
  timestamp bigint unsigned not null comment '打包区块时间',
  relative tinyint unsigned comment '1 此区块有自己的地址交易信息；   0 此区块无自己的地址交易信息',
  synced tinyint unsigned not null default 0 comment '是否同步 0：未同步 1：已同步',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
  constraint idx_hash_height unique (hash, height)
)default collate utf8_bin comment 'ont区块信息';

create table if not exists ont_query.address (
  id bigint unsigned auto_increment primary key comment '唯一编号',
  wallet_id char(64) not null comment '钱包编号',
  addr char(34) not null comment '地址',
  platform smallint unsigned not null comment '平台 ios android',
  addr_type int unsigned not null comment '地址类型：4 创建， 16 导入',
  create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
  constraint unique_addr unique (addr),
  constraint unique_wallet_id unique (wallet_id)
)default charset = utf8 comment '地址信息表';

create table if not exists ont_query.token
(
	id bigint auto_increment comment '唯一编号'
		primary key,
	name varchar(55) not null comment '资产缩写',
	full_name varchar(100) null comment '资产全称',
	contract_addr char(40) not null comment '合约地址',
	asset_code char(40) not null comment '资产代码',
	decimals int default 0 not null comment '精度',
	can_shift smallint default 0 not null comment '是否支持换币',
	logo varchar(255) null comment '资产logo',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	asset_type varchar(50) not null comment '资产类型 MAIN,GAS,EOP-4,EOP-5,EOP-8 ',
	del smallint default 0 not null comment '是否删除 0：未删除 1：删除',
	order_num tinyint default 0 null,
	type int default 195 null,
	daddy varchar(50) default 'ONT' not null comment '主网资产',
	constraint asset_code
		unique (asset_code),
	constraint contract_addr
		unique (contract_addr)
)
comment '币种表' charset=utf8;

create table if not exists ont_query.address_asset (
  id bigint not null auto_increment primary key comment '唯一编号',
  contract_addr char(40) not null comment '合约地址',
  asset_code char(40) comment '资产代码',
  addr char(34) not null comment '地址',
  added char(1) default '1' not null comment '前端是否显示',
  amount decimal(25, 10) default 0.00000000 not null comment '余额',
  wallet_id char(64) not null comment '钱包编号',
	asset_type varchar(50) not null comment '资产类型 MAIN,GAS,EOP-4,EOP-5,EOP-8 ',
  create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint unique_addr_asset unique (addr, contract_addr)
)default charset = utf8 comment '地址资产表';

create table if not exists ont_query.tx (
  id bigint not null auto_increment primary key comment '唯一编号',
  hash char(64) not null comment '交易hash',
  from_addr char(34) null comment '出账地址',
  to_addr char(34) null comment '入账地址',
  fee decimal(25, 10) null comment '消耗的ong',
  amount decimal(25, 10) not null comment '转账金额',
  block_hash char(64) null comment '区块hash',
  block_height int null comment '区块高度',
  block_state smallint unsigned not null default 0 comment '是否是孤块 0：不是孤块 1：是孤块',
	timestamp bigint null comment '交易打包时间',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
  state int default 1 not null comment '交易状态，0表示执行失败，1表示执行成功',
  contract_addr char(40) not null comment '合约地址',
  asset_short_name varchar(55) null comment '资产缩写',
  status int default 0 not null comment '状态，0：未上链 1：已上链',
	confirm smallint default 0 not null comment '区块打包之后需要19个sr进行确认，也就是当前区块之后需要有18个以上的区块打包之后才算是上链了不会回退了',
  gas_limit decimal(65, 0) comment '交易指定的gas',
	gas_price decimal(25, 10) comment '交易指定gasPrice',
	tx_id varchar(100) not null unique comment '交易唯一编号',
	type varchar(50) comment '交易类型',
	plat_type smallint not null comment '是否是我们平台发送的交易 0：发送者是平台用户  1：接收者是平台用户  2:发送者和接收者都是平台用户 ',
	tx_num int null comment '交易下标',
	comment varchar(1000) default '' comment '交易备注'
)default charset = utf8 comment '区块交易表';

create table if not exists ont_query.tx_detail (
  id bigint not null auto_increment primary key comment '唯一编号',
  tx_hash char(64) not null comment '交易hash',
  wallet_id char(64) not null comment '钱包编号',
  addr char(34) not null comment '地址',
  amount decimal(25, 10) not null comment '交易金额',
  block_height int not null comment '区块高度',
  block_hash varchar(64) not null comment '区块hash',
  block_state smallint unsigned not null default 0 comment '是否是孤块 0：不是孤块 1：是孤块',
  timestamp bigint null comment '交易打包时间',
  state int default 1 not null comment '交易状态，0表示执行失败，1表示执行成功',
  contract_addr char(40) not null comment '合约地址',
  fee decimal(25, 10) null comment '消耗的ong',
	tx_num int null comment '交易下标',
	is_entry smallint not null comment '0:出账 1:入账',
	tx_id varchar(100) null comment '交易唯一编号',
	del smallint not null default 0 comment '是否删除 0：未删除 1：删除'
)default charset = utf8 comment '交易明细表';

create table if not exists ont_query.sync_error (
  id bigint auto_increment not null primary key comment '唯一编号',
  block_height int not null comment '区块高度',
  block_hash char(64) not null comment '区块hash',
  tx_hash char(64) comment '交易hash',
  tx_num int comment '交易编号',
  error_info mediumtext comment '错误信息',
  create_time timestamp not null default current_timestamp comment '创建时间'
)default charset = utf8 comment '同步错误表';

alter table ont_query.tx
	add nonce varchar(20) null comment 'nonce值';

