package com.java.no_36;

import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.Window;
import java.util.ArrayList;

public class HistoryPage extends AppCompatActivity implements AdapterView.OnItemClickListener, OnClickListener{
    ArrayList<NewsBriefBean> arraylistHistorys;
    private ListView mlistview;
    private SharedPreferences config;
    FloatingActionButton tts_Button;
    NewsHistoryAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        config = getSharedPreferences("config", MODE_PRIVATE);
        int themeId = getThemeId();
        if (themeId != 0) {
            setTheme(themeId);
        }
        setContentView(R.layout.activity_history_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tts_Button = (FloatingActionButton) findViewById(R.id.fab_history);
        tts_Button.setOnClickListener(this);

        mlistview = (ListView) findViewById(R.id.list_news_his);
        gethistory();
        newsAdapter = new NewsHistoryAdapter(this, arraylistHistorys);
        mlistview.setAdapter(newsAdapter);
        mlistview.setOnItemClickListener(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return true;
    }

    void gethistory() {
        arraylistHistorys = new HistoryDBUtils(HistoryPage.this).getHistorys();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        NewsBriefBean news = (NewsBriefBean) adapterView.getItemAtPosition(position);
        String url = news.getNews_url();
        Intent intent = new Intent(this, NewsPage.class);
        intent.setData(Uri.parse(url));
        intent.putExtra("id", news.getNews_id());
        intent.putExtra("title", news.getNews_title());
        intent.putExtra("brief", news.getNews_intro());
        if(news.getNews_pictures().length > 0)
            intent.putExtra("image", news.getNews_pictures()[0]);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_history:
                new HistoryDBUtils(this).deleteHistorys();
                runOnUiThread(new Runnable() {
                    public void run() {
                        arraylistHistorys.clear();
                        newsAdapter.notifyDataSetChanged();
                    }
                });
                break;
            default:
                break;
        }
    }

    private int getThemeId() {
        return config.getInt("theme_id", R.style.APPTheme_DayTheme);
    }
}
