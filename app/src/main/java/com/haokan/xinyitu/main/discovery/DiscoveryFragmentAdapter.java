package com.haokan.xinyitu.main.discovery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haokan.xinyitu.R;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.main.DemoTagBean;
import com.haokan.xinyitu.main.FragmentAdapterItemHelper;
import com.haokan.xinyitu.util.ImgAndTagWallManager;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.Random;

public class DiscoveryFragmentAdapter extends BaseAdapter {
    private Context mContext;
    private FragmentAdapterItemHelper mHelper;
    private ArrayList<ResponseBeanAlbumInfo.DataBean> mAlbumInfoBean;
    ArrayList<ResponseBeanDiscovery> mData = new ArrayList<>();

    //制造假数据用的
    private static int[] sW = {1440, 599, 1024, 640, 1200, 400, 900};
    private static int[] sH = {900, 686, 797, 640, 2132, 594, 596};
    private static int[] sIds = {R.drawable.test01, R.drawable.test02, R.drawable.test03
            , R.drawable.test04, R.drawable.test05, R.drawable.test06, R.drawable.test07};
    private static String[] sTags = {"风景如画", "猫", "好看摄影大赛第一季", "中国好声音", "萌", "北京雾霾天", "丝竹"};

    public DiscoveryFragmentAdapter(Context context, ArrayList<ResponseBeanAlbumInfo.DataBean> albumInfoBean, View.OnClickListener onClickListener) {
        mContext = context;
        mAlbumInfoBean = albumInfoBean;

        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            ResponseBeanDiscovery bean = new ResponseBeanDiscovery();
            int type = random.nextInt(4);
//            int type = 0;
            bean.setType(type);
            if (type != 3) {
                ArrayList<DemoImgBean> list = new ArrayList<>();
                int imgCount = random.nextInt(12) + 1;
                for (int j = 0; j < imgCount; j++) {
                    DemoImgBean imgBean = new DemoImgBean();
                    int index = random.nextInt(7);
                    int id = sIds[index];
                    int w = sW[index];
                    int h = sH[index];
                    imgBean.setHeigh(h);
                    imgBean.setWidth(w);
                    imgBean.setPath(ImageDownloader.Scheme.DRAWABLE.wrap(String.valueOf(id)));
                    list.add(imgBean);
                }
                bean.setImgs(list);

                if (type == 0) {
                    ArrayList<DemoTagBean> list1 = new ArrayList<>();
                    int tagCount = random.nextInt(12) + 1;
                    for (int k = 0; k < tagCount; k++) {
                        DemoTagBean tag = new DemoTagBean();
                        int tagindex = random.nextInt(7);
                        tag.setName(sTags[tagindex]);
                        list1.add(tag);
                    }
                    bean.setTags(list1);
                }
            }
            mData.add(bean);
        }

        mHelper = new FragmentAdapterItemHelper(context, onClickListener);
        //处理数据中的imgLine和tags
        ImgAndTagWallManager.getInstance(context).processImgAndTagWallForItem0(mData);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResponseBeanDiscovery beanDiscovery = mData.get(position);
        int type = beanDiscovery.getType();
        switch (type) {
            case 0: //用户timeline
                convertView = mHelper.initItem0(position, beanDiscovery, convertView, parent);
                break;
            case 1: //今天最热图片
                convertView = mHelper.initItem1(position, beanDiscovery, convertView, parent);
                break;
            case 2: //摄影师推荐
                convertView = mHelper.initItem2(position, beanDiscovery, convertView, parent);
                break;
            case 3: //这些家伙很牛B，推荐6个用户头像
                convertView = mHelper.initItem3(position, beanDiscovery, convertView, parent);
                break;
            default:
                break;
        }
        return convertView;
    }
}
