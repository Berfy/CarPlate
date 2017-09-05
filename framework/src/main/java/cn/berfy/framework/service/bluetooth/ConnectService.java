package cn.berfy.framework.service.bluetooth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import cn.berfy.framework.R;
import cn.berfy.framework.manager.ActivityManager;
import cn.berfy.framework.service.bluetooth.lib.SampleGattAttributes;
import cn.berfy.framework.service.bluetooth.lib.StringByte;
import cn.berfy.framework.utils.LogUtil;
import cn.berfy.framework.utils.StringUtil;
import cn.berfy.framework.utils.ToastUtil;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

/**
 * Created by Berfy on 2017/5/16.
 */
@SuppressLint({"NewApi"})
public class ConnectService extends Service {

    private Context mContext;
    private final String TAG = "ConnectService";

    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<Activity, OnBlueServiceStatusListener> mListeners = new HashMap<>();

    private BluetoothGatt mBluetoothGatt;//当前设备的gatt
    private int mConnectionState = 0;//连接状态
    private BluetoothDevice mBluetoothDevice;//当前连接设备的缓存
    private BluetoothDevice mBluetoothDeviceSousuo;//当前搜索到的设备

    public static final int LANYA_OPEN = 10002;
    private boolean mConnected;//蓝牙连接状态
    private final int UPLOAD_TIMEOUT_COUNT = 8;
    private final int SEARCHING_COUNT = 20;
    private final int CONNECTTING_COUNT = 20;

    public static final int STATUS_BLUTTOOTH_DISCONNECT = -2;//连接中断
    public static final int STATUS_BLUTTOOTH_NOT_SUPPORT = -1;//蓝牙不支持4.0
    public static final int STATUS_BLUTTOOTH_NOT_OPEN = 0;//蓝牙未打开
    public static final int STATUS_BLUTTOOTH_OPEN_NOT_ACCESS = 1;//蓝牙拒绝打开
    public static final int STATUS_BLUTTOOTH_OPENED = 2;//蓝牙已打开
    public static final int STATUS_BLUTTOOTH_SEARCHING_START = 3;//搜索开始
    public static final int STATUS_BLUTTOOTH_SEARCHING_STOP = 4;//取消搜索
    public static final int STATUS_BLUTTOOTH_SEARCHING_ERROR = 5;//搜索失败
    public static final int STATUS_BLUTTOOTH_CONNECTTING = 6;//连接中
    public static final int STATUS_BLUTTOOTH_CONNECT_ERROR = 7;//连接失败
    public static final int STATUS_BLUTTOOTH_CONNECTED = 8;//已连接
    public static final int STATUS_BLUTTOOTH_CMD = 9;//命令开始
    public static final int STATUS_BLUTTOOTH_CMD_RESULT = 10;//命令回应
    public static final int STATUS_BLUTTOOTH_UPLOAD = 11;//上传ing
    public static final int STATUS_BLUTTOOTH_UPLOAD_SUC = 12;//上传成功
    public static final int STATUS_BLUTTOOTH_UPLOAD_ERROR = 13;//上传失败
    private int mConnectStatus = 0;//-1不支持 0未开启 1已开启 2搜索中 3搜索失败 4连接中 5连接失败 6已连接 7连接中断 8命令就绪 9同步中 10同步完成

