package app.zerobugz.fcms.ims.attendance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.zerobugz.fcms.ims.R;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
public class MyAttendanceAdapter extends RecyclerView.Adapter<MyAttendanceAdapter.MyViewHolder> {

    private List<EventMonthFeeds> feedsList;
    private Context context;
    private LayoutInflater inflater;

    public MyAttendanceAdapter(Context context, List<EventMonthFeeds> feedsList) {

        this.context = context;
        this.feedsList = feedsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.singleitem_eventlistview, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EventMonthFeeds feeds = feedsList.get(position);
        //Pass the values of feeds object to Views
        holder.date_txt.setText(feeds.getEventdate());
        holder.title.setText(feeds.getName());
        holder.img_count.setText(feeds.getTotalimages());
        holder.vid_count.setText(feeds.getTotalvideos());

        String dtEvent = feeds.getEventdate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date date = formatter.parse(dtEvent);

            String day = (String) DateFormat.format("dd", date);
            String monthString  = (String) DateFormat.format("MMM", date);
            String year         = (String) DateFormat.format("yy", date);

            holder.date_txt.setText(day);
            holder.mon_yr_txt.setText(monthString + " " + year);

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView date_txt, title, mon_yr_txt, img_count, vid_count;

        public MyViewHolder(View itemView) {
            super(itemView);
            date_txt = (TextView) itemView.findViewById(R.id.date_txt);
            mon_yr_txt = (TextView) itemView.findViewById(R.id.mon_yr_txt);
            title = (TextView) itemView.findViewById(R.id.event_title);
            img_count = (TextView) itemView.findViewById(R.id.img_count);
            vid_count = (TextView) itemView.findViewById(R.id.vid_count);

        }
    }

}
