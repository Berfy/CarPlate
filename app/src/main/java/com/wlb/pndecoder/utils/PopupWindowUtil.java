package com.wlb.pndecoder.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wlb.pndecoder.R;

import cn.berfy.framework.base.BasePopupWindowUtil;

/**
 * Created by Berfy on 2017/6/15.
 * Popupwindo工具
 */
public class PopupWindowUtil extends BasePopupWindowUtil {

    private final String TAG = "PopupWindowUtil";

    public PopupWindowUtil(Context context) {
        super(context);
    }

    public void showConfirmTip(View view, String title, String content, String okText, String cancelText, final OnPopConfirmTipListener popListener) {
        View viewPop = View.inflate(mContext, R.layout.pop_confirm_tip, null);
        getPop().setContentView(viewPop);
        show(view);
        if (!TextUtils.isEmpty(title)) {
            ((TextView) viewPop.findViewById(R.id.tv_pop_title)).setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            ((TextView) viewPop.findViewById(R.id.tv_pop_content)).setText(content);
        }
        if (!TextUtils.isEmpty(okText)) {
            ((TextView) viewPop.findViewById(R.id.btn_pop_ok)).setText(okText);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            ((TextView) viewPop.findViewById(R.id.btn_pop_cancel)).setText(cancelText);
        }
        viewPop.findViewById(R.id.btn_pop_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != popListener) {
                    popListener.ok();
                }
            }
        });
        viewPop.findViewById(R.id.btn_pop_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != popListener) {
                    popListener.cancel();
                }
            }
        });
    }

}
