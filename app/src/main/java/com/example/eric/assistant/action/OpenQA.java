package com.example.eric.assistant.action;

import android.util.Log;

import com.example.eric.assistant.activity.MainActivity;

public class OpenQA {

	private String mText;
	MainActivity mActivity;
	
	public OpenQA(String text, MainActivity activity){
		mText=text;
		mActivity=activity;
	}
	public void start(){
		//Log.d("ss","here");
		mActivity.speakAnswer(mText);
	}
	
}
