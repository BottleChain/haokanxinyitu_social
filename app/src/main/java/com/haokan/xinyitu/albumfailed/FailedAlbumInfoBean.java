package com.haokan.xinyitu.albumfailed;

import com.haokan.xinyitu.base.BaseBean;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 发布失败的组图
 */
@DatabaseTable(tableName = "table_failedalbuminfo")
public class FailedAlbumInfoBean extends BaseBean {

    @DatabaseField(generatedId = true)
    private int _id;

    @DatabaseField
    private String userId;

    @DatabaseField
    private String album_id;

    @DatabaseField
    private String img_urls;

    @DatabaseField
    private String tags;

    @DatabaseField
    private String desc;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImg_urls() {
        return img_urls;
    }

    public void setImg_urls(String img_urls) {
        this.img_urls = img_urls;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
