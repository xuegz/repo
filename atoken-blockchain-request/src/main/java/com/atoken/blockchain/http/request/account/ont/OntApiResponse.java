package com.atoken.blockchain.http.request.account.ont;

public class OntApiResponse<T> {
    private String action;
    private Integer error;
    private String desc;
    private String version;
    private T result;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static class OntContractListResponse extends OntApiResponse<OntContractList> {}
}
