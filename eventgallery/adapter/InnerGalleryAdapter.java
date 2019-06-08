package app.zerobugz.fcms.ims.eventgallery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import app.zerobugz.fcms.ims.eventgallery.model.InnerImage;
import app.zerobugz.fcms.ims.R;

/**
 * Created by Lincoln on 31/03/16.
 */

public class InnerGalleryAdapter extends RecyclerView.Adapter<InnerGalleryAdapter.MyViewHolder> {

    private List<InnerImage> innerimages;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView inner_thumbnail, img_type;

        public MyViewHolder(View view) {
            super(view);
            inner_thumbnail = (ImageView) view.findViewById(R.id.inner_thumbnail);
            img_type = (ImageView) view.findViewById(R.id.img_type);
        }
    }


    public InnerGalleryAdapter(Context context, List<InnerImage> innerimages) {
        mContext = context;
        this.innerimages = innerimages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inner_gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InnerImage image = innerimages.get(position);

        Glide.with(mContext).load(image.getMedium())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.inner_thumbnail);

        String gallery_typ = image.getImg_type();
        if(gallery_typ.equals("image")){
            holder.img_type.setVisibility(View.GONE);
        }else if(gallery_typ.equals("video")){
            holder.img_type.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return innerimages.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private InnerGalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final InnerGalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
