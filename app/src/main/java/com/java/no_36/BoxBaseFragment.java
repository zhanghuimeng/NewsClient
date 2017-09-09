package com.java.no_36;

/**
 * Created by lwt on 17-9-5.
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class BoxBaseFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView mlistview;
    private Context mContext;
    NewsBriefUtils newsBriefUtils;
    List<NewsBriefBean> listNewsBriefBean;
   // View view;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            listNewsBriefBean = (List<NewsBriefBean>) msg.obj;
            NewsBriefAdapter newsAdapter = new NewsBriefAdapter(getActivity(), listNewsBriefBean);
            mlistview.setAdapter(newsAdapter);

        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mlistview = (ListView)inflater.inflate(R.layout.list_news_brief,container,false);
        mContext = getActivity();
        //mlistview = (ListView) view.findViewById(R.id.list_news_brief);

        newsBriefUtils = new NewsBriefUtils();
        NewsBriefDBUtils newsDatabase = new NewsBriefDBUtils(mContext);

        // 1.先去数据库中获取缓存的新闻数据展示到listview
        ArrayList<NewsBriefBean> allnews_database = NewsBriefUtils.getDBNews(mContext);

        if (allnews_database != null && allnews_database.size() > 0)
        {
            // 创建一个adapter设置给listview
            NewsBriefAdapter newsAdapter = new NewsBriefAdapter(mContext, allnews_database);
            mlistview.setAdapter(newsAdapter);
        }

        new Thread(new Runnable() {

            @Override
            public void run()
            {
                // 从网络中调取数据
                listNewsBriefBean = newsBriefUtils.getNetNewsBrief(mContext, 1, 20);
                Message message = Message.obtain();
                message.obj = listNewsBriefBean;
                mHandler.sendMessage(message);
            }
        }).start();

        mlistview.setOnItemClickListener(this);
        return mlistview;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        /*NewsBriefBean news = (NewsBriefBean) adapterView.getItemAtPosition(position);
        String url = news.getNews_url();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);*/
        NewsBriefBean news = (NewsBriefBean) adapterView.getItemAtPosition(position);
        String url = news.getNews_url();
        Intent intent = new Intent(getActivity(), NewsPage.class);
        intent.setData(Uri.parse(url));
        intent.putExtra("id", news.getNews_id());
        intent.putExtra("title", news.getNews_title());
        intent.putExtra("brief", news.getNews_intro());
        intent.putExtra("image", news.getNews_pictures()[0]);
        startActivity(intent);
    }



}
