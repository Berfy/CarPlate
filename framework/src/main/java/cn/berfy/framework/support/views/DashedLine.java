package cn.berfy.framework.support.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import cn.berfy.framework.R;

/**
 * 虚线
 */
public class DashedLine extends View {
    private Paint p;
    private int width;
    private int height;
    private int dash;

    public DashedLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DashedLine(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);
        p.setColor(context.getResources().getColor(R.color.white));

        final float scale = context.getResources().getDisplayMetrics().density;
        dash = (int) (4 * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width > 10) {
            for (int i = 0; i < width; i += dash) {
                canvas.drawLine(i, 0, i += dash, 0, p);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        p.setStrokeWidth(height);
        this.postInvalidate();
    }
}
