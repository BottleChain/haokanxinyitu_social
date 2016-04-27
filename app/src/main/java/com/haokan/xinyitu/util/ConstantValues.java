package com.haokan.xinyitu.util;

public class ConstantValues {

    //一些sharedpreference用到的key
    public static String KEY_SP_USERID = "userid";
    public static String KEY_SP_SESSIONID = "sessionid";
    public static String KEY_SP_AVATAR_URL = "avatarurl"; //保存的头像地址
    public static String KEY_SP_NICKNAME = "nickname"; //保存的昵称
    public static String KEY_SP_DESC = "desc"; //个人描述
    public static String KEY_SP_PHONENUM = "phone"; //个人手机号
    public static String KEY_SP_WEIXIN = "weixin"; //个人手机号
    public static String KEY_SP_WEIBO = "weibo";
    public static String KEY_SP_QQ = "qq";
    public static String KEY_SP_GETLOCATION = "getlocation"; //是否获取地理位置的开关

    public static String PATH_CLIP_PH = "user_cut_ph"; //剪裁的头像存储的位置
    public static String PATH_DOWNLOAD_PIC = "/HaoKanYiTu/YituDownloadPic/"; //保存下载的图片到本地用的位置
    public static String PATH_DOWNLOAD_UPDATA_APK = "/HaoKanYiTu/Updata/";
    public static String OS = "android";

    public static String ACTION_MYFOLLOWS_CHANGE = "action_myfollows_change";
    public static String ACTION_LIKESTATUS_CHANGE = "action_likestatus_change";
    public static String KEY_ALBUMID = "albumid";
    public static String KEY__USERID = "userid";
    public static String KEY_NEWSTATUS = "status";

    /** gridView列表页每次访问服务器加载多少个图片，按页访问，每页的数量 */
    public static int GRID_PAGE_SIZE = 30;
    /** 还剩下多少个的时候去加载更多 */
    public static final int LOAD_MORE_DELTA = 15;

    /** 发现页每次访问服务器加载多少个图片，按页访问，每页的数量 */
    public static final int DISCOVERY_PAGE_SIZE = 8;

    /**
     * 网络错误时，等待延时
     */
    public static final int NET_ERROR_DELAY = 500;

    /**
     * 是否开启锁屏服务用到key
     */
    public static final String KEY_SHARED_LOCKSCREEN = "key_shared_lockscreen";
    /**
     * 是否提示没有打开锁屏+开关的提示框
     */
    public static final String KEY_SHARED_LOCKSCREEN_SW = "key_shared_lockscreen_sw";

    /**
     * 应用的主色调，一些小色块和订阅按钮颜色用
     */
    public static final int COLOR_MAIN = 0xFF11A5EE;

    /** gridView展示图片的页面，没页能显示多少个，也就是convertView每隔多少个就会被复用 */
    public static final int REUSED_INTERVAL_COUNT_GRIDIMG = 2;

//    target_type定义
//    0 : 不可点击
//    1 : 进入单期杂志   target_paras:   id   :  单期杂志ID(必有)
//    2 : 进入杂志专区   target_paras:   id   :  杂志专区ID(必有)
//    3 : 进入分类页面   target_paras:   id   :  分类ID（可有）
//    4 : 进入标签页面   target_paras:   tag  :  标签名称（必有）
//    5 : 进入Web页面  target_paras:   url   :  web地址(必有)
    public static class TargetType {
        public static final int TARGET_MAGAZINE_ZONE= 2;
        public static final int TARGET_TAG = 4;
    }

    public static class ImageSize {
//        styleName:720x1280,styleBody:1e_720w_1280h_0c_0i_0o_90Q_1x.jpg
//        styleName:540x960,styleBody:1e_540w_960h_0c_0i_0o_90Q_1x.jpg
//        styleName:1440x2560,styleBody:1e_1440w_2560h_0c_0i_0o_90Q_1x.jpg
//        styleName:351x624,styleBody:1e_351w_624h_0c_1i_0o_75Q_1x.jpg
//        styleName:351x624q75,styleBody:1e_351w_624h_0c_1i_0o_75Q_1x.jpg
//        styleName:1536x2560,styleBody:1e_1536w_2560h_0c_1i_0o_1x.jpg
//        styleName:1080x1920,styleBody:1e_1080w_1920h_0c_0i_0o_1x.jpg
//        styleName:540x960q75,styleBody:1e_540w_960h_0c_0i_0o_75Q_1x.jpg
//        styleName:720x1280q75,styleBody:1e_720w_1080h_0c_0i_0o_75Q_1x.jpg
//        styleName:1080x1920q75,styleBody:1e_1080w_1920h_0c_0i_0o_75Q_1x.jpg
//        styleName:1440x2560q75,styleBody:1e_1440w_2560h_0c_0i_0o_75Q_1x.jpg
//        styleName:180x320,styleBody:1e_180w_320h_0c_0i_0o_75Q_1x.jpg
//        styleName:80x142,styleBody:1e_80w_142h_0c_0i_0o_75Q_1x.jpg
//        styleName:480x854,styleBody:1e_480w_854h_0c_0i_0o_90Q_1x.jpg
//        styleName:480x854q75,styleBody:1e_480w_854h_0c_0i_0o_75Q_1x.jpg
//        styleName:240x427q75,styleBody:1e_240w_427h_0c_0i_0o_75Q_1x.jpg

        public static final String SIZE_80x142= "80x142";
        public static final String SIZE_180x320= "180x320";
        public static final String SIZE_240x427= "240x427";
        public static final String SIZE_351x624= "351x624";
        public static final String SIZE_480x854= "480x854";
        public static final String SIZE_540x960= "540x960";
        public static final String SIZE_720x1280= "720x1280";
        public static final String SIZE_1080x1920= "1080x1920";
        public static final String SIZE_1440x2560= "1440x2560";
        public static final String SIZE_1536x2560= "1536x2560";
    }
}
