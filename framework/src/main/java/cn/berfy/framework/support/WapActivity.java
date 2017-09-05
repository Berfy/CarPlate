package cn.berfy.framework.support;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.berfy.framework.R;

import android.widget.TextView;

import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.support.views.AWebView;
import cn.berfy.framework.utils.DeviceUtil;
import cn.berfy.framework.utils.ToastUtil;

/**
 * Created by Berfy on 2016/5/12.
 * 详解详情
 */
public class WapActivity extends BaseActivity {

    private final String TAG = "WapActivity";
    private AWebView mWebView;
    private TextView mTvDetail;
    private String mUrl;
    private int mType;//0 url网络加载 1加载本地data html 2url本地加载
    private String mData;//html数据
    public static final String INTENT_EXTRA_TYPE = "type";
    public static final String INTENT_EXTRA_URL = "url";
    public static final String INTENT_EXTRA_DATA = "data";
    public static final int TYPE_URL = 0;
    public static final int TYPE_DATA = 1;
    public static final int TYPE_LOCAL = 2;
    private static final int REQUEST_CODE_GET_LOCAL_FILE = 0;
    private static final int REQUEST_CODE_GET_LOCAL_FILE_NEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariable() {
        // TODO Auto-generated method stub
        mUrl = getIntent().getStringExtra(INTENT_EXTRA_URL);
        mType = getIntent().getIntExtra(INTENT_EXTRA_TYPE, TYPE_URL);
        mData = getIntent().getStringExtra(INTENT_EXTRA_DATA);
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_webview_layout;
    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected void initView() {
        mWebView = (AWebView) findViewById(R.id.webView);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }else{
//
//        }
        mTvDetail = (TextView) findViewById(R.id.tv_content);
        initSettings();
        setAcceptThirdPartyCookies();
        initWebClient();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setOnUrlRequestListener(new AWebView.OnUrlRequestListener() {

            @Override
            public void onSuc() {
            }

            @Override
            public void onFailed() {
                mWebView.setVisibility(View.GONE);
                mTvDetail.setVisibility(View.VISIBLE);
                mTvDetail.setText(mUrl);
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        if (mType == TYPE_URL) {
            mWebView.loadUrlOnCheck(mUrl);
        } else if (mType == TYPE_DATA) {
            mWebView.loadData(mData,"","");
        } else if (mType == TYPE_LOCAL) {
            mWebView.loadUrl(mUrl);
        }
    }

    private void initSettings() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
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

    /**
     * 设置client
     */
    private void initWebClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.e(TAG, "onProgressChanged newProgress=" + newProgress);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                uploadFileNew = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_CODE_GET_LOCAL_FILE_NEW);
                } catch (Exception e) {
                    ToastUtil.getInstance().showToast(getString(R.string.tip_choose_file_error));
                    return false;
                }
                return true;
            }

            //
            // FILE UPLOAD <3.0
            //
            public void openFileChooser(ValueCallback<Uri> uploadFile) {
                chooseFile(uploadFile, null, null);
            }

            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
                chooseFile(uploadFile, acceptType, null);
            }

            /**
             * 4.x
             * @param uploadFile
             * @param acceptType
             * @param capture
             */
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                chooseFile(uploadFile, acceptType, capture);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "onReceivedError");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();  // 接受所有网站的证书
                Log.e(TAG, "onReceivedSslError");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "shouldOverrideUrlLoading url=" + url);
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    /**
     * 设置跨域cookie读取
     */
    public final void setAcceptThirdPartyCookies() {
        //target 23 default false, so manual set true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }
    }

    private ValueCallback<Uri[]> uploadFileNew;
    private ValueCallback<Uri> uploadFile;

    /**
     * 文件选择
     *
     * @param uploadFile
     * @param acceptType
     * @param capture
     */
    private void chooseFile(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (TextUtils.isEmpty(acceptType)) {
            acceptType = "*/*";
        }
        intent.setType(acceptType);

        this.uploadFile = uploadFile;

        try {
            startActivityForResult(Intent.createChooser(intent, capture), REQUEST_CODE_GET_LOCAL_FILE);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
    }

    @Override
    protected void doClickEvent(int viewId) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GET_LOCAL_FILE:
                if (uploadFile != null) {
                    uploadFile.onReceiveValue((resultCode == Activity.RESULT_OK && data != null) ? data.getData() :
                            null);
                    uploadFile = null;
                }
                break;
            case REQUEST_CODE_GET_LOCAL_FILE_NEW:
                if (uploadFileNew != null) {
                    uploadFileNew.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    uploadFileNew = null;
                }
                break;
        }
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
}
