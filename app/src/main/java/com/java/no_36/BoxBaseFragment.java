package com.java.no_36;

/**
 * Created by lwt on 17-9-5.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import android.widget.AbsListView;
import android.net.*;
import android.widget.AdapterView.OnItemClickListener;



public class BoxBaseFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView mlistview;
    private View mview;
    private Context mContext;
    NewsBriefUtils newsBriefUtils;
    List<NewsBriefBean> listNewsBriefBean;
    private NewsBriefAdapter newsAdapter;
    private NewsBriefDBUtils newsDatabase;
    private LinearLayout loading; // 在下侧显示正在加载
    private final int PAGE_SIZE = 20;
    private int page = 0;
    private MsgReceiver msgReceiver; // 用来接收刷新信息

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            newsAdapter = new NewsBriefAdapter(getActivity(), listNewsBriefBean);
            mlistview.setAdapter(newsAdapter);
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mview = (View)inflater.inflate(R.layout.content_main,container,false);
        mContext = getActivity();

        loading = (LinearLayout) mview.findViewById(R.id.load_more_linearlayout);

        mlistview = (ListView) mview.findViewById(R.id.list_news_brief);
        newsBriefUtils = new NewsBriefUtils();
        newsDatabase = new NewsBriefDBUtils(mContext);

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
        setListViewScroll();
        return mview;
    }

    // 为了unregister
    // https://stackoverflow.com/questions/16616654/registering-and-unregistering-broadcastreceiver-in-a-fragment
    // 据说在onResume里register，在onPause里unRegister比较好
    @Override
    public void onResume()
    {
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.java.no_36.REFRESH_SHIELD");
        getActivity().registerReceiver(msgReceiver, intentFilter);
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(msgReceiver);
        super.onPause();
    }

    /**
     * 广播接收器
     * @author len
     *
     */
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 重建这个界面
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    listNewsBriefBean = null;
                    page = 0;
                    setListViewScroll();
                }
            });
        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final int pos = position;
        NewsBriefBean news = (NewsBriefBean) adapterView.getItemAtPosition(position);
        NewsBriefDBUtils newsBriefDBUtils = new NewsBriefDBUtils(getActivity());
        newsBriefDBUtils.update_isvisit(news.getNews_id());
        news.setNews_isread(1);
        HistoryDBUtils historyDBUtils = new HistoryDBUtils(getActivity());
        historyDBUtils.saveHistorys(news.getNews_id());
        if(isAdded()) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    newsAdapter.setSelectedPosition(pos);
                    newsAdapter.notifyDataSetChanged();
                }
            });
        }
        String url = news.getNews_url();
        Intent intent = new Intent(getActivity(), NewsPage.class);
        intent.setData(Uri.parse(url));
        intent.putExtra("id", news.getNews_id());
        intent.putExtra("title", news.getNews_title());
        intent.putExtra("brief", news.getNews_intro());
        if(news.getNews_pictures().length > 0)
            intent.putExtra("image", news.getNews_pictures()[0]);
        startActivity(intent);
    }


    private void setListViewScroll()
    {

        while (listNewsBriefBean == null || listNewsBriefBean.size() == 0)
        {
            List<NewsBriefBean> list = newsDatabase.getNews(PAGE_SIZE, page++, false);
            if (list == null || list.size() == 0) // 从数据库中获取不到任何信息了
                break;
            listNewsBriefBean = CommonUtils.screenList(list);
        }


        if (listNewsBriefBean != null && listNewsBriefBean.size() > 0)
        {
            // 创建一个adapter设置给listview
            newsAdapter = new NewsBriefAdapter(mContext, listNewsBriefBean);
            mlistview.setAdapter(newsAdapter);
        }

        new Thread(new Runnable() {

            @Override
            public void run()
            {
                // 如果没能从数据库中获取信息，就从网络上调取
                // 同时加了关键词过滤
                while (listNewsBriefBean == null || listNewsBriefBean.size() == 0) {
                    List<NewsBriefBean> list = NewsBriefUtils.getNetNewsBrief(mContext, 1, 20);
                    if (list == null || list.size() == 0) // 没网了
                        break;
                    listNewsBriefBean = CommonUtils.screenList(list);
                    page++;
                }

                // 说明大概没网了
                if (listNewsBriefBean == null || listNewsBriefBean.size() == 0)
                    return;

                Message message = Message.obtain();
                mHandler.sendMessage(message);

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
                List<NewsBriefBean> getNewBriefBean = newsDatabase.getNews(PAGE_SIZE, page, false);
                if(getNewBriefBean == null || getNewBriefBean.size() == 0) getNewBriefBean = NewsBriefUtils.getNetNewsBrief(getActivity(), page, PAGE_SIZE);
                if(getNewBriefBean != null) {
                    if (listNewsBriefBean == null) {
                        isset = false;
                        listNewsBriefBean = CommonUtils.screenList(getNewBriefBean);
                    } else {
                        isset = true;
                        listNewsBriefBean.addAll(CommonUtils.screenList(getNewBriefBean));
                    }

                    if (isAdded()) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                loading.setVisibility(View.INVISIBLE);
                                if (newsAdapter != null) {
                                    if (isset)
                                        newsAdapter.notifyDataSetChanged();
                                    else
                                        mlistview.setAdapter(newsAdapter);
                                } else {
                                    newsAdapter = new NewsBriefAdapter(getActivity().getApplicationContext(), listNewsBriefBean);
                                    mlistview.setAdapter(newsAdapter);
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }



}
