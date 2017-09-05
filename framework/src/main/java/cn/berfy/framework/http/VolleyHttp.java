package cn.berfy.framework.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.message.BasicNameValuePair;

import java.util.HashMap;
import java.util.Map;

import cn.berfy.framework.utils.FileUtils;
import cn.berfy.framework.utils.LogUtil;

/**
 * @author Berfy
 * @ClassName: VolleyHttp
 * @Description: 网络请求封装类
 */
public class VolleyHttp {

    private Context mContext;
    private static VolleyHttp mVolleyHttp;
    private RequestQueue mRequestQueue;

    public static VolleyHttp init(Context context) {
        if (null == mVolleyHttp) {
            mVolleyHttp = new VolleyHttp(context);
        }
        return mVolleyHttp;
    }

    public static VolleyHttp getInstances() {
        if (null == mVolleyHttp) {
            try {
                throw new NullPointerException("未初始化VolleyHttp，请在Application中调用VolleyHttp.init(Context context)方法");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mVolleyHttp;
    }

    private VolleyHttp(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue() {
        if (null == mRequestQueue) {
            try {
                throw new NullPointerException("未初始化VolleyHttp，请在Application中调用VolleyHttp.init(Context context)方法");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mRequestQueue;
    }

    private boolean isNull() {
        if (null == getRequestQueue()) {
            try {
                throw new NullPointerException("未初始化VolleyHttp，请在Application中调用VolleyHttp.init(Context context)方法");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public <T> void get(final String url, HttpParams httpParams, final ResponseHandler<T> responseHandler) {
        if (!isNull()) {
            final HttpParams post = null == httpParams ? new HttpParams() : httpParams;
            responseHandler.start(url, post);
            StringRequest jr = new StringRequest(Request.Method.GET, url + post.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String result = response.toString();
                    responseHandler.finish(result);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        VolleyError temperror = new VolleyError(new String(error.networkResponse.data));
                        error = temperror;
                        LogUtil.e("Volley错误POST (" + url + ")", error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                        FileUtils.saveFile(mContext, error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    } else {
                        LogUtil.e("Volley错误POST (" + url + ")", "网络错误");
                        FileUtils.saveFile(mContext, "网络错误");
                    }
                    responseHandler.error(error);
                }
            });
            jr.setRetryPolicy(new DefaultRetryPolicy(
                            20 * 1000,//默认超时时间，应设置一个稍微大点儿的
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            mRequestQueue.add(jr);
        }
    }

    public void get(final String url, HttpParams httpParams, final VolleyCallBack callBack, final boolean isShowLog) {
        if (!isNull()) {
            if (null == httpParams) {
                httpParams = new HttpParams();
            }
            LogUtil.e("Volley传参GET", url + httpParams.toString());
            StringRequest jr = new StringRequest(Request.Method.GET, url + httpParams.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String result = response.toString();
                    if (isShowLog && response.length() < 1024 * 1024 * 4) {
                        LogUtil.e("Volley返回值GET (" + url + ")", result);
                    }
                    callBack.finish(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtil.e("Volley错误GET" + url, error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    FileUtils.saveFile(mContext, error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    callBack.error(error);
                }
            });
            jr.setRetryPolicy(new DefaultRetryPolicy(
                            20 * 1000,//默认超时时间，应设置一个稍微大点儿的
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            mRequestQueue.add(jr);
        }
    }

    public void get(final String url, HttpParams httpParams, final VolleyCallBack callBack) {
        if (!isNull()) {
            if (null == httpParams) {
                httpParams = new HttpParams();
            }
            LogUtil.e("Volley传参GET", url + httpParams.toString());
            StringRequest jr = new StringRequest(Request.Method.GET, url + httpParams.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String result = response.toString();
                    if (response.length() < 1024 * 1024 * 4) {
                        LogUtil.e("Volley返回值GET (" + url + ")", result);
                    }
                    callBack.finish(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtil.e("Volley错误GET" + url, error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    FileUtils.saveFile(mContext, error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    callBack.error(error);
                }
            });
            jr.setRetryPolicy(new DefaultRetryPolicy(
                            20 * 1000,//默认超时时间，应设置一个稍微大点儿的
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            mRequestQueue.add(jr);
        }
    }

    public <T> void post(final String url, HttpParams httpParams, final ResponseHandler<T> responseHandler) {
        if (!isNull()) {
            final HttpParams post = null == httpParams ? new HttpParams() : httpParams;
            responseHandler.start(url, post);
            StringRequest jr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String result = response.toString();
                    responseHandler.finish(result);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        VolleyError temperror = new VolleyError(new String(error.networkResponse.data));
                        error = temperror;
                        LogUtil.e("Volley错误POST (" + url + ")", error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                        FileUtils.saveFile(mContext, error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    } else {
                        LogUtil.e("Volley错误POST (" + url + ")", "网络错误");
                        FileUtils.saveFile(mContext, "网络错误");
                    }
                    responseHandler.error(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // TODO Auto-generated method stub
                    Map<String, String> map = new HashMap<String, String>();
                    for (BasicNameValuePair basicNameValuePair : post.getParamsList()) {
                        map.put(basicNameValuePair.getName(), basicNameValuePair.getValue());
                    }
                    return map;
                }
            };
            jr.setRetryPolicy(new DefaultRetryPolicy(
                            20 * 1000,//默认超时时间，应设置一个稍微大点儿的
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            mRequestQueue.add(jr);
        }
    }

    public void post(final String url, HttpParams httpParams, final VolleyCallBack callBack) {
        if (!isNull()) {
            final HttpParams post = (null == httpParams ? new HttpParams() : httpParams);
            LogUtil.e("Volley传参POST", url + post.toString());
            StringRequest jr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String result = response.toString();
                    if (response.length() < 1024 * 1024 * 4) {
                        LogUtil.e("Volley返回值POST (" + url + ")", result);
                    }
                    callBack.finish(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtil.e("Volley错误POST (" + url + ")", error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    FileUtils.saveFile(mContext, error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    callBack.error(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams()
                        throws AuthFailureError {
                    // TODO Auto-generated method stub
                    Map<String, String> map = new HashMap<String, String>();
                    for (BasicNameValuePair basicNameValuePair : post.getParamsList()) {
                        LogUtil.e("传参", basicNameValuePair.getName() + "=" + basicNameValuePair.getValue());
                        map.put(basicNameValuePair.getName(), basicNameValuePair.getValue());
                    }
                    return map;
                }
            };
            jr.setRetryPolicy(new DefaultRetryPolicy(
                            20 * 1000,//默认超时时间，应设置一个稍微大点儿的
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            mRequestQueue.add(jr);
        }
    }

    public void post(final String url, HttpParams httpParams, final VolleyCallBack callBack, final boolean isShowLog) {
        if (!isNull()) {
            final HttpParams post = (null == httpParams ? new HttpParams() : httpParams);
            LogUtil.e("Volley传参POST", url + post.toString());
            StringRequest jr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String result = response.toString();
                    if (isShowLog && response.length() < 1024 * 1024 * 4) {
                        LogUtil.e("Volley返回值GET (" + url + ")", result);
                    }
                    callBack.finish(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtil.e("Volley错误POST (" + url + ")", error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    FileUtils.saveFile(mContext, error.getLocalizedMessage() + " " + error.getMessage() + "  " + error.getCause() + "  " + error.getStackTrace() + "  " + error.getNetworkTimeMs());
                    callBack.error(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams()
                        throws AuthFailureError {
                    // TODO Auto-generated method stub
                    Map<String, String> map = new HashMap<String, String>();
                    for (BasicNameValuePair basicNameValuePair : post.getParamsList()) {
                        LogUtil.e("传参", basicNameValuePair.getName() + "=" + basicNameValuePair.getValue());
                        map.put(basicNameValuePair.getName(), basicNameValuePair.getValue());
                    }
                    return map;
                }
            };
            jr.setRetryPolicy(new DefaultRetryPolicy(
                            20 * 1000,//默认超时时间，应设置一个稍微大点儿的
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );
            mRequestQueue.add(jr);
        }
    }
}
