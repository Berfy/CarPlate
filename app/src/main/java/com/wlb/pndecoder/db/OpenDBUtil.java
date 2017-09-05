package com.wlb.pndecoder.db;

/**
 * 所有表的名字
 * @author Berfy
 */
public class OpenDBUtil {
    public static final String TAB_HISTROY = "tab_histroy";// 历史车牌

    public static final String[] KEYS_TAB_HISTROY = new String[]{
            "phone", "number", "path", "status", "updateTime"};

    public static final String TAB_CREATE_HISTROY = "CREATE TABLE if not exists "
            + TAB_HISTROY
            + "("
            + KEYS_TAB_HISTROY[0]
            + " text not null,"
            + KEYS_TAB_HISTROY[1]
            + " text not null,"
            + KEYS_TAB_HISTROY[2]
            + " text not null,"
            + KEYS_TAB_HISTROY[3]
            + " integer not null,"
            + KEYS_TAB_HISTROY[4]
            + " text not null )";

    public static final String TAB_USER = "tab_user";// 历史车牌

    public static final String[] KEYS_TAB_USER = new String[]{
            "phone", "name", "updateTime"};

    public static final String TAB_CREATE_USER = "CREATE TABLE if not exists "
            + TAB_USER
            + "("
            + KEYS_TAB_USER[0]
            + " text not null,"
            + KEYS_TAB_USER[1]
            + " text not null,"
            + KEYS_TAB_USER[2]
            + " text not null )";
}
