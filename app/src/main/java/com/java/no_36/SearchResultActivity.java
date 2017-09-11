package com.java.no_36;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.app.SearchManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * Created by lwt on 17-9-10.
 */

public class SearchResultActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView mlistview;
    private String SearchContent;
    NewsBriefUtils newsBriefUtils;
    List<NewsBriefBean> listNewsBriefBean;
    private SharedPreferences config;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            listNewsBriefBean = (List<NewsBriefBean>) msg.obj;
            NewsBriefAdapter newsAdapter = new NewsBriefAdapter(SearchResultActivity.this, listNewsBriefBean);
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
        setContentView(R.layout.activity_search_result);
        SearchContent = getIntent().getStringExtra(SearchManager.QUERY);

        mlistview = (ListView) findViewById(R.id.list_news_search);
        newsBriefUtils = new NewsBriefUtils();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // 从网络中调取数据
                listNewsBriefBean = newsBriefUtils.getNetSearchNewsBrief(SearchResultActivity.this, SearchContent, 1, 500);
                Message message = Message.obtain();
                message.obj = listNewsBriefBean;
                mHandler.sendMessage(message);
            }
        }).start();

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
