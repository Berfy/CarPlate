package cn.berfy.framework.utils;

import android.app.Activity;
import cn.berfy.framework.R;

/**
 * 界面切换跳转效果工具类 edit at 2012-8-30
 *
 * @author zts
 */
public class AnimUtil {
    public static int in, out;

    // 左推进推出效果
    public static void pushLeftInAndOut(Activity activity) {
        activity.overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

    // 右推进推出效果
    public static void pushRightInAndOut(Activity activity) {
        activity.overridePendingTransition(R.anim.push_right_in,
                R.anim.push_right_out);
    }

    /**
     * 设置自己的自定义动画
     *
     * @param in
     * @param out
     */
    public static void setInAndOut(int in, int out) {
        AnimUtil.in = in;
        AnimUtil.out = out;
    }

    /**
     * 清空自己的自定义动画
     */
    public static void clear() {
        AnimUtil.in = 0;
        AnimUtil.out = 0;
    }
}
