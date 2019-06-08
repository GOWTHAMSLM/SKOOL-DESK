package app.zerobugz.fcms.ims.messages;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
//Simple POJO to hold values of our JSON

public class NewsFeeds {

    private String feedName, content, date, time;

    public NewsFeeds(String name, String content, String date, String time) {
        this.feedName = name;
        this.content = content;
        this.date = date;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getFeedName() {
        return feedName;
    }


}
