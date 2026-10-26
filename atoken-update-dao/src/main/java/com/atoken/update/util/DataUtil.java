package com.atoken.update.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author qingyun.yu
 * @date 2018/11/22
 */
public class DataUtil {
    public static String generateIndexId(String... strs) {
        return Stream.of(strs).reduce((temp, item) -> temp + "_" + item).get();
    }

    public static List<Integer> generateLongList(Integer startLong, Integer endLong) {
        List<Integer> result = new ArrayList<>();
        for(Integer l = startLong; l < endLong;) {
            result.add(++l);
        }
        return result;
    }
}
