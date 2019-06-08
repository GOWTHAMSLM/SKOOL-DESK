package app.zerobugz.fcms.ims.marks;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.zerobugz.fcms.ims.R;


public class MyMarkAdapter extends RecyclerView.Adapter<MyMarkAdapter.ViewHolder> {

    private ArrayList<Group> repos;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private Context context;

    public MyMarkAdapter(ArrayList<Group> repos) {
        this.repos = repos;
        //set initial expanded state to false
        for (int i = 0; i < repos.size(); i++) {
            expandState.append(i, false);
        }
    }

    @Override
    public MyMarkAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.singleitem_mark_header, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final  int i) {

        viewHolder.tvName.setText(repos.get(i).getName());

        if(i == repos.size()-1){
            expandState.put(i, true);
            //viewHolder.expandableLayout.setVisibility(View.VISIBLE);
        }

        ArrayList<Child> ch_details = repos.get(i).getItems();

        for(int j=0; j<ch_details.size(); j++){

            View newView = LayoutInflater.from(context).inflate(R.layout.singleitem_mark_body, null);

            viewHolder.expandableLayout.addView(newView);

            TextView subject_name = (TextView) newView.findViewById(R.id.textView_subject);
            TextView subject_marks = (TextView) newView.findViewById(R.id.textView_subjectmarks);
            subject_name.setText(ch_details.get(j).getName());
            subject_marks.setText(ch_details.get(j).getMark());

        }

        //check if view is expanded
        final boolean isExpanded = expandState.get(i);
        viewHolder.expandableLayout.setVisibility(isExpanded?View.VISIBLE:View.GONE);

        viewHolder.buttonLayout.setRotation(expandState.get(i) ? 180f : 0f);
        viewHolder.buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickButton(viewHolder.expandableLayout, viewHolder.buttonLayout,  i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        public LinearLayout buttonLayout;
        public LinearLayout expandableLayout;

        public ViewHolder(View view) {
            super(view);

            tvName = (TextView)view.findViewById(R.id.textView_header);
            buttonLayout = (LinearLayout) view.findViewById(R.id.expand_button);
            expandableLayout = (LinearLayout) view.findViewById(R.id.expandableLayout);
        }
    }

    private void onClickButton(final LinearLayout expandableLayout, final LinearLayout buttonLayout, final  int i) {

        //Simply set View to Gone if not expanded
        //Not necessary but I put simple rotation on button layout
        if (expandableLayout.getVisibility() == View.VISIBLE){
            createRotateAnimator(buttonLayout, 180f, 0f).start();
            expandableLayout.setVisibility(View.GONE);
            expandState.put(i, false);
        }else{
            createRotateAnimator(buttonLayout, 0f, 180f).start();
            expandableLayout.setVisibility(View.VISIBLE);
            expandState.put(i, true);
        }
    }

    //Code to rotate button
    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }
}