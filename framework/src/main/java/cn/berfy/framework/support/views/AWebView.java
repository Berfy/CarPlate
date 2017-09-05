package cn.berfy.framework.support.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.android.volley.VolleyError;
import cn.berfy.framework.http.VolleyCallBack;
import cn.berfy.framework.http.VolleyHttp;
import cn.berfy.framework.utils.LogUtil;

/**
 * Created by Berfy on 2016/5/18.
 * 判断链接是否有效的WebView
 */
public class AWebView extends WebView {

    private OnUrlRequestListener onUrlRequestListener;

    public AWebView(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= 19) { // KITKAT
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        if (Build.VERSION.SDK_INT  >= 21) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public AWebView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void setOnUrlRequestListener(OnUrlRequestListener onUrlRequestListener) {
        this.onUrlRequestListener = onUrlRequestListener;
    }

    public void loadUrlOnCheck(final String url) {
        VolleyHttp.getInstances().get(url, null, new VolleyCallBack() {
            @Override
            public void finish(String result) {
                LogUtil.e("请求成功", result);
                loadUrl(url);
                onUrlRequestListener.onSuc();
            }

            @Override
            public void error(VolleyError volleyError) {
                LogUtil.e("请求错误", "==========");
                if (null != volleyError) {
                    if (null != volleyError.networkResponse) {
                        LogUtil.e("请求错误", volleyError.networkResponse.statusCode + "");
                        if (volleyError.networkResponse.statusCode >= 500) {//过滤服务器代码错误，继续请求
                            loadUrl(url);
                            onUrlRequestListener.onSuc();
                            return;
                        }
                    }
                }
                onUrlRequestListener.onFailed();
                return;
            }
        });
    }

    public interface OnUrlRequestListener {
        void onSuc();

        void onFailed();
    }
}
