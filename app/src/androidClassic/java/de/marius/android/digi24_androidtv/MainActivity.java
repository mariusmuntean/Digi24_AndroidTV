/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.marius.android.digi24_androidtv;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;


/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity implements View.OnKeyListener {

    private static final String STREAM_URL_FALLBACK = "http://82.76.40.76:80/digi24edge/digi24live/index.m3u8";
    private static final Logger LOG = Logger.getLogger(MainActivity.class.getSimpleName());

    private RelativeLayout rootLayout;
    private VideoView videoView;
    private boolean startingPlayback = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        LOG.info("onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlaybackState();
            }
        });

        videoView = (VideoView) findViewById(R.id.videoView2);
        videoView.setOnKeyListener(this);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LOG.warning("Handling error");
                resolveStreammingUrlAndStartPlayback();
                return true;
            }
        });


        resolveStreammingUrlAndStartPlayback();

        LOG.info("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (startingPlayback) {
            LOG.info("onResume - startingPlayback: true");
            return;
        }

        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
            LOG.info("onResume - startingPlayback: false");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (videoView != null) {
            videoView.pause();
        }
        LOG.info("onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        if (videoView != null) {
            videoView.stopPlayback();
        }

        LOG.info("onStop");
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY && event.getAction() == KeyEvent.ACTION_DOWN) {
            videoView.start();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE && event.getAction() == KeyEvent.ACTION_DOWN) {
            videoView.pause();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE && event.getAction() == KeyEvent.ACTION_DOWN) {
            togglePlaybackState();
            return true;
        }

        return false;
    }

    private void togglePlaybackState() {
        if (videoView.isPlaying()) {
            videoView.pause();
            LOG.info("paused video");
        } else {
            videoView.start();
            LOG.info("resumed video");
        }
    }

    private void resolveStreammingUrlAndStartPlayback() {
        this.startingPlayback = true;
        new GetLatestStreamUrl().execute();
    }


    private class GetLatestStreamUrl extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // Try to get the current stream URL
            String streamRepoUrl = getResources().getString(R.string.streamRepositoryUrl);

            String currentStreamUrl = null;
            try {
                currentStreamUrl = IOUtils.toString(URI.create(streamRepoUrl));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return currentStreamUrl;
        }

        @Override
        protected void onPostExecute(String result) {
            boolean shouldUseFallback = result == null || result.length() == 0;

            if (shouldUseFallback) {
                Toast.makeText(MainActivity.this, "Trying fallback", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Using: " + result, Toast.LENGTH_LONG).show();
            }

            // Fallback if none found
            String currentStreamUrl = shouldUseFallback ? STREAM_URL_FALLBACK : result;

            // Set the videoview source and start
            videoView.setVideoURI(Uri.parse(currentStreamUrl));
            videoView.requestFocus();
            videoView.start();

            MainActivity.this.startingPlayback = false;
        }
    }
}
