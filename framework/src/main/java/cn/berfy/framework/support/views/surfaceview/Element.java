package cn.berfy.framework.support.views.surfaceview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.BufferedInputStream;

import cn.berfy.framework.R;

public class Element {
    private float mX;
    private float mY;

    private Bitmap mBitmap;

    @SuppressWarnings("ResourceType")
    public Element(Resources res, int x, int y) {
        mBitmap = BitmapFactory.decodeStream(new BufferedInputStream(res.openRawResource(R.mipmap.ic_launcher)));
        mX = x - mBitmap.getWidth() / 2;
        mY = y - mBitmap.getHeight() / 2;
    }

    public void doDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, mX, mY, null);
    }
}
