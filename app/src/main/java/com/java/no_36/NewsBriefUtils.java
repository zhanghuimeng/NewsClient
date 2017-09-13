package com.java.no_36;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class NewsBriefUtils
{

    // 从网络中获取Json数据，解析Json数据
    // 获取的是某pagesize和pageno下的数据

    private final static String NEWS_BRIEF_URL =
            "http://166.111.68.66:2042/news/action/query/latest?pageNo=%d&pageSize=%d";

    private  final static String NEWS_SEARCH_URL =
            "http://166.111.68.66:2042/news/action/query/search?keyword=%s&pageNo=%d&pageSize=%d";


    private final static String NEWS_CATEGORY_URL =
            "http://166.111.68.66:2042/news/action/query/latest?pageNo=%d&pageSize=%d&category=%d";

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static ArrayList<NewsBriefBean> getNetNewsBrief(Context context, int pageNo, int pageSize)
    {
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
                Log.i("NewsBriefUtils", result);
                is.close();
                return setNetBeans(context, result, true);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void getNetNewsBrief_onlyDB(Context context, int pageNo, int pageSize)
    {
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
                Log.i("NewsBriefUtils", result);
                is.close();

                // 把JS存到数据库里
                NewsBriefDBHelper dbHelper = new NewsBriefDBHelper(context);
                NewsBriefDBUtils newsBriefDBUtils = new NewsBriefDBUtils(context);
                SQLiteDatabase sqLite = dbHelper.getReadableDatabase();
                try{
                    JSONObject root_json = new JSONObject(result);
                    JSONArray jsonArray  = root_json.getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i ++ )
                    {
                        JSONObject json = jsonArray.getJSONObject(i);
                        ContentValues value = new ContentValues();
                        value.put("news_id", json.getString("news_ID"));
                        value.put("lang_type", json.getString("lang_Type"));
                        value.put("news_class_tag", json.getString("newsClassTag"));
                        value.put("news_author", json.getString("news_Author"));
                        value.put("news_pictures", TextUtils.join(NewsBriefDBUtils.DELIMITER, json.getString("news_Pictures").split("\\s|;")));
                        value.put("news_source", json.getString("news_Source"));
                        Date time = null;
                        try { time = sdf.parse(json.getString("news_Time").substring(0, 8)); }
                        catch (Exception e) { time = sdf.parse("20150101"); }
                        value.put("news_time", time.getTime());
                        value.put("news_title", json.getString("news_Title"));
                        value.put("news_url", json.getString("news_URL"));
                        value.put("news_video", TextUtils.join(NewsBriefDBUtils.DELIMITER, json.getString("news_Video").split("\\s|;")));
                        value.put("news_intro", json.getString("news_Intro"));
                        value.put("news_isread", newsBriefDBUtils.get_isread(json.getString("news_ID")));
                        sqLite.insert(NewsBriefDBHelper.TABLE_NAME, null, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<NewsBriefBean> getNetTypeNewsBrief(Context context, int classTag, int pageNo, int pageSize) {
        try
        {
            URL url = new URL(String.format(NEWS_CATEGORY_URL, pageNo, pageSize, classTag));
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
                is.close();
                return setNetBeans(context, result, true);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<NewsBriefBean> getNetSearchNewsBrief(Context context, String searchkeyword, int pageNo, int pageSize) {
        try
        {
            URL url = new URL(String.format(NEWS_SEARCH_URL, searchkeyword, pageNo, pageSize));
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
                is.close();
                return setNetBeans(context, result, false);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    private static ArrayList<NewsBriefBean> setNetBeans(Context context, String result, boolean isStore) {
        ArrayList<NewsBriefBean> arraylistNews = new ArrayList<NewsBriefBean>();
        try{
        JSONObject root_json = new JSONObject(result);
        JSONArray jsonArray  = root_json.getJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i ++ ) {
            JSONObject json = jsonArray.getJSONObject(i);
            NewsBriefBean bean = new NewsBriefBean();

            bean.setLang_type(json.getString("lang_Type"));
            bean.setNews_class_tag(json.getString("newsClassTag"));
            bean.setNews_author(json.getString("news_Author"));
            bean.setNews_id(json.getString("news_ID"));
            bean.setNews_pictures(json.getString("news_Pictures").split("\\s|;"));
            bean.setNews_source(json.getString("news_Source"));
            try {
                bean.setNews_time(sdf.parse(json.getString("news_Time").substring(0, 8)));
            } catch (Exception e) {
                bean.setNews_time(sdf.parse("20150101"));
            }
            // temporary fix: 这个API返回的结果非常鬼畜
            bean.setNews_title(json.getString("news_Title"));
            bean.setNews_url(json.getString("news_URL"));
            bean.setNews_video(json.getString("news_Video").split("\\s|;"));
            bean.setNews_intro(json.getString("news_Intro"));
            bean.setNews_isread(new NewsBriefDBUtils(context).get_isread(json.getString("news_ID")));
            bean.setScore();
            // Log.i("NewsBriefUtils", bean.getNews_url());
            arraylistNews.add(bean);
        }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(isStore)
            new NewsBriefDBUtils(context).saveNews(arraylistNews);
        return arraylistNews;
    }

}
