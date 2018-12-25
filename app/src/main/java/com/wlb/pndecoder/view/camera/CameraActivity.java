package com.wlb.pndecoder.view.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aiseminar.EasyPR.PlateRecognizer;
import com.aiseminar.util.BitmapUtil;
import com.aiseminar.util.FileUtil;
import com.wlb.pndecoder.R;
import com.wlb.pndecoder.common.Constants;
import com.wlb.pndecoder.view.TitleBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.utils.AppUtil;
import cn.berfy.framework.utils.Camera2Util;
import cn.berfy.framework.utils.CheckUtil;
import cn.berfy.framework.utils.DeviceUtil;
import cn.berfy.framework.utils.LogUtil;
import cn.berfy.framework.utils.ToastUtil;
import cn.berfy.framework.utils.ViewUtils;

public class CameraActivity extends BaseActivity {

    @BindView(R.id.svCamera)
    SurfaceView mSvCamera;
    @BindView(R.id.ivPlateRect)
    ImageView mIvPlateRect;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    @BindView(R.id.toggle_btn)
    ToggleButton mTgBtn;
    Camera2BasicFragment mCamera2BasicFragment;
    private int num;

    private static final String TAG = CameraActivity.class.getSimpleName();

    private Camera2Util mCameraUtil;
    private PlateRecognizer mPlateRecognizer;

    private boolean mIsCanDecode = true;

