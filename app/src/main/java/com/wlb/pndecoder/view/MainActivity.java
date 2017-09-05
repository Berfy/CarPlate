package com.wlb.pndecoder.view;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aiseminar.EasyPR.PlateRecognizer;
import com.baidu.speech.VoiceRecognitionService;
import com.wlb.pndecoder.R;
import com.wlb.pndecoder.common.Constants;
import com.wlb.pndecoder.db.histroy.TabHistroy;
import com.wlb.pndecoder.model.histroy.HistroyNumber;
import com.wlb.pndecoder.utils.AllCapTransformationMethod;
import com.wlb.pndecoder.view.camera.CameraActivity;
import com.wlb.pndecoder.view.look.IWebViewActivity;
import com.wlb.pndecoder.view.photo.MultiImageSelectorFragment;
import com.wlb.pndecoder.view.photo.SelectorActivity;
import com.wlb.pndecoder.view.setting.SettingActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.cache.TempSharedData;
import cn.berfy.framework.utils.CheckUtil;
import cn.berfy.framework.utils.DeviceUtil;
import cn.berfy.framework.utils.LogUtil;
import cn.berfy.framework.utils.TimeUtil;
import cn.berfy.framework.utils.ToastUtil;

public class MainActivity extends BaseActivity implements RecognitionListener {

    private final String TAG = "MainActivity";
    @Bind(R.id.tv_setting)
    TextView mTvSetting;
    @Bind(R.id.btn_takephoto)
    TextView mTvTakePhoto;
    @Bind(R.id.btn_voice)
    TextView mTvVoice;
    @Bind(R.id.btn_image)
    TextView mTvImage;
    @Bind(R.id.btn_intro)
    TextView mTvIntro;
    @Bind(R.id.edit_result)
    EditText mEditResult;
    @Bind(R.id.tv_province)
    TextView mTvProvince;
    View mSpeechTips, mSpeechWave;
    private SpeechRecognizer mSpeechRecognizer;
    private PlateRecognizer mPlateRecognizer;

