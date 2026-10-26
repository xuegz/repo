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
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    out.add(parse(line));
                } catch (Exception e) {
                    e.printStackTrace();
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
