package cn.berfy.framework.utils;

import android.content.Context;
import android.graphics.Bitmap.Config;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 图像处理渲染工具
 *
 * @author Berfy
 */
public class ImageUtil {

    private static DisplayImageOptions mDefaultOptions;
    private static DisplayImageOptions mOptions;

    public static ImageLoaderConfiguration getConfiguration(Context context) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheSize(8 * 1024 * 1024)
                .threadPriority(3)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCacheSize(50 * 1024 * 1024) // 50 MiB
                .build();
        return configuration;
    }

    public static DisplayImageOptions getImageLoaderOptions(int loadingResId,int defaultResId) {
        if (null == mOptions) {
            mOptions = new DisplayImageOptions.Builder().displayer(new SimpleBitmapDisplayer())
                    .showImageOnLoading(loadingResId)
                    .showImageOnFail(defaultResId)
                    .cacheInMemory(true).cacheOnDisc(true)
                    .bitmapConfig(Config.RGB_565)
                    .showImageForEmptyUri(defaultResId).build();
        }
        return mOptions;
    }
}
