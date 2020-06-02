package com.example.eric.assistant.bean;


public class SlotsBean {
    private String name;
    private String value;

    private String content;
    private String code;
    private String messageType;
    private String keywords;

    private DatetimeBean datetime;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public DatetimeBean getDatetime() {
        return datetime;
    }

    public void setDatetime(DatetimeBean datetime) {
        this.datetime = datetime;
    }

}
