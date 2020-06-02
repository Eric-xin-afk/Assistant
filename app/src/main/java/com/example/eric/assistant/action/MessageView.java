package com.example.eric.assistant.action;

import android.content.Intent;
import com.example.eric.assistant.activity.MainActivity;


public class MessageView {
	private MainActivity mActivity;
	
	public MessageView(MainActivity activity){
		mActivity=activity;
	}
	
	public void start(){
		Intent intent=new Intent();
		intent.setClassName("com.android.mms","com.android.mms.ui.ConversationList");
		mActivity.startActivity(intent);
	}
}
