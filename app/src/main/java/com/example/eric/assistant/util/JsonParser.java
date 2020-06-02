package com.example.eric.assistant.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.eric.assistant.bean.MainBean;
import java.lang.reflect.Type;

public class JsonParser {
    private static MainBean mBean;
    /**
     * 一次语音听写会话的结果是分多次以JSON格式返回的
     * 当前说话的内容，json格式数据存储在Java Bean中
     */
    public static MainBean parseIatResult(String json) {
        mBean = new MainBean();
        try {
            Type type = new TypeToken<MainBean>() {}.getType();
            mBean = new Gson().fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBean;
    }
}
