package app.zerobugz.fcms.ims.Profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.zerobugz.fcms.ims.utils.Tools;
import app.zerobugz.fcms.ims.R;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
public class MyStudentProfileAdapter extends RecyclerView.Adapter<MyStudentProfileAdapter.MyViewHolder> {

    private List<StudentFeeds> feedsList;
    private Context context;
    private LayoutInflater inflater;

    public MyStudentProfileAdapter(Context context, List<StudentFeeds> feedsList) {

        this.context = context;
        this.feedsList = feedsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.singleitem_student_profile, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StudentFeeds feeds = feedsList.get(position);
        //Pass the values of feeds object to Views
        holder.stu_name.setText(feeds.getStu_name());
        holder.group_name.setText(feeds.getGrp_name());
        String img_url = feeds.getImgURL();
        //Student Image Set Section
        if(img_url.equals("M-NULL")){
            holder.stu_image.setImageResource(R.drawable.student_male_avator);
        }else if(img_url.equals("F-NULL")){
            holder.stu_image.setImageResource(R.drawable.student_female_avator);
        }else{
            Tools.disImageRound(context, holder.stu_image, feeds.getImgURL());
        }


        //holder.stu_image.setImageUrl(feeds.getImgURL(), NetworkController.getInstance(context).getImageLoader());
    }

    @Override
    public int getItemCount() {
        return feedsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView group_name, stu_name;
        private ImageView stu_image;
        private View lyt_parent;

        public MyViewHolder(View itemView) {
            super(itemView);
            stu_name = (TextView) itemView.findViewById(R.id.stu_name);
            group_name = (TextView) itemView.findViewById(R.id.group_name);
            // Volley's NetworkImageView which will load Image from URL
            stu_image = (ImageView) itemView.findViewById(R.id.stu_image);
            lyt_parent = (View) itemView.findViewById(R.id.lyt_parent);

            // It's Shows Student sysid when click the student list
            /*lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Rated By User : " + feedsList.get(getAdapterPosition()).getStu_sysid(), Toast.LENGTH_SHORT).show();
                }
            });*/

        }
    }

}
