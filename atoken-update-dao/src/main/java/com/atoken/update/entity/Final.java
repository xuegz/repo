package com.atoken.update.entity;

public class Final<T> {
    private T type;

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public static <T>Final<T> newFinal(T t) {
        Final<T> tFinal = new Final<>();
        tFinal.setType(t);
        return tFinal;
    }
}
