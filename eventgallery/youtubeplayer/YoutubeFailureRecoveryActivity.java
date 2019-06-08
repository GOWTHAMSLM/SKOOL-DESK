package app.zerobugz.fcms.ims.eventgallery.youtubeplayer;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import app.zerobugz.fcms.ims.appconfig.Config;
import app.zerobugz.fcms.ims.R;

public abstract class YoutubeFailureRecoveryActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    //Paste your API key obtained from Console here
    //String DEVELOPER_KEY = "AIzaSyCMf8Jlp55B5tCLB9VOssJRLf92ERQGI18";
    //String DEVELOPER_KEY =  "AIzaSyBJbHy1YxGtwoRriQwEkqOFAvbA4EG_8og";


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
            //getYouTubePlayerProvider().initialize(DEVELOPER_KEY, this);
        }
    }

    protected abstract YouTubePlayer.Provider getYouTubePlayerProvider();

}
