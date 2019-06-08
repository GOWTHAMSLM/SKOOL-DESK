package app.zerobugz.fcms.ims.bottomstudentlist;

public class StudentListFeeds {

    private String stu_sysid, imgURL, stu_name, cls_name, sec_name, grp_name;

    public StudentListFeeds(String stu_sysid, String name, String cls_name, String sec_name, String grp_name, String imgurl) {
        this.stu_sysid = stu_sysid;
        this.stu_name = name;
        this.grp_name = grp_name;
        this.imgURL = imgurl;
        this.cls_name = cls_name;
        this.sec_name = sec_name;
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

    public String getCls_name() {
        return cls_name;
    }

    public String getSec_name() {
        return sec_name;
    }
}
