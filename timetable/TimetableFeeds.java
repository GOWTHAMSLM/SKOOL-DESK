package app.zerobugz.fcms.ims.timetable;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
//Simple POJO to hold values of our JSON

public class TimetableFeeds {

    private String feedName, from_time, end_time;


    public TimetableFeeds(String name, String from_time, String end_time) {
        this.feedName = name;
        this.from_time = from_time;
        this.end_time = end_time;

    }

    public String getFeedName() {
        return feedName;
    }

    public String getFrom_time() {
        return from_time;
    }

    public String getEnd_time() {
        return end_time;
    }
}
