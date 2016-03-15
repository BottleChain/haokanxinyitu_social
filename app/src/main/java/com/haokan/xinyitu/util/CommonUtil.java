package com.haokan.xinyitu.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.text.TextUtils;

public class CommonUtil {
    private static final String TAG = "CommonUtil";
    private static long sLastClickTime;
    public static boolean isQuickClick() {
        long time = SystemClock.uptimeMillis();
        if ( time - sLastClickTime < 600) {
            return true;
        }
        sLastClickTime = time;
        return false;
    }

    /**
     * 获取应用程序版本的名称，清单文件中的versionName属性
     */
    public static String getLocalVersionName(Context c) {
        try {
            PackageManager manager = c.getPackageManager();
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * 获取应用程序版本的名称，清单文件中的versionCode属性
     */
    public static int getLocalVersionCode(Context c) {
        try {
            PackageManager manager = c.getPackageManager();
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取渠道id
     */
    public static String getChannelID(Context c) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = c.getPackageManager().getApplicationInfo(
                    c.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo == null) {
            return "212 ";
        }
        return String.valueOf(appInfo.metaData.getInt("UMENG_CHANNEL"));
    }


    /**
     * 获取一些设备信息
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if( TextUtils.isEmpty(device_id) ){
                device_id = mac;
            }

            if( TextUtils.isEmpty(device_id) ){
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 每行3列的图片用到的尺寸
//     */
//    public static String getSmallImgUrlSuffix(Context context) {
//        int density = 3;
//        if (context != null) {
//            density = (int) (context.getResources().getDisplayMetrics().density + .5f);
//        }
//        if (density == 2) {
//            return ConstantValues.ImageSize.SIZE_240x427;
//        } else if (density == 3) {
//            return ConstantValues.ImageSize.SIZE_351x624;
//        } else if (density > 3) {
//            return ConstantValues.ImageSize.SIZE_351x624;
//        } else {
//            return ConstantValues.ImageSize.SIZE_180x320;
//        }
//    }

    /**
     * 每行4列的图片用到的尺寸
     */
//    public static String getMiniImgUrlSuffix(Context context) {
//        int density = 3;
//        if (context != null) {
//            density = (int) (context.getResources().getDisplayMetrics().density + .5f);
//        }
//        if (density == 2) {
//            return ConstantValues.ImageSize.SIZE_180x320;
//        } else if (density == 3) {
//            return ConstantValues.ImageSize.SIZE_240x427;
//        } else if (density > 3) {
//            return ConstantValues.ImageSize.SIZE_351x624;
//        } else {
//            return ConstantValues.ImageSize.SIZE_180x320;
//        }
//    }

//    public static String getBigImgUrlSuffix(Context context) {
//        int density = 3;
//        if (context != null) {
//            density = (int) (context.getResources().getDisplayMetrics().density + .5f);
//        }
//        if (density == 2) {
//            return ConstantValues.ImageSize.SIZE_720x1280;
//        } else if (density == 3) {
//            return ConstantValues.ImageSize.SIZE_1080x1920;
//        } else if (density > 3) {
//            return ConstantValues.ImageSize.SIZE_1080x1920;
//        } else {
//            return ConstantValues.ImageSize.SIZE_540x960;
//        }
//    }

    /**
     * 根据已有的url，返回大图url，格式例如：xxxx.jpg@!1080x1920q75
     */
//    public static String getBigImgUrl(Context context, String url) {
//        LogHelper.i("CommonUtil", "url = " + url);
//        String bigUrl;
//        int i = url.lastIndexOf("!");
//        if (i == -1) {
//            bigUrl = url + "@!" + CommonUtil.getBigImgUrlSuffix(context) + "q75";
//        } else {
//            bigUrl = url.substring(0, i + 1) + CommonUtil.getBigImgUrlSuffix(context) + "q75";
//        }
//        LogHelper.i("CommonUtil", "bigUrl = " + bigUrl);
//        return bigUrl;
//        return getBigImgUrlForWebp(context, url);
//    }

    /**
     * 根据已有的url
     */
//    public static String getBigImgUrlForWebp(Context context, String url) {
//        LogHelper.i("CommonUtil", "url = " + url);
//        String bigUrl;
//        int i = url.lastIndexOf("!");
//        if (i == -1) {
//            bigUrl = url + "@!" + CommonUtil.getBigImgUrlSuffix(context) + ".webp";
//        } else {
//             //http://img.wp.levect.com/14536860227724.jpg@!240x427.webp
//            bigUrl = url.replace(url.substring(i + 1, i + 8), CommonUtil.getBigImgUrlSuffix(context));
//            //bigUrl = url.substring(0, i + 1) + CommonUtil.getBigImgUrlSuffix(context) + ".webp";
//        }
//        LogHelper.i("CommonUtil", "bigUrl = " + bigUrl);
//        return bigUrl;
//    }

}
