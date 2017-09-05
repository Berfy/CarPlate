package cn.berfy.framework.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import cn.berfy.framework.utils.AnimUtil;
import cn.berfy.framework.utils.LogUtil;

public abstract class BaseFragment extends Fragment implements
        OnClickListener {

    protected Activity mContext;
    private View mView;
    private OnFragmentStatusListener mOnFragmentStatusListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = View.inflate(mContext, initContentViewById(), null);
        if (null != mOnFragmentStatusListener)
            mOnFragmentStatusListener.onViewCreate();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != mOnFragmentStatusListener)
            mOnFragmentStatusListener.onActivityCreated();
        initView();
    }

    public void setListener(OnFragmentStatusListener onFragmentStatusListener) {
        mOnFragmentStatusListener = onFragmentStatusListener;
    }

    @Nullable
    @Override
    public View getView() {
        return mView;
    }

    protected View findViewById(int redId) {
        return getView().findViewById(redId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        initVariable();
    }

    abstract protected int initContentViewById();

    /**
     * 初始化函数
     */
    abstract protected void initVariable();

    /**
     * 初始化视图
     */
    abstract protected void initView();

    /**
     * 点击事件
     */
    abstract protected void doClickEvent(int viewId);

    @Override
    public void onClick(View view) {
        doClickEvent(view.getId());
    }

    private long mClickTime = 0;

    /**
     * 跳转Activity+动画
     */
    public void startActivityWithAnim(Intent intent) {
        if (System.currentTimeMillis() - mClickTime > 1000) {
            LogUtil.e("跳转", "====" + intent.getComponent());
            mClickTime = System.currentTimeMillis();
            startActivity(intent);
            AnimUtil.pushLeftInAndOut(mContext);
        } else {
            LogUtil.e("等待跳转", "====");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != mOnFragmentStatusListener)
            mOnFragmentStatusListener.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mOnFragmentStatusListener)
            mOnFragmentStatusListener.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mOnFragmentStatusListener)
            mOnFragmentStatusListener.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != mOnFragmentStatusListener)
            mOnFragmentStatusListener.onStop();
    }

    public interface OnFragmentStatusListener {
        void onViewCreate();

        void onActivityCreated();

        void onStart();

        void onResume();

        void onPause();

        void onStop();
    }
}