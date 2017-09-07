package com.java.no_36;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by admin on 2017/9/7.
 */

public class NewsDetailActivity extends AppCompatActivity
{
    // 用来处理多线程问题
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            NewsBean bean = (NewsBean) msg.obj;

            GlideImageView iv = (GlideImageView) findViewById(R.id.detail_image);
            String[] news_pictures = bean.getNews_pictures();
            if (news_pictures != null && news_pictures.length > 0)
                iv.setImage_url(news_pictures[0]);

            TextView title = (TextView) findViewById(R.id.detail_title);
            title.setText(bean.getNews_title());

            TextView content = (TextView) findViewById(R.id.detail_content);
            content.setText(bean.getNews_content());
        };
    };

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_layout);

        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("id");
        final Context context = this;

        new Thread(new Runnable() {

            @Override
            public void run() {
                NewsBean bean = NewsUtils.getNetNews(context, id);
                Message message = Message.obtain();
                message.obj = bean;
                mHandler.sendMessage(message);
            }
        }).start();

    }
}
