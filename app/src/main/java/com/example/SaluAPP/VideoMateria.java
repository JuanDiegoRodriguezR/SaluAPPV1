package com.example.SaluAPP;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoMateria extends AppCompatActivity {

    private PlayerView playerView;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.materiabase);

        playerView = findViewById(R.id.player_view);
        initializePlayer();
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Reemplaza "YOUR_VIDEO_ID" con el ID del video de YouTube que deseas reproducir
        String videoId = "_gIvZtVJk4k"; // Por ejemplo: "k2Ap3FcXg1L02YzClfI"
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

        // Crea un MediaItem a partir del URL del video
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));

        // Establece el media item al reproductor y comienza la reproducción
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