    private final int TYPE_SCANNING = 2;//识别ing
    private final int TYPE_TAKE_PHOTO = 0;//定时预览
    private final int TYPE_SUC = 1;//识别成功
    private final int TYPE_CAR_NUMBER_ERROR = -1;//车牌格式不正确
    private final int TYPE_NO_BEIJING = -2;//不是北京车牌
    private final int TYPE_NO_FIND = -3;//没有车牌信息
    private final int TYPE_ERROR = -4;//其他错误

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariable() {
        mCameraUtil = new Camera2Util(mContext, new Camera2Util.OnStatusListener() {
            @Override
            public void takePhoto(byte[] bytes, int width, int height) {
                LogUtil.e(TAG, "预览数据");
                doBytes(bytes, width, height);
            }

            @Override
            public void onOpened() {
                mIsCanDecode = true;
                mHandler.sendEmptyMessage(TYPE_TAKE_PHOTO);
            }
        });
    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {
        mTitleBar.showLeft(true, this);
        mTitleBar.setTitle(getString(R.string.title_takephoto));
        mTgBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCameraUtil.isCamera2()) {
                    mCamera2BasicFragment.checkLight(isChecked);
                } else {
                    mCameraUtil.checkLight(isChecked);
                }
            }
        });
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mIvPlateRect.getLayoutParams();
        layoutParams.width = ViewUtils.getScreenWidth(mContext) / 2;
        layoutParams.height = layoutParams.width / 3;
        mIvPlateRect.setLayoutParams(layoutParams);
        if (DeviceUtil.selfPermissionGranted(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA}, 0)) {
            init();
        }
    }

    @Override
    protected void doClickEvent(int viewId) {
        switch (viewId) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isOk = false;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                isOk = true;
            } else {
                isOk = false;
                ToastUtil.getInstance().showToast("您拒绝了权限");
            }
        }
        if (isOk)
            switch (requestCode) {
                case 0:
                    if (null == mPlateRecognizer) {
                        Log.e("CameraActivity", "权限修改");
                        init();
                    }
                    break;
            }
    }

    private void init() {
        mPlateRecognizer = new PlateRecognizer(this);
        if (mCameraUtil.isCamera2()) {
            findViewById(R.id.svCamera).setVisibility(View.GONE);
            findViewById(R.id.fragment_camera2).setVisibility(View.VISIBLE);
            mCamera2BasicFragment = Camera2BasicFragment.newInstance();
            mCamera2BasicFragment.setOnResultListener(new Camera2BasicFragment.OnResultListener() {
                @Override
                public void onResult(byte[] bytes, int width, int height) {
                    doBytes(bytes, width, height);
                }
            });
            getFragmentManager().beginTransaction().replace(R.id.fragment_camera2, mCamera2BasicFragment).commit();
            mIsCanDecode = true;
            mHandler.sendEmptyMessage(TYPE_TAKE_PHOTO);
        } else {
            findViewById(R.id.svCamera).setVisibility(View.VISIBLE);
            findViewById(R.id.fragment_camera2).setVisibility(View.GONE);
            mCameraUtil.init(mSvCamera);
        }
    }

    private byte[] mData;

    private void doBytes(byte[] data, final int width, final int height) {
        mData = data;
        if (mIsCanDecode) {
            Constants.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mIsCanDecode && !isFinishing() && getActivityState() == BaseActivity.ACTIVITY_RESUME) {
                            YuvImage image = new YuvImage(mData, ImageFormat.NV21, width, height, null);
                            ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
                            // 转Bitmap
                            if (!image.compressToJpeg(new Rect(0, 0, width, height), 100, os)) {
                                return;
                            }
                            byte[] tmp = os.toByteArray();
                            os.close();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
                            Bitmap rotatedBitmap = BitmapUtil.createRotateBitmap(bitmap);
                            bitmap.recycle();
                            cropBitmapAndRecognize(rotatedBitmap);
                            System.gc();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraUtil.initCamera(false);
        mCameraUtil.addCallBack();
        mCameraUtil.startDisplay();
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         * 记得释放camera，方便其他应用调用
         */
        mCameraUtil.releaseCamera();
        mIsCanDecode = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object o = msg.obj;
            switch (msg.what) {
                case TYPE_SUC:
                    if (null != o) {
                        Intent intent = new Intent();
                        intent.putExtra("data", o.toString());
                        setResult(100, intent);
                        finish();
                        mIsCanDecode = false;
                    }
                    break;
                case TYPE_SCANNING:
                    mTvTip.setText("正在识别" + System.currentTimeMillis());
                    break;
                case TYPE_TAKE_PHOTO:
                    if (mCameraUtil.isCamera2()) {
                        mCamera2BasicFragment.takePicture();
                    } else {
                        mCameraUtil.takePhoto();
                    }
                    break;
                case TYPE_CAR_NUMBER_ERROR://车牌不正确
                    mTvTip.setText("车牌不正确");
//                    num++;
//                    mTvTip.setText("识别错误，车牌不正确 bitmap时间" + time1 + " 解析时间" + time2 + "  " + +System.currentTimeMillis());
//                    ToastUtil.getInstance().showToast("识别错误，车牌不正确" + "  第" + num + "次", Toast.LENGTH_SHORT);
                    mHandler.sendEmptyMessage(TYPE_TAKE_PHOTO);
                    break;
                case TYPE_NO_BEIJING://不是京牌
                    mTvTip.setText("不是京牌");
//                    num++;
//                    mTvTip.setText("识别错误，不是京牌");
//                    ToastUtil.getInstance().showToast("识别错误，不是京牌" + "  第" + num + "次", Toast.LENGTH_SHORT);
                    mHandler.sendEmptyMessage(TYPE_TAKE_PHOTO);
                    break;
                case TYPE_NO_FIND://没有车牌信息
                    num++;
//                    mTvTip.setText("识别错误，没有车牌信息 bitmap时间" + time1 + " 解析时间" + time2 + "  " + +System.currentTimeMillis());
//                    ToastUtil.getInstance().showToast("识别错误，没有车牌信息" + "  第" + num + "次", Toast.LENGTH_SHORT);
                    mHandler.sendEmptyMessage(TYPE_TAKE_PHOTO);
                    break;
                case TYPE_ERROR://异常
//                    num++;
//                    mTvTip.setText("识别错误，异常 bitmap时间" + time1 + " 解析时间" + time2 + "  " + +System.currentTimeMillis());
//                    ToastUtil.getInstance().showToast("识别错误，异常" + "  第" + num + "次", Toast.LENGTH_SHORT);
                    mHandler.sendEmptyMessage(TYPE_TAKE_PHOTO);
                    break;
            }
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                mIsCanDecode = false;
                LogUtil.e(TAG, "退出");
                doBackAction();
                break;
            case 999:
                mCameraUtil.tabCamera();
                break;
        }
    }

    public void cropBitmapAndRecognize(Bitmap rotatedBitmap) {
        LogUtil.e(TAG, "cropBitmapAndRecognize   是否主线程" + AppUtil.isMainThread());
        if (mIsCanDecode && !isFinishing() && getActivityState() == BaseActivity.ACTIVITY_RESUME) {
            // 裁剪出关注区域
            long time1 = System.currentTimeMillis();
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            Bitmap sizeBitmap = Bitmap.createScaledBitmap(rotatedBitmap, metric.widthPixels, metric.heightPixels, true);
            rotatedBitmap.recycle();
            int maxWidth = sizeBitmap.getWidth();
            int maxHeight = sizeBitmap.getHeight();
            int rectWidth = mIvPlateRect.getWidth();
            int rectHeight = (int) (mIvPlateRect.getHeight());
            int[] location = new int[2];
            mIvPlateRect.getLocationOnScreen(location);
            location[1] -= DeviceUtil.getStatusBarHeight(mContext);
            location[0] = location[0] < 0 ? 0 : location[0];
            location[1] = location[1] < 0 ? 0 : location[1];
            LogUtil.e(TAG, "大小宽高" + maxWidth + "," + maxHeight + "   " + location[0] + " " + location[1] + "  " + rectWidth + "  " + rectHeight);
            LogUtil.e(TAG, "正在识别");
            int left = location[0] - 100;
            int width = rectWidth + 200;
            int top = location[1] - 50;
            int height = rectHeight + 50;
            int bigWidth = (int) (rectWidth * 1.5), bigHeight = height;
            LogUtil.e(TAG, "图片位置 left=" + left + ",right=" + width + ",top=" + top + ",bottom=" + height);
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f, 0.5f);
            final Bitmap normalBitmap = Bitmap.createBitmap(sizeBitmap, left, top, width, height, matrix, false);
            sizeBitmap.recycle();
            time1 = System.currentTimeMillis() - time1;
            Bitmap tempBitmap = Bitmap.createBitmap(bigWidth, bigHeight, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(tempBitmap);
            Rect rect1 = new Rect(0, 0, width, height);
            Rect rect2 = new Rect(0, 0, width, height);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawColor(getResources().getColor(R.color.white));
            canvas.drawBitmap(normalBitmap, rect1, rect2, paint);
            normalBitmap.recycle();
            long time2 = System.currentTimeMillis();
            Message message = new Message();
            message.what = TYPE_SCANNING;
            mHandler.sendMessage(message);
            message = new Message();
            if (null != tempBitmap) {
                try {
                    // 保存图片并进行车牌识别
                    File pictureFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_PLATE);
                    if (pictureFile == null) {
                        Log.d(TAG, "Error creating media file, check storage permissions: ");
                        return;
                    }

                    // 进行车牌识别
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    tempBitmap.recycle();
                    fos.close();
                    // 最后通知图库更新
//                        CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + pictureFile.getAbsolutePath())));
                    String plate = mPlateRecognizer.recognize(pictureFile.getAbsolutePath());
                    time2 = System.currentTimeMillis() - time2;
                    message.obj = (float) (time1 * 100 / 1000 * 0.01) + "," + (float) (time2 * 100 / 1000 * 0.01);
                    LogUtil.e(TAG, "识别结果  " + plate + "   " + time2);
                    if (null != plate && !plate.equalsIgnoreCase("0")) {
                        String province = plate.substring(3, 4);
                        LogUtil.e(TAG, "识别结果  " + province + "    " + plate);
                        if (province.contains("京")) {
                            plate = plate.substring(3, plate.length());
                            if (CheckUtil.isCarNumber(plate)) {
                                message.what = TYPE_SUC;
                                message.obj = plate;
                                mHandler.sendMessage(message);
                            } else {
                                message.what = TYPE_CAR_NUMBER_ERROR;
                                mHandler.sendMessage(message);
                                LogUtil.e(TAG, "识别错误，不是车牌");
                            }
                        } else {
                            message.what = TYPE_NO_BEIJING;
                            mHandler.sendMessage(message);
                            LogUtil.e(TAG, "识别错误，不是京牌");
                        }
                    } else {
                        LogUtil.e(TAG, "识别错误，没发现车牌信息");
                        message.what = TYPE_NO_FIND;
                        mHandler.sendMessage(message);
                    }
                    pictureFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Exception: " + e.getMessage());
                    LogUtil.e(TAG, "识别错误");
                    message.what = TYPE_ERROR;
                    mHandler.sendMessage(message);
                }
            } else {
                LogUtil.e(TAG, "识别错误 bitmap = null");
                message.what = TYPE_ERROR;
                mHandler.sendMessage(message);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mIsCanDecode = false;
            LogUtil.e(TAG, "返回键");
            doBackAction();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
