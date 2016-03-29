package com.haokan.xinyitu.main.discovery;

import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.DemoTagBean;

import java.util.List;

public class ResponseBeanAlbumInfo extends BaseResponseBean {

    /**
     * data : [{"album_id":"131","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458887009","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"130","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458887009","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"129","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458886992","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"128","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458886845","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"127","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458886845","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"126","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458886823","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"125","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458886771","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"124","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458886409","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"123","user_id":"1000015","album_title":"组图标题","album_desc":"组图描述","createtime":"1458886323","tags":[{"id":"0932a2eb9924300a","name":"好看"}]},{"album_id":"122","user_id":"1000016","album_title":"组图标题","album_desc":"","createtime":"1458879905","tags":[{"id":"25084cf9c5f60a31","name":"测试标签4"},{"id":"65cfd300fe57111a","name":"测试标签2"},{"id":"cccd83be5cf131de","name":"测试标签1"},{"id":"f2e49ce23e813170","name":"测试标签3"}]}]
     */

    private List<DataBean> data;

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<DataBean> getData() {
        return data;
    }

    public static class DataBean {
        /**
         * album_id : 131
         * user_id : 1000015
         * album_title : 组图标题
         * album_desc : 组图描述
         * createtime : 1458887009
         * tags : [{"id":"0932a2eb9924300a","name":"好看"}]
         */

        private String album_id;
        private String user_id;
        private String album_title;
        private String album_desc;
        private String createtime;
        private List<DemoTagBean> tags;

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

        public List<DemoTagBean> getTags() {
            return tags;
        }
    }
}
