package com.settle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettlementService {

    public static SettleResult settle(List<LedgerRecord> records, String periodStart, String periodEnd) {
        SettleResult result = new SettleResult();
        Map<String, Integer> bought = new HashMap<>();
        for (LedgerRecord r : records) {
            if (!inPeriod(r, periodStart, periodEnd)) {
                continue;
            }
            if (r.getType() == LedgerType.BUY) {
                bought.put(r.getOrderId(), r.getQuantity());
            }
        }
        for (LedgerRecord r : records) {
            if (!inPeriod(r, periodStart, periodEnd)) {
                continue;
            }
            if (r.getType() == LedgerType.BUY) {
                result.netPoints += r.getQuantity() * 2;
            } else {
                Integer boughtQty = bought.get(r.getOrderId());
                boolean full = boughtQty != null && isFullRefund(r.getQuantity(), boughtQty);
                if (full) {
                    result.netPoints -= boughtQty * 2;
                    result.closedOrders++;
                    result.reversals++;
                } else {
                    result.netPoints -= r.getQuantity() * 2;
                    result.reversals++;
                }
            }
        }
        return result;
    }

    static boolean inPeriod(LedgerRecord r, String start, String end) {
        String t = r.getTradeTime();
        return t.compareTo(start) >= 0 && t.compareTo(end) <= 0;
    }

    static boolean isFullRefund(Integer refundQty, Integer boughtQty) {
        return refundQty == boughtQty;
    }
}
