package com.haokan.xinyitu.main.discovery;

import com.haokan.xinyitu.base.BaseRequestBean;

import java.util.List;

public class RequestBeanAlbumInfo extends BaseRequestBean {

    /**
     * album : [{"id":"101"},{"id":"102"}]
     */

    private List<AlbumBean> album;
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setAlbum(List<AlbumBean> album) {
        this.album = album;
    }

    public List<AlbumBean> getAlbum() {
        return album;
    }

    public static class AlbumBean {
        /**
         * id : 101
         */

        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
