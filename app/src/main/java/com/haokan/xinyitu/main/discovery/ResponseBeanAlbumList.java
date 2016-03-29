package com.haokan.xinyitu.main.discovery;

import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

public class ResponseBeanAlbumList extends BaseResponseBean {

    /**
     * data : {"AlbumList":[{"album_id":"131"},{"album_id":"130"},{"album_id":"129"},{"album_id":"128"},{"album_id":"127"},{"album_id":"126"},{"album_id":"125"},{"album_id":"124"},{"album_id":"123"},{"album_id":"122"},{"album_id":"121"},{"album_id":"120"},{"album_id":"119"},{"album_id":"118"},{"album_id":"117"},{"album_id":"116"},{"album_id":"115"},{"album_id":"114"},{"album_id":"113"},{"album_id":"112"},{"album_id":"111"},{"album_id":"110"},{"album_id":"109"},{"album_id":"108"},{"album_id":"107"},{"album_id":"106"},{"album_id":"105"},{"album_id":"104"},{"album_id":"103"},{"album_id":"102"},{"album_id":"101"},{"album_id":"100"},{"album_id":"99"},{"album_id":"98"},{"album_id":"97"},{"album_id":"96"},{"album_id":"95"},{"album_id":"94"},{"album_id":"93"},{"album_id":"92"},{"album_id":"91"},{"album_id":"90"},{"album_id":"89"},{"album_id":"88"},{"album_id":"87"},{"album_id":"86"},{"album_id":"85"},{"album_id":"84"},{"album_id":"83"},{"album_id":"82"},{"album_id":"81"},{"album_id":"80"},{"album_id":"79"},{"album_id":"78"},{"album_id":"77"},{"album_id":"76"},{"album_id":"75"},{"album_id":"74"},{"album_id":"73"},{"album_id":"72"},{"album_id":"71"},{"album_id":"70"},{"album_id":"69"},{"album_id":"68"},{"album_id":"67"},{"album_id":"66"},{"album_id":"65"},{"album_id":"64"},{"album_id":"63"},{"album_id":"62"},{"album_id":"61"},{"album_id":"60"},{"album_id":"59"},{"album_id":"58"},{"album_id":"57"},{"album_id":"56"},{"album_id":"55"},{"album_id":"54"},{"album_id":"53"},{"album_id":"52"},{"album_id":"51"},{"album_id":"50"},{"album_id":"49"},{"album_id":"48"},{"album_id":"47"},{"album_id":"46"},{"album_id":"45"},{"album_id":"44"},{"album_id":"43"},{"album_id":"42"},{"album_id":"41"},{"album_id":"40"},{"album_id":"39"},{"album_id":"38"},{"album_id":"37"},{"album_id":"36"},{"album_id":"35"},{"album_id":"34"},{"album_id":"33"},{"album_id":"32"},{"album_id":"31"},{"album_id":"30"},{"album_id":"29"},{"album_id":"28"},{"album_id":"27"},{"album_id":"26"},{"album_id":"25"},{"album_id":"24"},{"album_id":"23"},{"album_id":"22"},{"album_id":"21"},{"album_id":"20"},{"album_id":"19"},{"album_id":"18"},{"album_id":"17"},{"album_id":"16"},{"album_id":"15"},{"album_id":"14"},{"album_id":"13"},{"album_id":"12"},{"album_id":"11"},{"album_id":"10"},{"album_id":"9"},{"album_id":"8"},{"album_id":"7"},{"album_id":"6"},{"album_id":"5"},{"album_id":"4"},{"album_id":"3"},{"album_id":"2"},{"album_id":"1"}]}
     */

    private DataBean data;

    public void setData(DataBean data) {
        this.data = data;
    }

    public DataBean getData() {
        return data;
    }

    public static class DataBean {
        /**
         * AlbumList : [{"album_id":"131"},{"album_id":"130"},{"album_id":"129"},{"album_id":"128"},{"album_id":"127"},{"album_id":"126"},{"album_id":"125"},{"album_id":"124"},{"album_id":"123"},{"album_id":"122"},{"album_id":"121"},{"album_id":"120"},{"album_id":"119"},{"album_id":"118"},{"album_id":"117"},{"album_id":"116"},{"album_id":"115"},{"album_id":"114"},{"album_id":"113"},{"album_id":"112"},{"album_id":"111"},{"album_id":"110"},{"album_id":"109"},{"album_id":"108"},{"album_id":"107"},{"album_id":"106"},{"album_id":"105"},{"album_id":"104"},{"album_id":"103"},{"album_id":"102"},{"album_id":"101"},{"album_id":"100"},{"album_id":"99"},{"album_id":"98"},{"album_id":"97"},{"album_id":"96"},{"album_id":"95"},{"album_id":"94"},{"album_id":"93"},{"album_id":"92"},{"album_id":"91"},{"album_id":"90"},{"album_id":"89"},{"album_id":"88"},{"album_id":"87"},{"album_id":"86"},{"album_id":"85"},{"album_id":"84"},{"album_id":"83"},{"album_id":"82"},{"album_id":"81"},{"album_id":"80"},{"album_id":"79"},{"album_id":"78"},{"album_id":"77"},{"album_id":"76"},{"album_id":"75"},{"album_id":"74"},{"album_id":"73"},{"album_id":"72"},{"album_id":"71"},{"album_id":"70"},{"album_id":"69"},{"album_id":"68"},{"album_id":"67"},{"album_id":"66"},{"album_id":"65"},{"album_id":"64"},{"album_id":"63"},{"album_id":"62"},{"album_id":"61"},{"album_id":"60"},{"album_id":"59"},{"album_id":"58"},{"album_id":"57"},{"album_id":"56"},{"album_id":"55"},{"album_id":"54"},{"album_id":"53"},{"album_id":"52"},{"album_id":"51"},{"album_id":"50"},{"album_id":"49"},{"album_id":"48"},{"album_id":"47"},{"album_id":"46"},{"album_id":"45"},{"album_id":"44"},{"album_id":"43"},{"album_id":"42"},{"album_id":"41"},{"album_id":"40"},{"album_id":"39"},{"album_id":"38"},{"album_id":"37"},{"album_id":"36"},{"album_id":"35"},{"album_id":"34"},{"album_id":"33"},{"album_id":"32"},{"album_id":"31"},{"album_id":"30"},{"album_id":"29"},{"album_id":"28"},{"album_id":"27"},{"album_id":"26"},{"album_id":"25"},{"album_id":"24"},{"album_id":"23"},{"album_id":"22"},{"album_id":"21"},{"album_id":"20"},{"album_id":"19"},{"album_id":"18"},{"album_id":"17"},{"album_id":"16"},{"album_id":"15"},{"album_id":"14"},{"album_id":"13"},{"album_id":"12"},{"album_id":"11"},{"album_id":"10"},{"album_id":"9"},{"album_id":"8"},{"album_id":"7"},{"album_id":"6"},{"album_id":"5"},{"album_id":"4"},{"album_id":"3"},{"album_id":"2"},{"album_id":"1"}]
         */

        private List<AlbumListBean> AlbumList;

        public void setAlbumList(List<AlbumListBean> AlbumList) {
            this.AlbumList = AlbumList;
        }

        public List<AlbumListBean> getAlbumList() {
            return AlbumList;
        }

        public static class AlbumListBean {
            /**
             * album_id : 131
             */

            private String album_id;

            public void setAlbum_id(String album_id) {
                this.album_id = album_id;
            }

            public String getAlbum_id() {
                return album_id;
            }
        }
    }
}
