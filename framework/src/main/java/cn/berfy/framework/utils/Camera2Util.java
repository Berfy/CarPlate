package cn.berfy.framework.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Berfy on 2017/6/16.
 * 控制闪光灯
 */

public class Camera2Util {

    private final String TAG = "Camera2Util";
    private Context mContext;
    private CameraManager mManager;
    private boolean mIsOpen;
    private Camera mCamera;
    private CameraDevice mCameraDevice;
    private Camera.CameraInfo mCameraInfo;
    private Camera.Parameters mParameters;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private int mCameraPosition = 0; // 0表示后置，1表示前置
    private OnStatusListener mOnStatusListener;
    private int mScanType;//0车牌1二维码

    @TargetApi(25)
    public Camera2Util(Context context, OnStatusListener onStatusListener) {
        mContext = context;
        mOnStatusListener = onStatusListener;
        mManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        if (isCamera2()) {
            try {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    mManager.openCamera("0", new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            mCameraDevice = camera;
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                            camera.close();
                            mCameraDevice = null;
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                            camera.close();
                        }

                        @Override
                        public void onClosed(CameraDevice camera) {
                            //相机完全关闭时回调此方法
                            super.onClosed(camera);
                        }
                    }, new Handler() {
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            initCamera(false);
        }
    }

    /**
     * @param scanType 0车牌 1二维码
     */
    public void setScanType(int scanType) {
        mScanType = scanType;
    }

    /**
     * @param isFront 是否打开前置
     */
    public void initCamera(boolean isFront) {
        LogUtil.e(TAG, "initCamera " + isFront + "  " + mSurfaceHolder);
        if (checkCameraHardware() && (mCamera == null)) {
            // 打开camera
            Camera camera = null;
            try {
                if (isFront) {
                    int cameraCount = 0;
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
                    for (int i = 0; i < cameraCount; i++) {
                        Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            mCamera = Camera.open(i);
                            break;
                        }
                    }
                } else {
                    mCamera = Camera.open();
                }
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                int width = ViewUtils.getScreenWidth(mContext);
                int height = ViewUtils.getScreenHeight(mContext);
                int finalW = 0, finalH = 0;
                for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                    if (size.width / size.height == height / width) {
                        LogUtil.e(TAG, "支持的预览尺寸 " + size.width + "," + size.height);
                        if (size.width > finalH) {
                            finalH = size.width;
                            finalW = size.height;
                        }
                    }
                }
                parameters.setPreviewSize(finalH, finalW);
                parameters.setPictureSize(finalH, finalW);
//                if (null != mSurfaceView) {
                RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
                layoutParams1.height = finalH - ViewUtils.dip2px(mContext, 50);
                layoutParams1.width = layoutParams1.height * finalW / finalH;
                mSurfaceView.setLayoutParams(layoutParams1);
//                }
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
                if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
                    setDisplayOrientation(mCamera, 90);
                } else {
                    parameters.setRotation(90);
                }
                mCamera.setParameters(parameters);
                mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                // 设置camera方向
                mCameraInfo = getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK);
                if (null != mCameraInfo) {
                    adjustCameraOrientation();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Camera is not available (in use or does not exist)
                mCamera = null;
                Log.e(TAG, "Camera is not available (in use or does not exist)");
            }
        }
    }

    /**
     * 初始化相关data
     */

    public void init(SurfaceView surfaceView) {
        LogUtil.e(TAG, "init " + surfaceView);
        mSurfaceView = surfaceView;
        initCamera(false);
        addCallBack();
    }

    public void addCallBack() {
        // 获得句柄
        mSurfaceHolder = mSurfaceView.getHolder(); // 获得句柄
        // 添加回调
        mSurfaceHolder.addCallback(mCallback);
    }

    private int mMaxNum = 0;

    public void takePhoto() {//通过预览获取实时图像代替拍照
        try {
            if (null != mCamera) {
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        if (null != mOnStatusListener) {
                            mMaxNum++;
                            Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
                            final int w = size.width;  //宽度
                            final int h = size.height;
                            switch (mScanType) {
                                case 0:
                                    if (mMaxNum >= 6) {
                                        mMaxNum = 0;
                                        mCamera.setPreviewCallback(null);
                                    }
                                    mOnStatusListener.takePhoto(data, w, h);
                                    break;
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.e(TAG, "surfaceCreated");
            if (null != mOnStatusListener) {
                mOnStatusListener.onOpened();
            }
//            if (!mThread.isAlive()) {
//                mThread = new ViewThread(mSurfaceView);
//                mThread.setRunning(true);
//                mThread.start();
//            }
            startDisplay();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.e(TAG, "surfaceChanged");
            if (mSurfaceHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            // stop preview before making changes
            try {
                stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            startDisplay();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.e(TAG, "surfaceDestroyed");
//            if (mThread.isAlive()) {
//                mThread.setRunning(false);
//            }
            // 当surfaceview关闭时，关闭预览并释放资源
            /**
             * 记得释放camera，方便其他应用调用
             */
            releaseCamera();
        }
    };

    public Camera getCamera() {
        return mCamera;
    }

    public void stopPreview() {
        if (null == mCamera) {
            return;
        }
        mCamera.stopPreview();
    }

    public boolean isOpen() {
        return mIsOpen;
    }


    @TargetApi(25)
    /** 打开关闭闪光灯*/
    public void checkLight(boolean isOn) {
        LogUtil.e(TAG, "checkLight   " + isOn);
        try {
            mIsOpen = isOn;
            if (!isCamera2()) {
                if (null == mCamera) {
                    return;
                }
                //低于6.0系统的手电筒
                releaseCamera();
                initCamera(false);
                if (isOn) {
                    mParameters = mCamera.getParameters();
                    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// 开启
                    mCamera.setParameters(mParameters);
                    startDisplay();
                } else {
                    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);// 关闭
                    mCamera.setParameters(mParameters);
                    startDisplay();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCameraPostion() {
        return mCameraPosition;
    }

    public void tabCamera() {
        LogUtil.e(TAG, "tabCamera");
        // 切换前后摄像头
        releaseCamera();
        if (mCameraPosition == 1) {//前置切换后置
            initCamera(false);
        } else {
            initCamera(true);
        }
        // 通过surfaceview显示取景画面
        startDisplay();
        mCameraPosition = mCameraPosition == 0 ? 1 : 0;
    }

    /**
     * 设置camera显示取景画面,并预览
     */
    public void startDisplay() {
        LogUtil.e(TAG, "startDisplay");
        if (null == mCamera || null == mSurfaceHolder) {
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error starting activity_qr_camera preview: " + e.getMessage());
        }
    }

    /**
     * Check if this device has a activity_qr_camera
     */

    private boolean checkCameraHardware() {
        LogUtil.e(TAG, "checkCameraHardware");
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a activity_qr_camera
            return true;
        } else {
            // no activity_qr_camera on this device
            return false;
        }
    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        LogUtil.e(TAG, "setDisplayOrientation");
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }

    private void adjustCameraOrientation() { // 调整摄像头方向
        LogUtil.e(TAG, "adjustCameraOrientation");
        if (null == mCameraInfo || null == mCamera) {
            return;
        }
        int orientation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getOrientation();
        int degrees = 0;

        switch (orientation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        LogUtil.e(TAG, "旋转角度" + degrees);

        int result;
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            // back-facing
            result = (mCameraInfo.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    /**
     * 释放mCamera
     */
    public void releaseCamera() {
        LogUtil.e(TAG, "销毁相机");
        if (null != mCamera) {
            mSurfaceHolder.removeCallback(mCallback);
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.CameraInfo getCameraInfo(int facing) {
        LogUtil.e(TAG, "getCameraInfo");
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return cameraInfo;
            }
        }
        return null;
    }

    public boolean isCamera2() {
//        return DeviceUtil.getSdkversion() >= Build.VERSION_CODES.LOLLIPOP;
        return false;
    }

    public interface OnStatusListener {
        void takePhoto(byte[] data, int width, int height);

        void onOpened();
    }
}
