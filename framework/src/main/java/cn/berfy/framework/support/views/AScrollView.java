package cn.berfy.framework.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class AScrollView extends HorizontalScrollView {

	private OnScrollListener mOnScrollListener;

	public AScrollView(Context context) {
		super(context);
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		mOnScrollListener = onScrollListener;
	}

	public AScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		mOnScrollListener.onScroll(l);
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public interface OnScrollListener {
		void onScroll(int x);
	}

}
