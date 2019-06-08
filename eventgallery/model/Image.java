package app.zerobugz.fcms.ims.eventgallery.model;

import java.io.Serializable;

/**
 * Created by Lincoln on 04/04/16.
 */
public class Image implements Serializable{
    private String eventname;
    private String eventsysid;
    private String imageurl;
    private String eventdate;

    public Image() {
    }

    public Image(String name, String sysid, String imgurl, String eventdate) {
        this.eventname = name;
        this.eventsysid = sysid;
        this.imageurl = imgurl;
        this.eventdate = eventdate;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public String getEventsysid() {
        return eventsysid;
    }

    public void setEventsysid(String eventsysid) {
        this.eventsysid = eventsysid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getEventdate() {
        return eventdate;
    }

    public void setEventdate(String eventdate) {
        this.eventdate = eventdate;
    }
}
