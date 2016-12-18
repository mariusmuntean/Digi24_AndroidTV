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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;


/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {

    private static final String STREAM_URL_FALLBACK = "http://82.76.40.76:80/digi24edge/digi24live/index.m3u8";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            VideoView vv = (VideoView) findViewById(R.id.videoView2);
            vv.setVideoURI(Uri.parse(currentStreamUrl));
            vv.requestFocus();
            vv.start();
        }
    }
}
