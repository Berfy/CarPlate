/* 
 * Date: 14-8-18 
 * Project: Access-Control-V2 
 */
package cn.berfy.framework.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 抽象的PagerAdapter实现类,封装了内容为View的公共操作.
 * Author: msdx (645079761@qq.com)
 * Time: 14-8-18 下午2:34
 */
public abstract class BaseViewPagerAdapter<T> extends PagerAdapter {

    protected Context mContext;
    protected List<T> mData;
    private SparseArray<View> mViews;

    public BaseViewPagerAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data;
        mViews = new SparseArray<View>(data.size());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        if (view == null) {
            view = newView(position);
            mViews.put(position, view);
        }
        container.addView(view);
        return view;
    }

    public abstract View newView(int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    public T getItem(int position) {
        return mData.get(position);
    }
}  