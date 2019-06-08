package app.zerobugz.fcms.ims.homework;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.zerobugz.fcms.ims.R;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
public class MyHomeworkAdapter extends RecyclerView.Adapter<MyHomeworkAdapter.MyViewHolder> {

    private List<HomeworkFeeds> feedsList;
    private Context context;
    private LayoutInflater inflater;

    public MyHomeworkAdapter(Context context, List<HomeworkFeeds> feedsList) {

        this.context = context;
        this.feedsList = feedsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.singleitem_homeworkview, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HomeworkFeeds feeds = feedsList.get(position);
        //Pass the values of feeds object to Views
        holder.title.setText(feeds.getFeedName());
        holder.content.setText(feeds.getContent());
        holder.ref_link.setText(feeds.getRef_link());
    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView content, title, ref_link;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_view);
            content = (TextView) itemView.findViewById(R.id.content_view);
            ref_link = (TextView) itemView.findViewById(R.id.ref_link);
            ref_link.setMovementMethod(LinkMovementMethod.getInstance());

        }
    }

}
