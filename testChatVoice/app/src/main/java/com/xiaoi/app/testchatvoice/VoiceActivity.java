package com.xiaoi.app.testchatvoice;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.carlos.voiceline.mylibrary.VoiceLineView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.xiaoi.app.testchatvoice.adapter.VoiceAdapter;
import com.xiaoi.app.testchatvoice.bean.DictationResult;
import com.xiaoi.app.testchatvoice.bean.VoiceImfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VoiceActivity extends AppCompatActivity {
    public static final String APPID = "57833929";// 科大讯飞所注册应用的标识ID
    public static final int QUEST_CONTENT = 0;// 科大讯飞所注册应用的标识ID
    public static final int ANSWER_CONTENT = 1;// 科大讯飞所注册应用的标识ID
    private static final String TAG = "VoiceActivity";
    @Bind(R.id.ll)
    LinearLayout ll;
    @Bind(R.id.listv_main)
    RecyclerView listvMain;
    @Bind(R.id.iv_voice_speak)
    ImageView ivVoiceSpeak;
    @Bind(R.id.voicLine)
    VoiceLineView voicLine;
    @Bind(R.id.rl_voice_bcg)
    RelativeLayout rlVoiceBcg;
    private VoiceAdapter voiceAdapter;
    private String dictationResultStr = "[";
    List<VoiceImfo> voiceImfos = new ArrayList<VoiceImfo>();
    List<VoiceImfo> voiceHelper = new ArrayList<>();
    private SpeechSynthesizer mTts;
    private String answer;//语音回答
    private String[] cmds;//语音命令的动作
    private String mQuestion;
    private boolean isShowBove;//是否处于监听状态
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SpeechUtility.createUtility(VoiceActivity.this, SpeechConstant.APPID + "=" + APPID);
        voicLine.setVisibility(View.INVISIBLE);
        initRecycleView();
    }

    private void initRecycleView() {
        //设置布局管理器
        listvMain.setLayoutManager(new LinearLayoutManager(this));
        //设置adapter
        voiceAdapter = new VoiceAdapter(this);
        listvMain.setAdapter(voiceAdapter);
        //设置默认动画
        listvMain.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick({R.id.iv_voice_speak, R.id.rl_voice_bcg})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice_speak:
                beginVoice();
                break;
            case R.id.rl_voice_bcg:
                if (isShowBove) {
                    beginVoice();
                }
                break;
            default:
                break;
        }
    }
    private void beginVoice() {
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        if (SpeechSynthesizer.getSynthesizer() == null) {
            mTts = SpeechSynthesizer.createSynthesizer(this, null);
        } else {
            mTts = SpeechSynthesizer.getSynthesizer();
        }
        mTts.stopSpeaking();
        dictationResultStr = "[";
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(this, null);
//2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
//听写监听器
        RecognizerListener mRecoListener = new RecognizerListener() {

            @Override
            public void onVolumeChanged(int i, byte[] bytes) {
                isShowBove = true;
                voicLine.setVolume(i * 6);
            }

            @Override
            public void onBeginOfSpeech() {
                voicLine.setVisibility(View.VISIBLE);
                ivVoiceSpeak.setVisibility(View.INVISIBLE);
                isShowBove = true;
            }

            @Override
            public void onEndOfSpeech() {
                voicLine.setVisibility(View.INVISIBLE);
                ivVoiceSpeak.setVisibility(View.VISIBLE);
                isShowBove = false;
            }

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                try {
                    if (!isLast) {
                        dictationResultStr += results.getResultString() + ",";
                    } else {
                        dictationResultStr += results.getResultString() + "]";
                    }
                    if (isLast) {
                        isShowBove = false;
                        // 解析Json列表字符串
                        Gson gson = new Gson();
                        List<DictationResult> dictationResultList = gson
                                .fromJson(dictationResultStr,
                                        new TypeToken<List<DictationResult>>() {
                                        }.getType());
                        String finalResult = "";
                        for (int i = 0; i < dictationResultList.size() - 1; i++) {
                            finalResult += dictationResultList.get(i).toString();

                        }
                        //识别的问题文字，区分聊天还是点播节目
                        showContent(finalResult,QUEST_CONTENT);//问题
                        //答案
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showContent("你好啊",ANSWER_CONTENT);//答案
                            }
                        },500);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "服务器响应发生错误" + e.getMessage());
                }

            }

            @Override
            public void onError(SpeechError speechError) {
                isShowBove = false;
                if (speechError != null) {
                    Log.e(TAG,speechError.toString());
                    int i= speechError.toString().indexOf(".");
                    showContent(speechError.toString().substring(0,i), QUEST_CONTENT);

                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };
        if (!isShowBove) {
            mIat.startListening(mRecoListener);
        } else {
            //手动结束录音
            mIat.stopListening();
            voicLine.setVisibility(View.INVISIBLE);
            ivVoiceSpeak.setVisibility(View.VISIBLE);
            isShowBove = false;
        }
    }


    private void showContent(String content, int type) {
        switch (type) {
            case QUEST_CONTENT:
                if (content != null) {
                    VoiceImfo voiceImfo = new VoiceImfo();
                    voiceImfo.setRequest(content);
                    voiceImfo.setIsLast(1);//占满全屏的数据
                    for (VoiceImfo imfo : voiceImfos) {
                        imfo.setIsLast(0);//将先前的数据置换为0，wrap_content
                    }
                    voiceImfos.add(voiceImfo);
                    voiceAdapter.setList(voiceImfos);
                    listvMain.smoothScrollToPosition(voiceImfos.size() - 1);
                }
                break;
            case ANSWER_CONTENT:
                voiceImfos.get(voiceImfos.size() - 1).setAnswer(content);//更新答案
                voiceAdapter.setList(voiceImfos);
                voiceCompound(content);
                break;
            default:
                break;
        }
    }

    private void voiceCompound(final String answer) {
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        if (mTts != null) {
            mTts.stopSpeaking();//防止说话重叠
        }
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaowanzi");//设置发音人：小丸子
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码c
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/mode_school_hint.wav");
        //合成监听器
        try {
            SynthesizerListener mSynListener = new SynthesizerListener() {
                //会话结束回调接口，没有错误时，error为null
                public void onCompleted(SpeechError error) {
                }

                //缓冲进度回调
                //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
                public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
                }

                //开始播放
                public void onSpeakBegin() {
                }

                //暂停播放
                public void onSpeakPaused() {
                }

                //播放进度回调
                //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
                public void onSpeakProgress(int percent, int beginPos, int endPos) {
                }

                //恢复播放回调接口
                public void onSpeakResumed() {
                }

                //会话事件回调接口
                public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
                }
            };
            mTts.startSpeaking(answer, mSynListener);

        } catch (Exception e) {
            Log.e(TAG, "启动说话报错" + e.getMessage());
        }
    }
}
