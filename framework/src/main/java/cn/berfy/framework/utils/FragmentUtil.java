package cn.berfy.framework.utils;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * Created by Berfy on 2016/7/19.
 * Fragment切换显示工具，避免频繁replace，提高效率，降低内存
 */
public class FragmentUtil {

    // 用药提醒下的部分
    private FragmentManager mFragmentManager;

    public FragmentUtil(FragmentActivity activity) {
        mFragmentManager = activity.getSupportFragmentManager();
    }

    public void update(int frameLayoutResId, Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (null != fragments && fragments.size() > 0 && fragments.contains(fragment)) {
            LogUtil.e(getClass().getName(), "Fragment存在，显示" + fragment + "  " + frameLayoutResId);
            transaction.show(fragment);
        } else {
            if (!fragment.isAdded()) {
                LogUtil.e(getClass().getName(), "Fragment不存在，加入" + fragment + "  " + frameLayoutResId);
                try {
                    transaction.replace(frameLayoutResId, fragment);
                    transaction.addToBackStack(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LogUtil.e(getClass().getName(), "Fragment已加入其他，显示" + fragment + "  " + frameLayoutResId);
                transaction.show(fragment);
            }
        }
        transaction.commit();
    }

    public void show(DialogFragment fragment, String tag) {
        fragment.show(mFragmentManager, tag);
    }
}
