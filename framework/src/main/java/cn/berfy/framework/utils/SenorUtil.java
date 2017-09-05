package cn.berfy.framework.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Vibrator;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Berfy on 2016/11/28.
 * 陀螺仪相关逻辑工具
 */
public class SenorUtil implements SensorEventListener {

    private Context mContext;
    private Handler mHandler = new Handler();
    private int mCount;//晃动次数
    private boolean mIsCanYaoyiyao = true;
    //Sensor管理器
    private SensorManager mSensorManager;
    //震动
    private Vibrator mVibrator;

    private OnSenorListener mOnSenorListener;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mIsCanYaoyiyao = true;
        }
    };

    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_SHRESHOLD = 3000;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 70;
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;

    public SenorUtil(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
    }

    public void setListener(OnSenorListener onSenorListener) {
        mOnSenorListener = onSenorListener;
    }

    public void start() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //摇一摇算法
        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPTATE_INTERVAL_TIME)
            return;
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;
        // 获得x,y,z坐标
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / timeInterval * 10000;
        // 达到速度阀值，发出提示
        if (speed >= SPEED_SHRESHOLD) {
            if (mIsCanYaoyiyao) {
                mCount++;
                if (mCount == 2) {
                    mCount = 0;
                    mIsCanYaoyiyao = false;
                    mVibrator.vibrate(100);
                    mHandler.postDelayed(mRunnable, 1000);
                    if (null != mOnSenorListener) {
                        mOnSenorListener.shake();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface OnSenorListener {
        void shake();
    }
}
