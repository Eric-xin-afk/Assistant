package com.example.eric.assistant.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eric.assistant.bean.SemanticBean;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;
import com.example.eric.assistant.R;
import com.example.eric.assistant.action.CallAction;
import com.example.eric.assistant.action.OpenAppAction;
import com.example.eric.assistant.action.OpenQA;
import com.example.eric.assistant.action.SendMessage;
import com.example.eric.assistant.bean.AnswerBean;
import com.example.eric.assistant.bean.MainBean;
import com.example.eric.assistant.bean.SlotsBean;
import com.example.eric.assistant.util.JsonParser;

public class MainActivity extends Activity implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private MainBean mMainBean;
    private SpeechUnderstander mSpeechUnderstander;      //语义理解类
    private Toast mToast;
    private TextView mAskText, mUnderstanderText;
    private HeartProgressBar heartProgressBar;
    private FiveLine mFiveLine;
    private SpeechSynthesizer mTts;         // 语音合成对象


    public static int weixin_times=0;
    public static int qq_times=0;
    public static int weibo_times=0;
    public static int moji_times=0;
    public static int zhifu_times=0;

    public static boolean service_flag = false;    //表示是否在一项服务中
    public static String SRResult = "";             //识别结果




    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5e85fcb7");

        // 初始化对象
        mToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(MainActivity.this, mSpeechUdrInitListener);
        mSpeechUnderstander.setParameter(SpeechConstant.NLP_VERSION, "3.0");
        mSpeechUnderstander.setParameter(SpeechConstant.SCENE, "main_box");
        mSpeechUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, mTtsInitListener);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        initLayout();
    }

    /**
     * 初始化Layout。
     */
    private void initLayout() {
        mAskText = (TextView) findViewById(R.id.tv_ask);
        mUnderstanderText = (TextView) findViewById(R.id.tv_answer);
        heartProgressBar = (HeartProgressBar) findViewById(R.id.progressBar);
        mFiveLine = (FiveLine) findViewById(R.id.fiveLine);
        mUnderstanderText.setText("我能帮您做什么吗?");
        speakAnswer("我能帮您做什么吗?");
        findViewById(R.id.start_understander).setOnClickListener(MainActivity.this);
    }

    int ret = 0;// 函数调用返回值

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 开始语音语义理解
            case R.id.start_understander:
                mFiveLine.setVisibility(View.INVISIBLE);
                heartProgressBar.start();
                mTts.stopSpeaking();

                if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
                    mSpeechUnderstander.stopUnderstanding();
                    //showTip("停止录音");
                }
                ret = mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
                if (ret != 0) {
                    showTip("语义理解失败,错误码:" + ret);
                } else {
                    showTip("请开始说话…");
                }
                break;
        }
    }

    //帮助按钮图标
    public void help(View view) {
        startActivity(new Intent(MainActivity.this, HelpActivity.class));
    }
    //统计图表按钮图标
    public void table(View view){
        startActivity(new Intent(MainActivity.this,ChartActivity.class));
}

    /**
     * 初始化监听器（语音语义理解）。
     */
    private InitListener mSpeechUdrInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };
    /**
     * 初始化监听器（语音合成）。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 语音语义理解回调。
     */
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {
        @Override
        public void onResult(final UnderstanderResult result) {
            if (result != null) {
                Log.d(TAG, result.getResultString());
                // 显示
                String text = result.getResultString();
                Log.e(TAG, text);

                mMainBean = JsonParser.parseIatResult(text);    //开始解析
                if (!TextUtils.isEmpty(text)) {
                    mAskText.setText(mMainBean.getText());
                    if (mMainBean.getRc() == 0) {
                        SRResult = mMainBean.getText();
                        judgeService();
                    } else {
                        mUnderstanderText.setText("我听不懂您说什么，亲爱的，下次可能我就明白了");
                        speakAnswer("我听不懂您说什么，亲爱的，下次可能我就明白了");
                    }
                }
            } else {
                showTip("识别结果不正确。");
            }
        }
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, data.length + "");
        }
        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //showTip("结束说话");
            heartProgressBar.dismiss();
        }
        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //showTip("开始说话");
        }
        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    /**
     * 语音合成回调。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
        }
        @Override
        public void onSpeakPaused() {
            //showTip("暂停播放");
        }
        @Override
        public void onSpeakResumed() {
            //showTip("继续播放");
        }
        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            //mPercentForBuffering = percent;
            //showTip(String.format(getString(R.string.tts_toast_format),
            //      mPercentForBuffering, mPercentForPlaying));
        }
        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            //mPercentForPlaying = percent;
            //showTip(String.format(getString(R.string.tts_toast_format),
            //       mPercentForBuffering, mPercentForPlaying));
        }
        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                //showTip("播放完成");
                mFiveLine.setVisibility(View.INVISIBLE);
            }
        }
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    public void speakAnswer(String text) {
        int code = mTts.startSpeaking(text, mTtsListener);
        mFiveLine.setVisibility(View.VISIBLE);
        mUnderstanderText.setText(text);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {  //引擎有云端和离线语记
                //未安装则跳转到提示安装页面
                showTip("请安装语记!");
            } else {
                showTip("语音合成失败,错误码: " + code);
            }
        }
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    //---------------语义场景判断、响应用户指令-------------------
    private void judgeService() {
        SRResult = null;
        String service = mMainBean.getService();
        String operation = mMainBean.getOperation();

        AnswerBean answerBean = new AnswerBean();
        if (mMainBean.getAnswer() != null) {
            answerBean = mMainBean.getAnswer();
        }


        if (!service_flag) {              //如果不在一项服务中才进行服务的判断
            switch (service) {

                case "telephone": {        //电话功能模块
                    SemanticBean semanticBean = mMainBean.getSemantic().get(0);
                    String intent = semanticBean.getIntent();
                    SlotsBean slotsBean = semanticBean.getSlots().get(0);
                    if (slotsBean==null){
                        return;
                    }
                    String value=slotsBean.getValue();
                    switch (intent) {
                        case "DIAL": {    // 拨打电话
                            //必要条件【电话号码code】,可选条件【人名value】，可由多个可选条件确定必要条件
                            CallAction callAction = new CallAction(value, slotsBean.getCode(), MainActivity.this);//目前可根据名字或电话号码拨打电话
                            callAction.start();
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }

                case "message": {                 //短信相关服务
                    Log.d(TAG, service);
                    SemanticBean semanticBean = mMainBean.getSemantic().get(0);
                    String intent = semanticBean.getIntent();
                    SlotsBean slotsBean = semanticBean.getSlots().get(0);

                    switch (intent) {
                        case "SEND": {//1发送短信
                            SendMessage sendMessage = new SendMessage(slotsBean.getValue(), slotsBean.getCode(), slotsBean.getContent(), MainActivity.this);
                            sendMessage.start();
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }

                case "app": {            // 应用相关服务-----已实现
                    SemanticBean semanticBean = mMainBean.getSemantic().get(0);
                    String intent = semanticBean.getIntent();
                    SlotsBean slotsBean = semanticBean.getSlots().get(0);
                    switch (intent) {
                        case "LAUNCH": {      //打开应用
                            if (slotsBean.getValue().equals("微信")){
                                Log.d(TAG,"weixin_times");
                                weixin_times++;
                                Log.d(TAG,"weixin_times");
                            }
                            else if (slotsBean.getValue().equals("微博")){
                                weibo_times++;
                            }
                            else if (slotsBean.getValue().equals("qq")){
                                qq_times++;
                            }
                            else if (slotsBean.getValue().equals("支付宝")){
                                zhifu_times++;
                            }
                            OpenAppAction openApp = new OpenAppAction(slotsBean.getValue(), MainActivity.this);
                            openApp.start();

                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }

                case "weather":{               //天气功能模块--已实现
                    SemanticBean semanticBean = mMainBean.getSemantic().get(0);
                    String intent = semanticBean.getIntent();
                    switch (intent){
                        case "QUERY":{
                            OpenQA openQA = new OpenQA(answerBean.getText(), this);
                            openQA.start();
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }

                case "Turing": {            //聊天相关服务----已实现
                    Log.d(TAG, service);
                    switch (operation) {
                        case "ANSWER": {     //聊天模式
                            Log.d(TAG, operation);
                            OpenQA openQA = new OpenQA(answerBean.getText(), this);
                            openQA.start();
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }


                default:
                    mUnderstanderText.setText("我听不懂您说什么，亲爱的，下次可能我就明白了");
                    speakAnswer("我听不懂您说什么，亲爱的，下次可能我就明白了");
                    break;
            }
        }
    }

    /**
     * 双击退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return false;
    }

    private long time = 0;

    public void exit() {
        if (System.currentTimeMillis() - time > 2000) {
            time = System.currentTimeMillis();
            showTip("再点击一次退出应用程序");
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mSpeechUnderstander.cancel();
        mSpeechUnderstander.destroy();
        mTts.stopSpeaking();
        // 退出时释放连接
        mTts.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
