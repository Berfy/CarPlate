package com.wlb.pndecoder.view.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wlb.pndecoder.R;
import com.wlb.pndecoder.common.Constants;
import com.wlb.pndecoder.view.TitleBar;

import butterknife.Bind;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.cache.TempSharedData;
import cn.berfy.framework.utils.ToastUtil;

/**
 * Created by Berfy on 2017/6/15.
 */

public class PricePercentActivity extends BaseActivity {

    @Bind(R.id.titleBar)
    TitleBar mTitleBar;

    @Bind(R.id.btn_ok)
    Button mBtnOk;

    @Bind(R.id.edit_price_percent)
    EditText mEditPricePercent;

    private String mPricePercent = "22";

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
        return R.layout.activity_setting_price_percent;
    }

    @Override
    protected void initView() {
        mTitleBar.setTitle(getString(R.string.title_price_set));
        mBtnOk.setOnClickListener(this);
        mEditPricePercent.setText(TextUtils.isEmpty(TempSharedData.getInstance(mContext).getData(Constants.XML_PRICE_PERCENT)) ? "22" : TempSharedData.getInstance(mContext).getData(Constants.XML_PRICE_PERCENT));
        mEditPricePercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPricePercent = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void doClickEvent(int viewId) {
        switch (viewId) {
            case R.id.btn_ok:
                if (mPricePercent.length() > 2) {
                    ToastUtil.getInstance().showToast(getString(R.string.tip_price_percent));
                } else {
                    TempSharedData.getInstance(mContext).save(Constants.XML_PRICE_PERCENT, mPricePercent);
                    doBackAction();
                }
                break;
        }
    }
}
