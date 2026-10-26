alter table ont_query.token modify can_shift smallint default 0 not null comment '是否支持换币';

alter table ont_query.token modify del smallint default 0 not null comment '是否删除 0：未删除 1：删除';

alter table ont_query.tx modify confirm smallint default 0 not null comment '区块打包之后需要19个sr进行确认，也就是当前区块之后需要有18个以上的区块打包之后才算是上链了不会回退了';

alter table ont_query.tx_detail modify is_entry smallint not null comment '0:出账 1:入账';

alter table ont_query.tx_detail modify del smallint default 0 not null comment '是否删除 0：未删除 1：删除';