package com.atoken.update.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @description:
* @author: atoken-team
* @date: 2020/4/7 12:05
**/
public class StringUtil {
    private static Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
    private static Pattern paramPattern = Pattern.compile("\\{(.*?)\\}");
    private static final String GENERATE_CODE_STR = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String GENERATE_CODE_NUMBER_STR = "1234567890";
    private static Random random = new Random();

    public static String parseProperty(String originStr, Environment environment) {
        Matcher matcher = pattern.matcher(originStr);
        while (matcher.find()) {
            String propertyName = matcher.group(1);
            String propertyValue = environment.getProperty(propertyName);
            VerifyUtil.notBlank(propertyValue, propertyName + " is not found");
            originStr = originStr.replaceFirst("\\$\\{" + propertyName + "\\}", propertyValue);
        }
        return originStr;
    }

    public static void parseParam(JSONObject obj, Map<String, String> paramMap, String objName) {
        obj.entrySet().stream().forEach(item -> {
            if (item.getValue() instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) item.getValue();
                parseParam(jsonObject, paramMap, objName + "." + item.getKey());
            } else {
                paramMap.put(objName + "." + item.getKey(), item.getValue().toString());
            }
        });
    }

    public static String replaceStr(String originStr, Map<String, String> paramMap) {
        Matcher matcher = paramPattern.matcher(originStr);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = paramMap.get(paramName);
            VerifyUtil.notBlank(paramValue, paramName + " is not found");
            originStr = originStr.replaceFirst("\\{" + paramName + "\\}", paramValue);
        }
        return originStr;
    }

    public static String generateCode(int len, boolean onlyNumber) {
        char[] baseChars;
        if (onlyNumber) {
            baseChars = GENERATE_CODE_NUMBER_STR.toCharArray();
        } else {
            baseChars = GENERATE_CODE_STR.toCharArray();
        }
        StringBuilder tempStringBuilder = new StringBuilder();
        int length = baseChars.length;
        for(int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(length);
            tempStringBuilder.append(baseChars[randomIndex]);
        }
        return tempStringBuilder.toString();
    }

    public static String generateCode(int len) {
        return generateCode(len, false);
    }

    public static String capitalFirstLetter(String origin) {
        return origin.substring(0, 1).toUpperCase() + origin.substring(1);
    }
}
