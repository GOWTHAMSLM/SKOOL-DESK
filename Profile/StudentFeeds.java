package app.zerobugz.fcms.ims.Profile;

public class StudentFeeds {

    private String stu_sysid, imgURL, stu_name, grp_name;

    public StudentFeeds(String stu_sysid, String name, String grp_name, String imgurl) {
        this.stu_sysid = stu_sysid;
        this.stu_name = name;
        this.grp_name = grp_name;
        this.imgURL = imgurl;
    }

    public String getStu_sysid() {
        return stu_sysid;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getStu_name() {
        return stu_name;
    }

    public String getGrp_name() {
        return grp_name;
    }
}
