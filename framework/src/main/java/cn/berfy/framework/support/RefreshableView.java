package cn.berfy.framework.support;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import cn.berfy.framework.R;
import cn.berfy.framework.common.Constants;
import cn.berfy.framework.utils.LogUtil;
import cn.berfy.framework.utils.ViewUtils;

/**
 * 刷新控制view
 *
 * @author Nono
 * @author Berfy 修改 2016.10.13
 * @author Berfy 修改 2016.5.4
 */
public class RefreshableView extends RelativeLayout {

    private static final String TAG = "LILITH";
    private Scroller mScroller;
    private RefreshCircleView mCircleView;
    private View mHeaderView;
    private TextView mTvTip;
    private RefreshListener mRefreshListener;
    private long mRefreshTime;
    private int mRefreshHeight = 80, mMaxHeight = 120;//最大高度
    private final long MIN_REFRESH_TIME = 1000;//最小刷新时间,刷新操作至少1秒，显示美观
    private final static int SCROLL_DURATION = 300;
    private int mLastY;
    // 拉动标记
    private boolean mIsDragging = false;
    // 在刷新中标记
    private boolean mIsRefreshing = false;

    private Context mContext;

    public RefreshableView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        // 滑动对象
        mMaxHeight = ViewUtils.dip2px(mContext, 120);
        mRefreshHeight = ViewUtils.dip2px(mContext, 80);
        mScroller = new Scroller(mContext, new DecelerateInterpolator());

