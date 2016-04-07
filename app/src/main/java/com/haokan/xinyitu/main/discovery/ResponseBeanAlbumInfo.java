package com.haokan.xinyitu.main.discovery;

import com.haokan.xinyitu.base.AvatarUrlBean;
import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.main.DemoTagBean;

import java.util.List;

public class ResponseBeanAlbumInfo extends BaseResponseBean {

    /**
     * data : [{"album_id":"151","user_id":"1000016","album_title":"组图标题","album_desc":"测试上传","createtime":"1459307024","images":[{"id":"100315","name":"145930649666673131964.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/64/19/145930649666673131964.jpg@!fw240","width":"800","height":"411"},{"id":"100316","name":"145930649666667234593.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/93/45/145930649666667234593.jpg@!fw240","width":"800","height":"415"},{"id":"100317","name":"145930649666061963943.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/43/39/145930649666061963943.jpg@!fw240","width":"800","height":"409"},{"id":"100318","name":"145930702398064278561.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/61/85/145930702398064278561.jpg@!fw240","width":"800","height":"413"},{"id":"100319","name":"145930702403460579161.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/61/91/145930702403460579161.jpg@!fw240","width":"800","height":"416"}],"tags":[{"id":"1ecf3adb83863f05","name":"动物"},{"id":"853dc138a581c9a7","name":"北京雾霾天"},{"id":"869b3abbe2771fe3","name":"中国好声音"}],"nickname":"用户_1457510026808822","avatar_url":{"50":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/62/15/145930787122507831562.jpeg@!at50","100":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/62/15/145930787122507831562.jpeg@!at100","150":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/62/15/145930787122507831562.jpeg@!at150"}},{"album_id":"150","user_id":"1000014","album_title":"aaaaa","album_desc":"呵呵哈哈","createtime":"1459306846","images":[{"id":"100152","name":"","url":"","width":"","height":""},{"id":"100153","name":"","url":"","width":"","height":""},{"id":"100154","name":"","url":"","width":"","height":""},{"id":"100155","name":"","url":"","width":"","height":""}],"tags":[{"id":"99ab0bf4e9d02dfd","name":"t1"},{"id":"cf68c399c5f4cf32","name":"t2"}],"nickname":"","avatar_url":{"50":"","100":"","150":""}}]
     */

    private List<DataEntity> data;

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public static class DataEntity {
        /**
         * album_id : 151
         * user_id : 1000016
         * album_title : 组图标题
         * album_desc : 测试上传
         * createtime : 1459307024
         * images : [{"id":"100315","name":"145930649666673131964.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/64/19/145930649666673131964.jpg@!fw240","width":"800","height":"411"},{"id":"100316","name":"145930649666667234593.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/93/45/145930649666667234593.jpg@!fw240","width":"800","height":"415"},{"id":"100317","name":"145930649666061963943.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/43/39/145930649666061963943.jpg@!fw240","width":"800","height":"409"},{"id":"100318","name":"145930702398064278561.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/61/85/145930702398064278561.jpg@!fw240","width":"800","height":"413"},{"id":"100319","name":"145930702403460579161.jpg","url":"http://haokanres.img-cn-hangzhou.aliyuncs.com/images/61/91/145930702403460579161.jpg@!fw240","width":"800","height":"416"}]
         * tags : [{"id":"1ecf3adb83863f05","name":"动物"},{"id":"853dc138a581c9a7","name":"北京雾霾天"},{"id":"869b3abbe2771fe3","name":"中国好声音"}]
         * nickname : 用户_1457510026808822
         * avatar_url : {"50":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/62/15/145930787122507831562.jpeg@!at50","100":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/62/15/145930787122507831562.jpeg@!at100","150":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/62/15/145930787122507831562.jpeg@!at150"}
         */
        private String album_id;
        private String user_id;
        private String album_title;
        private String album_desc;
        private String createtime;
        private String nickname;
        private List<DemoImgBean> images;
        private List<DemoTagBean> tags;
        private AvatarUrlBean avatar_url;
        private int type; //发现页第2,6,12显示不同个type

        public AvatarUrlBean getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(AvatarUrlBean avatar_url) {
            this.avatar_url = avatar_url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setAlbum_id(String album_id) {
            this.album_id = album_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public void setAlbum_title(String album_title) {
            this.album_title = album_title;
        }

        public void setAlbum_desc(String album_desc) {
            this.album_desc = album_desc;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setImages(List<DemoImgBean> images) {
            this.images = images;
        }

        public void setTags(List<DemoTagBean> tags) {
            this.tags = tags;
        }

        public String getAlbum_id() {
            return album_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getAlbum_title() {
            return album_title;
        }

        public String getAlbum_desc() {
            return album_desc;
        }

        public String getCreatetime() {
            return createtime;
        }

        public String getNickname() {
            return nickname;
        }

        public List<DemoImgBean> getImages() {
            return images;
        }

        public List<DemoTagBean> getTags() {
            return tags;
        }
    }
}