    public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    private final int SEARCH_TIMEOUT = 0;
    private final int CONNECT_TIMEOUT = 1;
    private final int BLE_SCAN = 3;
    private final int UPLOAD_TIMECOUNT = 4;//上传剩余时间
    private final int CMD_TIMEOUT = 5;//命令超时未响应
    private final int START_BLE_SCAN = 6;
    private final int BIND = 7;
    private int mSearchingCount, mConnencttingCount;//倒计时
    private int mUploadTimeCount;//可以上传到计时，倒计时结束可以上传
    private int mCmdTimeCount;//命令相应到计时，倒计时结束命令未执行则失败
    private BluetoothGattCharacteristic gatt2a05 = null, gatt2a11 = null, gatt2a16 = null, gatt2a17 = null, gatt2a2b = null, gatt2a18 = null, gatt2a52 = null;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        LogUtil.i(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG, "onBind");
        registerReceiver(mStateReceiver, makeUpdateIntentFilter());
        return new MyBinder();
    }

    public boolean isConnected() {
        return mConnected;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public ConnectService getService() {
            return ConnectService.this;
        }
    }

    public int checkSupport() {
        LogUtil.i(TAG, "checkSupport");
        if (!isSupport()) {
            return -2;
        }
        return checkBluetooth();
    }

    private boolean isSupport() {
        boolean isSupport = (null != mBluetoothAdapter);
        if (!isSupport) {
            ToastUtil.getInstance().showToast("蓝牙不支持4.0");
            mConnectStatus = STATUS_BLUTTOOTH_NOT_SUPPORT;
            for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
            }
        }
        return isSupport;
    }

    public int checkBluetooth() {
        LogUtil.i(TAG, "checkBluetooth");
        if (!isConnected()) {
            // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启�?,弹出对话框向用户要求授予权限来启�?
            if (!mBluetoothAdapter.isEnabled() && !mConnected) {
                LogUtil.i(TAG, "蓝牙未开启" + ActivityManager.getInstance().currentActivity());
                mConnectStatus = STATUS_BLUTTOOTH_NOT_OPEN;
                for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                    entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                }
                return -1;//未打开
            } else {
                LogUtil.i(TAG, "已打开蓝牙" + mConnectStatus);
                if (mConnectStatus > STATUS_BLUTTOOTH_OPENED) {//如果是其他状态，搜索或者连接中
                    reset();
                }
                mConnectStatus = STATUS_BLUTTOOTH_OPENED;
                for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                    entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                }
                return 1;//一打开
            }
        } else {
            mConnectStatus = STATUS_BLUTTOOTH_CONNECTED;
            for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                LogUtil.i(TAG, "已连接");
                entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
            }
            return 2;//已连接
        }
    }

    public int getStatus() {
        LogUtil.e(TAG, "获取状态" + mConnectStatus);
        return mConnectStatus;
    }

    public void setOnStatusListener(Activity activity, OnBlueServiceStatusListener onListener) {
        LogUtil.e(TAG, "设置监听" + activity);
        if (null != mListeners.get(activity)) {
            mListeners.remove(activity);
        }
        mListeners.put(activity, onListener);
    }

    public void removeListener(Activity activity, OnBlueServiceStatusListener onBlueServiceStatusListener) {
        if (null != mListeners.get(activity)) {
            mListeners.remove(activity);
        }
    }

    private void reset() {
        mSearchingCount = 0;
        mConnencttingCount = 0;
        mHandler.removeMessages(SEARCH_TIMEOUT);
        mHandler.removeMessages(CONNECT_TIMEOUT);
        if (mConnectStatus == STATUS_BLUTTOOTH_SEARCHING_START || mConnencttingCount > 0) {//搜索中
            stopSearch();
        } else if (mConnectStatus == STATUS_BLUTTOOTH_CONNECTTING) {
            disConnect();
        }
    }

    /**
     * @param searchTime 搜索时间 单位秒
     */
    public void startSearch(int searchTime) {
        if (isSupport()) {
            LogUtil.i(TAG, "startSearch");
            if (mSearchingCount <= 0) {
                if (searchTime == 0) {
                    mSearchingCount = SEARCHING_COUNT;
                } else {
                    mSearchingCount = searchTime;
                }
                mHandler.removeMessages(BLE_SCAN);
                mHandler.removeMessages(START_BLE_SCAN);
                mHandler.sendEmptyMessageDelayed(START_BLE_SCAN, 2000);
                mHandler.removeMessages(SEARCH_TIMEOUT);
                mHandler.sendEmptyMessageDelayed(SEARCH_TIMEOUT, 2000);
                mConnectStatus = STATUS_BLUTTOOTH_SEARCHING_START;
                for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                    entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                }
            }
        }
    }

    public void startSearch() {
        if (isSupport()) {
            LogUtil.i(TAG, "startSearch");
            //确定蓝牙打开而且未搜索
            if (mConnectStatus >= STATUS_BLUTTOOTH_OPENED && mSearchingCount <= 0) {
                mSearchingCount = SEARCHING_COUNT;
                mHandler.removeMessages(BLE_SCAN);
                mHandler.removeMessages(START_BLE_SCAN);
                mHandler.sendEmptyMessageDelayed(START_BLE_SCAN, 2000);
                mHandler.removeMessages(SEARCH_TIMEOUT);
                mHandler.sendEmptyMessageDelayed(SEARCH_TIMEOUT, 2000);
                mConnectStatus = STATUS_BLUTTOOTH_SEARCHING_START;
                for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                    entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                }
            }
        }
    }

    public void stopSearch() {
        if (isSupport()) {
            if (mSearchingCount > 0) {
                LogUtil.i(TAG, "搜索停止" + mSearchingCount);
                mHandler.removeMessages(START_BLE_SCAN);
                mHandler.removeMessages(SEARCH_TIMEOUT);
                mSearchingCount = 0;
                mConnectStatus = STATUS_BLUTTOOTH_SEARCHING_STOP;
                for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                    entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                }
            }
            mBluetoothAdapter.stopLeScan(mLeScanCallback);// 停止扫描！
        }
    }

    /**
     * 到了20秒之后停止搜索
     */
    public void stopSearch2() {
        if (isSupport()) {
            if (mSearchingCount > 0) {
                LogUtil.i(TAG, "搜索停止" + mSearchingCount);
                mHandler.removeMessages(START_BLE_SCAN);
                mHandler.removeMessages(SEARCH_TIMEOUT);
                mSearchingCount = 0;
                mConnectStatus = STATUS_BLUTTOOTH_SEARCHING_STOP;

            }
            mBluetoothAdapter.stopLeScan(mLeScanCallback);// 停止扫描！
        }
    }


    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Message message = new Message();
            message.obj = device;
            message.what = BLE_SCAN;
            mHandler.sendMessage(message);
        }
    };

    public boolean connect(BluetoothDevice bluetoothDevice) {
        if (isSupport()) {
            stopSearch();
            if (null != bluetoothDevice) {
                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    removeBond(getClass(), bluetoothDevice);
                }
                //连接中不能再次连接
                if (mConnencttingCount <= 0) {
                    LogUtil.i(TAG, "连接ing");
                    mConnectStatus = STATUS_BLUTTOOTH_CONNECTTING;
                    for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                        LogUtil.i(TAG, "监听" + entry.getValue());
                        entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                    }
                    mConnencttingCount = CONNECTTING_COUNT;
                    mHandler.removeMessages(CONNECT_TIMEOUT);
                    mHandler.sendEmptyMessageDelayed(CONNECT_TIMEOUT, 1000);
                    final boolean result = connect(bluetoothDevice.getAddress());
                    LogUtil.i(TAG, "Connect request result=" + result);
                    if (!result) {
                        LogUtil.i(TAG, "连接失败");
                        mConnectStatus = STATUS_BLUTTOOTH_CONNECT_ERROR;
                        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                        }
                    }
                    return result;
                } else {

                }
            }
        }
        return false;
    }

    /**
     * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    private boolean removeBond(Class btClass, BluetoothDevice btDevice) {
        if (isSupport()) {
            try {
                Method removeBondMethod = mBluetoothAdapter.getClass().getMethod("removeBond");
                Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
                return returnValue.booleanValue();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return false;
    }

    private IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        return intentFilter;
    }

    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            LogUtil.i(TAG, "action=" + action);
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {//配对状态
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        LogUtil.i(TAG, "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        LogUtil.i(TAG, "完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        LogUtil.i(TAG, "取消配对");
                    default:
                        break;
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {//配对状态
                int status = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (status) {
                    case BluetoothAdapter.STATE_ON:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        if (status == BluetoothAdapter.STATE_ON) {
                            LogUtil.i(TAG, "开启蓝牙");
                            checkBluetooth();
                        } else {
                            LogUtil.i(TAG, "正在打开蓝牙");
                        }
                        break;
                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (status == BluetoothAdapter.STATE_OFF) {
                            reset();
                            LogUtil.i(TAG, "关闭蓝牙" + ActivityManager.getInstance().currentActivity());
                            mConnectStatus = STATUS_BLUTTOOTH_NOT_OPEN;
                            for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                                entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                            }
                        } else {
                            LogUtil.i(TAG, "正在关闭蓝牙");
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @SuppressLint("NewApi")
    @TargetApi(JELLY_BEAN_MR2)
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            LogUtil.i(TAG, "1 gatt Characteristic" + uuid);
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            if (uuid.contains("1801")) {
                SampleGattAttributes.mGatts.put("1801", uuid);
            } else if (uuid.contains("1805")) {//时间
                SampleGattAttributes.mGatts.put("1805", uuid);
            } else if (uuid.contains("1806")) {//IO服务
                SampleGattAttributes.mGatts.put("1806", uuid);
            } else if (uuid.contains("1807")) {//时间
                SampleGattAttributes.mGatts.put("1807", uuid);
            } else if (uuid.contains("1808")) {//IO服务
                SampleGattAttributes.mGatts.put("1808", uuid);
            }
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                LogUtil.i(TAG, "2 gatt Characteristic: " + uuid + " 权限" + gattCharacteristic.getPermissions() + " Descriptors" + gattCharacteristic.getDescriptors() + " Properties" + gattCharacteristic.getProperties());
                LogUtil.i(TAG, "读写权限  读" + (gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) + "  写" + (gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) + "  " + (gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE));
                //gattCharacteristicGroupData.add(currentCharaData);
                if (uuid.contains("2a18")) {//配置蓝牙
                    gatt2a18 = gattCharacteristic;
                    SampleGattAttributes.mGatts.put("2a18", uuid);
                } else if (uuid.contains("2a52")) {
                    gatt2a52 = gattCharacteristic;
                    SampleGattAttributes.mGatts.put("2a52", uuid);
                } else if (uuid.contains("2a2b")) {
                    gatt2a2b = gattCharacteristic;
                    SampleGattAttributes.mGatts.put("2a2b", uuid);
                } else if (uuid.contains("2a11")) {
                    gatt2a11 = gattCharacteristic;
                    SampleGattAttributes.mGatts.put("2a11", uuid);
                } else if (uuid.contains("2a16")) {
                    gatt2a16 = gattCharacteristic;
                    SampleGattAttributes.mGatts.put("2a16", uuid);
                } else if (uuid.contains("2a17")) {
                    gatt2a17 = gattCharacteristic;
                    SampleGattAttributes.mGatts.put("2a17", uuid);
                } else if (uuid.contains("2a05")) {
                    gatt2a05 = gattCharacteristic;
                    SampleGattAttributes.mGatts.put("2a05", uuid);
                }
            }
        }
        //2a18
        setCharacteristicNotification(gatt2a18, true);
        readCharacteristic(gatt2a18);
        //2a52
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setCharacteristicNotification(gatt2a52, true);
                readCharacteristic(gatt2a52);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mConnectStatus = STATUS_BLUTTOOTH_CMD;
                        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                        }
                    }
                }, 500);
            }
        }, 1000);
        //2a11
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                readCharacteristic(gatt2a11);
            }
        }, 2000);
        //2a2b
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setCharacteristicNotification(gatt2a2b, true);
                //同步时间
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readCharacteristic(gatt2a2b);
                        syncTimeData();
                    }
                }, 500);
            }
        }, 3000);
    }

    public void analysisData(String bData) {//解析数据
        LogUtil.i(TAG, "获得数据  " + bData);
        mHandler.removeMessages(UPLOAD_TIMECOUNT);
        mUploadTimeCount = UPLOAD_TIMEOUT_COUNT;
        mHandler.sendEmptyMessageDelayed(UPLOAD_TIMECOUNT, 1000);
        try {
            if (bData.substring(bData.length() - 4, bData.length() - 3).equals("F") && bData.length() == 26) {
                float val = Integer.parseInt(bData.substring(bData.length() - 6, bData.length() - 4), 16);
                int year = Integer.parseInt(bData.substring(8, 10) + bData.substring(6, 8), 16);
                int month = Integer.parseInt(bData.substring(10, 12), 16);
                int day = Integer.parseInt(bData.substring(12, 14), 16);
                int hour = Integer.parseInt(bData.substring(14, 16), 16);
                int minute = Integer.parseInt(bData.substring(16, 18), 16);
                int s = Integer.parseInt(bData.substring(18, 20), 16);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, s);
                LogUtil.i("测量时间", calendar.getTimeInMillis() + "");
                float testValue = val / 10;
                LogUtil.i(TAG, "测量结果为：" + testValue + "mmol/L");
                for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                    entry.getValue().result_data(String.valueOf(testValue), calendar.getTimeInMillis());
                }
                //这里处理存入数据库
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTime() {
        LogUtil.i(TAG, "命令开始");
        mConnectStatus = STATUS_BLUTTOOTH_CMD;
        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
        }
        LogUtil.e("发送命令 getTime", mBluetoothGatt + "");
        if (mConnectionState == 2) {
            LogUtil.e(TAG, "发送指令   蓝牙" +
                    mBluetoothGatt.getDevice().getName() + "  地址" +
                    mBluetoothGatt.getDevice().getAddress());
            BluetoothGattService gattService = mBluetoothGatt
                    .getService(UUID.fromString(SampleGattAttributes.mGatts.get("1805")));
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.mGatts.get("2a2b")));
//            characteristic.setValue(hexStr2ByteArray("01"));
            LogUtil.e("发送命令" + characteristic.getUuid(), mBluetoothGatt.readCharacteristic(characteristic) + "");
        }
    }

    public void getMaxTestNum() {
        LogUtil.i(TAG, "命令开始");
        mConnectStatus = STATUS_BLUTTOOTH_CMD;
        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
        }
        LogUtil.e("发送命令 MaxTestNum", mBluetoothGatt + "");
        if (mConnectionState == 2) {
            LogUtil.e(TAG, "发送指令   蓝牙" +
                    mBluetoothGatt.getDevice().getName() + "  地址" +
                    mBluetoothGatt.getDevice().getAddress());
            BluetoothGattService gattService = mBluetoothGatt
                    .getService(UUID.fromString(SampleGattAttributes.mGatts.get("1808")));
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.mGatts.get("2a52")));
            characteristic.setValue(new byte[]{0x05, 0x01});
            LogUtil.e("发送命令" + characteristic.getUuid(), mBluetoothGatt.writeCharacteristic(characteristic) + "");
        }
    }

    public void syncHistoryData() {
        LogUtil.i(TAG, "命令开始");
        mConnectStatus = STATUS_BLUTTOOTH_CMD;
        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
        }
        //获取历史数据设置标记
        LogUtil.i(TAG, "同步开始");
        mConnectStatus = STATUS_BLUTTOOTH_UPLOAD;
        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
        }
//        UserSharedData.getInstance(mContext).saveYcTestSugarGetrHistory(true);
        //上传倒计时
        mHandler.removeMessages(UPLOAD_TIMECOUNT);
        mUploadTimeCount = UPLOAD_TIMEOUT_COUNT;
        mHandler.sendEmptyMessageDelayed(UPLOAD_TIMECOUNT, 1000);
        LogUtil.e("发送命令 同步历史数据", mBluetoothGatt + "");
        if (mConnectionState == 2) {
            LogUtil.e(TAG, "发送指令   蓝牙" +
                    mBluetoothGatt.getDevice().getName() + "  地址" +
                    mBluetoothGatt.getDevice().getAddress());
            BluetoothGattService gattService = mBluetoothGatt
                    .getService(UUID.fromString(SampleGattAttributes.mGatts.get("1808")));
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.mGatts.get("2a52")));
            characteristic.setValue(new byte[]{0x01, 0x01});
            LogUtil.e("发送命令" + characteristic.getUuid(), mBluetoothGatt.writeCharacteristic(characteristic) + "");
        }
    }

    public void syncTimeData() {
        LogUtil.i(TAG, "命令开始");
        mConnectStatus = STATUS_BLUTTOOTH_CMD;
        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
        }
        syncTime();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCH_TIMEOUT://搜索倒计时
                    if (mSearchingCount != 0) {
                        LogUtil.i(TAG, "搜索倒计时" + mSearchingCount + "");
                        --mSearchingCount;
                        mHandler.sendEmptyMessageDelayed(SEARCH_TIMEOUT, 1000);
                    } else {
                        if (getCurrentDeviceSousuo() != null) {
                            stopSearch2();
                        } else {
                            stopSearch();
                            LogUtil.i(TAG, "搜索倒计时失败");
                            mConnectStatus = STATUS_BLUTTOOTH_SEARCHING_ERROR;
                            for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                                entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                            }
                            mHandler.removeMessages(SEARCH_TIMEOUT);
                        }
                    }
                    break;
                case CONNECT_TIMEOUT://连接倒计时
                    if (mConnencttingCount != 0) {
                        LogUtil.i(TAG, "连接倒计时" + mConnencttingCount + "");
                        --mConnencttingCount;
                        mHandler.sendEmptyMessageDelayed(CONNECT_TIMEOUT, 1000);
                    } else {
                        disConnect();
                        LogUtil.i(TAG, "连接失败");
                        mConnectStatus = STATUS_BLUTTOOTH_CONNECT_ERROR;
                        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                        }
                        mHandler.removeMessages(CONNECT_TIMEOUT);
                    }
                    break;
                case UPLOAD_TIMECOUNT://上传倒计时
                    if (mUploadTimeCount != 0) {
                        LogUtil.i(TAG, "上传倒计时" + mUploadTimeCount + "");
                        --mUploadTimeCount;
                        mHandler.sendEmptyMessageDelayed(UPLOAD_TIMECOUNT, 1000);
                    } else {
                        LogUtil.i(TAG, "5s没有数据，可以上传了");
                        mHandler.removeMessages(UPLOAD_TIMECOUNT);
                    }
                    break;
                case CMD_TIMEOUT://命令超时
                    if (mCmdTimeCount != 0) {
                        LogUtil.i(TAG, "命令响应倒计时" + mCmdTimeCount + "");
                        --mCmdTimeCount;
                        mHandler.sendEmptyMessageDelayed(CMD_TIMEOUT, 1000);
                    } else {
                        LogUtil.i(TAG, "5s没有回应，命令失败");
                        mHandler.removeMessages(CMD_TIMEOUT);
                        mConnectStatus = STATUS_BLUTTOOTH_CMD_RESULT;
                        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                        }
                    }
                    break;
                case BLE_SCAN:
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    LogUtil.i(TAG, "搜索ing");
                    String bluetoothName = device.getName();
                    LogUtil.i(TAG, "bluetoothName=" + bluetoothName);
                    LogUtil.i(TAG, "uuid=" + device.getUuids());
                    LogUtil.i(TAG, "address=" + device.getAddress());
                    for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                        entry.getValue().bt_searching(device);
                        mBluetoothDeviceSousuo = device;
                    }
                    break;
                case START_BLE_SCAN:
                    LogUtil.i(TAG, "openAndSearch" + mSearchingCount);
                    mBluetoothAdapter.startLeScan(null, mLeScanCallback);
                    break;
            }
        }
    };

    public void disable() {
        if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled()) {
            LogUtil.i(TAG, "蓝牙未开启" + ActivityManager.getInstance().currentActivity());
            mConnectStatus = STATUS_BLUTTOOTH_NOT_OPEN;
            for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
            }
            mBluetoothAdapter.disable();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConnected = false;
        mSearchingCount = 0;
        mConnencttingCount = 0;
        mHandler.removeMessages(SEARCH_TIMEOUT);
        mHandler.removeMessages(CONNECT_TIMEOUT);
        try {
            unregisterReceiver(mStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        disable();
    }

    /*onActivityResult回调*/
    public void resultOpenStatus() {
        LogUtil.i(TAG, "resultOpenStatus");
        if (isSupport()) {
            if (!mBluetoothAdapter.isEnabled()) {
                LogUtil.i(TAG, "蓝牙拒绝打开");
                mConnectStatus = STATUS_BLUTTOOTH_OPEN_NOT_ACCESS;
                for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                    entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                }
            } else {
                LogUtil.i(TAG, "已打开蓝牙" + mConnectStatus);
                if (mConnectStatus <= STATUS_BLUTTOOTH_OPENED) {
                    mConnectStatus = STATUS_BLUTTOOTH_OPENED;
                }
                for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                    entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                }
            }
        }
    }

    public boolean connect(String address) {
        if (isSupport()) {
            if ((null == mBluetoothAdapter) || (null == address)) {
                LogUtil.e(TAG, "BluetoothAdapter未初始化或指定的地址");
                return false;
            }

            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
            if (device == null) {
                LogUtil.e(TAG, "未发现设备");
                return false;
            }
            mBluetoothGatt = device.connectGatt(this, false, this.mGattCallback);
            Log.d(TAG, "试图创建一个新的连接");
            mConnectionState = 1;
            mBluetoothDevice = device;
        } else {
            return false;
        }
        return true;
    }

    public void disConnect() {
        if (isSupport()) {
            if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null)) {
                LogUtil.e(TAG, "BluetoothAdapter未初始化");
                return;
            }
            mBluetoothDevice = null;
            mBluetoothDeviceSousuo = null;
            mBluetoothGatt.disconnect();
        }
    }

    public void close() {
        if (isSupport()) {
            if (mBluetoothGatt == null) {
                return;
            }
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        LogUtil.e(TAG, "readCharacteristic" + characteristic.getUuid());
        if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null)) {
            LogUtil.e(TAG, "BluetoothAdapter未初始化");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null)) {
            LogUtil.e(TAG, "BluetoothAdapter未初始化");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        LogUtil.e(TAG, "setCharacteristicNotification" + characteristic.getUuid().toString());
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        if (enabled) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        } else {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        }
        this.mBluetoothGatt.writeDescriptor(descriptor);
    }

    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = new UUID(45088566677504L, -9223371485494954757L);

    public void setCharacteristicIndication(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null)) {
            LogUtil.e(TAG, "BluetoothAdapter未初始化");
            return;
        }
        this.mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        LogUtil.e(TAG, "setCharacteristicIndication" + characteristic.getUuid().toString());
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        this.mBluetoothGatt.writeDescriptor(descriptor);
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (this.mBluetoothGatt == null) {
            return null;
        }
        return this.mBluetoothGatt.getServices();
    }

    /**
     * 获取当前要链接的设备
     */
    public BluetoothDevice getCurrentDevice() {
        return mBluetoothDevice;
    }

    /**
     * 获取当前搜索到的设备
     */
    public BluetoothDevice getCurrentDeviceSousuo() {
        return mBluetoothDeviceSousuo;
    }

    /**
     * Convert the integer to an unsigned number.
     */
    private static String toHex(int i) {
        char[] buf = new char[32];
        int charPos = 32;
        int radix = 1 << 4;
        int mask = radix - 1;
        do {
            buf[--charPos] = digits[i & mask];
            i >>>= 4;
        } while (i != 0);
        String result = new String(buf, charPos, (32 - charPos));
        return result.length() % 2 != 0 ? ("0" + result) : result;
    }

    /**
     * All possible chars for representing a number as a String
     */
    final static char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    private byte[] hexStr2ByteArray(String hexString) {
        LogUtil.e(TAG, "转换字符" + hexString);
        String[] s = hexString.split(",");
        LogUtil.e(TAG, "转换字符" + s.length);
        int length = 0;
        for (String hex : s) {
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            LogUtil.e(TAG, "转换" + hex);
            length += hex.length() / 2;
        }
        final byte[] total = new byte[length];
        LogUtil.e(TAG, "转换字符byte数组大小" + length);
        int len = 0;
        for (String hex : s) {
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            if (TextUtils.isEmpty(hex))
                throw new IllegalArgumentException("this hexString must not be empty");
            hex = hex.toLowerCase();
            int k = 0;
            LogUtil.e(TAG, "转换每个hex" + hex);
            for (int i = 0; i < hex.length() / 2; i++) {
                //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
                //将hex 转换成byte   "&" 操作为了防止负数的自动扩展
                // hex转换成byte 其实只占用了4位，然后把高位进行右移四位
                // 然后“|”操作  低四位 就能得到 两个 16进制数转换成一个byte.
                //
                byte high = (byte) (Character.digit(hex.charAt(k), 16) & 0xff);
                byte low = (byte) (Character.digit(hex.charAt(k + 1), 16) & 0xff);
                k += 2;
                total[len] = (byte) (high << 4 | low);
                LogUtil.e(TAG, "转换byte" + Integer.valueOf(total[len]));
                len++;
            }
        }
        return total;
    }

    public void deleteLastestData() {
        LogUtil.e("发送命令 deleteLastestData", mBluetoothGatt + "");
        if (mConnectionState == 2) {
            LogUtil.e(TAG, "发送指令   蓝牙" +
                    mBluetoothGatt.getDevice().getName() + "  地址" +
                    mBluetoothGatt.getDevice().getAddress());
            BluetoothGattService gattService = mBluetoothGatt
                    .getService(UUID.fromString(SampleGattAttributes.mGatts.get("1808")));
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.mGatts.get("2a52")));
            characteristic.setValue(new byte[]{0x02, 0x06});
            LogUtil.e("发送命令" + characteristic.getUuid(), mBluetoothGatt.writeCharacteristic(characteristic) + "");
        }
    }

    public void syncTime() {
        //EC 03 0C 05 18 E0 07 0C 15 14 2D 32 03 00 01
        //说明：
        //E0 07--2016年的低位，高位
        //0C ----12月
        //15---- 21日
        //14-----20点
        //2D-----45分
        //32-----50秒
        //03 ----周三
        // 00-----多个  256分之一秒，取值:0-255,可不关心
        // 01-----调整理由，为1  或者3才会更新时间。
        //血糖仪检查收到数据，如果为：EC 03 0C 05 18则认为是设置时间的功能。就把后面E0 07等数据进行解析，并更新血糖仪时间。
        //测试返回值e3033303330000000000

        LogUtil.e("发送命令 同步时间", mBluetoothGatt + "");
        if (mConnectionState == 2) {
            LogUtil.e(TAG, "发送指令   蓝牙" +
                    mBluetoothGatt.getDevice().getName() + "  地址" +
                    mBluetoothGatt.getDevice().getAddress());
            BluetoothGattService gattService = mBluetoothGatt
                    .getService(UUID.fromString(SampleGattAttributes.mGatts.get("1805")));
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(SampleGattAttributes.mGatts.get("2a2b")));
//            characteristic.setValue(hexStr2ByteArray("01,01,EF,0B,09,0A,02,1A"));
            Calendar.getInstance().setTimeZone(TimeZone.getTimeZone("GMT+8"));
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int s = Calendar.getInstance().get(Calendar.SECOND);
            String yearHex = toHex(year);
            LogUtil.e(TAG, "要同步的时间" + yearHex + "   " + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + s);
            characteristic.setValue(hexStr2ByteArray(yearHex.substring(2, yearHex.length()) + yearHex.substring(0, 2) + toHex(month) + toHex(day)
                    + "," + toHex(hour) + toHex(minute) + toHex(s) + "00" + "0001"));
//            characteristic.setValue(hexStr2ByteArray("00,00,01,01,00,00"));
            LogUtil.e("发送命令" + characteristic.getUuid(), mBluetoothGatt.writeCharacteristic(characteristic) + "");
        }
    }

    private byte getXor(byte[] datas) {
        byte temp = datas[0];
        for (int i = 1; i < datas.length; i++) {
            temp ^= datas[i];
        }
        return temp;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (newState == 2) {//已连接
                        mConnectionState = 2;
                        mConnected = true;
                        mConnencttingCount = 0;
                        mHandler.removeMessages(CONNECT_TIMEOUT);
                        LogUtil.i(TAG, "已连接");
                        LogUtil.i(TAG, "蓝牙连接成功！----" + (null == getCurrentDevice() ? "未知" : getCurrentDevice().getName()));
                        Log.i("console", "Connected to GATT server.");
                        Log.i("console", "Attempting to start service discovery:" +
                                mBluetoothGatt.discoverServices());
                    } else if (newState == 0) {//断开
                        mConnectionState = 0;
                        Log.i("console", "Disconnected from GATT server.");
                        mConnected = false;
                        LogUtil.i(TAG, "蓝牙断开!");
                        mConnectStatus = STATUS_BLUTTOOTH_DISCONNECT;
                        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                        }
                        reset();
                    }
                }
            });
        }

        public void onServicesDiscovered(BluetoothGatt gatt, final int status) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (status == 0) {//服务发现
                        displayGattServices(getSupportedGattServices());
                        LogUtil.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                        LogUtil.i(TAG, "已连接onServicesDiscovered");
                        mConnectStatus = STATUS_BLUTTOOTH_CONNECTED;
                        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
                            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
                        }
                        mHandler.sendEmptyMessage(BIND);
