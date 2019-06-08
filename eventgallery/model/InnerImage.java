package app.zerobugz.fcms.ims.eventgallery.model;

import java.io.Serializable;

/**
 * Created by Lincoln on 04/04/16.
 */
public class InnerImage implements Serializable{
    private String small, medium, large;
    private String org_img;
    private String img_type;

    public InnerImage() {
    }

    public InnerImage(String small, String medium, String large, String org_img, String img_type) {
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.org_img = org_img;
        this.img_type = img_type;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getOrg_img() {
        return org_img;
    }

    public void setOrg_img(String org_img) {
        this.org_img = org_img;
    }

    public String getImg_type() {
        return img_type;
    }

    public void setImg_type(String img_type) {
        this.img_type = img_type;
    }
}