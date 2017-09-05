package cn.berfy.framework.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class StringUtil {

    /**
     * 求出某年某月某日的这一天是星期几
     *
     * @param y
     * @param m
     * @param d
     * @return
     */
    public static String dayToWhichWeek(int y, int m, int d) {
        if (m == 1 || m == 2) {
            m += 12;
            y--;
        }
        int week = d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400
                + 1;
        week = week % 7;
        String w = "日一二三四五六".substring(week, week + 1);
        return w;
    }

    /**
     * 求星期组的下标
     *
     * @param y
     * @param m
     * @param d
     * @return
     */
    public static int dayToWhichWeekNum(int y, int m, int d) {
        if (m == 1 || m == 2) {
            m += 12;
            y--;
        }
        int week = d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400
                + 1;
        week = week % 7;
        return week;
    }

    /**
     * 计算指定年份的某个月有多少天
     *
     * @param y
     * @param m
     * @return
     */
    public static int month(int y, int m) {
        System.out.println(m);
        int days = 0;
        String leap = year(y);
        if (m > 7) {
            if (m % 2 == 0) {
                days = 31;
            } else {
                days = 30;
            }
        } else {
            if (m % 2 == 0) {
                days = 30;
            } else {
                days = 31;
            }
            if (m == 2 && leap.equals("闰年")) {
                days = 29;
            } else if (m == 2 && leap.equals("平年")) {
                days = 28;
            }
        }
        return days;
    }

    /**
     * 判断该年是不是闰年
     *
     * @param y
     * @return
     */
    public static String year(int y) {
        String leap = (y % 400 == 0 || (y % 4 == 0 && y % 100 != 0)) ? "闰年"
                : "平年";
        return leap;
    }

    /**
     * 判断该年是不是闰年
     *
     * @param y
     * @return
     */
    public static boolean isLeapYear(int y) {
        boolean leap = (y % 400 == 0 || y % 4 == 0 && y % 100 != 0) ? true
                : false;
        return leap;
    }

    /**
     * 根据时间格式转换为字符串
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTime(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return sdf.format(date == null ? new Date() : date);
    }

    /**
     * 根据时间格式转换为字符串
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTime(String pattern, long date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date date2 = new Date();
        date2.setTime(date);
        return sdf.format(date2);
    }

    /**
     * 根据格式转换
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTime(String pattern, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            return sdf.format(sdf.parse(date).getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据格式互转
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTimeFromPatternToPattern(String rawPattern, String pattern, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(rawPattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        SimpleDateFormat newSdf = new SimpleDateFormat(pattern);
        try {
            return newSdf.format(sdf.parse(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 根据时间格式转换为时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    public static long getFormatTimeToLong(String pattern, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            if (!TextUtils.isEmpty(time)) {
                return sdf.parse(time).getTime();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据时间格式转换为时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    public static Date getFormatTimeToDate(String pattern, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            Date newTime = sdf.parse(time);
            return newTime;
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 根据时间格式转换为时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    public static long checkFormatTimeToLong(String pattern, String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        long newTime = sdf.parse(
                "".equals(time) ? System.currentTimeMillis() + "" : time)
                .getTime();
        return newTime;
    }

    /**
     * 根据时间格式转换为字符串
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTimeNoZone(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return sdf.format(date == null ? new Date() : date);
    }

    /**
     * 设置保留2个小数位，四舍五入
     */
    public static String fomatScale(float num) {
        if (num == 0) {
            return "0.00";
        }
        String[] nums = (num + "").split("\\.");
        if (nums.length > 1) {
            if (nums[1].length() == 1) {
                return num + "0";
            }
        }
        String result = num + "";//默认返回原值
        try {
            DecimalFormat fnum = new DecimalFormat("##0.00");
            result = fnum.format(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置保留1个小数位，四舍五入
     */
    public static String fomatScale1(float num) {
        DecimalFormat fnum = new DecimalFormat("##0.0");
        String result = fnum.format(num);
        return result;
    }

    /**
     * 设置保留2个小数位，四舍五入
     */
    public static float fomatFloat(float num) {
        BigDecimal b = new BigDecimal(num);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    /**
     * 设置保留2个小数位，四舍五入
     */
    public static Double fomatDouble(double num) {
        BigDecimal b = new BigDecimal(num);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 设置保留2个小数位，四舍五入 (去掉多余的0)
     */
    public static String getMoneyString(double num) {
        BigDecimal b = new BigDecimal(num);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return subZeroAndDot(f1 + "");
    }

    /**
     * 设置保留scale个小数位，四舍五入 (去掉多余的0)
     *
     * @param num
     * @param scale 小数点数量
     */
    public static String getMoneyString(double num, int scale) {
        BigDecimal b = new BigDecimal(num);
        double f1 = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        return subZeroAndDot(f1 + "");
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * @param num 设置保留个小数位，四舍五入 (不去掉多余的0)
     */
    public static String getMoneyString2(double num) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(num);
    }

    /**
     * 获取资源文件的id
     */
    public static int getRes(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type,
                context.getPackageName());
    }

    /**
     * 根据时间格式转换为时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    public static Date getFormatTimeString(String pattern, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.parse("".equals(time) ? System.currentTimeMillis() + ""
                    : time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }

    public static String[] mBigNums = new String[]{"一", "二", "三", "四", "五",
            "六", "七", "八", "九", "十", "十一", "十二"};

    /**
     * 获取中文月份
     *
     * @param month
     */
    public static String getBigMonth(int month) {
        if (mBigNums.length > month - 1) {
            return mBigNums[month - 1];
        }
        return "";
    }

    /**
     * 处理时间(毫秒格式 转换为n天前)
     *
     * @param timestamp
     * @return
     */
    public static String fixTime(long timestamp) {
        LogUtil.e("时间", getFormatTime("yyyy-MM-dd", timestamp));
        try {
            if (timestamp == 0) {//不识别
                return "";
            }
            if (System.currentTimeMillis() - timestamp < 1 * 60 * 1000) {//小于一分钟
                return "刚刚";
            } else if (System.currentTimeMillis() - timestamp < 60 * 60 * 1000) {//小于一小时
                return ((System.currentTimeMillis() - timestamp) / 1000 / 60)
                        + "分钟前";
            } else {
                Calendar now = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(timestamp);
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && c.get(Calendar.DATE) == now.get(Calendar.DATE)) {//
                    return now.get(Calendar.HOUR_OF_DAY)
                            - c.get(Calendar.HOUR_OF_DAY) + "小时前";
                }
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && c.get(Calendar.DATE) == now.get(Calendar.DATE) - 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
                    return sdf.format(c.getTime());
                } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
                    return now.get(Calendar.DAY_OF_MONTH)
                            - c.get(Calendar.DAY_OF_MONTH) + "天前";
                } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                    return now.get(Calendar.MONTH) - c.get(Calendar.MONTH)
                            + "月前";
                } else {
                    return new SimpleDateFormat("yyyy年M月d日")
                            .format(c.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getStarMobile(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            if (mobile.length() >= 11)
                return mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length());
        } else {
            return "";
        }
        return mobile;
    }

    public static String getSplitString(String text, String split) {
        String[] districts = text.split(split);
        int length;
        if (districts.length <= 1) {
            districts = text.split(" ");
        }
        length = districts.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(districts[i]);
        }
        return sb.toString();
    }

    public static List<String> getSplitStringList(String text, String split) {
        List<String> splits = new ArrayList<String>();
        String[] districts = text.split(split);
        int length;
        if (districts.length <= 1) {
            districts = text.split(" ");
        }
        length = districts.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            splits.add(districts[i]);
        }
        return splits;
    }

    /**
     * 实现文本复制功能
     */
    public static void copy(Context context, String text) {
        ClipData clip = ClipData.newPlainText("simple text", text);
        try {
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查时间格式是否正确
     *
     * @param str     时间格式字符
     * @param pattern 时间格式
     */
    public static boolean isValidDate(String str, String pattern) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 局部文字变色
     */
    public static SpannableString getColorTextByKeywords(String text, int colorId, String keyword) {
        SpannableString ss = new SpannableString(text);
        int start = text.indexOf(keyword);
        int end = start + keyword.length();
        if (start != -1 && end != -1) {
            //用颜色标记文本
            ss.setSpan(new ForegroundColorSpan(colorId), start, end,
                    //setSpan时需要指定的 flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括).
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    /**
     * 将毫秒数转换成时分秒格式
     * time 毫秒
     */
    public static String getSecondToHourMinutes(long time) {
        long second = time / 1000;
        long hour = second / 60 / 60;
        long minute = (second - hour * 60 * 60) / 60;
        long sec = (second - hour * 60 * 60) - minute * 60;

        String rHour = "";
        String rMin = "";
        String rSs = "";
        // 时
        if (hour < 10) {
            rHour = "0" + hour;
        } else {
            rHour = hour + "";
        }
        // 分
        if (minute < 10) {
            rMin = "0" + minute;
        } else {
            rMin = minute + "";
        }
        // 秒
        if (sec < 10) {
            rSs = "0" + sec;
        } else {
            rSs = sec + "";
        }
        return rHour + ":" + rMin + ":" + rSs;
    }

    /**
     * 将毫秒数换算成x天x时x分x秒x毫秒
     * time 毫秒
     */
    public static String getSecondToDayHourMinutes(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day;
        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        return strDay + ":" + strHour + ":" + strMinute + ":" + strSecond;
    }

    /**
     * 将毫秒数换算成x天x时x分x秒x毫秒
     * time 毫秒
     */
    public static String getLongTimeToDayHourMinutes(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        StringBuffer sb = new StringBuffer();
        String dayText = "";
        String hourText = "";
        String minutesText = "";
        String secondText = "";
        if (day > 0) {
            sb.append(day < 10 ? "0" + day : "" + day);
            sb.append("天");
        }
        if (day > 0) {
            sb.append("  ");
        }
        if (day > 0 || hour > 0) {
            sb.append(hour < 10 ? "0" + hour : "" + hour);
            sb.append(":");
        }
        if (minute > 0 || day > 0 || hour > 0) {
            sb.append(minute < 10 ? "0" + minute : "" + minute);
            sb.append(":");
        }
        sb.append(second < 10 ? "0" + second : "" + second);
        sb.append("");
        return sb.toString();
    }


    /**
     * 求邀请码 糖友汇内容
     * namestr 商品名
     * mEndTime 剩余时间秒
     */
    public static String getRequestInviteCodeSC(String namestr, long mEndTime) {
        String[] endtime = StringUtil.getSecondToDayHourMinutes(mEndTime * 1000).split(":");
        if (endtime[0].equals("00")) {
            return "土豪快来发邀请码!" + namestr + "，还有" + endtime[1] + "小时"
                    + endtime[2] + "分" + endtime[3] + "秒团购结束。";
        } else {
            return "土豪快来发邀请码!" + namestr + "，还有" + endtime[0] + "天" + endtime[1] + "小时"
                    + endtime[2] + "分" + endtime[3] + "秒团购结束。";
        }
    }

    /**
     * 求邀请码、邀请好友参团（未参团情况下） 分享Text
     * mEndTime 剩余时间秒
     * mSoldCount 已售多少件
     * mDetail 商品介绍
     */
    public static String getRequestInviteCodeText(long mEndTime, int mSoldCount, String mDetail) {
        String[] endtime = StringUtil.getSecondToDayHourMinutes(mEndTime * 1000).split(":");
        if (endtime[0].equals("00")) {
            if (TextUtils.isEmpty(mDetail)) {
                return "剩余" + endtime[1] + "小时" + endtime[2] + "分" + endtime[3] + "秒结束。已售" + mSoldCount + "件。";
            } else {
                return "剩余" + endtime[1] + "小时" + endtime[2] + "分" + endtime[3] + "秒结束。已售" + mSoldCount + "件。" + mDetail;
            }
        } else {
            if (TextUtils.isEmpty(mDetail)) {
                return "剩余" + endtime[0] + "天" + endtime[1] + "小时" + endtime[2] + "分" + endtime[3] + "秒结束。已售" + mSoldCount + "件。";
            } else {
                return "剩余" + endtime[0] + "天" + endtime[1] + "小时" + endtime[2] + "分" + endtime[3] + "秒结束。已售" + mSoldCount + "件。" + mDetail;
            }
        }
    }

    /**
     * 邀请好友参团 （未参团情况下）分享title
     * namestr 商品名
     * purchasePricestr 团购价格
     */
    public static String getInviteFriendsTitle(String namestr, double purchasePricestr) {
        return namestr + "。限时秒杀价" + getMoneyString(purchasePricestr) + "元，快来抢购吧";
    }

    /**
     * 邀请好友参团 （已参团情况下）title
     * namestr 商品名
     * inviteCodestr 团购邀请码
     */
    public static String getInviteFriendsTitleready(String namestr, String inviteCodestr) {
        return "我的邀请码" + inviteCodestr + "！" + namestr + "在立减，快来抢购吧";
    }

    /**
     * 邀请好友参团 （已参团情况下）Text
     * mPurchasePricestr 商品团购价
     * mEndTime 团购剩余时间
     * mSoldcount 已售多少件
     * mDetail 商品介绍
     */
    public static String getInviteFriendsTextready(double mPurchasePricestr, long mEndTime, int mSoldcount, String mDetail) {
        String[] endtime = StringUtil.getSecondToDayHourMinutes(mEndTime * 1000).split(":");
        if (endtime[0].equals("00")) {
            if (TextUtils.isEmpty(mDetail)) {
                return "限时秒杀价" + getMoneyString(mPurchasePricestr) + "元，剩余" + endtime[1] + "小时" + endtime[2] + "分" + endtime[3]
                        + "秒结束。已售" + mSoldcount + "件。";
            } else {
                return "限时秒杀价" + getMoneyString(mPurchasePricestr) + "元，剩余" + endtime[1] + "小时" + endtime[2] + "分" + endtime[3]
                        + "秒结束。已售" + mSoldcount + "件。" + mDetail;
            }
        } else {
            if (TextUtils.isEmpty(mDetail)) {
                return "限时秒杀价" + getMoneyString(mPurchasePricestr) + "元，剩余" + endtime[0] + "天" + endtime[1] + "小时" + endtime[2] + "分"
                        + endtime[3] + "秒结束。已售" + mSoldcount + "件。";
            } else {
                return "限时秒杀价" + getMoneyString(mPurchasePricestr) + "元，剩余" + endtime[0] + "天" + endtime[1] + "小时" + endtime[2] + "分"
                        + endtime[3] + "秒结束。已售" + mSoldcount + "件。" + mDetail;
            }
        }
    }

    /**
     * 邀请好友参团 （未参团情况下）糖友汇内容
     * namestr 商品名
     * mEndTime 团购剩余时间
     */
    public static String getInviteFriendsSC(String namestr, long mEndTime) {
        String[] endtime = StringUtil.getSecondToDayHourMinutes(mEndTime * 1000).split(":");
        if (endtime[0].equals("00")) {
            return namestr + "。还有" + endtime[1] + "小时" + endtime[2] + "分" + endtime[3] + "秒团购结束，快来抢购吧！";
        } else {
            return namestr + "。还有" + endtime[0] + "天"
                    + endtime[1] + "小时" + endtime[2] + "分" + endtime[3] + "秒团购结束，快来抢购吧！";
        }
    }

    /**
     * 邀请好友参团 （已参团情况下）糖友汇内容
     * namestr 商品名
     * inviteCodestr 团购邀请码
     */
    public static String getInviteFriendsSCready(String namestr, String inviteCodestr, long mEndTime) {
        String[] endtime = StringUtil.getSecondToDayHourMinutes(mEndTime * 1000).split(":");
        if (endtime[0].equals("00")) {
            return "我团购了" + namestr + "，邀请码是" + inviteCodestr + "。支付输入邀请码，团购享立减。还有"
                    + endtime[1] + "小时" + endtime[2] + "分" + endtime[3] + "秒团购结束，快来抢购吧！";
        } else {
            return "我团购了" + namestr + "，邀请码是" + inviteCodestr + "。支付输入邀请码，团购享立减。还有"
                    + endtime[0] + "天" + endtime[1] + "小时" + endtime[2] + "分" + endtime[3] + "秒团购结束，快来抢购吧！";
        }
    }

}
