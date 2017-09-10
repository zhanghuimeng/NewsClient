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
    private final int PAGE_SIZE = 20;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            listNewsBriefBean = (List<NewsBriefBean>) msg.obj;
            NewsBriefAdapter newsAdapter = new NewsBriefAdapter(MainActivity.this, listNewsBriefBean);
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
        /*
        listview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        */

        // 1.先去数据库中获取缓存(<=PAGE_SIZE条)的新闻数据展示到listview
        ArrayList<NewsBriefBean> allnews_database = NewsBriefUtils.getDBNews(mContext);
        Log.i("setListViewScroll", String.valueOf(allnews_database.size()));

        if (allnews_database != null && allnews_database.size() > 0)
        {
            // 创建一个adapter设置给listview
            newsAdapter = new NewsBriefAdapter(mContext, allnews_database);
            listview.setAdapter(newsAdapter);
        }

        new Thread(new Runnable() {

            @Override
            public void run()
            {
                // 从网络中调取数据
                listNewsBriefBean = newsBriefUtils.getNetNewsBrief(mContext, 1, 20);
                if (listNewsBriefBean == null)
                    return;
                Message message = Message.obtain();
                message.obj = listNewsBriefBean;
                mHandler.sendMessage(message);
            }
        }).start();

        listview.setOnItemClickListener(this);
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
