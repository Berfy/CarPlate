package com.wlb.pndecoder;

import android.Manifest;
import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.berfy.framework.common.Constants;
import cn.berfy.framework.http.VolleyHttp;
import cn.berfy.framework.utils.ImageUtil;
import cn.berfy.framework.utils.LogUtil;
import cn.berfy.framework.utils.ToastUtil;

/**
 * Created by Berfy on 2017/6/13.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setTag("Berfy");
        ToastUtil.init(getApplicationContext());
        VolleyHttp.init(getApplicationContext());
        ImageLoader.getInstance().init(ImageUtil.getConfiguration(getApplicationContext()));
    }
}
