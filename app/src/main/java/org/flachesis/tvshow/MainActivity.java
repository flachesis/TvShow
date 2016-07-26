package org.flachesis.tvshow;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView tvList;

    public class TvListRender extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... parms) {
            String content = "";
            String fallback = "{\"tvlist\":[{\"name\":\"三立新聞\",\"id\":\"9AE5FFmMDfk\"},{\"name\":\"民視新聞台\",\"id\":\"XxJKnDLYZz4\"},{\"name\":\"東森新聞台\",\"id\":\"jMN4cxyhJjk\"},{\"name\":\"中天新聞台\",\"id\":\"hgIfZz8STLk\"},{\"name\":\"華視新聞台\",\"id\":\"g9uJqP0hT_I\"},{\"name\":\"台視新聞台\",\"id\":\"yk2CUjbyyQY\"},{\"name\":\"中視新聞台\",\"id\":\"zJtmGiNfTMM\"},{\"name\":\"公共電視\",\"id\":\"TaxTsgmMw_c\"},{\"name\":\"大愛一臺\",\"id\":\"ESKjSwcswBM\"},{\"name\":\"大愛二臺\",\"id\":\"-81c_O1NoPo\"},{\"name\":\"人間衛視\",\"id\":\"LZWKzQCKNI0\"}]}";
                try {
                    URL url = new URL("https://api.myjson.com/bins/3rmr3");
                    URLConnection conn = url.openConnection();
                    conn.setConnectTimeout(1000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
                    String data;
                    while ((data = reader.readLine()) != null) {
                        content += data;
                    }
                } catch (IOException e) {
                    content = fallback;
                    e.printStackTrace();
                }
            return content;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {
            ArrayList<TvItem> tvItemList = new ArrayList<>();
            try {
                JSONObject obj = new JSONObject(result);
                JSONArray arr = obj.getJSONArray("tvlist");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject rec = arr.getJSONObject(i);
                    TvItem item = new TvItem(rec.getString("name"), rec.getString("id"));
                    tvItemList.add(item);
                }
            } catch (JSONException e) {
                tvItemList.clear();
            }

            rendTvList(tvItemList);
        }
    }

    protected void rendTvList(ArrayList<TvItem> tvItemList) {
        int layoutId = android.R.layout.simple_list_item_1;
        ArrayAdapter<TvItem> adapter = new ArrayAdapter<>(this, layoutId, tvItemList);
        tvList = (ListView) findViewById(R.id.listView);
        tvList.setAdapter(adapter);

        this.tvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TvItem item = (TvItem) parent.getAdapter().getItem(position);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (new TvListRender()).execute();
    }
}
