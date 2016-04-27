package com.haokan.xinyitu.util;

import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * 缓存清除管理类
 */
public class CacheManager {

    /**
     * 获取本应用的缓存大小
     */
    public static String getCacheSize(Context context) {
        Log.d("wangzixu", "updataCacheSize getFolderSize start...");
        long cacheSize = FileUtil.getFolderSize(ImageLoader.getInstance().getDiskCache().getDirectory());
        Log.d("wangzixu", "updataCacheSize getFolderSize end...");
//        long cacheSize1 = FileUtil.getFolderSize(getWebViewAppCacheDir(context));
//        long cacheSize2 = FileUtil.getFolderSize(getWebViewDbCacheDir(context));
        return FileUtil.getFormatSize(cacheSize);
    }

    /**
     * 清理缓存
     */
    public static void clearCache(Context context) {
        File directory = ImageLoader.getInstance().getDiskCache().getDirectory();
        FileUtil.deleteContents(directory);
        //ImageLoader.getInstance().getDiskCache().clear();
//        FileUtil.deleteContents(getWebViewAppCacheDir(context));
//        FileUtil.deleteContents(getWebViewDbCacheDir(context));
    }

//    public static File getWebViewAppCacheDir(Context context) {
//        return new File(context.getCacheDir(), "web_cache");
//    }
//
//    public static File getWebViewDbCacheDir(Context context) {
//        return new File(context.getCacheDir(), "web_db");
//    }
}
