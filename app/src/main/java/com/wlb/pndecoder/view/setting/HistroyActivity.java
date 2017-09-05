package com.wlb.pndecoder.view.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wlb.pndecoder.R;
import com.wlb.pndecoder.adapter.histroy.HistroyListAdapter;
import com.wlb.pndecoder.common.Constants;
import com.wlb.pndecoder.db.histroy.TabHistroy;
import com.wlb.pndecoder.view.TitleBar;
import com.wlb.pndecoder.view.look.IWebViewActivity;

import butterknife.Bind;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.cache.TempSharedData;

/**
 * Created by Berfy on 2017/6/14.
 */
public class HistroyActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.titleBar)
    TitleBar mTitleBar;
    @Bind(R.id.listView)
    ListView mListView;
    private HistroyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_listview;
    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected void initView() {
        mTitleBar.setTitle(getString(R.string.title_histroy));
        mAdapter = new HistroyListAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mAdapter.getData().addAll(TabHistroy.getInstances(mContext).getAllData());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void doClickEvent(int viewId) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TempSharedData.getInstance(mContext).save(Constants.XML_NUMBER, mAdapter.getData().get(position).getNumber());
        Intent intent = new Intent(mContext, IWebViewActivity.class);
        startActivityWithAnim(intent);
    }
}
