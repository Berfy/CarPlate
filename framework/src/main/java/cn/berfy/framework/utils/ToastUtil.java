package cn.berfy.framework.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static ToastUtil mToastUtil;
    private Context mContext;
    private Toast mToast;

    public static ToastUtil init(Context context) {
        if (mToastUtil == null) {
            mToastUtil = new ToastUtil(context);
        }
        return mToastUtil;
    }

    public static ToastUtil getInstance() {
        if (mToastUtil == null) {
            throw new NullPointerException("请在Application中初始化ToastUtil");
        }
        return mToastUtil;
    }

    private ToastUtil(Context context) {
        mContext = context;
        init();
    }

    /**
     * toast string消息,时间2秒
     *
     * @param msg
     */
    public void showToast(String msg) {
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * toast string消息,时间2秒
     *
     * @param msg
     */
    public void showToast(String msg, int time) {
        mToast.setText(msg);
        mToast.setDuration(time);
        mToast.show();
    }

    /**
     * toast string消息,时间2秒
     *
     * @param resId
     */
    public void showToast(int resId) {
        mToast.setText(mContext.getResources().getString(resId));
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void init() {
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
    }
}
