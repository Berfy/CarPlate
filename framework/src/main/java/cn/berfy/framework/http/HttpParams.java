package cn.berfy.framework.http;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Berfy
 * 接口参数
 */
public class HttpParams {

    private List<BasicNameValuePair> mData = new ArrayList<BasicNameValuePair>();
    private StringBuffer sb;

    public void put(String key, String value) {
        // TODO Auto-generated method stub
        BasicNameValuePair pair = new BasicNameValuePair(key, value);
        if (!mData.contains(pair)) {
            mData.add(pair);
        }
    }

    public List<BasicNameValuePair> getParamsList() {
        return mData;
    }

    @Override
    public String toString() {
        sb = new StringBuffer();
        sb.append("?");
        for (BasicNameValuePair pair : mData) {
            sb.append(pair.getName() + "=" + pair.getValue() + "&");
        }
        String text = sb.substring(0, sb.length() - 1);
        sb = null;
        return text;
    }
}
