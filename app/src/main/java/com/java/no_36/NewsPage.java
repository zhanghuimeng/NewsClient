package com.java.no_36;

/**
 * Created by lwt on 17-9-5.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.onekeyshare.OnekeyShare;
import android.os.Handler;
import android.content.Context;
import android.os.Message;

public class NewsPage extends AppCompatActivity {

    Intent mintent;
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
            String text = bean.getNews_content();
            text = text.replaceAll("  ", "\n");
            content.setText(text);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);
        this.mintent = getIntent();
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
        Bundle bundle = mintent.getExtras();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news_page_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.Share:
                /*Toast.makeText(this,"you clicked Share",Toast.LENGTH_SHORT).show();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/
                showShare();
                break;
            case R.id.Collect:
                Toast.makeText(this,"you clicked Collect",Toast.LENGTH_SHORT).show();
            case R.id.Read:
                Toast.makeText(this,"you clicked Read",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        Bundle bundle = mintent.getExtras();
        oks.disableSSOWhenAuthorize();
        oks.setTitle(bundle.getString("title"));
        oks.setTitleUrl(mintent.getDataString());
        oks.setText(bundle.getString("brief"));
        oks.setImageUrl(bundle.getString("image"));
        oks.setUrl(mintent.getDataString());
        oks.setComment("我是测试评论文本");
        oks.show(this);
    }
}