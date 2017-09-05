package cn.berfy.framework.http;

/**
 * @author Berfy
 *         网络请求基类
 */
public class NetMessage {
    /**
     * 错误代码
     */
    public static final int ERROR = 400;
    /**
     * 是否是缓存
     */
    public boolean isCache;
    /**
     * 接口调用错误信息 用于Log查看信息
     */
    public String msg;
    /**
     * 错误代码
     */
    public int code;
    /**
     * 接口返回数据信息
     */
    public String data;
}
