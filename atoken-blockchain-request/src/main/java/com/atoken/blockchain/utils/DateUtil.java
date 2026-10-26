package com.atoken.blockchain.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {

    private static final Object lockObj = new Object();

    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    public static final String FORMAT_ALL = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String FORMAT_T1 = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    public static Date parse(String dateStr, String pattern) {
        try {
            return getSdf(pattern).parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date strToDate(String dateStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date formatDate = simpleDateFormat.parse(dateStr);
            if("'Z'".equals(format.substring(format.length() - 3))) {
                // 1、取得本地时间：
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(formatDate);
                // 2、取得时间偏移量：
                int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
                // 3、取得夏令时差：
                int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
                // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
                calendar.add(Calendar.MILLISECOND, (zoneOffset + dstOffset));
                return calendar.getTime();
            } else {
                return formatDate;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }


}
