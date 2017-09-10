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

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewsPage extends AppCompatActivity implements OnClickListener{

    Intent mintent;
    private SpeechSynthesizer mySynthesizer;
    private Button tts_Button;

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

    private InitListener myInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("mySynthesiezer:", "InitListener init() code = " + code);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        tts_Button = (Button) findViewById(R.id.fab);
        tts_Button.setOnClickListener(this);
        /*
        在使用语音平台上传应用包的时候会自定生成一个appid   应该使用与包相对应的appid在申请提交后没有使用次数的限制

        */
        //语音初始化，在使用应用使用时需要初始化一次就好，如果没有这句会出现10111初始化失败
        SpeechUtility.createUtility(NewsPage.this, "appid=59b23e6b,force_login=true");
        //处理语音合成关键类
        mySynthesizer = SpeechSynthesizer.createSynthesizer(this, myInitListener);

        this.mintent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }
        @Override
        public void onSpeakPaused() {
        }
        @Override
        public void onSpeakResumed() {
        }
        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }
        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        @Override
        public void onCompleted(SpeechError error) {
            if(error!=null)
            {
                Log.d("complete code:", error.getErrorCode()+"");
            }
            else
            {
                Log.d("complete code:", "0");
            }
        }
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()){
            case R.id.fab:
                //设置发音人
                mySynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
                //设置音调
                mySynthesizer.setParameter(SpeechConstant.PITCH,"50");
                //设置音量
                mySynthesizer.setParameter(SpeechConstant.VOLUME,"50");
                int code = mySynthesizer.startSpeaking("", mTtsListener);
                Log.d("start code:", code+"");
                break;
            default:
                break;
        }
    }
}