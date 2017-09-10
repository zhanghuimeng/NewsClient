package com.java.no_36;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TypePage extends AppCompatActivity {

    private Intent mintent;
    private String type_name;
    private int class_tag = 0;
    private ListView mlistview;
    NewsBriefUtils newsBriefUtils;
    List<NewsBriefBean> listNewsBriefBean;

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
                    listNewsBriefBean = newsBriefUtils.getNetTypeNewsBrief(TypePage.this, type_name, 1, 20);
                    Message message = Message.obtain();
                    message.obj = listNewsBriefBean;
                    mHandler.sendMessage(message);
                }
            }).start();
        }


    }

}
