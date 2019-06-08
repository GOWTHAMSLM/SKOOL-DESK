package app.zerobugz.fcms.ims.eventgallery.model;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import app.zerobugz.fcms.ims.eventgallery.SlideshowDialogFragment;
import app.zerobugz.fcms.ims.R;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("Download Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;

    private String image_url;

    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String  ANDROID_CHANNEL_ID = "downloadnotify";
        notificationBuilder = new NotificationCompat.Builder(this, ANDROID_CHANNEL_ID)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setChannelId("downloadnotify")
                .setVibrate(new long[]{0L})
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        image_url = (String) intent.getExtras().get("img_url");
        if(!image_url.isEmpty()){
            initDownload(image_url);
        }else{
            Toast.makeText(this, "You are not download this image!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initDownload(String image_url){

        //String image_url = "http://192.168.1.100:8090/App_Upload/Events/Annual_Day_27-7-2018/Original/1.jpg";

        try {
            downloadFile(image_url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.100:8090/")
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<ResponseBody> request = retrofitInterface.downloadFile();
        try {

            downloadFile(request.execute().body());

        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

        }*/
    }

    private void downloadFile(String resource) throws IOException {

        int count = 0;
        long fileSize;
        byte data[] = new byte[1024 * 4];

        URL url = new URL(resource);
        String fileName = resource.substring(resource.lastIndexOf('/') + 1);
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        fileSize = urlConnection.getContentLength();

        File new_folder = new File("sdcard/Skooldesk");
        if(!new_folder.exists()){
            new_folder.mkdir();
        }

        File outputFile =  new File(new_folder, fileName);
        InputStream bis = new BufferedInputStream(url.openStream(), 1024 * 8);
        OutputStream output = new FileOutputStream(outputFile);

        /*long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "gowtham.jpg");
        OutputStream output = new FileOutputStream(outputFile);*/

        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();

    }

    private void sendNotification(Download download){

        sendIntent(download);
        notificationBuilder.setProgress(100,download.getProgress(),false);
        notificationBuilder.setContentText("Downloading file "+ download.getCurrentFileSize() +"/"+totalFileSize +" MB");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendIntent(Download download){

        Intent intent = new Intent(SlideshowDialogFragment.MESSAGE_PROGRESS);
        intent.putExtra("download",download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete(){
        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText("File Downloaded");
        notificationBuilder.setSmallIcon(getNotificationIcon());

        //Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File("sdcard/Skooldesk/");
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(0, notificationBuilder.build());*/

        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File("sdcard/Skooldesk/");

        if (Build.VERSION.SDK_INT >= 24) {

            Uri apkURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "resource/folder");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            intent.setDataAndType(Uri.fromFile(file), "resource/folder");
        }

        notificationManager.notify(0, notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_stat_ic_notification : R.mipmap.ic_launcher;
    }

}