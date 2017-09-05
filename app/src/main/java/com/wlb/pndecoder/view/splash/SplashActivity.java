package com.wlb.pndecoder.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.wlb.pndecoder.R;
import com.wlb.pndecoder.common.Constants;
import com.wlb.pndecoder.view.MainActivity;
import com.wlb.pndecoder.view.login.LoginActivity;

import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.cache.TempSharedData;

/**
 * Created by Berfy on 2017/6/14.
 * 启动页面
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TempSharedData.getInstance(mContext).save(Constants.XML_NUMBER, "");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(TempSharedData.getInstance(mContext).getData(Constants.XML_PHONE))) {
                    startActivityWithAnim(new Intent(mContext, MainActivity.class));
                } else {
                    startActivityWithAnim(new Intent(mContext, LoginActivity.class));
                }
                finish();
            }
        }, 1000);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void doClickEvent(int viewId) {
        switch (viewId) {

        }
    }
}
