package com.wlb.pndecoder.view.look;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wlb.pndecoder.R;
import com.wlb.pndecoder.common.Constants;
import com.wlb.pndecoder.view.TitleBar;

import butterknife.BindView;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.cache.TempSharedData;
import cn.berfy.framework.support.views.AWebView;
import cn.berfy.framework.utils.DeviceUtil;
import cn.berfy.framework.utils.LogUtil;

/**
 * Created by Berfy on 2017/6/14.
 */

public class IWebViewActivity extends BaseActivity {

    private final String TAG = "IWebViewActivity";
    @BindView(R.id.webView)
    AWebView mWebView;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView() {
        mTitleBar.showLeft(true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    doBackAction();
                }
            }
        });
        initSettings();
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading();
                LogUtil.e(TAG, "加载开始" + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissLoading();
                LogUtil.e(TAG, "加载完成" + url);
                if (url.lastIndexOf("baojia.html") > 0) {
                    String price = TextUtils.isEmpty(TempSharedData.getInstance(mContext).getData(Constants.XML_PRICE_PERCENT)) ? "22" : TempSharedData.getInstance(mContext).getData(Constants.XML_PRICE_PERCENT);
                    mWebView.loadUrl("javascript:picCallback('"
                            + TempSharedData.getInstance(mContext).getData(Constants.XML_NUMBER)
                            + "','" + price + "')");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "shouldOverrideUrlLoading url=" + url);
                mWebView.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();  // 接受所有网站的证书
                Log.e(TAG, "onReceivedSslError");
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTitleBar.setTitle(title);
            }
        });
        LogUtil.e(TAG, "加载" + "file:///android_asset/price/baojia.html");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mWebView.loadUrl("http://www.baidu.com");
                mWebView.loadUrl("file:///android_asset/price/baojia.html");
                showLoading();
            }
        },1000);
    }

    private void initSettings() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setSavePassword(false);//禁止存储密码
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " VersionCode/" + DeviceUtil.getPackageVersionName(mContext));
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_webview;
    }

    @Override
    protected View initContentView() {
        return null;

    }

    @Override
    protected void doClickEvent(int viewId) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                doBackAction();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除临时车牌
        TempSharedData.getInstance(mContext).save("number", "");
    }
}
