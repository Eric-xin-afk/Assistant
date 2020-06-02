package com.example.eric.assistant.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainBean {

    private int rc ;                //response code   回复码
    private String service;
    private String operation;
    private AnswerBean answer;
    private String text;
    private List<SemanticBean> semantic;
    private WebPageBean webPage;
    private DataBean data;

    public int getRc() {
        return rc;
    }
    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }

    public AnswerBean getAnswer() {
        return answer;
    }
    public void setAnswer(AnswerBean answer) {
        this.answer = answer;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public List<SemanticBean> getSemantic() {
        return semantic;
    }

    public void setSemantic(List<SemanticBean> semantic) {
        this.semantic = semantic;
    }

    public WebPageBean getWebPage() {
        return webPage;
    }
    public void setWebPage(WebPageBean webPage) {
        this.webPage = webPage;
    }

    public DataBean getData() {
        return data;
    }
    public void setData(DataBean data) {
        this.data = data;
    }

}
