package com.java.no_36;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener
{
    private Context mContext;
    private TextView textView;
    private NewsBriefUtils newsBriefUtils;
    private List<NewsBriefBean> listNewsBriefBean;
    private ListView listview;
    private NewsBriefAdapter newsAdapter;
    private NewsBriefDBUtils newsDatabase;
    private LinearLayout loading; // 在下侧显示正在加载
    private final int PAGE_SIZE = 20;
    private int page = 0;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            newsAdapter = new NewsBriefAdapter(MainActivity.this, listNewsBriefBean);
            listview.setAdapter(newsAdapter);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // auto-gen start
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // auto-gen end

        // 设置CommonUtils的内容
        CommonUtils.setContext(getApplicationContext());
        // 测试：文字模式
        CommonUtils.setTextMode(false);

        loading = (LinearLayout) findViewById(R.id.load_more_linearlayout);

        // 设置listview相关
        setListViewScroll();
    }

    private void setListViewScroll()
    {
        mContext = MainActivity.this;
        listview = (ListView) findViewById(R.id.list_news_brief);
        newsBriefUtils = new NewsBriefUtils();
        newsDatabase = new NewsBriefDBUtils(mContext);

        // 抄来的代码
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 当滚动状态放生改变时调用
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 空闲状态
                        // 判断当前listview滚动的位置
                        // 获取最后一条可见条目在集合里面的位置
                        int lastVisiblePosition = listview.getLastVisiblePosition();
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
        listNewsBriefBean = newsDatabase.getNews(PAGE_SIZE, page++, false);
        Log.e("从数据库中获取缓存", String.valueOf(listNewsBriefBean.size()));

        if (listNewsBriefBean != null && listNewsBriefBean.size() > 0)
        {
            // 创建一个adapter设置给listview
            newsAdapter = new NewsBriefAdapter(mContext, listNewsBriefBean);
            listview.setAdapter(newsAdapter);
        }

        new Thread(new Runnable() {

            @Override
            public void run()
            {
            // 如果没能从数据库中获取信息，就从网络上调取
            if (listNewsBriefBean == null || listNewsBriefBean.size() == 0) {
                listNewsBriefBean = NewsBriefUtils.getNetNewsBrief(mContext, 1, 20);
                if (listNewsBriefBean == null || listNewsBriefBean.size() == 0)
                    return;
                page++;
                Message message = Message.obtain();
                mHandler.sendMessage(message);
            }

            for (int i = 1; i <= 500; i++)
                NewsBriefUtils.getNetNewsBrief(mContext, i, 500);

            }
        }).start();

        listview.setOnItemClickListener(this);
    }

    private void loadNextData(final int page)
    {
        loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {
                        loading.setVisibility(View.INVISIBLE);
                        if (newsAdapter == null)
                        {
                            if (listNewsBriefBean == null) {
                                listNewsBriefBean = newsDatabase.getNews(PAGE_SIZE, page, false);
                            } else {
                                listNewsBriefBean.addAll(newsDatabase.getNews(PAGE_SIZE, page, false));
                            }
                            if (newsAdapter != null)
                            {
                                newsAdapter = new NewsBriefAdapter(getApplicationContext(), listNewsBriefBean);
                                listview.setAdapter(newsAdapter);
                            }
                        }
                        else // adapter存在的话，通知更新（listNewsBriefBean必然也不为空了）
                        {
                            listNewsBriefBean.addAll(newsDatabase.getNews(PAGE_SIZE, page, false));
                            // 测试：加载某一种类的新闻（由于开始加载部分的没有改，所以前20条会返回奇怪的东西……
                            // 总之像下面这样调用就可以返回固定种类的新闻了）
                            /* listNewsBriefBean.addAll(newsDatabase.getNews(PAGE_SIZE, page,
                                    new String[]{"1", "3"}, false)); */
                            newsAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).start();
    }


    // 当List被点击的时候
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        /* 以下部分打开一个网页浏览新的新闻界面
        NewsBriefBean news = (NewsBriefBean) parent.getItemAtPosition(position);
        String url = news.getNews_url();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        */

        // 写了一个简单的新闻详情界面
        NewsBriefBean news = (NewsBriefBean) parent.getItemAtPosition(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", news.getNews_id());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(MainActivity.this, NewsDetailActivity.class);
        Log.i("MainActivity", news.getNews_id());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