//                UserSharedData.getInstance(mContext).saveYcTestSugarFirst(true);
//                UserSharedData.getInstance(mContext).saveYcTestSugarGetrHistory(false);
                        stopSearch();
                    } else {
                        LogUtil.e("console", "onServicesDiscovered received: " + status);
                    }
                }
            });
        }

        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e(TAG, "onCharacteristicRead" + characteristic.getUuid() + characteristic.getUuid() + "  " + StringByte.encodeHexStr(characteristic.getValue(), true));
                    if (status == 0) {
                        dataResult(characteristic);
                    }
                }
            });
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e(TAG, "onCharacteristicChanged" + characteristic.getUuid() + "  " + StringByte.encodeHexStr(characteristic.getValue(), true));
                    dataResult(characteristic);
                }
            });
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBluetoothGatt.readCharacteristic(characteristic);
                    LogUtil.e("写入命令", characteristic.getValue().length + "");
                }
            });
        }
    };

    private void dataResult(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x1) != 0) {
            format = 18;
            Log.d("console", "Heart rate format UINT16.");
        } else {
            format = 17;
            Log.d("console", "Heart rate format UINT8.");
        }
        String result = "", uuid = "";
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int heartRate = characteristic.getIntValue(format, 1).intValue();
            Log.d("console", String.format("Received heart rate: %d", new Object[]{Integer.valueOf(heartRate)}));
            result = String.valueOf(heartRate);
            uuid = UUID_HEART_RATE_MEASUREMENT.toString();
        } else {
            byte[] data = characteristic.getValue();
            LogUtil.e(TAG, "数据长度   value" + data.length + "   int" + characteristic.getIntValue(format, 1));
            if ((data != null) && (data.length > 0)) {
                result = StringByte.encodeHexStr(data, false);
                uuid = characteristic.getUuid().toString();
            }
        }
        //可用数据
        mCmdTimeCount = 0;
        mHandler.removeMessages(CMD_TIMEOUT);
        LogUtil.i(TAG, "命令完成");
        mConnectStatus = STATUS_BLUTTOOTH_CMD_RESULT;
        for (Map.Entry<Activity, OnBlueServiceStatusListener> entry : mListeners.entrySet()) {
            entry.getValue().statusUpdate(mConnectStatus, getCurrentDevice());
        }
        if (!TextUtils.isEmpty(uuid)) {
            if (uuid.contains("2a18")) {
                analysisData(result);
            }
        }
    }

    public interface OnBlueServiceStatusListener {

        void statusUpdate(int status, BluetoothDevice bluetoothDevice);//状态更新

        void bt_searching(BluetoothDevice bluetoothDevice);//搜索的设备

        void result_data(String data, long time);//测量结果，包括历史数据都从这里

        void bindSuc();//绑定成功，这里接口给封装进来了

        void bindError(int code, String err);//绑定失败
    }
}
