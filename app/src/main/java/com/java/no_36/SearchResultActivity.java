package com.java.no_36;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.app.SearchManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
    private NewsBriefAdapter newsAdapter;
    private NewsBriefDBUtils newsDatabase;
    private LinearLayout loading; // 在下侧显示正在加载
    private final int PAGE_SIZE = 20;
    private int page = 0;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
           // listNewsBriefBean = (List<NewsBriefBean>) msg.obj;
            newsAdapter = new NewsBriefAdapter(SearchResultActivity.this, listNewsBriefBean);
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

        loading = (LinearLayout) findViewById(R.id.load_more_linearlayout);
        setListViewScroll();

     /*   mlistview = (ListView) findViewById(R.id.list_news_search);
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

        mlistview.setOnItemClickListener(this); */
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        NewsBriefBean news = (NewsBriefBean) adapterView.getItemAtPosition(position);
        NewsBriefDBUtils newsBriefDBUtils = new NewsBriefDBUtils(this);
        newsBriefDBUtils.update_isvisit(news.getNews_id());
        news.setNews_isread(1);
        final int pos = position;
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
        mlistview = (ListView) findViewById(R.id.list_news_search);
        newsBriefUtils = new NewsBriefUtils();

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


        new Thread(new Runnable() {

            @Override
            public void run()
            {
                // 如果没能从数据库中获取信息，就从网络上调取
                if (listNewsBriefBean == null || listNewsBriefBean.size() == 0) {
                    listNewsBriefBean = NewsBriefUtils.getNetSearchNewsBrief(SearchResultActivity.this, SearchContent, 1, 20);
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
                if (listNewsBriefBean == null) {
                    isset = false;
                    listNewsBriefBean = NewsBriefUtils.getNetSearchNewsBrief(SearchResultActivity.this, SearchContent, page, PAGE_SIZE);
                }
                else {
                    isset = true;
                    listNewsBriefBean.addAll(NewsBriefUtils.getNetSearchNewsBrief(SearchResultActivity.this, SearchContent, page, PAGE_SIZE));
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
