package com.jiancai.common;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * CSV 导出：UTF-8 BOM，Excel 可直接打开
 */
public final class CsvUtil {

    private CsvUtil() {
    }

    public static byte[] build(String[] headers, List<String[]> rows) {
        StringBuilder sb = new StringBuilder("\uFEFF");
        sb.append(join(headers)).append("\r\n");
        for (String[] row : rows) {
            sb.append(join(row)).append("\r\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String join(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(cell(values[i]));
        }
        return sb.toString();
    }

    private static String cell(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0
                || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }
}
