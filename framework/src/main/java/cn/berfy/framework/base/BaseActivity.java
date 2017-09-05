package cn.berfy.framework.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.google.gson.Gson;

import butterknife.ButterKnife;
import cn.berfy.framework.cache.TempSharedData;
import cn.berfy.framework.manager.ActivityManager;
import cn.berfy.framework.utils.AnimUtil;
import cn.berfy.framework.utils.DeviceUtil;
import cn.berfy.framework.utils.FragmentUtil;
import cn.berfy.framework.utils.LogUtil;
import cn.berfy.framework.utils.PopupWindowUtil;
import cn.berfy.framework.utils.ToastUtil;

public abstract class BaseActivity extends FragmentActivity implements
        OnClickListener {

    protected Activity mContext;
    private Gson mGson;
    private TempSharedData mTempSharedData;
    private boolean mIsClickTwoText;//点击两次退出
    private int mActivityState;//Activity当前生命周期状态
    private FragmentUtil mFragmentUtil;
    public static final int ACTIVITY_ONCREATE = 0;
    public static final int ACTIVITY_START = 1;
    public static final int ACTIVITY_RESTART = 2;
    public static final int ACTIVITY_RESUME = 3;
    public static final int ACTIVITY_STOP = 4;
    public static final int ACTIVITY_PAUSE = 5;
    public static final int ACTIVITY_DESTROY = 6;
    protected Handler mHandler = new Handler();
    private PopupWindowUtil mPopupWindowUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(getClass().getName(), "onCreate()");
        mActivityState = ACTIVITY_ONCREATE;
        // 加入栈管理
        ActivityManager.getInstance().pushActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        if (null != getParent()) {
            mContext = getParent();
        }
        mFragmentUtil = new FragmentUtil(this);
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        initVariable();
        View view = initContentView();
        if (null == view) {
            view = View.inflate(mContext, initContentViewById(), null);
        }
        setContentView(view);
        ButterKnife.bind(this);
        LogUtil.e("base底层", getClass().getName() + "    setContentView" + initContentViewById());
        initView();
    }

    abstract protected View initContentView();

    abstract protected int initContentViewById();

    /**
     * 让App字体不随系统改变
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();//
        config.fontScale = 1.0f;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    private TempSharedData getCacheUtil() {// 用到再实例化
        if (null == mTempSharedData) {
            mTempSharedData = TempSharedData.getInstance(mContext);
        }
        return mTempSharedData;
    }

    /**
     * Gson
     */
    protected Gson getGson() {
        if (null == mGson) {
            mGson = new Gson();
        }
        return mGson;
    }

    protected void showLoading() {
        mPopupWindowUtil.showLoading();
    }

    protected void dismissLoading() {
        mPopupWindowUtil.dismiss();
    }

    public void update(int frameLayoutResId, Fragment fragment) {
        mFragmentUtil.update(frameLayoutResId, fragment);
    }

    public int getActivityState() {
        return mActivityState;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mActivityState = ACTIVITY_START;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mActivityState = ACTIVITY_RESTART;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mActivityState = ACTIVITY_RESUME;
        LogUtil.i(getClass().getName(), "onResume()");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mActivityState = ACTIVITY_PAUSE;
        LogUtil.i(getClass().getName(), "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mActivityState = ACTIVITY_STOP;
        LogUtil.i(getClass().getName(), "onStop()");
    }

    protected void setClickTwoExit(boolean isClickTwoText) {
        mIsClickTwoText = isClickTwoText;
    }

    /**
     * 该方法用于初始化页面变量
     */
    protected abstract void initVariable();

    /**
     * 对页面控件进行设置
     */
    protected abstract void initView();

    @Override
    public void onClick(View v) {
        doClickEvent(v.getId());
    }

    /**
     * 控件点击事件
     *
     * @param viewId
     */
    protected abstract void doClickEvent(int viewId);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsClickTwoText) {
                exitProgram();
            } else {
                doBackAction();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private boolean mIsExit;

    public Handler exitHandler = new Handler() {
        public void handleMessage(Message msg) {
            mIsExit = false;
        }
    };

    /**
     * 退出App
     */
    public void exitProgram() {
        if (!mIsExit) {
            mIsExit = true;
            ToastUtil.getInstance().showToast("再按一次\"退出\"程序");
            exitHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            ActivityManager.getInstance().popAllActivityExceptOne(null);//清空所有activity
            System.gc();
        }
    }

    private long mClickTime = 0;

    /**
     * 跳转Activity+动画
     */
    public void startActivityWithAnim(Intent intent) {
        if (System.currentTimeMillis() - mClickTime > 1000) {
            LogUtil.e("跳转", "====" + intent.getComponent());
            mClickTime = System.currentTimeMillis();
            startActivity(intent);
            AnimUtil.pushLeftInAndOut(mContext);
        } else {
            LogUtil.e("等待跳转", "====");
        }
    }

    /**
     * 跳转Activity+动画
     */
    public void startActivityForResultWithAnim(Intent intent, int requestCode) {
        if (System.currentTimeMillis() - mClickTime > 1000) {
            LogUtil.e("跳转", "====" + intent.getComponent());
            mClickTime = System.currentTimeMillis();
            startActivityForResult(intent, requestCode);
            AnimUtil.pushLeftInAndOut(mContext);
        } else {
            LogUtil.e("等待跳转", "====");
        }
    }

    /**
     * 物理返回键执行的动作
     */
    protected void doBackAction() {
        finish();
        AnimUtil.pushRightInAndOut(mContext);
    }

    /**
     * toast string消息,时间2秒
     *
     * @param msg
     */
    protected void showToast(String msg) {
        ToastUtil.getInstance().showToast(msg);
    }

    /**
     * toast 资源中的消息,时间2秒
     *
     * @param resId
     */
    protected void showToast(int resId) {
        ToastUtil.getInstance().showToast(resId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        DeviceUtil.closeKeyboard(mContext, getWindow().getDecorView().getWindowToken());
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().popActivity(this);
        mActivityState = ACTIVITY_DESTROY;
        LogUtil.i(getClass().getName(), "onDestroy()");
    }
}