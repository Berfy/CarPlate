package com.wlb.pndecoder.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Berfy on 2017/6/14.
 * 全局配置
 */

public class Constants {
    public static final String EXTRA_OFFLINE_ASR_BASE_FILE_PATH = "asr-base-file-path";
    public static final String EXTRA_LICENSE_FILE_PATH = "license-file-path";
    public static final String EXTRA_OFFLINE_SLOT_DATA = "slot-data";
    public static final String XML_PRICE_PERCENT = "percentPrice";
    public static final String XML_PHONE = "phone";
    public static final String XML_NUMBER = "number";
    //最多开启6个线程利用CPU 6核
    public static ExecutorService EXECUTOR = Executors.newFixedThreadPool(6);
}
