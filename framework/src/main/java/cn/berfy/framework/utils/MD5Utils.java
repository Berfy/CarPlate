package cn.berfy.framework.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String encodeMD5(String password) {
        try {
            // MessageDigest 类是一个引擎类，它是为了提供诸如 SHA1 或 MD5 等密码上安全的报文摘要功能而设计的。
            LogUtil.e("MD5加密", password);
            if (TextUtils.isEmpty(password)) {
                return "";
            } else {
                MessageDigest digest = MessageDigest.getInstance("md5");
                byte[] result = digest.digest(password.getBytes());
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < result.length; i++) {
                    String str = Integer.toHexString((0xFF & result[i]));
                    if (str.length() == 1) {
                        sb.append("0" + str);
                    } else {
                        sb.append(str);
                    }
                }
                return sb.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encodeSHA1(String msg) {
        try {
            // MessageDigest 类是一个引擎类，它是为了提供诸如 SHA1 或 MD5 等密码上安全的报文摘要功能而设计的。
            if (TextUtils.isEmpty(msg)) {
                return "";
            } else {
                MessageDigest md5 = MessageDigest.getInstance("SHA-1");
                md5.update(msg.getBytes());
                byte[] rlt = md5.digest(msg.getBytes());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < rlt.length; i++) {
                    sb.append(rlt[i]);
                }
                return sb.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * MD5加密32位大写
     */
    public static String getMD532upper(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return strBuf.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
