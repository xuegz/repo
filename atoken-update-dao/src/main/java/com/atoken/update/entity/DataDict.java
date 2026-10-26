package com.atoken.update.entity;

/**
 * 数据字典
 */
public class DataDict {

    private Long id;

    /**
     * 大分类
     */
    private Integer category;

    /**
     * 小分类
     */
    private Integer type;

    /**
     * 字典key
     */
    private String key;

    /**
     * 字典value
     */
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
