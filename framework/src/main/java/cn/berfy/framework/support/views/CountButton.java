package cn.berfy.framework.support.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Button;
import cn.berfy.framework.R;
import cn.berfy.framework.utils.LogUtil;

/**
 * Created by Berfy on 2017/3/24.
 * 倒计时Button
 */
public class CountButton extends Button {

    private int mCount = 0;//倒计时
    private String mNormalText = "";//count后文字
    private int mNormalBtnColor,mDisableBtnColor;
    private boolean mIsCounting;//是否正在倒计时
    private OnCountListener mOnCountListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCount != 0) {
                LogUtil.e("倒计时", mCount + "");
                setText(getContext().getString(R.string.login_tip_code_send) + "(" + mCount + "S)");
                --mCount;
                mHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                setText(mNormalText);
                setEnabled(true);
                setBackgroundResource(mNormalBtnColor);
                mIsCounting = false;
                mHandler.removeMessages(0);
                mOnCountListener.finish();
            }
        }
    };

    public CountButton(Context context) {
        super(context);
    }

    public CountButton(Context context, AttributeSet attr) {
        super(context, attr);
    }

    /**
     * @param normalText 默认是显示的问题
     */
    public void setTip(String normalText) {
        mNormalText = normalText;
    }

    /**
     * @param normalBtnColor 正常状态背景颜色
     * @param disableBtnColor 不可用状态背景色
     */
    public void setColor(int normalBtnColor,int disableBtnColor) {
        mNormalBtnColor = normalBtnColor;
        mDisableBtnColor = disableBtnColor;
    }

    public boolean isCounting() {
        return mIsCounting;
    }

    /**
     * 设置从count开始倒计时
     */
    public void setmOnCountListener(OnCountListener onCountListener) {
        mOnCountListener = onCountListener;
    }

    public void startCountDown(int count) {
        if (!isCounting()) {
            mCount = count;
            setEnabled(false);
            setBackgroundResource(mDisableBtnColor);
            mIsCounting = true;
            if (null != mOnCountListener) {
                mOnCountListener.start();
            }
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    public interface OnCountListener {
        void start();

        void finish();
    }
}
