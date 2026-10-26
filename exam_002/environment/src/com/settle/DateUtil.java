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
        start.set(year, month - 1, 1);
        Calendar end = Calendar.getInstance();
        end.set(year, month, 0);
        return new String[]{SDF.format(start.getTime()), SDF.format(end.getTime())};
    }
}
