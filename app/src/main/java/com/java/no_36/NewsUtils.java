package com.java.no_36;

import com.java.no_36.asimplecache.ACache;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 与news相关的utility类
 * Created by admin on 2017/9/5.
 */

public class NewsUtils
{
    // 通过ID获取：如果在缓存中存在，则从缓存中读取；否则从网络中获取Json数据，并存入缓存中
    private static final int SAVE_TIME = 2 * ACache.TIME_DAY;
    private static final String GET_URL = "http://166.111.68.66:2042/news/action/query/detail?newsId=%s";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddssssss");

    public static NewsBean getNetNews(Context context, String news_id)
    {
        ACache mCache = ACache.get(context);
        NewsBean bean = new NewsBean();
        JSONObject json = mCache.getAsJSONObject(news_id);
        if (json == null)
        {
            try
            {
                URL url = new URL(String.format(GET_URL, news_id));


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
                    mCache.put(news_id, json, SAVE_TIME); // 存入缓存中
                    is.close();
                }

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        if (json == null)
            return null;

        // 将JSONObject转换为NewsBean
        try {
            JSONArray keywords_array = json.getJSONArray("Keywords");
            Keyword[] keywords = new Keyword[keywords_array.length()];
            for (int i = 0; i < keywords_array.length(); i++) {
                JSONObject word = keywords_array.getJSONObject(i);
                keywords[i] = new Keyword(word.getString("word"), word.getDouble("score"));
            }
            bean.setKeywords(keywords);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            JSONArray bow_array = json.getJSONArray("bagOfWords");
            Keyword[] bag_of_words = new Keyword[bow_array.length()];
            for (int i = 0; i < bow_array.length(); i++) {
                JSONObject word = bow_array.getJSONObject(i);
                bag_of_words[i] = new Keyword(word.getString("word"), word.getInt("score"));
            }
            bean.setBag_of_words(bag_of_words);
        } catch (Exception e) { e.printStackTrace(); }

        try { bean.setCrawl_source(json.getString("crawl_Source")); }
        catch (Exception e) { e.printStackTrace(); }

        try {
            Date crawl_time = SDF.parse(json.getString("crawl_Time"));
            bean.setCrawl_time(crawl_time);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            // 实际上，我也不知道这个是不是靠谱……不知道不空的列表是什么样子的
            bean.setInborn_keywords(json.getString("inborn_KeyWords").split(";|\\s"));
        } catch (Exception e) { e.printStackTrace(); }

        try { bean.setLang_type(json.getString("lang_Type")); }
        catch (Exception e) { e.printStackTrace(); }

        try {
            JSONArray loc_array = json.getJSONArray("locations");
            Keyword[] locations = new Keyword[loc_array.length()];
            for (int i = 0; i < loc_array.length(); i++) {
                JSONObject word = loc_array.getJSONObject(i);
                locations[i] = new Keyword(word.getString("word"), word.getInt("count"));
            }
            bean.setLocations(locations);
        } catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_class_tag(json.getString("newsClassTag")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_author(json.getString("news_Author")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_category(json.getString("news_Category")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_content(json.getString("news_Content")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_id(json.getString("news_ID")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_journal(json.getString("news_Journal")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_pictures(json.getString("news_Pictures").split(";|\\s")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_time(SDF.parse(json.getString("news_Time"))); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_title(json.getString("news_Title")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_url(json.getString("news_URL")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setNews_video(json.getString("news_Video").split(";|\\s")); }
        catch (Exception e) { e.printStackTrace(); }

        try {
            // 并不知道实际上是怎样的
            JSONArray org_array = json.getJSONArray("organizations");
            Keyword[] orgs = new Keyword[org_array.length()];
            for (int i = 0; i < org_array.length(); i++) {
                JSONObject word = org_array.getJSONObject(i);
                orgs[i] = new Keyword(word.getString("word"), word.getInt("count"));
            }
            bean.setOrganizations(orgs);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            JSONArray person_array = json.getJSONArray("persons");
            Keyword[] persons = new Keyword[person_array.length()];
            for (int i = 0; i < person_array.length(); i++) {
                JSONObject word = person_array.getJSONObject(i);
                persons[i] = new Keyword(word.getString("word"), word.getInt("count"));
            }
            bean.setPersons(persons);
        } catch (Exception e) { e.printStackTrace(); }

        try { bean.setRepeat_id(json.getInt("repeat_ID")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setSegged_p_list_of_content(json.getString("seggedPListOfContent")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setSegged_title(json.getString("seggedTitle")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setWord_count_of_content(json.getInt("wordCountOfContent")); }
        catch (Exception e) { e.printStackTrace(); }

        try { bean.setWord_count_of_title(json.getInt("wordCountOfTitle")); }
        catch (Exception e) { e.printStackTrace(); }

        return bean;
    }
}
