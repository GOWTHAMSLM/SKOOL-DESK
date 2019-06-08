package app.zerobugz.fcms.ims.bottomstudentlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.utils.Tools;
import app.zerobugz.fcms.ims.R;

/**
 * Created by AndroidNovice on 6/5/2016.
 */
public class MyStudentBottomListAdapter extends RecyclerView.Adapter<MyStudentBottomListAdapter.MyViewHolder> {

    private List<StudentListFeeds> feedsList;
    private Context context;
    private LayoutInflater inflater;

    private BottomSheetDialog mBottomSheetDialog;
    private TextView stuname, stuclass;
    private ImageView stuimage;

    public MyStudentBottomListAdapter(Context context, List<StudentListFeeds> feedsList, BottomSheetDialog mBottomSheetDialog, TextView stuname, TextView stuclass, ImageView stuimage) {
        this.context = context;
        this.feedsList = feedsList;
        this.mBottomSheetDialog = mBottomSheetDialog;
        this.stuname = stuname;
        this.stuclass = stuclass;
        this.stuimage = stuimage;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.singleitem_stu_bottom_list, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StudentListFeeds feeds = feedsList.get(position);
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

            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String stu_sys_id = feedsList.get(getAdapterPosition()).getStu_sysid();
                    String stu_name = feedsList.get(getAdapterPosition()).getStu_name();
                    String cls_name = feedsList.get(getAdapterPosition()).getCls_name();
                    String sec_name = feedsList.get(getAdapterPosition()).getSec_name();
                    String grp_name = feedsList.get(getAdapterPosition()).getGrp_name();
                    String img_url = feedsList.get(getAdapterPosition()).getImgURL();

                    SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Config.STUDENT_SYS_ID_SHARED_PREF, stu_sys_id);
                    editor.putString(Config.STUDENT_NAME_SHARED_PREF, stu_name);
                    editor.putString(Config.CLASS_NAME_SHARED_PREF, cls_name);
                    editor.putString(Config.SECTION_NAME_SHARED_PREF, sec_name);
                    editor.putString(Config.GROUP_NAME_SHARED_PREF, grp_name);
                    editor.putString(Config.STUDENT_IMAGE_SHARED_PREF, img_url);
                    editor.commit();

                    mBottomSheetDialog.cancel();
                    stuname.setText(stu_name);
                    stuclass.setText(grp_name);

                    //Student Image Set Section
                    if(img_url.equals("M-NULL")){
                        stuimage.setImageResource(R.drawable.student_male_avator);
                    }else if(img_url.equals("F-NULL")){
                        stuimage.setImageResource(R.drawable.student_female_avator);
                    }else{
                        Glide.with(context).load(img_url).into(stuimage);
                    }

                    //Toast.makeText(context, "Rated By User : " + feedsList.get(getAdapterPosition()).getStu_sysid(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
