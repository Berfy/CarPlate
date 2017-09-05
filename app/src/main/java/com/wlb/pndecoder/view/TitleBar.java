package com.wlb.pndecoder.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wlb.pndecoder.R;

import cn.berfy.framework.utils.AnimUtil;

/**
 * Created by Berfy on 2017/6/14.
 */

public class TitleBar extends LinearLayout {

    private Context mContext;

    public TitleBar(Context context) {
        super(context, null);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        addView(View.inflate(mContext, R.layout.include_titlebar, null), new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        showLeft(true, new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).finish();
                AnimUtil.pushRightInAndOut(((Activity) mContext));
            }
        });
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            ((TextView) findViewById(R.id.tv_title)).setText(title);
        }
    }

    public void showLeft(boolean isShow, OnClickListener onClickListener) {
        findViewById(R.id.tv_left).setVisibility(isShow ? VISIBLE : GONE);
        findViewById(R.id.tv_left).setOnClickListener(onClickListener);
    }

    public void showRight(boolean isShow, OnClickListener onClickListener) {
        findViewById(R.id.tv_right).setVisibility(isShow ? VISIBLE : GONE);
        findViewById(R.id.tv_right).setOnClickListener(onClickListener);
    }

    public void showRight(boolean isShow, String title, OnClickListener onClickListener) {
        findViewById(R.id.tv_right).setVisibility(isShow ? VISIBLE : GONE);
        findViewById(R.id.tv_right).setOnClickListener(onClickListener);
        if (!TextUtils.isEmpty(title)) {
            ((TextView) findViewById(R.id.tv_right)).setText(title);
        }
    }
}