    private String mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_main;
    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected void initVariable() {
        setClickTwoExit(true);
        if (DeviceUtil.selfPermissionGranted(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA}, 102)) {
            mPlateRecognizer = new PlateRecognizer(this);
        }
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        mSpeechRecognizer.setRecognitionListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == mPlateRecognizer) {
            if (DeviceUtil.selfPermissionGranted(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, 102)) {
                mPlateRecognizer = new PlateRecognizer(this);
            }
        }
    }

    @Override
    protected void initView() {
        mSpeechTips = findViewById(R.id.voice_tips);
        mSpeechWave = mSpeechTips.findViewById(R.id.wave);
        mSpeechTips.setVisibility(View.GONE);
        mTvTakePhoto.setOnClickListener(this);
        mTvImage.setOnClickListener(this);
        mTvIntro.setOnClickListener(this);
        mTvSetting.setOnClickListener(this);
        mTvVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (DeviceUtil.selfPermissionGranted(mContext, new String[]{Manifest.permission.RECORD_AUDIO}, 100)) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            LogUtil.e(TAG, "按下");
                            mSpeechTips.setVisibility(View.VISIBLE);
                            mSpeechRecognizer.cancel();
                            Intent intent = new Intent();
                            bindParams(intent);
                            intent.putExtra("vad", "touch");
                            mSpeechRecognizer.startListening(intent);
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            LogUtil.e(TAG, "移动" + event.getX() + "," + event.getY());
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            LogUtil.e(TAG, "抬起");
                            mSpeechRecognizer.stopListening();
                            mSpeechTips.setVisibility(View.GONE);
                            break;
                    }
                }
                return false;
            }
        });
        mEditResult.setTransformationMethod(new AllCapTransformationMethod(true));
        mEditResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNumber = s.toString().toUpperCase();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditResult.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    goBuy();
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isOk = false;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                isOk = true;
            } else {
                isOk = false;
                ToastUtil.getInstance().showToast("您拒绝了权限");
            }
        }
        if (isOk)
            switch (requestCode) {
                case 100:
                    break;
                case 101:
                    startActivityForResultWithAnim(new Intent(mContext, CameraActivity.class), 100);
                    break;
                case 102:
                    if (null == mPlateRecognizer) {
                        mPlateRecognizer = new PlateRecognizer(this);
                    }
                    break;
            }
    }

    public void bindParams(Intent intent) {
        intent.putExtra(Constants.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
        intent.putExtra(Constants.EXTRA_LICENSE_FILE_PATH, "/sdcard/easr/license-tmp-20150530.txt");
//        intent.putExtra(Constants.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
    }

    @Override
    protected void doClickEvent(int viewId) {
        switch (viewId) {
            case R.id.btn_takephoto:
                if (DeviceUtil.selfPermissionGranted(mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101)) {
                    startActivityForResultWithAnim(new Intent(mContext, CameraActivity.class), 100);
                }
                break;
            case R.id.btn_image:
                if (DeviceUtil.selfPermissionGranted(mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101)) {
                    Intent intent = new Intent(mContext, SelectorActivity.class);
                    intent.putExtra(SelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorFragment.MODE_SINGLE);
                    startActivityForResultWithAnim(intent, MultiImageSelectorFragment.MODE_SINGLE);
                }
                break;
            case R.id.btn_intro:
                goBuy();
                break;
            case R.id.tv_setting:
                startActivityWithAnim(new Intent(mContext, SettingActivity.class));
                break;
        }
    }

    private void goBuy() {
        String province = mTvProvince.getText().toString().trim();
        if (TextUtils.isEmpty(mNumber)) {
            ToastUtil.getInstance().showToast("请输入或者扫描车牌");
        } else {
            if (CheckUtil.isCarNumber(province + mNumber)) {
                HistroyNumber histroyNumber = new HistroyNumber();
                histroyNumber.setNumber(province + mNumber);
                histroyNumber.setUpdateTime(TimeUtil.getCurrentTime());
                histroyNumber.setPath("");
                histroyNumber.setPhone(TempSharedData.getInstance(mContext).getData(Constants.XML_PHONE));
                TabHistroy.getInstances(mContext).add(histroyNumber);
                TempSharedData.getInstance(mContext).save(Constants.XML_NUMBER, province + mNumber);
                Intent intent = new Intent(mContext, IWebViewActivity.class);
                startActivityWithAnim(intent);
            } else {
                ToastUtil.getInstance().showToast("车牌号不正确" + province + mNumber);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG, "onActivityResult" + requestCode);
        if (requestCode == 100) {
            if (null != data) {
                String plate = data.getStringExtra("data");
                try {
                    mEditResult.setText(plate.substring(1, plate.length()));
                    mTvProvince.setText(plate.substring(0, 1));
                    TempSharedData.getInstance(mContext).save(Constants.XML_NUMBER, plate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == MultiImageSelectorFragment.MODE_SINGLE) {
            if (null != data) {
                cn.berfy.framework.common.Constants.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.getInstance().showToast("识别图片中", Toast.LENGTH_LONG);
                        ArrayList<String> results = (ArrayList<String>) data.getStringArrayListExtra(SelectorActivity.EXTRA_RESULT);
                        LogUtil.e(TAG, "选择图片结果" + results);
                        if (results.size() > 0) {
                            final String plate = mPlateRecognizer.recognize(results.get(0));
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != plate && !plate.equalsIgnoreCase("0")) {
                                        try {
                                            String province = plate.substring(3, 4);
                                            LogUtil.e(TAG, "识别结果  " + province + "    " + plate);
                                            if (province.contains("京")) {
                                                String number = plate.substring(3, plate.length());
                                                if (CheckUtil.isCarNumber(number)) {
                                                    mEditResult.setText(number.substring(1, number.length()));
                                                    mTvProvince.setText(province);
                                                } else {
                                                    ToastUtil.getInstance().showToast("车牌号不正确" + number);
                                                    LogUtil.e(TAG, "识别错误");
                                                }
                                            } else {
                                                LogUtil.e(TAG, "识别错误,");
                                                ToastUtil.getInstance().showToast("识别错误，没发现车牌信息", Toast.LENGTH_LONG);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            LogUtil.e(TAG, "识别错误");
                                            ToastUtil.getInstance().showToast("识别错误，没发现车牌信息", Toast.LENGTH_LONG);
                                        }
                                    } else {
                                        LogUtil.e(TAG, "识别错误");
                                        ToastUtil.getInstance().showToast("识别错误，没发现车牌信息", Toast.LENGTH_LONG);
                                    }
                                }
                            });
                        } else {
                            LogUtil.e(TAG, "识别错误");
                            ToastUtil.getInstance().showToast("识别错误，没发现车牌信息", Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        } else if (requestCode == SelectorActivity.CANCEL) {
            LogUtil.e(TAG, "取消选择");
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        LogUtil.e(TAG, "识别onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        LogUtil.e(TAG, "识别onBeginningOfSpeech");
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        LogUtil.e(TAG, "识别onBufferReceived");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        LogUtil.e(TAG, "音量" + rmsdB);
        final int VTAG = 0xFF00AA01;
        Integer rawHeight = (Integer) mSpeechWave.getTag(VTAG);
        if (rawHeight == null) {
            rawHeight = mSpeechWave.getLayoutParams().height;
            mSpeechWave.setTag(VTAG, rawHeight);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSpeechWave.getLayoutParams();
        params.height = (int) (rawHeight * rmsdB * 0.01);
        params.height = Math.max(params.height, mSpeechWave.getMeasuredWidth());
        mSpeechWave.setLayoutParams(params);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        LogUtil.e(TAG, "识别onEvent");
    }

    @Override
    public void onError(int error) {
        LogUtil.e(TAG, "识别onError");
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        LogUtil.e(TAG, "识别失败" + sb.toString());
        ToastUtil.getInstance().showToast(sb.toString());
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        LogUtil.e(TAG, "识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String plate = nbest.get(0);
        if (CheckUtil.isCarNumber(plate)) {
            plate = plate.substring(1, plate.length()).toUpperCase();
            mEditResult.setText(plate);
        } else {
            ToastUtil.getInstance().showToast("车牌号不正确" + plate);
        }
    }

    @Override
    public void onEndOfSpeech() {
        LogUtil.e(TAG, "识别结束");
    }

    @Override
    protected void onDestroy() {
        mSpeechRecognizer.destroy();
        super.onDestroy();
    }
}
