package com.haokan.xinyitu.upload;

import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

public class ResponseBeanLocation extends BaseResponseBean{

    private List<LocationAddressBean> data;

    public List<LocationAddressBean> getData() {
        return data;
    }

    public void setData(List<LocationAddressBean> data) {
        this.data = data;
    }

    public static class LocationAddressBean {
        private String cityCode;
        private String formatted_address;

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }
    }
}
