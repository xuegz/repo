package com.atoken.update.dingding.annotaion;

import com.atoken.update.dingding.DingdingAdaptor;
import com.atoken.update.dingding.annotaion.handler.AlarmBaseHandler;
import com.atoken.update.dingding.annotaion.handler.ParamInfo;
import com.atoken.update.util.ObjectUtil;
import com.atoken.update.util.ReflectionUtil;
import com.atoken.update.util.StringUtil;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Aspect
@Component
public class AlarmAspect {
    private Logger log = LoggerFactory.getLogger(AlarmAspect.class);
    @Autowired
    private DingdingAdaptor dingdingAdaptor;
    @Autowired
    private Environment environment;
    private Pattern paramPattern = Pattern.compile("\\{(.*?)\\}");

    @Around("@annotation(sendAlarm)")
    public boolean around(ProceedingJoinPoint proceedingJoinPoint, SendAlarm sendAlarm) {
        try {
            ProxyMethodInvocation methodInvocation = (ProxyMethodInvocation) ReflectionUtil.getFieldValue(proceedingJoinPoint, "methodInvocation");
            Method method = methodInvocation.getMethod();
            Class clazz = method.getDeclaringClass();
            Class[] paramTypes = method.getParameterTypes();
            String[] paramNames = ReflectionUtil.getParameterNames(clazz, method.getName(), paramTypes);
            Object[] args = proceedingJoinPoint.getArgs();

            Map<String, ParamInfo> paramInfoMap = new HashMap<>(64);
            IntStream.range(0, paramNames.length).forEach(i -> {
                paramInfoHandler(paramNames[i], args[i], paramInfoMap);
            });

            Class handlerClass = sendAlarm.handler();
            AlarmBaseHandler alarmBaseHandler = (AlarmBaseHandler) handlerClass.newInstance();
            String originCondition = sendAlarm.condition();

            String condition = originCondition.contains("${") ? StringUtil.parseProperty(originCondition, environment) : originCondition;
            if (alarmBaseHandler.handler(paramInfoMap)
                    || conditionParser(condition, paramInfoMap)) {
                String originMessage = sendAlarm.value();
                String originAsyncStr = sendAlarm.async();
                String asyncStr = originAsyncStr.contains("${") ? StringUtil.parseProperty(originAsyncStr, environment) : originAsyncStr;
                asyncStr = asyncStr.contains("{") ? replaceName(asyncStr, paramInfoMap) : asyncStr;

                String message = originMessage.contains("${") ? StringUtil.parseProperty(originMessage, environment) : originMessage;
                message = message.contains("{") ? replaceName(message, paramInfoMap) : message;

                boolean async = Boolean.valueOf(asyncStr);
                if (async) {
                    dingdingAdaptor.asyncSendMessage(message);
                } else {
                    dingdingAdaptor.sendMessage(message);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("send notification failed, Exception: {}", e);
            return false;
        }
    }

    private boolean conditionParser(String condition, Map<String, ParamInfo> paramInfoMap) {
        String[] orConditions = condition.split("\\|\\|");
        boolean result = false;
        for (String orCondition : orConditions) {
            String[] andConditions = orCondition.split("&&");
            if (andConditions.length > 1) {
                boolean andResult = true;
                for (String andCondition : andConditions) {
                    andResult = andResult || ConditionTypeEnum.parseAndHandler(andCondition, paramInfoMap);
                    if (!andResult) {
                        break;
                    }
                }
                result = result || andResult;
                if (result) {
                    break;
                }
            } else {
                result = result || ConditionTypeEnum.parseAndHandler(orCondition, paramInfoMap);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    private String replaceName(String originStr, Map<String, ParamInfo> paramInfoMap) {
        Matcher matcher = paramPattern.matcher(originStr);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            ParamInfo paramInfo = paramInfoMap.get(paramName);
            String paramValue = Objects.isNull(paramInfo) || Objects.isNull(paramInfo.getParamValue()) ? "": paramInfo.getParamValue().toString();
            originStr = originStr.replaceFirst("\\{" + paramName + "\\}", paramValue);
        }
        return originStr;
    }

    private void paramInfoHandler(String paramName, Object paramValue, Map<String, ParamInfo> paramInfoMap) {

        if (Objects.nonNull(paramValue) && ObjectUtil.isObject(paramValue)) {
            Class clazz = paramValue.getClass();
            paramInfoMap.put(paramName, ParamInfo.builder(clazz, paramValue, null, paramName));

            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields).forEach(field -> {

                field.setAccessible(true);
                try {
                    Object tempValue = field.get(paramValue);
                    paramInfoHandler(paramName + "." + field.getName(), tempValue, paramInfoMap);
                } catch (IllegalAccessException e) {
                    log.debug("parse obj error: {}", e);
                }
            });

        } else {
            boolean isNull = Objects.isNull(paramValue);
            paramInfoMap.put(paramName, ParamInfo.builder(isNull? null: paramValue.getClass(), isNull ? "" : paramValue, null, paramName));
        }
    }

    enum ConditionTypeEnum {
        EQUALS("==", new EqualsHandler()), GREATER_EQUALS(">=", new GreaterEqualsHandler()), LESS_EQUALS("<=", new LessEqualsHandler()), GREATER(">", new GreaterHandler()), LESS("<", new LessHandler()), NOT_EQUALS("!=", new NotEqualsHandler());
        private String condition;
        private AbstractHandler handler;

        ConditionTypeEnum(String condition, AbstractHandler handler) {
            this.condition = condition;
            this.handler = handler;
        }

        public static boolean parseAndHandler(String condition, Map<String, ParamInfo> paramInfoMap) {
            if (Strings.isBlank(condition)) {
                return true;
            }
            ConditionTypeEnum conditionTypeEnum = Arrays.stream(ConditionTypeEnum.values())
                    .filter(item -> condition.contains(item.condition)).findFirst().orElse(null);
            if (Objects.isNull(conditionTypeEnum)) {
                return false;
            }
            String[] values = condition.split(conditionTypeEnum.condition);
            if (values.length != 2) {
                throw new IllegalArgumentException("Values length is not 2");
            }
            return conditionTypeEnum.handler.handler(values[0].trim(), values[1].trim(), paramInfoMap);
        }
    }

    public static abstract class AbstractHandler {
        public boolean handler(String valueLeft, String valueRight, Map<String, ParamInfo> paramInfoMap) {
            ParamInfo paramInfo = paramInfoMap.get(valueLeft);
            switch (paramInfo.getParamType().getSimpleName()) {
                case "String": {
                    String objLeft = (String) paramInfo.getParamValue();
                    String objRight = valueRight;
                    return handler(objLeft, objRight);
                }
                case "Integer": {
                    Integer objLeft = (Integer) paramInfo.getParamValue();
                    Integer objRight = Integer.valueOf(valueRight);
                    return handler(objLeft, objRight);
                }
                case "Long": {
                    Long objLeft = (Long) paramInfo.getParamValue();
                    Long objRight = Long.valueOf(valueRight);
                    return handler(objLeft, objRight);
                }
                case "Float": {
                    Float objLeft = (Float) paramInfo.getParamValue();
                    Float objRight = Float.valueOf(valueRight);
                    return handler(objLeft, objRight);
                }
                case "Double": {
                    Double objLeft = (Double) paramInfo.getParamValue();
                    Double objRight = Double.valueOf(valueRight);
                    return handler(objLeft, objRight);
                }
                case "Boolean": {
                    Boolean objLeft = (Boolean) paramInfo.getParamValue();
                    Boolean objRight = Boolean.valueOf(valueRight);
                    return handler(objLeft, objRight);
                }
                case "Character": {
                    Character objLeft = (Character) paramInfo.getParamValue();
                    Character objRight = Character.valueOf(valueRight.charAt(0));
                    return handler(objLeft, objRight);
                }
                case "Byte": {
                    Byte objLeft = (Byte) paramInfo.getParamValue();
                    Byte objRight = Byte.valueOf(valueRight);
                    return handler(objLeft, objRight);
                }
                case "Short": {
                    Short objLeft = (Short) paramInfo.getParamValue();
                    Short objRight = Short.valueOf(valueRight);
                    return handler(objLeft, objRight);
                }
                case "Date": {
                    Date objLeft = (Date) paramInfo.getParamValue();
                    Date objRight = Date.valueOf(valueRight);
                    return handler(objLeft, objRight);
                }
                case "BigDecimal": {
                    BigDecimal objLeft = (BigDecimal) paramInfo.getParamValue();
                    BigDecimal objRight = new BigDecimal(valueRight);
                    return handler(objLeft, objRight);
                }
                case "BigInteger": {
                    BigInteger objLeft = (BigInteger) paramInfo.getParamValue();
                    BigInteger objRight = new BigInteger(valueRight);
                    return handler(objLeft, objRight);
                }
                default:
                    throw new RuntimeException("Value is not a base type!!!");
            }

        }

        protected abstract <T extends Comparable> boolean handler(T valueLeft, T valueRight);
    }

    public static class EqualsHandler extends AbstractHandler {

        @Override
        protected <T extends Comparable> boolean handler(T valueLeft, T valueRight) {
            return valueLeft.equals(valueRight);
        }
    }

    public static class NotEqualsHandler extends AbstractHandler {


        @Override
        protected <T extends Comparable> boolean handler(T valueLeft, T valueRight) {
            return !valueLeft.equals(valueRight);
        }
    }

    public static class GreaterEqualsHandler extends AbstractHandler {

        @Override
        protected <T extends Comparable> boolean handler(T valueLeft, T valueRight) {
            return valueLeft.compareTo(valueRight) >= 0;
        }
    }

    public static class GreaterHandler extends AbstractHandler {

        @Override
        protected <T extends Comparable> boolean handler(T valueLeft, T valueRight) {
            return valueLeft.compareTo(valueRight) > 0;
        }
    }

    public static class LessEqualsHandler extends AbstractHandler {

        @Override
        protected <T extends Comparable> boolean handler(T valueLeft, T valueRight) {
            return valueLeft.compareTo(valueRight) <= 0;
        }
    }

    public static class LessHandler extends AbstractHandler {

        @Override
        protected <T extends Comparable> boolean handler(T valueLeft, T valueRight) {
            return valueLeft.compareTo(valueRight) < 0;
        }
    }
}
