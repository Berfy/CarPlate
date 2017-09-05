package com.wlb.pndecoder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wlb.pndecoder.model.User;

import java.util.ArrayList;
import java.util.List;

import cn.berfy.framework.utils.LogUtil;

/**
 * Created by Berfy on 2017/6/14.
 * 用户表
 */

public class TabUser {

    private static TabUser mTabUser;
    private final String TAG = "TabUser";
    private OpenHelper mOpenHelper;
    private SQLiteDatabase mDb;

    synchronized public static TabUser getInstances(Context context) {
        synchronized (TabUser.class) {
            if (null == mTabUser) {
                mTabUser = new TabUser(context);
            }
        }
        return mTabUser;
    }

    public TabUser(Context context) {
        mOpenHelper = OpenHelper.getInstance(context);
        mDb = mOpenHelper.getDb();
    }

    public void add(User user) {
        if (null != getData(user.getPhone())) {
            LogUtil.e(TAG, "更新TabUser " + user.getPhone());
            mDb.update(OpenDBUtil.TAB_USER, getValues(user), OpenDBUtil.KEYS_TAB_USER[0]
                    + " = ?", new String[]{user.getPhone()});
        } else {
            LogUtil.e(TAG, "插入TabUser " + user.getPhone());
            mDb.insert(OpenDBUtil.TAB_USER, null, getValues(user));
        }
    }

    public User getData(String phone) {
        LogUtil.e(TAG, "获取TabUser单项是否存在" + phone);
        Cursor cursor = mDb.query(OpenDBUtil.TAB_USER, null, OpenDBUtil.KEYS_TAB_USER[0]
                + " = ?", new String[]{phone}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setPhone(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[0])));
            user.setName(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[1])));
            user.setUpdateTime(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[2])));
        }
        return user;
    }

    public List<User> getAllData() {
        LogUtil.e(TAG, "获取getAllData");
        List<User> numbers = new ArrayList<>();
        Cursor cursor = mDb.query(OpenDBUtil.TAB_USER, null, null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                User user = new User();
                user.setPhone(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[0])));
                user.setName(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[1])));
                user.setUpdateTime(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[2])));
                numbers.add(user);
                LogUtil.e(TAG, "获取getAllData" + user.getPhone());
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

    public List<User> getAllDataBySearch(String number) {
        LogUtil.e(TAG, "获取getAllDataBySearch" + number);
        List<User> numbers = new ArrayList<>();
        Cursor cursor = mDb.query(OpenDBUtil.TAB_USER, null, OpenDBUtil.KEYS_TAB_USER[1] + " like ?", new String[]{"%" + number + "%"},
                null, null, OpenDBUtil.KEYS_TAB_USER[4] + " desc");
        try {
            while (cursor.moveToNext()) {
                User user = new User();
                user.setPhone(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[0])));
                user.setName(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[1])));
                user.setUpdateTime(cursor.getString(cursor.getColumnIndex(OpenDBUtil.KEYS_TAB_USER[2])));
                numbers.add(user);
                LogUtil.e(TAG, "获取getAllDataBySearch" + user.getPhone());
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

    private ContentValues getValues(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpenDBUtil.KEYS_TAB_USER[0], user.getPhone());
        contentValues.put(OpenDBUtil.KEYS_TAB_USER[1], user.getName());
        contentValues.put(OpenDBUtil.KEYS_TAB_USER[2], user.getUpdateTime());
        return contentValues;
    }
}
