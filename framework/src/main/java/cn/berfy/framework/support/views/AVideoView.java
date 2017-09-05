package cn.berfy.framework.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Berfy on 2016/1/25.
 */
public class AVideoView extends VideoView {

    private Context mContext;

    public AVideoView(Context context) {
        super(context);
        mContext = context;
    }

    public AVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(100, widthMeasureSpec);
        int height = getDefaultSize(100, heightMeasureSpec);
        setMeasuredDimension(width,height);
    }
}
