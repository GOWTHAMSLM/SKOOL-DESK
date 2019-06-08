package app.zerobugz.fcms.ims.eventgallery;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import app.zerobugz.fcms.ims.eventgallery.model.Download;
import app.zerobugz.fcms.ims.eventgallery.model.DownloadService;
import app.zerobugz.fcms.ims.eventgallery.model.InnerImage;
import app.zerobugz.fcms.ims.utils.TouchImageView;
import app.zerobugz.fcms.ims.utils.ViewAnimation;
import app.zerobugz.fcms.ims.R;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<InnerImage> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, img_position;
    private int selectedPosition = 0;

    private View back_drop;
    private boolean rotate = false;
    private View lyt_high;
    private View lyt_small;


    //Download image service section
    public static final String MESSAGE_PROGRESS = "message_progress";
    private static final int PERMISSION_REQUEST_CODE = 1;

    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        img_position = (TextView) v.findViewById(R.id.img_position);

        back_drop = v.findViewById(R.id.back_drop);
        final FloatingActionButton fab_high = (FloatingActionButton) v.findViewById(R.id.fab_high);
        final FloatingActionButton fab_small = (FloatingActionButton) v.findViewById(R.id.fab_small);
        final FloatingActionButton fab_download = (FloatingActionButton) v.findViewById(R.id.fab_download);
        lyt_high = v.findViewById(R.id.lyt_high);
        lyt_small = v.findViewById(R.id.lyt_small);
        ViewAnimation.initShowOut(lyt_high);
        ViewAnimation.initShowOut(lyt_small);
        back_drop.setVisibility(View.GONE);

        fab_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(v);
            }
        });

        back_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(fab_download);
            }
        });

        fab_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    int img_pos_number = Integer.parseInt(img_position.getText().toString());
                    InnerImage image = images.get(img_pos_number);
                    String img_url = image.getOrg_img();
                    //Toast.makeText(getActivity(), img_url, Toast.LENGTH_SHORT).show();
                    startDownload(img_url);
                } else {
                    requestPermission();
                }
            }
        });

        fab_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    int img_pos_number = Integer.parseInt(img_position.getText().toString());
                    InnerImage image = images.get(img_pos_number);
                    String img_url = image.getLarge();
                    //Toast.makeText(getActivity(), img_url, Toast.LENGTH_SHORT).show();
                    startDownload(img_url);
                } else {
                    requestPermission();
                }
            }
        });

        registerReceiver();
        createNotificationChannel();

        images = (ArrayList<InnerImage>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);


        return v;
    }

    private void toggleFabMode(View v) {
        //rotate = ViewAnimation.rotateFab(v, !rotate);
        if (back_drop.getVisibility() == View.GONE) {
            ViewAnimation.showIn(lyt_high);
            ViewAnimation.showIn(lyt_small);
            back_drop.setVisibility(View.VISIBLE);
        } else {
            ViewAnimation.showOut(lyt_high);
            ViewAnimation.showOut(lyt_small);
            back_drop.setVisibility(View.GONE);
        }
    }

    // Begin Section of Download Button Service

    private void startDownload(String img_url) {
        Intent intent = new Intent(getActivity(), DownloadService.class);
        intent.putExtra("img_url", img_url);
        getActivity().startService(intent);
    }

    private void registerReceiver() {

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MESSAGE_PROGRESS)) {

                Download download = intent.getParcelableExtra("download");
                //mProgressBar.setProgress(download.getProgress());

                if (download.getProgress() == 100) {

                    //mProgressText.setText("File Download Complete");
                    //Toast.makeText(context,"Image Downloaded Successfully...", Toast.LENGTH_SHORT).show();

                } else {

                    //mProgressText.setText(String.format("Downloaded (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize()));
                    //Toast.makeText(context,"Failed...", Toast.LENGTH_SHORT).show();

                }
            }
        }
    };

    //Begin Run Time Permission access required for Stroage

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    int img_pos_number = Integer.parseInt(img_position.getText().toString());
                    InnerImage image = images.get(img_pos_number);
                    String img_url = image.getOrg_img();
                    startDownload(img_url);
                } else {
                    Toast.makeText(getActivity(), "Permission Denied, Please allow to proceed !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //End Run Time Permission access required for Stroage

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_id = "downloadnotify";
            String name = "channel_ids";

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            //channel.setDescription(description);
            channel.enableVibration(false);
            channel.setSound(null,null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }

    // End Section of Download Button Service

    // Set Current Item Info details
    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());
        img_position.setText(String.valueOf(position));
        /*InnerImage image = images.get(position);*/
        /*lblTitle.setText("Mohan");
        lblDate.setText("20-08-2018");*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            TouchImageView imageViewPreview = (TouchImageView) view.findViewById(R.id.image_preview);

            InnerImage image = images.get(position);

            Glide.with(getActivity()).load(image.getLarge())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
