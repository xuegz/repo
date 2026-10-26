package com.settle;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("usage: Main <ledger-file> <scene-name>");
            System.exit(2);
        }
        List<LedgerRecord> records = LedgerLoader.load(args[0]);
        String scene = args[1];
        if (records.isEmpty()) {
            System.err.println("no records parsed from " + args[0]);
            System.exit(2);
        }
        String firstTime = records.get(0).getTradeTime();
        int year = Integer.parseInt(firstTime.substring(0, 4));
        int month = Integer.parseInt(firstTime.substring(5, 7));
        String[] range = DateUtil.monthRange(year, month);
        SettleResult result = SettlementService.settle(records, range[0], range[1]);
        System.out.println("SCENE=" + scene);
        System.out.println("PERIOD_START=" + range[0]);
        System.out.println("PERIOD_END=" + range[1]);
        System.out.println("NET_POINTS=" + result.netPoints);
        System.out.println("REVERSALS=" + result.reversals);
        System.out.println("CLOSED_ORDERS=" + result.closedOrders);
        System.out.println("ERROR_LINES=" + LedgerLoader.lastErrorCount);
    }
}
