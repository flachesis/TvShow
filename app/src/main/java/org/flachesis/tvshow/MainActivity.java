package org.flachesis.tvshow;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<TvItem> tvItemList = new ArrayList<>();
        tvItemList.add(new TvItem("三立新聞台", "9AE5FFmMDfk"));
        tvItemList.add(new TvItem("民視新聞台", "XxJKnDLYZz4"));
        tvItemList.add(new TvItem("東森新聞台", "jMN4cxyhJjk"));
        tvItemList.add(new TvItem("中天新聞台", "hgIfZz8STLk"));
        tvItemList.add(new TvItem("華視新聞台", "g9uJqP0hT_I"));
        tvItemList.add(new TvItem("台視新聞台", "yk2CUjbyyQY"));
        tvItemList.add(new TvItem("中視新聞台", "zJtmGiNfTMM"));
        tvItemList.add(new TvItem("公共電視", "TaxTsgmMw_c"));
        tvItemList.add(new TvItem("大愛一臺", "ESKjSwcswBM"));
        tvItemList.add(new TvItem("大愛二臺", "-81c_O1NoPo"));
        tvItemList.add(new TvItem("人間衛視", "LZWKzQCKNI0"));
        int layoutId = android.R.layout.simple_list_item_1;
        ArrayAdapter<TvItem> adapter = new ArrayAdapter<>(this, layoutId, tvItemList);
        ListView tvList = (ListView) findViewById(R.id.listView);
        tvList.setAdapter(adapter);

        tvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
}
