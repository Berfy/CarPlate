package cn.berfy.framework.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import cn.berfy.framework.R;
import cn.berfy.framework.utils.LogUtil;
import cn.berfy.framework.utils.ViewUtils;

/**
 * Created by Berfy on 2017/6/15.
 * Popupwindo工具
 */
public abstract class BasePopupWindowUtil {

    private final String TAG = "BasePopupWindowUtil";
    protected Context mContext;
    private PopupWindow mPopupWindow;

    public BasePopupWindowUtil(Context context) {
        mContext = context;
        mPopupWindow = new PopupWindow();
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setWidth(ViewUtils.getScreenWidth(mContext));
        mPopupWindow.setHeight(ViewUtils.getScreenHeight(mContext));
    }

    protected PopupWindow getPop() {
        return mPopupWindow;
    }

    public boolean isCanShow() {
        if (!((Activity) mContext).isFinishing() && null != mPopupWindow && !mPopupWindow.isShowing()) {
            return true;
        }
        return false;
    }

    public void show(View view) {
        if (!((Activity) mContext).isFinishing() && null != mPopupWindow && !mPopupWindow.isShowing()) {
            LogUtil.e(TAG, "显示");
            mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    public void dismiss() {
        if (null != mPopupWindow && !((Activity) mContext).isFinishing() && mPopupWindow.isShowing()) {
            LogUtil.e(TAG, "隐藏");
            mPopupWindow.dismiss();
        }
    }

    public void showLoading() {
        if (isCanShow()) {
            View view = View.inflate(mContext, R.layout.pop_loading, null);
            mPopupWindow.setContentView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            show(((Activity) mContext).getWindow().getDecorView());
        }
    }

    public interface OnPopListener {
        void onDismiss();

        void onShow();
    }

    public interface OnPopConfirmTipListener {
        void ok();

        void cancel();
    }
}
