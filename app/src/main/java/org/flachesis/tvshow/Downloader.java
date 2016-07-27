package org.flachesis.tvshow;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Downloader extends AsyncTask<URL, Integer, String[]> {
    ExecuteCallback callback;

    public Downloader(ExecuteCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String[] doInBackground(URL... urls) {
        String[] result = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            String content = "";
            try {
                URLConnection conn = urls[i].openConnection();
                conn.setConnectTimeout(1000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String data;
                while ((data = reader.readLine()) != null) {
                    content += data;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            result[i] = content;
        }

        return result;
    }

    protected void onPostExecute(String[] result) {
        callback.onTaskComplete(result);
    }
}
