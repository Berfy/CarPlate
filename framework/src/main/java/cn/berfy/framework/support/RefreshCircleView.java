package cn.berfy.framework.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import cn.berfy.framework.R;
import cn.berfy.framework.common.Constants;

public class RefreshCircleView extends View {

    private int TOTALDEGREE = 360;// 不要完整的园
    private float DEGREE = 0;// 角度 180度作为开始

    private Paint mCirclePaint, mSmallCirclePaint, mLinePaint;
    private int mRadio = 0, mEyeRadio;// 半径,眼睛的半径
    private int mStroke = 6, mSmallStroke = 12;
    private int mColor;
    boolean mIsAnimming, mIsDrawNormal;

    public RefreshCircleView(Context context) {
        super(context);
        init(context);
    }

    public RefreshCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mColor = R.color.white;
        mCirclePaint = new Paint();
        mCirclePaint.setColor(context.getResources().getColor(
                mColor));
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(mStroke);
        mCirclePaint.setStyle(Style.STROKE);

        mSmallCirclePaint = new Paint();
        mSmallCirclePaint.setColor(context.getResources().getColor(
                mColor));
        mSmallCirclePaint.setStrokeWidth(mStroke);
        mSmallCirclePaint.setAntiAlias(true);
        mSmallCirclePaint.setStyle(Style.STROKE);

        mLinePaint = new Paint();
        mLinePaint.setColor(context.getResources().getColor(
                mColor));
        mLinePaint.setStrokeWidth(mStroke);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Style.STROKE);
    }

    public void setColor(int colorId) {
        mColor = colorId;
        if (null != mCirclePaint)
            mCirclePaint.setColor(getContext().getResources().getColor(
                    mColor));
        if (null != mSmallCirclePaint)
            mSmallCirclePaint.setColor(getContext().getResources().getColor(
                    mColor));
        if (null != mLinePaint)
            mLinePaint.setColor(getContext().getResources().getColor(
                    mColor));
    }

    public void startAnim() {
        mIsAnimming = true;
        Constants.EXECUTOR.execute(new Runnable() {

            @Override
            public void run() {
                while (mIsAnimming) {
                    mHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void stopAnim() {
        mIsAnimming = false;
        DEGREE = 360;
    }

    public void setProgress(float percent) {
        if (percent > 1) {
            percent = 1;
        }
        DEGREE = (float) TOTALDEGREE * percent;
        // LogUtil.e("设置进度", percent + "");
        postInvalidate();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            postInvalidate();
        }

        ;
    };

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.save();
        int width = getWidth();
        if (mRadio == 0) {// 半径
            mRadio = width / 2 - mSmallStroke;
            mEyeRadio = mRadio / 6;
        }
        int circle = width / 2;
        RectF rectF = new RectF();
        rectF.top = circle - mRadio;
        rectF.left = circle - mRadio;
        rectF.right = circle + mRadio;
        rectF.bottom = circle + mRadio;
        RectF srectF = new RectF();
        srectF.top = circle - mRadio + 15;
        srectF.left = circle - mRadio + 15;
        srectF.right = circle + mRadio - 15;
        srectF.bottom = circle + mRadio - 15;
        int degree = (int) (DEGREE * ((DEGREE * 0.1) / (36)));
        float nsx, nsy;
        if (mIsAnimming) {
            nsx = (float) (circle + mRadio
                    * Math.cos(2 * Math.PI / 360 * DEGREE));
            nsy = (float) (circle + mRadio
                    * Math.sin(2 * Math.PI / 360 * DEGREE));
            canvas.drawArc(rectF, 0, 360, false, mCirclePaint);
        } else {
            nsx = (float) (circle + mRadio
                    * Math.cos(2 * Math.PI / 360 * degree));
            nsy = (float) (circle + mRadio
                    * Math.sin(2 * Math.PI / 360 * degree));
            canvas.drawArc(rectF, 0, degree, false, mCirclePaint);
        }
        if (degree == 360) {
            canvas.drawArc(srectF, 40, 100, false, mLinePaint);
        }
        canvas.drawCircle(circle - mEyeRadio * 2, circle - mEyeRadio, mEyeRadio, mSmallCirclePaint);
        canvas.drawCircle(circle + mEyeRadio * 2, circle - mEyeRadio, mEyeRadio, mSmallCirclePaint);
//        canvas.drawCircle(nsx, nsy, SMALL_STOKE, mSmallCirclePaint);
        canvas.restore();
    }
}
