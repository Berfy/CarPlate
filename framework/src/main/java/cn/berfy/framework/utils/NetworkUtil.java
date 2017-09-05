package cn.berfy.framework.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;

/**
 * @category 网络工具
 * @author yuepengfei
 * */
public class NetworkUtil {

	private final static String TAG = NetworkUtil.class.getSimpleName();
	public static final Uri CDMA_PREFERAPN_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn"); // for CDMA
	public static final Uri GSM_PREFERAPN_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn2"); // for GSM
	public static final String DEFAULT_DATA_NETWORK = "default_data_network";
	public static final String SAVED_DATA_NETWORK = "saved_data_network";
	public static final String CDMA_DATA_NETWORK = "cdma";
	public static final String GSM_DATA_NETWORK = "gsm";
	public static final String NONE_DATA_NETWORK = "none";
	public static int port = 80;
	public static String usrAgent;

	/**
	 * 网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isHasNetWork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) { // 当前网络不可用
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getWapProxyAddr(Context ctx) {
		String isPROXY = "";
		try {
			if (isWifiConnected(ctx)) {
				return null;
			}

			Uri uri;
			if (GSM_DATA_NETWORK.equals(getPreferredDataNetwork(ctx))) {
				uri = GSM_PREFERAPN_APN_URI;
			} else {
				uri = CDMA_PREFERAPN_APN_URI;
			}

			String projection[] = { "name", "apn", "proxy", "port" };
			Cursor c = ctx.getContentResolver().query(uri, projection, null,
					null, null);
			if (c == null) {
				return null;
			}
			if (c.getCount() <= 0) {
				c.close();
				return null;
			}
			c.moveToFirst();
			isPROXY = c.getString(c.getColumnIndex("proxy"));/* .trim() */
			LogUtil.i(TAG, "--------->isPROXY:" + isPROXY);
			String str_port = c.getString(c.getColumnIndex("port"));
			String str_apn = c.getString(c.getColumnIndex("apn"));
			LogUtil.i(TAG, "--------->str_apn:" + str_apn);
			if (str_port != null && !str_port.equals("")) {
				LogUtil.i(TAG, "--------->str_port:" + str_port);
				port = Integer.valueOf(str_port);
			}
			c.close();
		} catch (Exception e) {
		}

		return isPROXY;
	}

	@SuppressWarnings("deprecation")
	public static String isCMWAP(Context ctx) {
		if (isWifiConnected(ctx)) {
			return null;
		}

		Uri uri;
		if (GSM_DATA_NETWORK.equals(getPreferredDataNetwork(ctx))) {
			uri = GSM_PREFERAPN_APN_URI;
		} else {
			uri = CDMA_PREFERAPN_APN_URI;
		}
		String projection[] = { "name", "apn", "proxy", "port" };
		Cursor c = ctx.getContentResolver().query(uri, projection, null, null,
				null);

		if (c == null || c.getCount() <= 0) {
			return null;
		}
		c.moveToFirst();
		String str_port = c.getString(3);
		String apn = c.getString(1);
		if ("3gwap".equals(apn.toLowerCase())) {
			return null;
		}
		if ("cmwap".equals(apn.toLowerCase())) {
			return android.net.Proxy.getDefaultHost();
		}

		if (str_port != null && !str_port.equals("")) {
			port = Integer.valueOf(str_port);
		}
		c.close();
		return android.net.Proxy.getDefaultHost();
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getWapProxyPort(Context ctx) {
		return port;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return mWiFiNetworkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * none=-1,mobile=0,wifi=1... more {@link ConnectivityManager}
	 * 
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 读取默认网络类型
	 * 
	 * @param context
	 * @return gsm �??cdma网络类型
	 */
	private static String getPreferredDataNetwork(Context context) {
		String preferredDataNetwork = Settings.System.getString(
				context.getContentResolver(), DEFAULT_DATA_NETWORK);
		if (NONE_DATA_NETWORK.equals(preferredDataNetwork)) {
			preferredDataNetwork = Settings.System.getString(
					context.getContentResolver(), SAVED_DATA_NETWORK);
		}
		if (!GSM_DATA_NETWORK.equals(preferredDataNetwork)) {
			preferredDataNetwork = CDMA_DATA_NETWORK;
		}
		return preferredDataNetwork;
	}
}
