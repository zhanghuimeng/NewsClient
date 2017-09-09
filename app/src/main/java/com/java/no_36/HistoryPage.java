package com.java.no_36;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryPage extends AppCompatActivity {
    ArrayList<NewsBriefBean> arraylistHistorys;
    private ListView mlistview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mlistview = (ListView) findViewById(R.id.list_news_his);
        gethistory();
        NewsHistoryAdapter newsAdapter = new NewsHistoryAdapter(this, arraylistHistorys);
        mlistview.setAdapter(newsAdapter);
    }

    void gethistory() {
        arraylistHistorys = new NewsBriefDBUtils(HistoryPage.this).getHistory();
    }
}
