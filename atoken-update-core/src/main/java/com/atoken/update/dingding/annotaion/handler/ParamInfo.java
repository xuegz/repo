package com.atoken.update.dingding.annotaion.handler;

import java.lang.annotation.Annotation;

/**
 * Created by Administrator on 2018/9/17.
 */
public class ParamInfo {
    private Class paramType;
    private Object paramValue;
    private Annotation[] annotations;
    private String paramName;
    private Integer index;

    private ParamInfo(Class paramType, Object paramValue, Annotation[] annotations, String paramName, Integer index) {
        this.paramType = paramType;
        this.paramValue = paramValue;
        this.annotations = annotations;
        this.paramName = paramName;
        this.index = index;
    }

    public static ParamInfo builder(Class paramType, Object paramValue, Annotation[] annotations, String paramName) {
        return new ParamInfo(paramType, paramValue, annotations, paramName, null);
    }


    public static ParamInfo builder(Class paramType, Object paramValue, Annotation[] annotations, String paramName, Integer index) {
        return new ParamInfo(paramType, paramValue, annotations, paramName, index);
    }

    public Class getParamType() {
        return paramType;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public String getParamName() {
        return paramName;
    }

    public Integer getIndex() {
        return index;
    }
}
