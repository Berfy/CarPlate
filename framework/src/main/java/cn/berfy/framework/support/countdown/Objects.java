package cn.berfy.framework.support.countdown;

/**
 * @author WhatsAndroid
 */
public class Objects {
    private static final String TAG = "Objects";

    public static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }
}
