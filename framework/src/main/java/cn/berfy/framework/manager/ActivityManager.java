package cn.berfy.framework.manager;

import android.app.Activity;
import java.util.Stack;

import cn.berfy.framework.utils.LogUtil;

/**
 * @author Berfy
 * @category Activity堆栈管理
 */
public class ActivityManager {

    private Stack<Activity> activityStack;
    public static ActivityManager instance;

    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public Integer getActivityNum() {
        if (null != activityStack) {
            return activityStack.size();
        }
        return 0;
    }

    public void popActivity() {
        Activity activity = activityStack.lastElement();
        if (null != activity) {
            activity.finish();
            activity = null;
        }
    }

    public void popActivity(Activity activity) {
        if (null != activity) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    public void pushActivity(Activity activity) {
        if (null == activityStack) {
            activityStack = new Stack<Activity>();
        }
        LogUtil.d("跳转Activity", activity.getClass().getName());
        activityStack.add(activity);
    }

    public void popAllActivity() {
        while (null != activityStack && activityStack.size() > 0) {
            Activity activity = currentActivity();
            if (null == activity) {
                break;
            }
            popActivity(activity);
        }
        System.gc();
    }

    public void popAllActivityExceptOne(Class<?> cls) {
        while (null != activityStack && activityStack.size() > 0) {
            Activity activity = currentActivity();
            if (null == activity) {
                break;
            }
            if (null != cls) {
                if (activity.getClass().equals(cls)) {
                    break;
                }
            }
            popActivity(activity);
        }
        System.gc();
    }

    public void popActivity(Class<?> cls) {
        if (null != activityStack) {
            for (int i = 0; i < activityStack.size(); i++) {
                Activity activity = activityStack.elementAt(i);
                if (activityStack.elementAt(i).getClass().equals(cls)) {
                    LogUtil.e("关闭Activity", cls + "");
                    popActivity(activity);
                }
            }
        }
    }

    public Activity getActivity(int position) {
        if (null != activityStack && activityStack.size() > position) {
            return activityStack.get(position);
        }
        return null;
    }
}
