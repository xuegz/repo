alter table dot_query.address
	add type varchar(50) default 'STASH' null comment '地址类型：STASH,CONTROLLER,SESSION';

alter table dot_query.address
	add stake_status varchar(100) default 'NO_STAKE' null comment '质押状态：无质押：NO_STAKE,质押中：STAKING,质押完成：STAKED,取消质押中：CANCIL_STAKE,质押修改中：UPDATE_STAKE';

create table if not exists dot_query.validator
(
	id bigint auto_increment comment '唯一编号'
		primary key,
	addr varchar(100) not null comment '验证人地址',
	status smallint(6) not null comment '0: unbonded 1: unbonding 2: bonded',
	tokens decimal(30,15) not null comment '委托的token数量',
	commission_rate double null comment '佣金比率',
	annual_return double null comment '年华收益率',
	all_delegator_num int null comment '委托人的数量',
	order_num int default 0 null comment '排名 倒叙',
	keybase_url varchar(1000) null comment '验证人logo',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint validator_addr_uindex
		unique (addr)
)
comment '验证人信息表' charset=utf8;

create table if not exists dot_query.delegate_detail
(
	id bigint auto_increment comment '唯一编号'
		primary key,
	wallet_id char(64) not null comment '钱包编号',
	addr varchar(100) not null comment '地址',
	validator_addr varchar(100) not null comment '验证人地址',
	delegate_amount decimal(30,15) default 0.0000000000 not null comment '委托的dot数量',
	unbonding_amount decimal(30,15) default 0.0000000000 not null comment '赎回中的金额',
	rewards_amount decimal(30,15) default 0.0000000000 not null comment '质押收益',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint unique_addr_validator
		unique (addr, validator_addr)
)
comment '委托细节表' charset=utf8;

alter table dot_query.address drop key unique_wallet_id;

create table dot_query.nominate_extrinsic(
  id bigint auto_increment primary key comment '唯一标识',
  bond_hash varchar(100) not null comment '绑定交易的hash',
  hash varchar(100) comment '交易hash',
  extrinsic varchar(2000) not null comment '已签名的交易字符串',
  status varchar(30) default 'UNTREATED' comment '状态, 未处理：UNTREATED 已处理：PROCESSED 处理失败：FAILURE',
  error_message varchar(1000) comment '错误信息',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)default charset=utf8 collate=utf8_general_ci comment '提名交易表';