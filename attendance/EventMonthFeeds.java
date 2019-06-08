package app.zerobugz.fcms.ims.attendance;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
//Simple POJO to hold values of our JSON

public class EventMonthFeeds {

    private String eventsysid, eventdate, name, totalimages, totalvideos;

    public EventMonthFeeds(String sysid, String date, String name, String totalimages, String totalvideos) {
        this.eventsysid = sysid;
        this.eventdate = date;
        this.name = name;
        this.totalimages = totalimages;
        this.totalvideos = totalvideos;
    }

    public String getEventsysid() {
        return eventsysid;
    }

    public String getEventdate() {
        return eventdate;
    }

    public String getName() {
        return name;
    }

    public String getTotalimages() {
        return totalimages;
    }

    public String getTotalvideos() {
        return totalvideos;
    }
}
