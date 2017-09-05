package cn.berfy.framework.http;
/**
 * 
* @ClassName: NetResponse 
* @Description: 网络回调封装参数
* @author Berfy
*
 */
public class NetResponse<T> {
	/**
	 * 网络调用基本信息
	 */
	public NetMessage netMsg;
	/**
	 * 网络调用的数据信息
	 */
	public T content;
}
