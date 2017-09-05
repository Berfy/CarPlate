package cn.berfy.framework.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Looper;

import java.util.List;

/**
 * Created by Berfy on 2016/8/16.
 * 应用内操作类
 */
public class AppUtil {

    /**
     * 检查服务存在
     */
    public static boolean checkStepService(Context context, String serviceClassName) {
        LogUtil.e("检查服务存在", serviceClassName);
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().contains(serviceClassName) == true) {
                LogUtil.e("检查服务存在" + serviceClassName, "yes");
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportBluttooth40(boolean isNeedTip) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {//蓝牙4.0
            return true;
        }
        if (isNeedTip)
            ToastUtil.getInstance().showToast("该功能暂时只支持蓝牙4.0的设备");
        return false;
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
