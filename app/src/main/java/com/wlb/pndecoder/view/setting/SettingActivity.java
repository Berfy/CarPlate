package com.wlb.pndecoder.view.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wlb.pndecoder.R;
import com.wlb.pndecoder.utils.PopupWindowUtil;
import com.wlb.pndecoder.view.TitleBar;
import com.wlb.pndecoder.view.login.LoginActivity;

import butterknife.BindView;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.base.BasePopupWindowUtil;
import cn.berfy.framework.cache.TempSharedData;
import cn.berfy.framework.manager.ActivityManager;

/**
 * Created by Berfy on 2017/6/14.
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tv_histroy)
    TextView mTvHistroy;
    @BindView(R.id.tv_search)
    TextView mTvSearch;
    @BindView(R.id.tv_price_set)
    TextView mTvPriceSet;
    @BindView(R.id.btn_logout)
    Button mBtnLogout;
    private PopupWindowUtil mPopupWindowUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariable() {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_setting;
    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected void initView() {
        mTitleBar.setTitle(getString(R.string.title_setting));
        mTvHistroy.setOnClickListener(this);
        mTvSearch.setOnClickListener(this);
        mTvPriceSet.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
    }

    @Override
    protected void doClickEvent(int viewId) {
        switch (viewId) {
            case R.id.tv_histroy:
                startActivityWithAnim(new Intent(mContext, HistroyActivity.class));
                break;
            case R.id.tv_search:
                startActivityWithAnim(new Intent(mContext, SearchingActivity.class));
                break;
            case R.id.tv_price_set:
                startActivityWithAnim(new Intent(mContext, PricePercentActivity.class));
                break;
            case R.id.btn_logout:
                mPopupWindowUtil.showConfirmTip(getWindow().getDecorView(),
                        "提示", "退出登录将清空您的本地和历史记录，确定吗？", "", "",
                        new BasePopupWindowUtil.OnPopConfirmTipListener() {
                    @Override
                    public void ok() {
                        TempSharedData.getInstance(mContext).clear();
                        ActivityManager.getInstance().popAllActivity();
                        startActivityWithAnim(new Intent(mContext, LoginActivity.class));
                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;
        }
    }
}
