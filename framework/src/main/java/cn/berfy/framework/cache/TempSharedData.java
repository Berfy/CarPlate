package cn.berfy.framework.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import cn.berfy.framework.common.Constants;
import cn.berfy.framework.utils.LogUtil;

/**
 * 缓存
 *
 * @author Berfy
 */
public class TempSharedData {

    private final String TAG = "TempSharedData";
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    private static TempSharedData mUserSharedData;

    public static TempSharedData getInstance(Context context) {
        if (mUserSharedData == null) {
            synchronized (TempSharedData.class) {
                if (mUserSharedData == null) {
                    mUserSharedData = new TempSharedData(context);
                }
            }
        }
        return mUserSharedData;
    }

    private TempSharedData(Context context) {
        mSharedPreferences = context.getSharedPreferences(
                Constants.XML_TEMP_CACEH, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    /**
     * 保存json
     */
    public void save(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public String getData(String key) {
        String result = mSharedPreferences.getString(key, "");
        LogUtil.e(TAG, "获取Data" + result);
        return result;
    }

    /**
     * 保存json
     */
    public void save(String key, int value) {
        LogUtil.e(TAG, "存储key" + key + " 值" + value);
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public int getData(String key, int defaultInt) {
        return mSharedPreferences.getInt(key, defaultInt);
    }

    public void clear() {
        mEditor.clear().commit();
    }

}
