package cn.berfy.framework.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.berfy.framework.R;

import static android.view.View.Z;

/**
 * Created by Berfy on 2016/3/18.
 * 字段文字检查工具类
 */
public class CheckUtil {

    //判断身份证：要么是15位，要么是18位，最后一位可以为字母，并写程序提出其中的年月日。
    public static String IDCardValidate(String IDStr) {
        String errorInfo = "";// 记录错误信息
        try {
            String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4",
                    "3", "2"};
            String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                    "9", "10", "5", "8", "4", "2"};
            String Ai = "";
            // ================ 号码的长度 15位或18位 ================
            if (IDStr.length() != 15 && IDStr.length() != 18) {
                errorInfo = "身份证号码长度应该为15位或18位。";
                return errorInfo;
            }
            // =======================(end)========================
            // ================ 数字 除最后以为都为数字 ================
            if (IDStr.length() == 18) {
                Ai = IDStr.substring(0, 17);
            } else if (IDStr.length() == 15) {
                Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
            }
            if (isNumeric(Ai) == false) {
                errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
                return errorInfo;
            }
            // =======================(end)========================
            // ================ 出生年月是否有效 ================
            String strYear = Ai.substring(6, 10);// 年份
            String strMonth = Ai.substring(10, 12);// 月份
            String strDay = Ai.substring(12, 14);// 月份
            if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
                errorInfo = "身份证生日无效。";
                return errorInfo;
            }
            GregorianCalendar gc = new GregorianCalendar();
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围。";
                return errorInfo;
            }
            if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
                errorInfo = "身份证月份无效";
                return errorInfo;
            }
            if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
                errorInfo = "身份证日期无效";
                return errorInfo;
            }
            // =====================(end)=====================
            // ================ 地区码时候有效 ================
            Hashtable h = GetAreaCode();
            if (h.get(Ai.substring(0, 2)) == null) {
                errorInfo = "身份证地区编码错误。";
                return errorInfo;
            }
            // ==============================================
            // ================ 判断最后一位的值 ================
            int TotalmulAiWi = 0;
            for (int i = 0; i < 17; i++) {
                TotalmulAiWi = TotalmulAiWi
                        + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                        * Integer.parseInt(Wi[i]);
            }
            int modValue = TotalmulAiWi % 11;
            String strVerifyCode = ValCodeArr[modValue];
            Ai = Ai + strVerifyCode;
            if (IDStr.length() == 18) {
                if (Ai.equals(IDStr) == false) {
                    errorInfo = "身份证无效，不是合法的身份证号码";
                    return errorInfo;
                }
            } else {
                return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorInfo = "身份证无效，不是合法的身份证号码";
            return errorInfo;
        }
        // =====================(end)=====================
        return "";
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    private static boolean isDataFormat(String str) {
        boolean flag = false;
        //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 验证手机格式
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
    public static boolean isMobile(String mobiles) {
        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * @param editMobile    输入的手机号
     * @param requestMobile 获取验证码的手机号
     * @param editCode      输入的验证码
     * @param reqeustCode   获取的验证码
     */
    public static boolean checkMobileAndCodeEnable(String editMobile, String requestMobile, String editCode, String reqeustCode) {
        if (!CheckUtil.isMobile(editMobile)) {//手机号不对
            ToastUtil.getInstance().showToast(R.string.tip_mobile_error);
        } else if (TextUtils.isEmpty(reqeustCode)) {//未获取验证码
            ToastUtil.getInstance().showToast(R.string.tip_code_get_null);
        } else if (TextUtils.isEmpty(editCode)) {//未输入验证码
            ToastUtil.getInstance().showToast(R.string.tip_code_null);
        } else if (!editCode.equals(reqeustCode)) {//验证码不匹配
            ToastUtil.getInstance().showToast(R.string.tip_code_null);
        } else if (!editMobile.equals(requestMobile)) {//手机号不匹配
            ToastUtil.getInstance().showToast(R.string.tip_mobile_code_null);
        } else {
            return true;
        }
        return false;
    }

    /**
     * //获取完整的域名
     *
     * @param text 获取浏览器分享出来的text文本
     */
    public static boolean checkUrl(String text) {
        Pattern p = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        return matcher.find();
    }

    /**
     * @param editMobile 输入的手机号
     * @param editCode   输入的验证码
     */
    public static boolean checkMobileAndCodeEnableSimple(String editMobile, String editCode, boolean isTip) {
        if (!CheckUtil.isMobile(editMobile)) {//手机号不对
            if (isTip) {
                ToastUtil.getInstance().showToast(R.string.tip_mobile_error);
            }
        } else if (TextUtils.isEmpty(editCode)) {//未输入验证码
            if (isTip) {
                ToastUtil.getInstance().showToast(R.string.tip_code_null);
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * @param number 检查车牌号
     */
    public static boolean isCarNumber(String number) {
        boolean result = false;
        if (number.length() == 7) {
            Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5]{1}[A-HJ-NP-Z0-9]{6}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(number);
            result = matcher.find();
        }
        return result;
    }
}
