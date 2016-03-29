package com.haokan.xinyitu;

import android.app.Application;

import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.main.DemoTagBean;
import com.haokan.xinyitu.util.ImageLoaderManager;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;

public class App extends Application {
    public static String sessionId = "";

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
}
