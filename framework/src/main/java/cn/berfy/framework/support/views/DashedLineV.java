package cn.berfy.framework.support.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import cn.berfy.framework.R;
import cn.berfy.framework.utils.LogUtil;

/**
 * 虚线
 */
public class DashedLineV extends View {
    private Paint p;
    private int width;
    private int height;
    private int dash;

    public DashedLineV(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.dashLine);
        int textColor = a.getColor(R.styleable.dashLine_dash_color,
                getResources().getColor(R.color.white));
        p.setColor(textColor);
    }

    public DashedLineV(Context context) {
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
//        if (height > dash) {
        for (int i = 0; i < height; i += dash) {
            LogUtil.e("画虚线", i + "");
            canvas.drawLine(0, i, 0, i += dash, p);
        }
//        }
        super.onDraw(canvas);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        p.setStrokeWidth(width);
        this.postInvalidate();
    }
}
