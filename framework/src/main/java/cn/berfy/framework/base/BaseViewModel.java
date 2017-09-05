package cn.berfy.framework.base;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import cn.berfy.framework.utils.DeviceUtil;

/**
 * @author yuepengfei
 * @category View基类
 */
public abstract class BaseViewModel implements OnClickListener, OnTouchListener {

    protected Context mContext;
    protected View mView;
    private boolean mIsCanCloseImeByTouch = false;

    public BaseViewModel(Context ctx) {
        mContext = ctx;
        init();
    }

    /**
     * @param isTouch 设置true
     *                用户可以通过点击页面空白处关闭键盘，为了良好的用户体验，注意：包含全屏EditText的activity请不要设置
     */
    public void setCloseImeByTouch(boolean isTouch) {
        mIsCanCloseImeByTouch = isTouch;
    }

    private void init() {
        mView = View.inflate(mContext, initContentViewById(), null);
        initView();
    }

    public View getView() {
        return mView;
    }

    @Override
    public void onClick(View v) {
        doClickEvent(v.getId());
    }

    /**
     *
     */
    protected abstract int initContentViewById();

    /**
     *
     */
    protected abstract void initView();

    /**
     * @param viewId
     */
    protected abstract void doClickEvent(int viewId);

    protected View findViewById(int id) {
        return mView.findViewById(id);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mIsCanCloseImeByTouch) {
            DeviceUtil.closeKeyboard(mContext, mView.getWindowToken());
        }
        return false;
    }

}
