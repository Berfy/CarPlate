package cn.berfy.framework.support.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import cn.berfy.framework.R;

public class FooterListview extends ListView implements OnScrollListener {

    private Context mContext;
    private View mFooterView;
    private OnRefreshListener mOnRefreshListener;
    private boolean mIsFooterLoading = false;
    private boolean mIsLastItem = false;
    private boolean mIsScroll = false;
    private String mTipNoData;
    private String mTipLoading;

    public FooterListview(Context context) {
        super(context);
        mContext = context;
        initFootView();
        setOnScrollListener(this);
    }

    public FooterListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initFootView();
        setOnScrollListener(this);
    }

    private void initFootView() {
        mFooterView = View.inflate(mContext,
                R.layout.view_footer_loading_layout, null);
        mTipNoData = mContext.getString(R.string.tip_no_more_result_data);
        mTipLoading = mContext.getString(R.string.loading);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
        if (null != mOnRefreshListener) {
            addFooterView(mFooterView);
        }
    }

    public void setTip(String loadingTip, String noDataTip) {
        if (!TextUtils.isEmpty(loadingTip)) {
            mTipLoading = loadingTip;
        }
        if (!TextUtils.isEmpty(noDataTip)) {
            mTipNoData = noDataTip;
        }
    }

    public void finishFooterLoading() {
        if (mFooterView.getVisibility() == View.GONE) {
            mFooterView.setVisibility(View.VISIBLE);
        }
        if (mFooterView.findViewById(R.id.loading_pb_loading).getVisibility() == View.VISIBLE) {
            mFooterView.findViewById(R.id.loading_pb_loading).setVisibility(
                    View.GONE);
            ((TextView) mFooterView.findViewById(R.id.loading_tv_text))
                    .setText(mTipNoData);
        }
        mIsFooterLoading = false;
    }

    public void noData() {
        if (mFooterView.getVisibility() == View.GONE) {
            mFooterView.setVisibility(View.VISIBLE);
        }
        if (mFooterView.findViewById(R.id.loading_pb_loading).getVisibility() == View.VISIBLE) {
            mFooterView.findViewById(R.id.loading_pb_loading).setVisibility(
                    View.GONE);
            ((TextView) mFooterView.findViewById(R.id.loading_tv_text))
                    .setText(mTipNoData);
        }
        mIsFooterLoading = false;
    }

    private void showFooterLoading() {
        if (mFooterView.getVisibility() == View.GONE) {
            mFooterView.setVisibility(View.VISIBLE);
        }
        if (mFooterView.findViewById(R.id.loading_pb_loading).getVisibility() == View.GONE) {
            mFooterView.findViewById(R.id.loading_pb_loading).setVisibility(
                    View.VISIBLE);
            ((TextView) mFooterView.findViewById(R.id.loading_tv_text))
                    .setText(mTipLoading);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        if (null != mOnRefreshListener) {
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                mIsScroll = false;
                if (!mIsFooterLoading && mIsLastItem) {
                    mIsFooterLoading = true;
                    if (null != mOnRefreshListener)
                        mOnRefreshListener.onFootLoading();
                }
            } else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                mIsScroll = true;
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (null != mOnRefreshListener) {
            mIsLastItem = false;
            if (totalItemCount > visibleItemCount) {// 超出可视范围
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && mIsScroll) {
                    mIsLastItem = true;
                    showFooterLoading();
                } else {
                    mIsFooterLoading = false;
                }
            } else {
                if (mFooterView.getVisibility() == View.VISIBLE) {
                    mFooterView.setVisibility(View.GONE);
                }
            }
        }
    }

    public interface OnRefreshListener {
        void onFootLoading();
    }
}
