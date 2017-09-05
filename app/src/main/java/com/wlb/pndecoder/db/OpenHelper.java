package com.wlb.pndecoder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * 数据库工具类
 *
 * @author Berfy
 **/
public class OpenHelper extends SQLiteOpenHelper {

    private Context mContext;
    private static OpenHelper instance;
    private SQLiteDatabase mDb;
    private static final int DATABASEVERSION = 2;
    public static final String DATABASENAME = "wlb_pn"; // 数据库名称

    synchronized public static OpenHelper getInstance(Context context) {
        synchronized (OpenHelper.class) {
            if (instance == null) {
                instance = new OpenHelper(context.getApplicationContext());
            }
        }
        return instance;
    }

    private SQLiteDatabase initDb() {
        // TODO Auto-generated method stub
        SQLiteDatabase db = getReadableDatabase();
        if (Build.VERSION.SDK_INT >= 11) {
            db.enableWriteAheadLogging();// 允许读写同时进行
        }
        if (db.isReadOnly()) {
            db = getWritableDatabase();
        }
        return db;
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    private OpenHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
        // TODO Auto-generated constructor stub
        mDb = initDb();
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        execSql(db, OpenDBUtil.TAB_CREATE_HISTROY);
        execSql(db, OpenDBUtil.TAB_CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        execSql(db, OpenDBUtil.TAB_CREATE_HISTROY);
        execSql(db, OpenDBUtil.TAB_CREATE_USER);
    }

    private void execSql(SQLiteDatabase db, String sql) {
        try {
            // 添加修改时间字段
            db.execSQL(sql);// 给记血糖表添加是否与服务器同步字段
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
