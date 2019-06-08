package app.zerobugz.fcms.ims.eventgallery.youtubeplayer;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.R;

public class YoutubePlayer extends YoutubeFailureRecoveryActivity {


    private YouTubePlayerView playerView;
    private String youtube_url;

    // IMPORTANT : CHANGE THIS
    //String DEVELOPER_KEY = "AIzaSyCMf8Jlp55B5tCLB9VOssJRLf92ERQGI18";
    //String DEVELOPER_KEY =  "AIzaSyBJbHy1YxGtwoRriQwEkqOFAvbA4EG_8og";


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube_player);

        // The unique video id of the youtube video (can be obtained from video url)
        //youtube_url = "5FpS_8TLtNI" ;
        youtube_url = getIntent().getExtras().getString("video_id");

        playerView = (YouTubePlayerView) findViewById(R.id.player);
        //playerView.initialize(DEVELOPER_KEY, this);
        playerView.initialize(Config.YOUTUBE_API_KEY, this);

    }



    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return playerView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

        player.setFullscreen(true);
        player.setShowFullscreenButton(true);
        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        if (!wasRestored) {
            player.loadVideo(youtube_url);
        }
    }
}
