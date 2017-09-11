package com.java.no_36;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

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

            if (bean == null)
            {
                TextView title = (TextView) findViewById(R.id.detail_title);
                title.setText("网络错误");

                TextView content = (TextView) findViewById(R.id.detail_content);
                content.setText("网络错误");

                return;
            }

            GlideImageView iv = (GlideImageView) findViewById(R.id.detail_image);
            String[] news_pictures = bean.getNews_pictures();
            if (news_pictures != null && news_pictures.length > 0)
                iv.setImage_url(news_pictures[0]);

            TextView title = (TextView) findViewById(R.id.detail_title);
            title.setText(bean.getNews_title());

            TextView content = (TextView) findViewById(R.id.detail_content);
            // content.setText(bean.getNews_content());
            String result = processSeggedContent(bean.getSegged_p_list_of_content());
            if (result != null)
                content.setText(result);
        };
    };

    private String processSeggedContent(String seggedCont)
    {
        JSONArray json;
        String content;
        try
        {
            json = new JSONArray(seggedCont);
            content = json.getString(0);
        }
        catch (Exception e) { e.printStackTrace(); return null; }

        content = content.replaceAll("/[a-zA-Z]+\\s\\s", "");
        // content = seggedCont;
        return content;
    }

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
