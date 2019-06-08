package app.zerobugz.fcms.ims.messages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.zerobugz.fcms.ims.R;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private List<NewsFeeds> feedsList;
    private Context context;
    private LayoutInflater inflater;

    public MyRecyclerAdapter(Context context, List<NewsFeeds> feedsList) {

        this.context = context;
        this.feedsList = feedsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.singleitem_messageview, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NewsFeeds feeds = feedsList.get(position);
        //Pass the values of feeds object to Views
        holder.title.setText(feeds.getFeedName());
        holder.content.setText(feeds.getContent());
        //holder.imageview.setImageUrl(feeds.getImgURL(), NetworkController.getInstance(context).getImageLoader());
        //holder.ratingbar.setProgress(feeds.getRating());
        holder.date.setText(feeds.getDate());
        holder.time.setText(feeds.getTime());
    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView content, title, date, time;
        //private NetworkImageView imageview;
        //private ProgressBar ratingbar;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_view);
            content = (TextView) itemView.findViewById(R.id.content_view);
            date = (TextView) itemView.findViewById(R.id.date_view);
            time = (TextView) itemView.findViewById(R.id.time_view);
            // Volley's NetworkImageView which will load Image from URL
            /* imageview = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            ratingbar = (ProgressBar) itemView.findViewById(R.id.ratingbar_view);
            ratingbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Rated By User : " + feedsList.get(getAdapterPosition()).getRating(), Toast.LENGTH_SHORT).show();
                }
            });*/

        }
    }

}
