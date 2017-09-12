package com.java.no_36;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.java.no_36.asimplecache.ACache;

public class NewsBriefUtils extends AppCompatActivity
{

    // 从网络中获取Json数据，解析Json数据
    // 获取的是某pagesize和pageno下的数据

    private final static String NEWS_BRIEF_URL =
            "http://166.111.68.66:2042/news/action/query/latest?pageNo=%d&pageSize=%d";

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static ArrayList<NewsBriefBean> getNetNewsBrief(Context context, int pageNo, int pageSize)
    {
        ArrayList<NewsBriefBean> arraylistNews = new ArrayList<NewsBriefBean>();
        try
        {
            URL url = new URL(String.format(NEWS_BRIEF_URL, pageNo, pageSize));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            int responseCode = conn.getResponseCode();

            if (responseCode == 200)
            {
                // 获取请求到的流信息
                InputStream is = conn.getInputStream();
                String result = StreamUtils.convertStream(is);
                // Log.i("NewsBriefUtils", result);

                JSONObject root_json = new JSONObject(result);
                JSONArray jsonArray  = root_json.getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i ++ )
                {
                    JSONObject json = jsonArray.getJSONObject(i);
                    NewsBriefBean bean = new NewsBriefBean();

                    bean.setLang_type(json.getString("lang_Type"));
                    bean.setNews_class_tag(json.getString("newsClassTag"));
                    bean.setNews_author(json.getString("news_Author"));
                    bean.setNews_id(json.getString("news_ID"));
                    bean.setNews_pictures(json.getString("news_Pictures").split("\\s|;"));
                    bean.setNews_source(json.getString("news_Source"));
                    try { bean.setNews_time(sdf.parse(json.getString("news_Time").substring(0, 8))); }
                    catch (Exception e) { bean.setNews_time(sdf.parse("20150101")); }
                    // temporary fix: 这个API返回的结果非常鬼畜
                    bean.setNews_title(json.getString("news_Title"));
                    bean.setNews_url(json.getString("news_URL"));
                    bean.setNews_video(json.getString("news_Video").split("\\s|;"));
                    bean.setNews_intro(json.getString("news_Intro"));
                    bean.setScore();
                    int test;
                    if(bean.getNews_source()=="新浪新闻")
                        test = 0;
                    if(bean.getNews_pictures().length == 0 || bean.getNews_pictures()[0] == "") {
                        get_keyword(bean, context);
                        bean.setNews_pictures(Crawl.get_picture(bean.getKeywords()[0].word));
                    }

                    arraylistNews.add(bean);


                }

                // 如果获取到网络上的数据，就删除之前获取的新闻数据，保存新的新闻数据
                // new NewsDBUtils(context).deleteNews();
                new NewsBriefDBUtils(context).saveNews(arraylistNews);

                is.close();

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return arraylistNews;
    }

    // 返回数据库缓存到的数据
    public static ArrayList<NewsBriefBean> getDBNews(Context context)
    {
        return new NewsBriefDBUtils(context).getNews();
    }

    static void get_keyword(NewsBriefBean bean, Context context){

        String GET_URL = "http://166.111.68.66:2042/news/action/query/detail?newsId=%s";

        ACache mCache = ACache.get(context);
        JSONObject json = mCache.getAsJSONObject(bean.getNews_id());
        if (json == null)
        {
            try {
                URL url = new URL(String.format(GET_URL, bean.getNews_id()));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20 * 1000);
                int responseCode = conn.getResponseCode();

                if (responseCode == 200)
                {
                    // 获取请求到的流信息
                    InputStream is = conn.getInputStream();
                    String result = StreamUtils.convertStream(is);
                    json = new JSONObject(result);
                    mCache.put(bean.getNews_id(), json); // 存入缓存中
                    is.close();
                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            JSONArray keywords_array = json.getJSONArray("Keywords");
            Keyword[] keywords = new Keyword[keywords_array.length()];
            for (int i = 0; i < keywords_array.length(); i++) {
                JSONObject word = keywords_array.getJSONObject(i);
                keywords[i] = new Keyword(word.getString("word"), word.getDouble("score"));
            }
            bean.setKeywords(keywords);
        } catch (Exception e) { e.printStackTrace(); }

    }

}