        // 刷新视图顶端的的view
        mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.refresh_listview_head, null);
        mCircleView = (RefreshCircleView) mHeaderView.findViewById(R.id.circleview);
        // 下拉显示text
        mTvTip = (TextView) mHeaderView.findViewById(R.id.head_tipsTextView);
        addHeader();
    }

    private void addHeader() {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        addView(mHeaderView, lp);
    }

    public int getVisiableHeight() {
        return mHeaderView.getHeight();
    }

    public void setVisiableHeight(int height) {
        doMovement(height);
    }

    public void setBgColor(int bgColorId, int circleColorId, int tipColorId) {
        if (null != mHeaderView) {
//            mTvTip.setBackgroundResource(bgColorId);
//            mHeaderView.setBackgroundColor(mContext.getResources().getColor(bgColorId));
            mTvTip.getPaint().setFakeBoldText(true);
            mTvTip.setTextColor(getContext().getResources().getColor(bgColorId));
            mCircleView.setColor(bgColorId);
        }
    }

    public void setBgColor(int bgColorId) {
        if (null != mHeaderView) {
            mTvTip.getPaint().setFakeBoldText(true);
            mTvTip.setTextColor(getContext().getResources().getColor(bgColorId));
            mCircleView.setColor(bgColorId);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录下y坐标
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE" + "  " + mIsRefreshing);
                if (!mIsRefreshing) {
                    // y移动坐标
                    int m = y - mLastY;
                    if (!mIsDragging && (getVisiableHeight() > 0 || m > 0)) {
                        LogUtil.e("距离", getVisiableHeight() + "  " + m);
                        doMovement(getVisiableHeight() + m);
                    }
                    // 记录下此刻y坐标
                    this.mLastY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "ACTION_UP" + "  " + mIsRefreshing);
                if (!mIsRefreshing) {
                    mIsRefreshing = true;
                    fling();
                }
                break;
        }
        return true;
    }

    public void startRefreshing(final int deplayTime) {
        Constants.EXECUTOR_REFRESH.execute(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                try {
                    Thread.sleep(deplayTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (!((Activity) mContext).isFinishing() && !mIsRefreshing) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            doMovement(getVisiableHeight() + 20);
                        }
                    });
                    if (getVisiableHeight() >= mRefreshHeight) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mIsRefreshing = true;
                                LogUtil.e("循环", getVisiableHeight() + " " + mRefreshHeight);
                                refresh();
                            }
                        });
                        break;
                    }
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void startRefreshing() {
        Constants.EXECUTOR_REFRESH.execute(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (!((Activity) mContext).isFinishing() && !mIsRefreshing) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            doMovement(getVisiableHeight() + 20);
                        }
                    });
                    if (getVisiableHeight() >= mRefreshHeight) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mIsRefreshing = true;
                                LogUtil.e("循环", getVisiableHeight() + " " + mRefreshHeight);
                                refresh();
                            }
                        });
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * up事件处理
     */
    private void fling() {
        // TODO Auto-generated method stub
        LayoutParams lp = (LayoutParams) mHeaderView.getLayoutParams();
        Log.i(TAG, "fling()" + lp.height);
        if (lp.height >= mRefreshHeight) {// 拉到了触发可刷新事件
            refresh();
        } else {
            returnInitState();
        }
    }

    /**
     * 结束刷新事件
     */
    public void finishRefresh() {
        Log.i(TAG, "执行====finishRefresh");
        long time = System.currentTimeMillis();
        if (time - mRefreshTime >= MIN_REFRESH_TIME) {
            setUp();
        } else {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    setUp();
                }
            }, MIN_REFRESH_TIME - (time - mRefreshTime));
        }
    }

    private void setUp() {
        mCircleView.stopAnim();
        mHeaderView.setAlpha(1);
        mScroller.startScroll(0, getVisiableHeight(), 0, -getVisiableHeight(), SCROLL_DURATION);
        postInvalidate();
        mIsRefreshing = false;
    }

    private void returnInitState() {
        // TODO Auto-generated method stub
        mCircleView.stopAnim();
        mHeaderView.setAlpha(1);
        mScroller.startScroll(0, getVisiableHeight(), 0, -getVisiableHeight(), SCROLL_DURATION);
        mIsRefreshing = false;
        postInvalidate();
    }

    private void refresh() {
        // TODO Auto-generated method stub
        mIsRefreshing = true;
        mTvTip.setVisibility(View.VISIBLE);
        mTvTip.setText(R.string.refreshing_text);
        mScroller.startScroll(0, getVisiableHeight(), 0, mRefreshHeight - getVisiableHeight(), SCROLL_DURATION);
        postInvalidate();
        mCircleView.setProgress(1);
        mCircleView.startAnim();
        LogUtil.e("刷新", "=======>");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshTime = System.currentTimeMillis();
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh(RefreshableView.this);
                }
            }
        }, SCROLL_DURATION);
    }

    private Handler mHandler = new Handler() {
    };

    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if (mScroller.computeScrollOffset()) {
            LayoutParams lp = (LayoutParams) mHeaderView.getLayoutParams();
            lp.height = mScroller.getCurrY();
            mHeaderView.setLayoutParams(lp);
            postInvalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        LogUtil.e(TAG, "视图变化" + changed + "  " + l + "  " + t + "  " + r + "  " + b);
        if (getChildCount() > 0) {
            if (getChildAt(0) == mHeaderView) {
                removeViewAt(0);
                addHeader();
            }
        }
    }

    /**
     * 下拉move事件处理
     *
     * @param height
     */
    private void doMovement(int height) {
        // TODO Auto-generated method stub
        if (height < 0)
            height = 0;
        if (height > mMaxHeight) {
            height = mMaxHeight;
        }
        LayoutParams lp = (LayoutParams) mHeaderView.getLayoutParams();
        lp.height = height;
        mHeaderView.setLayoutParams(lp);
        mTvTip.setVisibility(View.VISIBLE);
        float progress = (float) ((height * 0.1) / (mRefreshHeight * 0.1));
        mCircleView.setProgress(progress);
        mHeaderView.setAlpha(progress);
        if (lp.height >= mRefreshHeight) {
            Log.i(TAG, "执行====松开刷新");
            mTvTip.setText(R.string.refresh_release_text);
        } else {
            Log.i(TAG, "执行====下拉刷新");
            mTvTip.setText(R.string.refresh_down_text);
        }
    }

    public void setRefreshListener(RefreshListener listener) {
        this.mRefreshListener = listener;
    }

    /*
     * 该方法一般和ontouchEvent (non-Javadoc)
     *
     * @see
     * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        // TODO Auto-generated method stub
        int action = e.getAction();
        int y = (int) e.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // y移动坐标
                int m = y - mLastY;
                // 记录下此刻y坐标
                this.mLastY = y;
                if (m > 6 && canScroll()) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    private boolean canScroll() {
        // TODO Auto-generated method stub
        View childView;
        if (getChildCount() > 1) {
            childView = this.getChildAt(0);
            if (childView instanceof ListView) {
                View view = ((ListView) childView).getChildAt(0);
                if (null != view) {
                    int top = view.getTop();
                    int pad = ((ListView) childView).getListPaddingTop();
                    if ((Math.abs(top - pad)) < 3
                            && ((ListView) childView).getFirstVisiblePosition() == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else if (childView instanceof ScrollView) {
                if (((ScrollView) childView).getScrollY() == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 刷新监听接口
     *
     * @author Nono
     */
    public interface RefreshListener {
        void onRefresh(ViewGroup view);
    }
}
