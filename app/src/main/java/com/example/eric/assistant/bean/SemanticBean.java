package com.example.eric.assistant.bean;

import java.util.List;

public class SemanticBean {

    private String intent;
    private List<SlotsBean> slots;

    public String getIntent() {
        return intent;
    }
    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<SlotsBean> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotsBean> slots) {
        this.slots = slots;
    }
}