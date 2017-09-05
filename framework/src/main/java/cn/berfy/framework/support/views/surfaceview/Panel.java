package cn.berfy.framework.support.views.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import cn.berfy.framework.utils.LogUtil;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

    private ArrayList<Element> mElements = new ArrayList<Element>();
    private int mElementNumber = 0;

    private Paint mPaint = new Paint();
    private String mScreenshotPath = Environment.getExternalStorageDirectory() + "/droidnova";

    public Panel(Context context) {
        super(context);
        init();
    }

    public Panel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public Panel(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init();
    }

    private void init() {
//        mSurfaceHolder = this.getHolder();
//        mSurfaceHolder.addCallback(this);
        mPaint.setColor(Color.WHITE);
//        mThread = new ViewThread(this);
    }

    public void remeveCallBack() {
        getHolder().removeCallback(this);
    }

    public void doDraw(long elapsed, Canvas canvas) {
//        canvas.drawColor(Color.BLACK);
        LogUtil.e("截图画画","fafaf");
        synchronized (mElements) {
            for (Element element : mElements) {
                element.doDraw(canvas);
            }
        }
//        canvas.drawText("FPS: " + Math.round(1000f / elapsed) + " Elements: " + mElementNumber, 10,
//                10, mPaint);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (getHolder().getSurface() == null) {
            // preview surface does not exist
            return;
        }
//        if (null != mOnSufaceViewCallBack) {
//            mOnSufaceViewCallBack.onChanged();
//        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        if (!mThread.isAlive()) {
//            mThread = new ViewThread(this);
//            mThread.setRunning(true);
//            mThread.start();
//        }
//        if (null != mOnSufaceViewCallBack) {
//            mOnSufaceViewCallBack.onCreated();
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        if (mThread.isAlive()) {
//            mThread.setRunning(false);
//        }
//        if (null != mOnSufaceViewCallBack) {
//            mOnSufaceViewCallBack.onDestroyed();
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (mElements) {
            mElements.add(new Element(getResources(), (int) event.getX(), (int) event.getY()));
            mElementNumber = mElements.size();
        }
        return super.onTouchEvent(event);
    }

    /**
     * If called, creates a screenshot and saves it as a JPG in the folder "droidnova" on the sdcard.
     */
    public Bitmap saveScreenshot(String path) {
//        if (FileUtils.createFolder(path)) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        doDraw(1, canvas);
//            File file = new File(path);
//            FileOutputStream fos;
//            try {
//                fos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.close();
//            } catch (FileNotFoundException e) {
//                Log.e("Panel", "FileNotFoundException", e);
//            } catch (IOException e) {
//                Log.e("Panel", "IOEception", e);
//            }
//        }
        return bitmap;
    }
}
