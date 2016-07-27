package org.flachesis.tvshow;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView tvList;
    ArrayList<TvItem> tvItemList;
    ArrayAdapter<TvItem> adapter;
    File cacheFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cacheFile = new File(getApplicationContext().getCacheDir(), "tv_list.json");
        tvItemList = new ArrayList<>();
        int layoutId = android.R.layout.simple_list_item_1;
        adapter = new ArrayAdapter<>(this, layoutId, tvItemList);
        tvList = (ListView) findViewById(R.id.listView);
        tvList.setAdapter(adapter);

        this.setViewItemClickListener(tvList);

        try {
            this.render();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected void render() throws MalformedURLException {
        String tvListJson = this.getCacheContent(true);
        try {
            if (null != tvListJson) {
                renderExecute(tvListJson);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        (new Downloader(this.getExecutor())).execute(new URL("https://api.myjson.com/bins/3rmr3"));
    }

    protected String getCacheContent(boolean isCheckTime) {
        if (!(cacheFile.exists())) {
            return null;
        }
        long timeInterval = System.currentTimeMillis() - cacheFile.lastModified();
        System.out.println(cacheFile.lastModified());
        System.out.println(System.currentTimeMillis());
        if (isCheckTime && (timeInterval > 3600000)) {
            return null;
        }

        String content = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFile)));
            String data;
            while (null != (data = reader.readLine())) {
                content += data;
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        return content;
    }

    protected void renderExecute(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        JSONArray arr = obj.getJSONArray("tvlist");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject rec = arr.getJSONObject(i);
            TvItem item = new TvItem(rec.getString("name"), rec.getString("id"));
            tvItemList.add(item);
        }

        adapter.notifyDataSetChanged();

        try {
            saveCacheFile(jsonString);
        } catch (IOException e) {
            this.deleteCacheFile();
            e.printStackTrace();
        }
    }

    protected void saveCacheFile(String jsonString) throws IOException {
        this.deleteCacheFile();
        FileOutputStream outputStream = new FileOutputStream(cacheFile);
        outputStream.write(jsonString.getBytes());

    }

    protected void deleteCacheFile() {
        if (!(cacheFile.delete())) {
            System.out.println("Delete cache file fail.");
        }
    }

    protected ExecuteCallback getExecutor() {
        return new ExecuteCallback() {
            @Override
            public void onTaskComplete(String[] result) {
                try {
                    renderExecute(result[0]);
                } catch (JSONException e) {
                    String cacheContent = getCacheContent(false);
                    if (null != cacheContent) {
                        try {
                            renderExecute(cacheContent);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    protected void setViewItemClickListener(ListView view) {
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TvItem item = (TvItem) adapterView.getAdapter().getItem(i);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + item.id));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (RuntimeException ex) {
                    Intent otherApp = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + item.id));
                    startActivity(otherApp);
                }
            }
        });
    }
}
