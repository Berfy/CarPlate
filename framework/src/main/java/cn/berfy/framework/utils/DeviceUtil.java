package cn.berfy.framework.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import cn.berfy.framework.R;
import cn.berfy.framework.common.Constants;

import static com.android.volley.VolleyLog.TAG;

/**
 * @author Berfy
 * 设备相关
 */
public class DeviceUtil {

    /**
     * get IMSI
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        String imsi = "";
        try {
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE, 0);
            TelephonyManager mTelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imsi = mTelephonyMgr.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE, 0);
        }
        return imsi;
    }

    /**
     * get IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE, 0);
            TelephonyManager mTelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = mTelephonyMgr.getDeviceId();

            if (imei == null || imei.length() <= 0) {
                try {
                    Class<?> c = Class.forName("android.os.SystemProperties");
                    Method get = c.getMethod("get", String.class);

                    imei = (String) get.invoke(c, "ro.serialno");
                } catch (SecurityException e) {
                    LogUtil.e("DeviceUtil", e.getLocalizedMessage());
                } catch (IllegalArgumentException e) {
                    LogUtil.e("DeviceUtil", e.getLocalizedMessage());
                } catch (ClassNotFoundException e) {
                    LogUtil.e("DeviceUtil", e.getLocalizedMessage());
                } catch (NoSuchMethodException e) {
                    LogUtil.e("DeviceUtil", e.getLocalizedMessage());
                } catch (IllegalAccessException e) {
                    LogUtil.e("DeviceUtil", e.getLocalizedMessage());
                } catch (InvocationTargetException e) {
                    LogUtil.e("DeviceUtil", e.getLocalizedMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE, 0);
        }
        return imei;
    }

    // 获取包名
    public static String getPackageName(Context ctx) {
        return ctx.getPackageName();
    }

    public static String getPhoneNumber(Context context) {
        try {
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE, 0);
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE, 0);
        }
        return "";
    }

    public static String getSimCardNumber(Context context) {
        try {
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE, 0);
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE, 0);
        }
        return "";
    }

    public static String getSimProvider(Context context) {
        try {
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE,0);
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
            selfPermissionGranted(context, Manifest.permission.READ_PHONE_STATE,0);
        }
        return "";
    }

    /**
     * get sdk version
     *
     * @return
     */
    public static int getSdkversion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机操作系统版本
     */
    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }

    /**
     * get package version
     *
     * @return
     */
    public static int getPackageVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * get package version
     *
     * @return
     */
    public static String getPackageVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * get package version
     *
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    /**
     * 打开键盘
     *
     * @param context
     */
    public static void openKeyboard(Context context) {
        ((InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 传�?token关闭键盘
     *
     * @param context
     * @param token
     */
    public static void closeKeyboard(Context context, IBinder token) {
        ((InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(token, 0);
    }

    public static boolean isAliveIme(Context context) {
        InputMethodManager m = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (m.isActive()) {
            return true;
        }
        return false;
    }

    /**
     * 获取本机ip
     */
    public static InetAddress getLocalIpAddress(Context context)
            throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        selfPermissionGranted(context, Manifest.permission.ACCESS_WIFI_STATE, 0);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return InetAddress.getByName(String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
    }

    public static String logMemory() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        return "total:" + maxMemory + ",current:" + totalMemory;
    }

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static boolean isMIUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 判断是否创建了快捷方式
     *
     * @param title 通过创建的快捷图片标题查找
     */
    public static boolean isAddShortCut(Context context, String title) {
        boolean isInstallShortcut = false;
        final ContentResolver cr = context.getContentResolver();

        int versionLevel = Build.VERSION.SDK_INT;
        String AUTHORITY = "com.android.launcher2.settings";

        //2.2以上的系统的文件文件名字是不一样的
        if (versionLevel >= 8) {
            AUTHORITY = "com.android.launcher2.settings";
        } else {
            AUTHORITY = "com.android.launcher.settings";
        }

        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI,
                new String[]{"title", "iconResource"}, "title=?",
                new String[]{title}, null);

        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        return isInstallShortcut;
    }

    /**
     * 创建快捷方式
     *
     * @param cla 快捷方式跳转Activity
     */
    public static void addShortCut(Context context, String title, int iconId, Class cla) {
        if (isAddShortCut(context, title)) {
            return;
        }
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 设置属性
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, iconId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconRes);

        // 是否允许重复创建
        shortcut.putExtra("duplicate", false);

        //设置桌面快捷方式的图标
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context, iconId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        //点击快捷方式的操作
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, cla);

        // 设置启动程序
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        //广播通知桌面去创建
        context.sendBroadcast(shortcut);
    }

    public static void copy(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }

    public static String paste(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboardManager.getText().toString();
    }

    /**
     * 判断版本号大于23有没有该权限
     * permission 权限名
     * 是否有权限，没有自动调用
     */
    public static boolean selfPermissionGranted(final Context context, String[] permissions, int requestCode) {
        try {
            boolean result = false;//没有权限
            if (Build.VERSION.SDK_INT >= 23) {//!isMIUI() &&
                boolean isNeedPermission = false;
                boolean isNeedTipPermission = false;
                for (String permission : permissions) {
                    int state = ContextCompat.checkSelfPermission(context, permission);
                    if (state != PackageManager.PERMISSION_GRANTED) {
                        isNeedPermission = true;
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                        isNeedTipPermission = true;
                    }
                }
                if (isNeedPermission) {
                    try {
                        if (isNeedTipPermission) {//告诉用户需要权限打开才能使用
                            LogUtil.e("用户没有完全拒绝", "继续调起权限");
                            ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
                        } else {//用户选择了不再提醒或者设备安装策略禁止权限
                            LogUtil.e("用户拒绝", "用户选择了不再提醒或者设备安装策略禁止权限");
                            intentPermissions(context, permissions[0]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        intentPermissions(context, permissions[0]);
                    }
                } else {
                    return true;//有权限
                }
            } else {
                return true;//有权限
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断版本号大于23有没有该权限
     * permission 权限名
     * 是否有权限，没有自动调用
     */
    public static boolean selfPermissionGranted(final Context context, String permission, int requestCode) {
        boolean result = false;//没有权限
        if (Build.VERSION.SDK_INT >= 23) {//!isMIUI() &&
            int state = ContextCompat.checkSelfPermission(context, permission);
            LogUtil.e("权限状态", permission + "    " + state + "   允许" + PackageManager.PERMISSION_GRANTED + " 拒绝" + PackageManager.PERMISSION_DENIED);
            if (state != PackageManager.PERMISSION_GRANTED) {
                try {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {//告诉用户需要权限打开才能使用
                        LogUtil.e("用户没有完全拒绝", "继续调起权限");
                        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
                    } else {//用户选择了不再提醒或者设备安装策略禁止权限
                        LogUtil.e("用户拒绝", "用户选择了不再提醒或者设备安装策略禁止权限");
                        intentPermissions(context, permission);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    intentPermissions(context, permission);
                }
            } else {
                return true;//有权限
            }
        } else {
            //6.0以下用这个判断权限
//    result = PermissionChecker.checkSelfPermission(context, permission)
//                == PermissionChecker.PERMISSION_GRANTED;
            return true;//有权限
        }
        LogUtil.e("版本", "Build.VERSION.SDK_INT ::" + Build.VERSION.SDK_INT);
        return result;
    }

    private static void intentPermissions(Context context, String permission) {
        LogUtil.e("需要跳转权限了", permission);
        if (System.currentTimeMillis() - mClickTime > 3000) {
            mClickTime = System.currentTimeMillis();
            if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE || permission == Manifest.permission.READ_EXTERNAL_STORAGE) {
                ToastUtil.getInstance().showToast(context.getResources().getString(R.string.tip_permission_sd) + context.getResources().getString(R.string.tip_permission_need), Toast.LENGTH_LONG);
            } else if (permission == Manifest.permission.READ_PHONE_STATE) {
                ToastUtil.getInstance().showToast(context.getResources().getString(R.string.tip_permission_phone_info) + context.getResources().getString(R.string.tip_permission_need), Toast.LENGTH_LONG);
            } else if (permission == Manifest.permission.ACCESS_FINE_LOCATION || permission == Manifest.permission.ACCESS_COARSE_LOCATION) {
                ToastUtil.getInstance().showToast(context.getResources().getString(R.string.tip_permission_gps) + context.getResources().getString(R.string.tip_permission_need), Toast.LENGTH_LONG);
            } else if (permission == Manifest.permission.CAMERA) {
                ToastUtil.getInstance().showToast(context.getResources().getString(R.string.tip_permission_camera) + context.getResources().getString(R.string.tip_permission_need), Toast.LENGTH_LONG);
            } else if (permission == Manifest.permission.READ_CONTACTS) {
                ToastUtil.getInstance().showToast(context.getResources().getString(R.string.tip_permission_contact) + context.getResources().getString(R.string.tip_permission_need), Toast.LENGTH_LONG);
            } else if (permission == Manifest.permission.READ_SMS) {
                ToastUtil.getInstance().showToast(context.getResources().getString(R.string.tip_permission_sms) + context.getResources().getString(R.string.tip_permission_need), Toast.LENGTH_LONG);
            } else if (permission == Manifest.permission.CALL_PHONE) {
                ToastUtil.getInstance().showToast(context.getResources().getString(R.string.tip_permission_callphone) + context.getResources().getString(R.string.tip_permission_need), Toast.LENGTH_LONG);
            } else if (permission == Manifest.permission.RECORD_AUDIO) {
                ToastUtil.getInstance().showToast(context.getResources().getString(R.string.tip_permission_audio) + context.getResources().getString(R.string.tip_permission_need), Toast.LENGTH_LONG);
            }
            DeviceUtil.goAppDetailSettingIntent(context);
        }
    }

    private static long mClickTime = 0;

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static void goAppDetailSettingIntent(final Context context) {
        final Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(context), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName(context));
        }
        Constants.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                context.startActivity(localIntent);
            }
        });
    }

    /**
     * 跳转第三方App
     */
    public static void doStartApplicationWithPackageName(Context context, String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int height = 0;
        try {
            Rect frame = new Rect();
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            height = frame.top;
            if (height > 0) {
                LogUtil.e(TAG, "状态栏高度" + height);
            } else {
                Class<?> c = null;
                Object obj = null;
                Field field = null;
                int x = 0, sbar = 38;//默认为38，貌似大部分是这样的
                height = sbar;
                try {
                    c = Class.forName("com.android.internal.R$dimen");
                    obj = c.newInstance();
                    field = c.getField("status_bar_height");
                    x = Integer.parseInt(field.get(obj).toString());
                    height = context.getResources().getDimensionPixelSize(x);
                    LogUtil.e(TAG, "状态栏高度1" + height);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "状态栏高度2" + height);
        return height;
    }

    /**
     * 获取CPU数量
     */
    public static int getCpuNumber() {
        int num = Runtime.getRuntime().availableProcessors();
        LogUtil.i(TAG, "getCpuNumber" + num);
        return num;
    }
}
