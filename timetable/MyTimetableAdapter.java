package app.zerobugz.fcms.ims.timetable;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.zerobugz.fcms.ims.R;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
public class MyTimetableAdapter extends RecyclerView.Adapter<MyTimetableAdapter.MyViewHolder> {

    private List<TimetableFeeds> feedsList;
    private Context context;
    private LayoutInflater inflater;
    ArrayList<String> colors=new ArrayList<String>();


    public MyTimetableAdapter(Context context, List<TimetableFeeds> feedsList) {
        Random random = new Random();
        this.context = context;
        this.feedsList = feedsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.singleitem_timetableview, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TimetableFeeds feeds = feedsList.get(position);
        //Pass the values of feeds object to Views
        String subject = feeds.getFeedName();
        holder.subject_view.setText(subject);

        String timing = feeds.getFrom_time() + " - " + feeds.getEnd_time();
        holder.time_view.setText(timing);

        holder.text_first.setText(subject.substring(0,1));

        if(position % 4 ==0){
            holder.dotmark.setColorFilter(Color.parseColor("#66BB6A"));
            holder.text_back.setColorFilter(Color.parseColor("#66BB6A"));
        }else if(position % 4 ==1){
            holder.dotmark.setColorFilter(Color.parseColor("#FF7043"));
            holder.text_back.setColorFilter(Color.parseColor("#FF7043"));
        }else if(position % 4 ==2){
            holder.dotmark.setColorFilter(Color.parseColor("#42A5F5"));
            holder.text_back.setColorFilter(Color.parseColor("#42A5F5"));
        }else if(position % 4 ==3){
            holder.dotmark.setColorFilter(Color.parseColor("#BA68C8"));
            holder.text_back.setColorFilter(Color.parseColor("#BA68C8"));
        }
    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView text_first, subject_view, time_view;
        private ImageView dotmark, text_back;

        public MyViewHolder(View itemView) {
            super(itemView);
            text_first = (TextView) itemView.findViewById(R.id.text_first);
            subject_view = (TextView) itemView.findViewById(R.id.subject_view);
            time_view = (TextView) itemView.findViewById(R.id.time_view);
            dotmark = (ImageView) itemView.findViewById(R.id.dotmark);
            text_back = (ImageView) itemView.findViewById(R.id.text_back);

        }
    }

}
