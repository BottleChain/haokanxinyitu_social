package com.haokan.xinyitu.main.mypersonalcenter;

import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

public class ResponseBeanAlbumListPersonnal extends BaseResponseBean {

    /**
     * data : [{"album_id":"271"},{"album_id":"257"},{"album_id":"256"},{"album_id":"242"},{"album_id":"241"},{"album_id":"240"},{"album_id":"239"},{"album_id":"238"},{"album_id":"230"},{"album_id":"223"},{"album_id":"222"},{"album_id":"221"},{"album_id":"218"},{"album_id":"217"},{"album_id":"201"},{"album_id":"198"},{"album_id":"197"},{"album_id":"196"},{"album_id":"195"},{"album_id":"191"},{"album_id":"178"},{"album_id":"177"},{"album_id":"176"},{"album_id":"175"},{"album_id":"174"},{"album_id":"173"},{"album_id":"172"},{"album_id":"171"},{"album_id":"170"},{"album_id":"169"},{"album_id":"168"},{"album_id":"167"},{"album_id":"166"},{"album_id":"164"},{"album_id":"153"}]
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
         * album_id : 271
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
