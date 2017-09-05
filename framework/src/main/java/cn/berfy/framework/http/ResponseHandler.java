package cn.berfy.framework.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import cn.berfy.framework.R;
import cn.berfy.framework.cache.TempSharedData;
import cn.berfy.framework.utils.LogUtil;
import cn.berfy.framework.utils.NetworkUtil;
import cn.berfy.framework.utils.ToastUtil;

/**
 * 接口回调处理
 *
 * @author Berfy
 */
public abstract class ResponseHandler<T> {

    private Context mContext;
    private RequestCallBack<T> mCallBack;
    private String mUrl;
    private HttpParams mHttpParams;
    /**
     * 是否显示来自服务器的信息
     */
    protected boolean mShowErrorMsgFromServer = true;
    protected boolean mShowSucMsgFromServer = false;
    protected boolean mIsSaveCache = false;
    protected boolean mIsLoadCache = false;
    protected boolean mRequestAllData = false;

    public ResponseHandler(Context context) {
        mContext = context;
    }

    public ResponseHandler(Context context, RequestCallBack<T> callBack) {
        mContext = context;
        mCallBack = callBack;
    }

    /**
     * @param isSaveCache              是否存储缓存
     * @param isLoadCache              是否先获取缓存
     * @param isShowErrorMsgFromServer 是否先获取缓存
     */
    public ResponseHandler(Context context, RequestCallBack<T> callBack, boolean isSaveCache, boolean isLoadCache,
                           boolean isShowSucMsgFromServer, boolean isShowErrorMsgFromServer) {
        mContext = context;
        mCallBack = callBack;
        mIsSaveCache = isSaveCache;
        mIsLoadCache = isLoadCache;
        mShowSucMsgFromServer = isShowSucMsgFromServer;
        mShowErrorMsgFromServer = isShowErrorMsgFromServer;
    }

    /**
     * @param isRequestAllData 是否获取全部json
     */
    public ResponseHandler(Context context, RequestCallBack<T> callBack, boolean isRequestAllData) {
        mContext = context;
        mCallBack = callBack;
        mRequestAllData = isRequestAllData;
    }

    public void onProgress(int bytesWritten, int totalSize) {
        if (mCallBack != null && mCallBack instanceof RequestCallBakProgress) {
            ((RequestCallBakProgress<T>) mCallBack).onProgress(bytesWritten,
                    totalSize);
        }
    }

    public void start(String url, HttpParams httpParams) {
        LogUtil.e("Volley传参POST", url + httpParams.toString());
        mUrl = url;
        mHttpParams = httpParams;

        if (mCallBack != null) {
            mCallBack.start();
        }

        if (mIsLoadCache) {
            String json = TempSharedData.getInstance(mContext).getData(mUrl + mHttpParams.toString());
            if (!TextUtils.isEmpty(json)) {
                LogUtil.e("Volley返回值POST 缓存 (" + mUrl + ")", json);
                NetResponse netResponse = getResponse(json);
                netResponse.netMsg.isCache = true;
                onDataReturn(netResponse);
                if (mCallBack != null) {
                    mCallBack.finish(netResponse);
                }
            }
        }
    }

    public void finish(String content) {
        if (mIsSaveCache) {
            TempSharedData.getInstance(mContext).save(mUrl + mHttpParams.toString(), content);
        }
        LogUtil.e("Volley返回值POST (" + mUrl + ")", content);
        NetResponse<T> result;
        if (null == content) {
            result = new NetResponse<T>();
            if (mCallBack != null) {
                mCallBack.finish(result);
            }
            onDataReturn(result);
        } else {
            result = getResponse(content);
            if (mCallBack != null) {
                mCallBack.finish(result);
            }
            onDataReturn(result);
        }
    }

    public void error(VolleyError error) {
        NetResponse<T> result = new NetResponse<T>();
        NetMessage netMessage = new NetMessage();
        if (mCallBack != null) {
            netMessage.code = netMessage.ERROR;
            if (NetworkUtil.isHasNetWork(mContext)) {
                netMessage.msg = mContext.getResources().getString(
                        R.string.request_error);
            } else {
                netMessage.msg = mContext.getResources().getString(
                        R.string.request_network_error);
            }
            result.netMsg = netMessage;
            onDataReturn(result);
            this.mCallBack.finish(result);
        }
        if (mShowErrorMsgFromServer) {//默认显示，但是可以关闭提
            if (NetworkUtil.isHasNetWork(mContext)) {
                ToastUtil.getInstance().showToast(
                        mContext.getResources().getString(
                                R.string.request_error));
            } else {
                ToastUtil.getInstance().showToast(
                        mContext.getResources().getString(
                                R.string.request_network_error));
            }
        }
    }

    /**
     * 不callback处理，自己处理返回数据
     */
    protected void onDataReturn(NetResponse<T> result) {
    }

    public NetResponse<T> getResponse(String content) {
        NetResponse<T> result = new NetResponse<T>();
        result.netMsg = getBaseNetMessage(content);
        if (mRequestAllData) {
            result.content = getContent(content);
            LogUtil.e("json返回", result.content.toString());
        } else {
            result.content = getContent(result.netMsg.data);
        }
        return result;
    }

    /**
     * 实现具体的数据与实体的转换 通过Gson即可
     *
     * @param json
     * @return
     */
    public abstract T getContent(String json);

    /**
     * 获取通用格式的返回值
     *
     * @param content
     * @return
     */
    public NetMessage getBaseNetMessage(String content) {
        NetMessage netMessage = new NetMessage();
        try {
            JSONObject json = new JSONObject(content);
            netMessage.code = json.optInt("code");
            netMessage.msg = json.optString("msg");
            netMessage.data = json.optString("data");
        } catch (Exception e) {
            e.printStackTrace();
            netMessage.msg = mContext.getResources().getString(
                    R.string.request_error);
            netMessage.code = -1;
            Log.e("getBaseNetMessage", e.toString());
        }
        if (!TextUtils.isEmpty(netMessage.msg)) {
            if (netMessage.code == 1) {
                if (mShowSucMsgFromServer) {//默认不显示，但是可以打开
                    ToastUtil.getInstance().showToast(netMessage.msg);
                }
            } else {
                if (mShowErrorMsgFromServer) {//默认显示，但是可以关闭提
                    ToastUtil.getInstance().showToast(netMessage.msg);
                }
            }
        }
        return netMessage;
    }
}
