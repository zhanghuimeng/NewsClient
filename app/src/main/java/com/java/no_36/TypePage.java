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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
    private NewsBriefAdapter newsAdapter;
    private NewsBriefDBUtils newsDatabase;
    private LinearLayout loading; // 在下侧显示正在加载
    private final int PAGE_SIZE = 20;
    private int page = 0;

    private SharedPreferences config;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            newsAdapter = new NewsBriefAdapter(TypePage.this, listNewsBriefBean);
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
        loading = (LinearLayout) findViewById(R.id.load_more_linearlayout);

        setListViewScroll();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final int pos = position;
        NewsBriefBean news = (NewsBriefBean) adapterView.getItemAtPosition(position);
        NewsBriefDBUtils newsBriefDBUtils = new NewsBriefDBUtils(this);
        newsBriefDBUtils.update_isvisit(news.getNews_id());
        news.setNews_isread(1);
        runOnUiThread(new Runnable() {
            public void run() {
                newsAdapter.setSelectedPosition(pos);
                newsAdapter.notifyDataSetChanged();
            }
        });
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

    private void setListViewScroll()
    {
        mlistview = (ListView) findViewById(R.id.list_news_type);
        newsBriefUtils = new NewsBriefUtils();
        newsDatabase = new NewsBriefDBUtils(this);

        // 抄来的代码
        mlistview.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 当滚动状态放生改变时调用
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 空闲状态
                        // 判断当前listview滚动的位置
                        // 获取最后一条可见条目在集合里面的位置
                        int lastVisiblePosition = mlistview.getLastVisiblePosition();
                        System.out.println("最后一个可见条目的位置 = " + lastVisiblePosition);
                        System.out.println("listViewBean的size = " + listNewsBriefBean.size());
                        // 到了最后一个可见位置后继续查找
                        if (lastVisiblePosition == listNewsBriefBean.size() - 1) {
                            loadNextData(page++);
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 触摸状态
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: // 惯性滑行状态
                        break;
                    default:
                        break;
                }
            }

            // 滚动时调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // 先试图从数据库中获取缓存(<=PAGE_SIZE条)的新闻数据展示到listview
        listNewsBriefBean = newsDatabase.getNews(PAGE_SIZE, page++, class_tag, false);

        if (listNewsBriefBean != null && listNewsBriefBean.size() > 0)
        {
            // 创建一个adapter设置给listview
            newsAdapter = new NewsBriefAdapter(this, listNewsBriefBean);
            mlistview.setAdapter(newsAdapter);
        }

        new Thread(new Runnable() {

            @Override
            public void run()
            {
                // 如果没能从数据库中获取信息，就从网络上调取
                if (listNewsBriefBean == null || listNewsBriefBean.size() == 0) {
                    listNewsBriefBean = NewsBriefUtils.getNetTypeNewsBrief(TypePage.this, class_tag, 1, 20);
                    if (listNewsBriefBean == null || listNewsBriefBean.size() == 0)
                        return;
                    page++;
                    Message message = Message.obtain();
                    mHandler.sendMessage(message);
                }

            }
        }).start();

        mlistview.setOnItemClickListener(this);
    }

    private void loadNextData(final int page)
    {
        loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean isset;
                List<NewsBriefBean> getNewBriefBean = newsDatabase.getNews(PAGE_SIZE, page, class_tag, false);
                if(getNewBriefBean == null || getNewBriefBean.size() == 0)
                    getNewBriefBean = NewsBriefUtils.getNetTypeNewsBrief(TypePage.this, class_tag, page, PAGE_SIZE);
                if (listNewsBriefBean == null) {
                    isset = false;
                    listNewsBriefBean = getNewBriefBean;
                }
                else {
                    isset = true;
                    listNewsBriefBean.addAll(getNewBriefBean);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        loading.setVisibility(View.INVISIBLE);
                        if (newsAdapter != null) {
                            if(isset)
                                newsAdapter.notifyDataSetChanged();
                            else
                                mlistview.setAdapter(newsAdapter);
                        }
                    }
                });
            }
        }).start();
    }

}
