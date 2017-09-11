package com.java.no_36;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2017/9/7.
 */

public class NewsDetailActivity extends AppCompatActivity implements OnClickListener
{
    private final String BASE_URL = "<a href=\"https://baike.baidu.com/item/%s\">%s</a>";

    private String jumpURL;

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

            // 为了用this移到了外面
            setLayout(bean);
        };
    };

    private void setLayout(NewsBean bean)
    {
        jumpURL = bean.getNews_url();
        findViewById(R.id.jump_url_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.jump_url_btn).setOnClickListener(this);

        GlideImageView iv = (GlideImageView) findViewById(R.id.detail_image);
        String[] news_pictures = bean.getNews_pictures();
        if (news_pictures != null && news_pictures.length > 0)
            iv.setImage_url(news_pictures[0]);

        TextView title = (TextView) findViewById(R.id.detail_title);
        title.setText(bean.getNews_title());

        TextView content = (TextView) findViewById(R.id.detail_content);
        // content.setText(bean.getNews_content());

        // 找出keyword里面得分最高的五个，以及分词里找出来的ORG、PER、LOC
        List<String> keywords = getKeywords(bean);
        String news_content = bean.getNews_content();
        String regex = TextUtils.join("|", keywords);
        Pattern pattern = Pattern.compile(regex);
        StringBuffer sb = new StringBuffer();
        Matcher matcher = pattern.matcher(news_content);
        while (matcher.find())
        {
            String word = matcher.group();
            matcher.appendReplacement(sb, String.format(BASE_URL, word, word));
        }
        matcher.appendTail(sb);
        System.out.println(sb.toString());

        // 设置链接可点击
        // 对不同API的适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            content.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_COMPACT));
        else
            content.setText(Html.fromHtml(sb.toString()));
        content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private List<String> getKeywords(NewsBean bean)
    {
        // organizations, persons, locations, Keywords
        List<String> keywords = new ArrayList<>();
        for (Keyword word : bean.getOrganizations())
            keywords.add(word.word);
        for (Keyword word : bean.getLocations())
            keywords.add(word.word);
        for (Keyword word : bean.getPersons())
            keywords.add(word.word);
        Keyword[] keys = bean.getKeywords();
        for (int i = 0; i < Math.min(keys.length, 5); i++)
            keywords.add(keys[i].word);

        return keywords;
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_layout);
        findViewById(R.id.jump_url_btn).setVisibility(View.INVISIBLE);

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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(jumpURL));
        startActivity(intent);
    }
}
