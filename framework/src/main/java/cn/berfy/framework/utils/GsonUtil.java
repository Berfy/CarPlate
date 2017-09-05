package cn.berfy.framework.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtil {

    private static GsonUtil mGsonUtil;
    private Gson mGson;

    public static GsonUtil getInstance() {
        if (null == mGsonUtil) {
            mGsonUtil = new GsonUtil();
        }
        return mGsonUtil;
    }

    private GsonUtil() {
        mGson = new Gson();
    }

    public <T> String toJson(T classType) {
        return mGson.toJson(classType);
    }

    public <T> T toClass(String json, Class<T> classname) {
        try {
            return mGson.fromJson(json, classname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T toClass(String json, Type type) {
        try {
            return mGson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
