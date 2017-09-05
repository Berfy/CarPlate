package cn.berfy.framework.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Berfy on 2017/4/18.
 * 反射
 */
public class ReflexUtil<T> {

    public ReflexUtil() {

    }

    public Object fromJson(Class c, String json) {
        try {
            Object object = c.newInstance();
            JSONObject jsonObject = new JSONObject(json);
            return paserJSONObject(object, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object paserJSONObject(Object object, JSONObject jsonObject) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (jsonObject.has(field.getName())) {
                    LogUtil.e("JSONObject检索到参数   类", object.getClass().getName() + "  " + field.getName());
                    try {
                        LogUtil.e("JSONObject检索到类名", field.getGenericType().toString().replace("class ", ""));
                        if (jsonObject.get(field.getName()) instanceof JSONArray) {
                            JSONArray jsonArray = jsonObject.getJSONArray(field.getName());
                            String type = field.getGenericType().toString();
                            Class c = Class.forName(type.substring(type.indexOf("<") + 1, type.indexOf(">")));
                            List objects = new ArrayList <>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Object object1 = c.newInstance();
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                paserJSONObject(object1, jsonObject1);
                                objects.add(object1);
                            }
                            LogUtil.e("JSONObject检索  数组", GsonUtil.getInstance().toJson(objects));
                            field.set(object, new ArrayList<>());
                        } else if (jsonObject.get(field.getName()) instanceof JSONObject) {
                            Class c = Class.forName(field.getGenericType().toString().replace("class ", ""));
                            Object object1 = c.newInstance();
                            paserJSONObject(object1, jsonObject.getJSONObject(field.getName()));
                        } else {
                            field.setAccessible(true);
                            field.set(object, jsonObject.opt(field.getName()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object set(Class c, String key, Object val) {
        try {
            Object object = c.newInstance();
            Field field = c.getDeclaredField(key); // 获取实体类的所有属性，返回Field数组
            field.setAccessible(true);
            field.set(object, val);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, Object> getClassFields(Class c) {
        HashMap<String, Object> hashMap = null;
        try {
            Class<?> obj = c;
            Field[] f = obj.getDeclaredFields();
            hashMap = new HashMap<>();
            for (Field field : f) {
                field.setAccessible(true);
                hashMap.put(field.getName(), field.get(obj.newInstance()));
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hashMap;
    }

    public void invoke(Class c) {
        try {
            //获取某个特定的方法
            //通过：方法名+形参列表
            Method[] methods = c.getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                System.out.println("方法名称:" + methodName + "  返回" + method.getReturnType());
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length > 0) {
                    for (Class<?> clas : parameterTypes) {
                        String parameterName = clas.getName();
                        System.out.println("参数名称:" + parameterName);
                    }
                } else {
                    continue;
                }
                System.out.println("*****************************");
            }
            Method id = c.getMethod("setId", long.class);
            Method title = c.getDeclaredMethod("setTitle", String.class);
            Method content = c.getDeclaredMethod("setContent", String.class);
            Object o = c.newInstance();
            //通过反射机制执行login方法.
            //调用o对象的m方法,传递"admin""123"参数，方法的执行结果是retValue
            Object idValue = id.invoke(o, 111);
            Object titleValue = title.invoke(o, "测试反射");
            Object contentValue = content.invoke(o, "测试反射");
            LogUtil.e("测试反射赋值", GsonUtil.getInstance().toJson(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
