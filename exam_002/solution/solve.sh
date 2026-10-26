#!/bin/bash
set -e

# Fix 1: DateUtil - clear calendar fields and normalize time to day boundaries.
cat > /app/src/com/settle/DateUtil.java <<'EOF'
package com.settle;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Returns the reporting period of a natural month.
     * result[0] = first second of the month, result[1] = last second of the month.
     */
    public static String[] monthRange(int year, int month) {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(year, month - 1, 1, 0, 0, 0);
        Calendar end = Calendar.getInstance();
        end.clear();
        end.set(year, month, 0, 23, 59, 59);
        return new String[]{SDF.format(start.getTime()), SDF.format(end.getTime())};
    }
}
EOF

# Fix 2: SettlementService - compare wrapper values by equals, not by reference.
cat > /app/src/com/settle/SettlementService.java <<'EOF'
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
        return refundQty != null && refundQty.equals(boughtQty);
    }
}
EOF

# Fix 3: LedgerLoader - count unparsable lines instead of silently dropping them.
cat > /app/src/com/settle/LedgerLoader.java <<'EOF'
package com.settle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LedgerLoader {
    public static int lastErrorCount = 0;

    public static List<LedgerRecord> load(String path) throws IOException {
        List<LedgerRecord> out = new ArrayList<>();
        lastErrorCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    out.add(parse(line));
                } catch (Exception e) {
                    lastErrorCount++;
                }
            }
        }
        return out;
    }

    static LedgerRecord parse(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 4) {
            throw new IllegalArgumentException("malformed line");
        }
        LedgerRecord r = new LedgerRecord();
        r.setTradeTime(parts[0].trim());
        if (!r.getTradeTime().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("bad timestamp: " + r.getTradeTime());
        }
        r.setOrderId(parts[1].trim());
        r.setQuantity(Integer.valueOf(parts[2].trim()));
        r.setType(LedgerType.valueOf(parts[3].trim()));
        return r;
    }
}
EOF

echo "solution applied"
