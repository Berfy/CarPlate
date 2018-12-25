package com.wlb.pndecoder.view.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.wlb.pndecoder.R;
import com.wlb.pndecoder.db.TabUser;
import com.wlb.pndecoder.model.User;
import com.wlb.pndecoder.view.MainActivity;

import java.util.List;

import butterknife.BindView;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.cache.TempSharedData;
import cn.berfy.framework.utils.CheckUtil;
import cn.berfy.framework.utils.GsonUtil;
import cn.berfy.framework.utils.TimeUtil;
import cn.berfy.framework.utils.ToastUtil;

/**
 * Created by Berfy on 2017/6/14.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edit_phone)
    EditText mEditPhone;
    @BindView(R.id.btn_login)
    Button mBntLogin;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_login;
    }

    @Override
    protected void initVariable() {
        XXPermissions.with(this)
                .permission(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , android.Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.CAMERA
                        , Manifest.permission.RECORD_AUDIO
                ).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                ToastUtil.getInstance().showToast("你拒绝了" + GsonUtil.getInstance().toJson(denied) + "权限");
                XXPermissions.gotoPermissionSettings(mContext);
            }
        });
        User user = new User();
        user.setName("翟总");
        user.setPhone("18600673775");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("杨总");
        user.setPhone("15699800088");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("王飞/财神爷");
        user.setPhone("13520343537");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("张筱乾/张总");
        user.setPhone("13911095134");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("超哥");
        user.setPhone("15300028011");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("刘义方");
        user.setPhone("15120046792");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("易辽宏");
        user.setPhone("13625290096");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("韩雪");
        user.setPhone("15010210559");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("王诗锦");
        user.setPhone("15643235062");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("宓学志");
        user.setPhone("17600380778");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("张少荣");
        user.setPhone("15901209107");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("杨晓岚");
        user.setPhone("15110093429");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("潘松");
        user.setPhone("18201403225");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("许春林");
        user.setPhone("18211187055");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("岳鹏飞");
        user.setPhone("18611897927");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("王强伟");
        user.setPhone("18710164122");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("苗胜");
        user.setPhone("13121428843");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("武仁彦");
        user.setPhone("18703422746");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("鹿智明 ");
        user.setPhone("18910744589");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("杨曼");
        user.setPhone("13521458699");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
        user.setName("郭晓坤");
        user.setPhone("15810423616");
        user.setUpdateTime(TimeUtil.getCurrentTime());
        TabUser.getInstances(mContext).add(user);
    }

    @Override
    protected void initView() {
        mBntLogin.setOnClickListener(this);
        mEditPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhone = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                }
                return false;
            }
        });
    }

    @Override
    protected void doClickEvent(int viewId) {
        switch (viewId) {
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        if (TextUtils.isEmpty(mPhone)) {
            ToastUtil.getInstance().showToast(getString(R.string.hint_phone));
        } else if (CheckUtil.isMobile(mPhone)) {
            User user = TabUser.getInstances(mContext).getData(mPhone);
            if (null != user) {
                TempSharedData.getInstance(mContext).save("phone", mPhone);
                startActivityWithAnim(new Intent(mContext, MainActivity.class));
                finish();
                ToastUtil.getInstance().showToast("你好，" + user.getName(), Toast.LENGTH_LONG);
            } else {
                ToastUtil.getInstance().showToast(getString(R.string.tip_login_permission));
            }
        } else {
            ToastUtil.getInstance().showToast(getString(R.string.tip_phone));
        }
    }
}
