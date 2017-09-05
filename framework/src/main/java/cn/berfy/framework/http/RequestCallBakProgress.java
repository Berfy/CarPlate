package cn.berfy.framework.http;

/**
 * 
 * @ClassName: RequestCallBakProgress
 * @Description: 扩展了接口回调，如果需要监听进度，可实现此接口
 * @author Berfy
 *
 */
public interface RequestCallBakProgress<T> extends RequestCallBack<T> {
	/**
	 * 进度回调
	 * 
	 * @param currentSize
	 *            当前进度
	 * @param totalSize
	 *            总进度
	 */
	public void onProgress(int currentSize, int totalSize);
}
