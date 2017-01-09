package com.haokan.xinyitu;

import android.app.Application;

import com.haokan.xinyitu.follow.ResponseBeanFollwsUsers;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.main.DemoTagBean;
import com.haokan.xinyitu.main.discovery.AlbumInfoBean;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    public static String sessionId = null;
    public static String user_Id = "";
    public static float sDensity = 3;
    public static int sPreviewImgSize = 240; //加载图片时缩略图的大小，应该在splash时根据density给此值赋值，默认240宽
    public static int sBigImgSize = 720; //加载图片时大图的大小，应该在splash时根据density给此值赋值，默认240宽
    public static String sTagString = ""; //已经进过的tag标签，防止重复进

    @Override
    public void onCreate() {
        super.onCreate();
        //MobclickAgent.setDebugMode(true);
        ImageLoaderManager.initImageLoader(getApplicationContext()); //初始化imageLoader

        //微信 appid appsecret
        PlatformConfig.setWeixin("wx4d3ef877cd74ef1c", "273c0e55b07f61dc90110369df845583");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("3791834354", "916ab8034b2b4617dc1e77aa6b166525");
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone("1104684488", "KIlK2enqcjHR2EsU");
        //Log.LOG = false;
    }

    //图库选择图片时，点击大图时预览，预览大图时可以改变选中的状态，
    //为了方便，使这两个activity公用一套数据，以便于一个界面改变了数据中选中状态另一边可以直接拿来用，
    //所以用app来做为了中转，持有了数据集合的引用。
    //目前为了方便，所有跳转到大图预览的activity负责给这个data赋值，大图activity直接从这取就可以，
    // 注意！！！--- 给这个data赋值的activity在销毁时一定负责置空此引用，减少不必要的内存开销
    private ArrayList<DemoImgBean> mBigImgData = new ArrayList<>();
    private ArrayList<DemoImgBean> mCheckedImgs = new ArrayList<>();
    private ArrayList<ArrayList<DemoImgBean>> mImgDirs = new ArrayList<>();

    /**
     * 用户最新上传的一个组图，为了上传完后立马在主页面显示上传的结果，用这个变量temp一下
     * 上传完成后，给这个值赋值，然后通知主页，主页获取后取到这个值然后添加在第一个位置，然后情况这个值
     */
    private AlbumInfoBean mLastestUploadAlbum;

    public AlbumInfoBean getLastestUploadAlbum() {
        return mLastestUploadAlbum;
    }

    public void setLastestUploadAlbum(AlbumInfoBean lastestUploadAlbum) {
        mLastestUploadAlbum = lastestUploadAlbum;
    }

    public ArrayList<ArrayList<DemoImgBean>> getImgDirs() {
        return mImgDirs;
    }

    public void setImgDirs(ArrayList<ArrayList<DemoImgBean>> imgDirs) {
        mImgDirs = imgDirs;
    }

    public ArrayList<DemoImgBean> getBigImgData() {
        return mBigImgData;
    }

    public void setBigImgData(ArrayList<DemoImgBean> bigImgData) {
        mBigImgData = bigImgData;
    }

    public ArrayList<DemoImgBean> getCheckedImgs() {
        return mCheckedImgs;
    }

    public void setCheckedImgs(ArrayList<DemoImgBean> checkedImgs) {
        mCheckedImgs = checkedImgs;
    }

    /**
     * 发布作品是需要添加标签，标签页和添加页之间的标签数据需要来回传递，用intent麻烦，而且数据model没必要来回new新的，用app共享数据
     * //注意置空
     */
    private ArrayList<DemoTagBean> mTagsTemp;

    public ArrayList<DemoTagBean> getTagsTemp() {
        return mTagsTemp;
    }

    public void setTagsTemp(ArrayList<DemoTagBean> tagsTemp) {
        mTagsTemp = tagsTemp;
    }

    /**
     * 我关注的人的id，一个公共数据，因为很多地方都要显示我关注的人，并且一个地方变动，其他地方都要跟着改变，
     * 所以用这个共同的数据来维护，ps.关注的人很多时，可能需要改成用数据库存储的方式，暂时先用这个
     */
    public static List<ResponseBeanFollwsUsers.FollowUserIdBean> sMyFollowsUser = new ArrayList<>();
}
