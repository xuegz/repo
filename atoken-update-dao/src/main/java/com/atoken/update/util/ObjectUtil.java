package com.atoken.update.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2018/9/17.
 */
public class ObjectUtil {
    private static Logger logger = LoggerFactory.getLogger(ObjectUtil.class);
    public static boolean isObject(Object obj) {
        return isObject(obj.getClass());
    }

    public static boolean isObject(Class clazz) {
        switch (clazz.getSimpleName()) {
            case "Integer":
                return false;
            case "String":
                return false;
            case "Boolean":
                return false;
            case "Float":
                return false;
            case "Long":
                return false;
            case "Character":
                return false;
            case "Double":
                return false;
            case "Byte":
                return false;
            case "Short":
                return false;
            case "Date":
                return false;
            case "BigDecimal":
                return false;
            case "BigInteger":
                return false;
            default:
                logger.debug("value is object param");;
        }
        if ("Enum".equals(clazz.getSuperclass().getSimpleName())) {
            return false;
        }
        return true;
    }
}
