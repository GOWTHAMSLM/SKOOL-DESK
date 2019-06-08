package app.zerobugz.fcms.ims.homework;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
//Simple POJO to hold values of our JSON

public class HomeworkFeeds {

    private String feedName, content, ref_link;


    public HomeworkFeeds(String name, String content, String ref_link) {
        this.feedName = name;
        this.content = content;
        this.ref_link = ref_link;

    }

    public String getFeedName() {
        return feedName;
    }

    public String getContent() {
        return content;
    }

    public String getRef_link() {
        return ref_link;
    }
}
