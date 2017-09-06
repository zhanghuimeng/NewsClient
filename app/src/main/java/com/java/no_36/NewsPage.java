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
import android.widget.Toast;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsPage extends AppCompatActivity {

    Intent mintent;
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