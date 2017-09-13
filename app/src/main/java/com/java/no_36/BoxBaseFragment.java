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
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import android.widget.AbsListView;
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

        // 测试屏蔽
        // CommonUtils.addScreenedKeyword("货币");

        Log.e("BaseFrag", "onCreate");

        loading = (LinearLayout) mview.findViewById(R.id.load_more_linearlayout);
        setListViewScroll();

        Log.e("BaseFrag", "after setListViewScroll");
        return mview;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        NewsBriefBean news = (NewsBriefBean) adapterView.getItemAtPosition(position);
        NewsBriefDBUtils newsBriefDBUtils = new NewsBriefDBUtils(getActivity());
        newsBriefDBUtils.update_isvisit(news.getNews_id());
        news.setNews_isread(1);
        newsAdapter.setSelectedPosition(position);
        newsAdapter.notifyDataSetInvalidated();

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
                        // 设置滚动状态
                        newsAdapter.setScrollState(false);
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
                        newsAdapter.setScrollState(true);
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: // 惯性滑行状态
                        newsAdapter.setScrollState(true);
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

        Log.e("BaseFrag", "before accessing the database");

        // 先试图从数据库中获取缓存(<=PAGE_SIZE条)的新闻数据展示到listview
        // 外面包了一层筛选关键词
        while (listNewsBriefBean == null || listNewsBriefBean.size() == 0)
        {
            List<NewsBriefBean> list = newsDatabase.getNews(PAGE_SIZE, page++, false);
            if (list == null || list.size() == 0) // 从数据库中获取不到任何信息了
                break;
            listNewsBriefBean = CommonUtils.screenList(list);
            Log.e("BaseFrag", "Accessing " + page);
        }

        Log.e("从数据库中获取缓存", "a");

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

                // 从网络上缓存
                for (int i = CommonUtils.getCachedBrief() + 1; i <= 500; i++)
                {
                    // NewsBriefUtils.getNetNewsBrief(mContext, i, 500);
                    NewsBriefUtils.getNetNewsBrief_onlyDB(mContext, i, 500);
                    CommonUtils.setCachedBrief(i + 1);
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
                if(isAdded()) {
                    // 现在，需要执行对关键词的屏蔽
                    List<NewsBriefBean> new_list_to_add = newsDatabase.getNews(PAGE_SIZE, page, false);
                    final List<NewsBriefBean> screened_list = CommonUtils.screenList(new_list_to_add);
                    // screened_list现在应该存放了全部筛过的新闻
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            loading.setVisibility(View.INVISIBLE);
                            if (newsAdapter == null) {
                                if (listNewsBriefBean == null) {
                                    listNewsBriefBean = screened_list;
                                } else {
                                    listNewsBriefBean.addAll(screened_list);
                                }
                                newsAdapter = new NewsBriefAdapter(getActivity().getApplicationContext(), listNewsBriefBean);
                                mlistview.setAdapter(newsAdapter);
                            } else // adapter存在的话，通知更新（listNewsBriefBean必然也不为空了）
                            {
                                listNewsBriefBean.addAll(screened_list);
                                // 测试：加载某一种类的新闻（由于开始加载部分的没有改，所以前20条会返回奇怪的东西……
                                // 总之像下面这样调用就可以返回固定种类的新闻了）
                                // listNewsBriefBean.addAll(newsDatabase.getNews(PAGE_SIZE, page, new String[]{"1", "3"}, false));
                                newsAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }).start();
    }



}
