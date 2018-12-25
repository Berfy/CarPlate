package com.wlb.pndecoder.view.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wlb.pndecoder.R;
import com.wlb.pndecoder.adapter.histroy.HistroyListAdapter;
import com.wlb.pndecoder.common.Constants;
import com.wlb.pndecoder.db.histroy.TabHistroy;
import com.wlb.pndecoder.view.TitleBar;
import com.wlb.pndecoder.view.look.IWebViewActivity;

import butterknife.BindView;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.cache.TempSharedData;

/**
 * Created by Berfy on 2017/6/15.
 */

public class SearchingActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.edit_search)
    EditText mEditSearch;
    private HistroyListAdapter mAdapter;
    private String mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_search_listview;
    }

    @Override
    protected void initView() {
        mTitleBar.setTitle(getString(R.string.title_search));
        mAdapter = new HistroyListAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mKey = s.toString();
                mHandler.removeMessages(0);
                Message message = new Message();
                message.what = 0;
                message.obj = mKey;
                mHandler.sendMessage(message);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mHandler.removeMessages(0);
                    Message message = new Message();
                    message.what = 0;
                    message.obj = mKey;
                    mHandler.sendMessageDelayed(message, 500);
                }
                return false;
            }
        });
        Message message = new Message();
        message.what = 0;
        message.obj = mKey;
        mHandler.sendMessage(message);
    }

    @Override
    protected void doClickEvent(int viewId) {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String key = null == msg.obj ? "" : msg.obj.toString();
            mAdapter.getData().clear();
            if (TextUtils.isEmpty(key)) {
                key = "";
            }
            mAdapter.getData().addAll(TabHistroy.getInstances(mContext).getAllDataBySearch(key));
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TempSharedData.getInstance(mContext).save(Constants.XML_NUMBER, mAdapter.getData().get(position).getNumber());
        Intent intent = new Intent(mContext, IWebViewActivity.class);
        startActivityWithAnim(intent);
    }
}
