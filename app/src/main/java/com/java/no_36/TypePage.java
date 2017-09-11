package com.java.no_36;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TypePage extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private Intent mintent;
    private String type_name;
    private int class_tag = 0;
    private ListView mlistview;
    NewsBriefUtils newsBriefUtils;
    List<NewsBriefBean> listNewsBriefBean;
    private SharedPreferences config;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            listNewsBriefBean = (List<NewsBriefBean>) msg.obj;
            NewsBriefAdapter newsAdapter = new NewsBriefAdapter(TypePage.this, listNewsBriefBean);
            mlistview.setAdapter(newsAdapter);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        config = getSharedPreferences("config", MODE_PRIVATE);
        int themeId = getThemeId();
        if (themeId != 0) {
            setTheme(themeId);
        }
        setContentView(R.layout.activity_type_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mintent = getIntent();
        this.type_name = mintent.getDataString();
        for (int i = 1; i <= 12; i++)
            if (NewsBriefBean.NEWS_CLASS_TO_STRING[i].equals(type_name))
            {
                class_tag = i;
            }

        toolbar.setTitle(type_name);
        setSupportActionBar(toolbar);
        mlistview = (ListView) findViewById(R.id.list_news_type);

        newsBriefUtils = new NewsBriefUtils();

        ArrayList<NewsBriefBean> allnews_database = NewsBriefUtils.getTypeDBNews(this, class_tag);

        if (allnews_database != null && allnews_database.size() > 0)
        {
            NewsBriefAdapter newsAdapter = new NewsBriefAdapter(this, allnews_database);
            mlistview.setAdapter(newsAdapter);
        }
       else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // 从网络中调取数据
                    listNewsBriefBean = newsBriefUtils.getNetTypeNewsBrief(TypePage.this, class_tag, 1, 20);
                    Message message = Message.obtain();
                    message.obj = listNewsBriefBean;
                    mHandler.sendMessage(message);
                }
            }).start();
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
        return config.getInt("theme_id", 0);
    }

}
