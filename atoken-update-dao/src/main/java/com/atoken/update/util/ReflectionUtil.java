package com.atoken.update.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Qingyun Yu
 * @Description
 * @create 2018-04-25 13:54
 **/
public class ReflectionUtil {
    private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

    public static Object getFieldValue(Object obj, String fieldName) {
        Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
        field.setAccessible(true);
        return ReflectionUtils.getField(field, obj);
    }

    public static String[] getParameterNames(Class clazz, String methodName, Class... paramTypes) {
        String[] parameterNames;
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            logger.error("get parameter name failed,Exception:{}", e);
        }
        parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (Objects.isNull(parameterNames) || parameterNames.length == 0) {
            Parameter[] parameters = method.getParameters();
            parameterNames = Stream.of(parameters).map(parameter -> parameter.getName()).toArray((val) -> new String[val]);
        }
        return parameterNames;
    }

    public static Map<String, Object> getBeanValue(Object obj) {
        if (Objects.isNull(obj)) {
            throw new RuntimeException("obj is null");
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getFields();
        Map<String, Object> resultMap = new HashMap<>();
        for (Field field : fields) {
            resultMap.put(field.getName(), getFieldValue(obj, field.getName()));
        }
        return resultMap;
    }

    public static Class<?> parseGenericClass(Type returnType) {
        try {
            ParameterizedType listGenericType = (ParameterizedType) returnType;
            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
            Class clazz = Class.forName(listActualTypeArguments[0].getTypeName());
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
