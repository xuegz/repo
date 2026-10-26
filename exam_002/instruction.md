# 月度积分结算工具对账修复

## 背景

`/app` 下是一个用于生成月度积分对账报表的命令行工具。它能编译、能运行，但财务部门反馈：生成的报表与人工对账结果不一致，根因尚未查明。

## 用法

```bash
bash /app/build.sh
java -cp /app/out com.settle.Main <ledger-file> <scene-name>
```

流水文件每行一条记录，字段用 `|` 分隔：

```
<yyyy-MM-dd HH:mm:ss>|<orderId>|<quantity>|<TYPE>
```

`TYPE` 取值：`BUY`（购买）、`REFUND_FULL`（全额退款）、`REFUND_PART`（部分退款）。

## 业务规则

1. 报表统计周期（PERIOD）为流水**首条记录所在自然月**：从当月 1 日 `00:00:00` 起，至当月最后一天 `23:59:59` 止。只有交易时间落在周期内的记录才计入当月报表。
2. `BUY`：净积分 += quantity × 2。
3. 退款冲正：
   - `REFUND_FULL`：若判定为全额退款（退款数量等于该订单原购买数量），则冲正数量 = 原购买数量，且该订单标记为已关闭（`CLOSED_ORDERS` 计数 +1）。
   - 其余退款按退款数量冲正。
   - 两者 `REVERSALS` 均 +1。净积分按冲正数量 × 2 扣减。
4. 无法解析的行不计入任何统计，但必须计入 `ERROR_LINES`。
5. 报告逐行输出，格式固定：

```
SCENE=<场景名>
PERIOD_START=<yyyy-MM-dd 00:00:00>
PERIOD_END=<yyyy-MM-dd 23:59:59>
NET_POINTS=<整数>
REVERSALS=<整数>
CLOSED_ORDERS=<整数>
ERROR_LINES=<整数>
```

## 已知现象

`/app/data/ledgers/` 下有 6 份历史账目（scene1 ~ scene6）。财务人工对账确认：**目前程序在全部 6 份账目上生成的报表都与人工结果不一致**。

## 你的任务

定位并修复程序中的缺陷，使 6 份账目全部按上述业务规则对平。要求：

- 不得改变 CLI 用法与报告格式
- 不得修改 `/app/data` 下的账目文件
- 修复必须针对根因，禁止为了让特定输出变对而硬编码数值
