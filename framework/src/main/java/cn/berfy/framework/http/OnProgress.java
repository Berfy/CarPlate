package cn.berfy.framework.http;

/**
 * 
 * @ClassName: OnProgress
 * @Description: 进度回调
 * @author Berfy
 *
 */
public interface OnProgress {
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
