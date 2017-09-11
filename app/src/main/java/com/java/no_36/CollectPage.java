package com.java.no_36;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class CollectPage extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mlistview;
    private SharedPreferences config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        config = getSharedPreferences("config", MODE_PRIVATE);
        int themeId = getThemeId();
        if (themeId != 0) {
            setTheme(themeId);
        }
        setContentView(R.layout.activity_collect_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mlistview = (ListView) findViewById(R.id.list_news_collect);
        CollectDBUtils collectDatabase = new CollectDBUtils(this);
        ArrayList<NewsBriefBean> allcollects_database = collectDatabase.getCollects();
        if (allcollects_database != null && allcollects_database.size() > 0)
        {
            // 创建一个adapter设置给listview
            NewsHistoryAdapter newsAdapter = new NewsHistoryAdapter(this, allcollects_database);
            mlistview.setAdapter(newsAdapter);
        }

        mlistview.setOnItemClickListener(this);
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

    private int getThemeId() {
        return config.getInt("theme_id", R.style.APPTheme_DayTheme);
    }
}
