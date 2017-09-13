package com.java.no_36;

/**
 * Created by lwt on 17-9-5.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

import android.text.TextUtils;
import android.text.Html;
import android.os.Build;
import android.text.method.LinkMovementMethod;

import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.ActionBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsPage extends AppCompatActivity implements OnClickListener {

    Intent mintent;
    private String text;
    private SharedPreferences config;
    private SpeechSynthesizer mySynthesizer;
    private FloatingActionButton tts_Button;
    private boolean isclicked;
    private final String BASE_URL = "<a href=\"https://baike.baidu.com/item/%s\">%s</a>";
    private String jumpURL;

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

            text = bean.getNews_content();
            setLayout(bean);
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
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        config = getSharedPreferences("config", MODE_PRIVATE);
        int themeId = getThemeId();
        if (themeId != 0) {
            setTheme(themeId);
        }
        setContentView(R.layout.activity_news_page);

        this.mintent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tts_Button = (FloatingActionButton) findViewById(R.id.fab);
        tts_Button.setOnClickListener(this);

        isclicked = false;
        Bundle bundle = mintent.getExtras();
        final String id = bundle.getString("id");
        final Context context = this;
        SpeechUtility.createUtility(NewsPage.this, "appid=59b23e6b,force_login=true");
        //处理语音合成关键类
        mySynthesizer = SpeechSynthesizer.createSynthesizer(this, myInitListener);

        NewsBriefDBUtils newsBriefDBUtils = new NewsBriefDBUtils(this);
        newsBriefDBUtils.update_isvisit(id);
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
                showShare();
                break;
            case R.id.Collect:
                getCollect();
                break;
            case R.id.Read:
                disCollect();
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
                if(isclicked) {
                    mySynthesizer.stopSpeaking();
                    isclicked = false;
                } else {
                    //设置发音人
                    mySynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
                    //设置音调
                    mySynthesizer.setParameter(SpeechConstant.PITCH, "50");
                    //设置音量
                    mySynthesizer.setParameter(SpeechConstant.VOLUME, "50");
                    int code = mySynthesizer.startSpeaking(text, mTtsListener);
                    Log.d("start code:", code + "");
                    isclicked = true;
                }
                break;
            default:
                break;
        }
    }

    private void getCollect() {
        Bundle bundle = mintent.getExtras();
        final String id = bundle.getString("id");
        new CollectDBUtils(this).saveCollects(id);
        Toast.makeText(getApplicationContext(), "已收藏",
                Toast.LENGTH_SHORT).show();
    }

    private void disCollect() {
        Bundle bundle = mintent.getExtras();
        final String id = bundle.getString("id");
        boolean jud = new CollectDBUtils(this).deleteoneCollect(id);
        if(jud)
            Toast.makeText(getApplicationContext(), "已取消收藏",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "您还未收藏",
                    Toast.LENGTH_SHORT).show();
    }

    private int getThemeId() {
        return config.getInt("theme_id", R.style.APPTheme_DayTheme);
    }

    private Handler AddPicHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            GlideImageView iv = (GlideImageView) findViewById(R.id.detail_image);
            String[] pictures = (String[]) msg.obj;
            if (pictures != null && pictures.length > 0)
            {
                iv.setImage_url(pictures[0]);
                Log.i("setLayout", pictures[0]);
            }
        };
    };

    private void setLayout(final NewsBean bean)
    {
        GlideImageView iv = (GlideImageView) findViewById(R.id.detail_image);
        String[] news_pictures = bean.getNews_pictures();
        if (news_pictures != null && news_pictures.length > 0 && news_pictures[0].length() > 0)
            iv.setImage_url(news_pictures[0]);
        else // 设置补充图片，因为涉及到网络，必须重新开一个线程
        {
            Log.i("setLayout", "正在设置补充图片");
            new Thread(new Runnable() {

                @Override
                public void run()
                {
                    String[] pictures = null;
                    try
                    {
                        pictures = Crawl.get_picture(bean.getKeywords());
                        Message message = Message.obtain();
                        message.obj = pictures;
                        AddPicHandler.sendMessage(message);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

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

}