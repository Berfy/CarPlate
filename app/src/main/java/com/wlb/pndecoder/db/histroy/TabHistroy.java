package com.wlb.pndecoder.db.histroy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wlb.pndecoder.common.Constants;
import com.wlb.pndecoder.db.OpenDBUtil;
import com.wlb.pndecoder.db.OpenHelper;
import com.wlb.pndecoder.model.histroy.HistroyNumber;

import java.util.ArrayList;
import java.util.List;

import cn.berfy.framework.cache.TempSharedData;
import cn.berfy.framework.utils.LogUtil;

/**
 * Created by Berfy on 2017/6/14.
 */

public class TabHistroy {

    private Context mContext;
    private static TabHistroy mTabHistroy;
    private final String TAG = "TabHistroy";
    private OpenHelper mOpenHelper;
    private SQLiteDatabase mDb;

    synchronized public static TabHistroy getInstances(Context context) {
        synchronized (TabHistroy.class) {
            if (null == mTabHistroy) {
                mTabHistroy = new TabHistroy(context);
            }
        }
        return mTabHistroy;
    }

    public TabHistroy(Context context) {
        mContext = context;
        mOpenHelper = OpenHelper.getInstance(context);
        mDb = mOpenHelper.getDb();
    }

    public void add(HistroyNumber histroyNumber) {
        if (null != getData(histroyNumber.getPhone(), histroyNumber.getNumber())) {
            LogUtil.e(TAG, "更新TabHistroy " + histroyNumber.getPhone() + "  " + histroyNumber.getNumber());
            mDb.update(OpenDBUtil.TAB_HISTROY, getValues(histroyNumber), OpenDBUtil.KEYS_TAB_HISTROY[0]
                    + " = ? and " + OpenDBUtil.KEYS_TAB_HISTROY[1] + " = ?", new String[]{histroyNumber.getPhone(), histroyNumber.getNumber()});
        } else {
            LogUtil.e(TAG, "插入TabHistroy " + histroyNumber.getPhone() + "  " + histroyNumber.getNumber());
            mDb.insert(OpenDBUtil.TAB_HISTROY, null, getValues(histroyNumber));
        }
    }

    public HistroyNumber getData(String phone, String number) {
        LogUtil.e(TAG, "获取TabHistroy单项是否存在" + phone + "  " + number);
        Cursor cursor = mDb.query(OpenDBUtil.TAB_HISTROY, null, OpenDBUtil.KEYS_TAB_HISTROY[0]
                + " = ? and " + OpenDBUtil.KEYS_TAB_HISTROY[1] + " = ?", new String[]{phone, number}, null, null, null);
        HistroyNumber histroyNumber = null;
        if (cursor.moveToFirst()) {
            histroyNumber = new HistroyNumber();
            histroyNumber.setPhone(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[0])));
            histroyNumber.setNumber(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[1])));
            histroyNumber.setPath(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[2])));
            histroyNumber.setStatus(cursor.getInt(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[3])));
            histroyNumber.setUpdateTime(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[4])));
        }
        return histroyNumber;
    }

    public List<HistroyNumber> getAllData() {
        LogUtil.e(TAG, "获取getAllData");
        List<HistroyNumber> numbers = new ArrayList<>();
        Cursor cursor = mDb.query(OpenDBUtil.TAB_HISTROY, null, OpenDBUtil.KEYS_TAB_HISTROY[0] + " = ?",
                new String[]{TempSharedData.getInstance(mContext).getData(Constants.XML_PHONE)}, null, null,
                OpenDBUtil.KEYS_TAB_HISTROY[4] + " desc");
        try {
            while (cursor.moveToNext()) {
                HistroyNumber histroyNumber = new HistroyNumber();
                histroyNumber.setPhone(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[0])));
                histroyNumber.setNumber(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[1])));
                histroyNumber.setPath(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[2])));
                histroyNumber.setStatus(cursor.getInt(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[3])));
                histroyNumber.setUpdateTime(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[4])));
                numbers.add(histroyNumber);
                LogUtil.e(TAG, "获取getAllData  手机" + histroyNumber.getPhone() + "  车牌号" + histroyNumber.getNumber());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != cursor) {
                cursor.close();
            }
        }
        return numbers;
    }

    public List<HistroyNumber> getAllDataBySearch(String number) {
        LogUtil.e(TAG, "获取getAllDataBySearch" + number);
        List<HistroyNumber> numbers = new ArrayList<>();
        Cursor cursor = mDb.query(OpenDBUtil.TAB_HISTROY, null, OpenDBUtil.KEYS_TAB_HISTROY[1] + " like ?",
                new String[]{"%" + number + "%"},
                null, null, OpenDBUtil.KEYS_TAB_HISTROY[4] + " desc");
        try {
            while (cursor.moveToNext()) {
                HistroyNumber histroyNumber = new HistroyNumber();
                histroyNumber.setPhone(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[0])));
                histroyNumber.setNumber(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[1])));
                histroyNumber.setPath(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[2])));
                histroyNumber.setStatus(cursor.getInt(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[3])));
                histroyNumber.setUpdateTime(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_HISTROY[4])));
                numbers.add(histroyNumber);
                LogUtil.e(TAG, "获取getAllDataBySearch  手机" + histroyNumber.getPhone() + "  车牌号" + histroyNumber.getNumber());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != cursor) {
                cursor.close();
            }
        }
        return numbers;
    }

    private ContentValues getValues(HistroyNumber histroyNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpenDBUtil.KEYS_TAB_HISTROY[0], histroyNumber.getPhone());
        contentValues.put(OpenDBUtil.KEYS_TAB_HISTROY[1], histroyNumber.getNumber());
        contentValues.put(OpenDBUtil.KEYS_TAB_HISTROY[2], histroyNumber.getPath());
        contentValues.put(OpenDBUtil.KEYS_TAB_HISTROY[3], histroyNumber.getStatus());
        contentValues.put(OpenDBUtil.KEYS_TAB_HISTROY[4], histroyNumber.getUpdateTime());
        return contentValues;
    }
}
