package com.atoken.update.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class CurrentHeightDetail {
    @JSONField(name = "the_current_height")
    private Integer theCurrentHeight;

    @JSONField(name = "to_confirm_the_number")
    private Integer toConfirmTheNumber;

    private String error = "";

    public Integer getTheCurrentHeight() {
        return theCurrentHeight;
    }

    public void setTheCurrentHeight(Integer theCurrentHeight) {
        this.theCurrentHeight = theCurrentHeight;
    }

    public Integer getToConfirmTheNumber() {
        return toConfirmTheNumber;
    }

    public void setToConfirmTheNumber(Integer toConfirmTheNumber) {
        this.toConfirmTheNumber = toConfirmTheNumber;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}