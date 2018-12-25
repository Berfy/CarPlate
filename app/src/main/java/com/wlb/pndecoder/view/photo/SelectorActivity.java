package com.wlb.pndecoder.view.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.wlb.pndecoder.R;
import com.wlb.pndecoder.view.TitleBar;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import cn.berfy.framework.base.BaseActivity;
import cn.berfy.framework.utils.LogUtil;

public class SelectorActivity extends BaseActivity implements
        MultiImageSelectorFragment.Callback {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    private final String TAG = "SelectorActivity";
    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /**
     * 多选
     */
    public static final int CANCEL = -200;

    private ArrayList<String> resultList = new ArrayList<String>();
    //	private Button mSubmitButton;
    private int mDefaultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initContentViewById() {
        return R.layout.activity_multiimager_selector_layout;
    }

    @Override
    protected View initContentView() {
        return null;
    }

    @Override
    protected void initVariable() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MultiImageSelectorFragment.MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        if (mode == MultiImageSelectorFragment.MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent
                    .getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT,
                mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putInt(EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putStringArrayList(
                MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST,
                resultList);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.image_grid,
                        Fragment.instantiate(this,
                                MultiImageSelectorFragment.class.getName(),
                                bundle)).commit();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        mTitleBar.setTitle(getString(R.string.title_choose_image));
        showRightTitle(true, "完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultList != null && resultList.size() > 0) {
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putStringArrayListExtra(SelectorActivity.EXTRA_RESULT, resultList);
                    setResult(MultiImageSelectorFragment.MODE_MULTI, data);
                    ((BaseActivity) mContext).finish();
                }
            }
        });
        if (resultList == null || resultList.size() <= 0) {
            showRightTitle(false, "完成", null);
        } else {
            showRightTitle(true, "完成(" + resultList.size() + "/" + mDefaultCount + ")", null);
        }
    }

    private void showRightTitle(boolean isShow, String title, View.OnClickListener onClickListener) {
        mTitleBar.showRight(isShow, title, onClickListener);
    }

    @Override
    protected void doClickEvent(int viewId) {
    }

    @Override
    public void onSingleImageSelected(String path) {
        // TODO Auto-generated method stub
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        LogUtil.e(TAG, "单张图片");
        setResult(MultiImageSelectorFragment.MODE_SINGLE, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        // TODO Auto-generated method stub
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if (resultList.size() > 0) {
            showRightTitle(true, "完成(" + resultList.size() + "/" + mDefaultCount + ")", null);
        }
    }

    @Override
    public void onImageUnselected(String path) {
        // TODO Auto-generated method stub
        if (resultList.contains(path)) {
            resultList.remove(path);
        }
        showRightTitle(true, "完成(" + resultList.size() + "/" + mDefaultCount + ")", null);
        // 当为选择图片时候的状态
        if (resultList.size() == 0) {
            showRightTitle(false, "完成", null);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        // TODO Auto-generated method stub
        if (imageFile != null) {
            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(MultiImageSelectorFragment.MODE_MULTI, data);
            finish();
        }
    }

}
