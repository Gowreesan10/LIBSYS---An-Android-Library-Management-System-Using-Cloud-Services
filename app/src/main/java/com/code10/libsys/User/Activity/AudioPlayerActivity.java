package com.code10.libsys.User.Activity;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class AudioPlayerActivity extends AppCompatActivity {

    ExoPlayer exoPlayer;
    StyledPlayerView styledPlayerView;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // url = getIntent().getStringExtra("Bookurl");
        setContentView(R.layout.activity_audio_player);
        initPlayer();
    }

    private void initPlayer() {

        styledPlayerView = findViewById(R.id.playerView);
        styledPlayerView.setControllerShowTimeoutMs(0);
        styledPlayerView.setCameraDistance(30);

        exoPlayer = new ExoPlayer.Builder(this).build();
        styledPlayerView.setPlayer(exoPlayer);
        String userAgent = Util.getUserAgent(this, getString(R.string.app_name));
        DataSource.Factory dataSFactory = new DefaultDataSourceFactory(this, userAgent);

        Uri uri = Uri.parse("https://librivox.org/rss/10074");
        MediaItem mediaItem = MediaItem.fromUri(uri);
        MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSFactory).createMediaSource(mediaItem);

        exoPlayer.setMediaSource(audioSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);


    }

}