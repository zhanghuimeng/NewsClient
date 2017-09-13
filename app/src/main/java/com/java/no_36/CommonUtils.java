package com.java.no_36;

/**
 * Created by lwt on 17-9-10.
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonUtils {
    private static final String TEXT_MODE = "TEXT_MODE";
    private static final String PREF_KEY = "news_app";
    private static final String CACHED_BRIEF = "CACHED_BRIEF";
    // 用于关键词屏蔽
    private static Set<String> screened_keyword_set = new HashSet<>();

    private static Context context;

    /**
     * 成功加入的时候返回true；如果已经存在了，就返回false
     * @param word 需要添加的关键词
     * @return 是否添加成功
     */
    public static boolean addScreenedKeyword(String word)
    {
        if (screened_keyword_set.contains(word))
            return false;
        screened_keyword_set.add(word);
        return true;
    }

    /**
     *
     * @param word 需要删除的关键词
     * @return 是否删除成功
     */
    public static boolean deleteScreenedKeyword(String word)
    {
        if (screened_keyword_set.contains(word))
        {
            screened_keyword_set.remove(word);
            return true;
        }
        return false;
    }

    public static Set<String> getScreened_keyword_set() { return screened_keyword_set; }
    public static List<String> getScreened_keyword_list()
    {
        return new ArrayList<>(screened_keyword_set);
    }

    public static List<NewsBriefBean> screenList(List<NewsBriefBean> list)
    {
        List<NewsBriefBean> screened_list = new ArrayList<NewsBriefBean>();
        for (NewsBriefBean bean: list)
        {
            NewsBean newsBean = NewsUtils.getNetNews(context, bean.getNews_id());
            if (newsBean == null)
                continue;
            String text = newsBean.getNews_content() + newsBean.getNews_title();
            if (!CommonUtils.isInScreenedSet(text))
                screened_list.add(bean);
        }
        return screened_list;
    }

    /**
     * 判断text中是否出现了需要屏蔽的关键词
     * @param text 需要判断的text
     * @return 里面是否出现了关键词
     */
    public static boolean isInScreenedSet(String text)
    {
        // 简单实现
        for (String x : screened_keyword_set)
        {
            if (text.contains(x))
                return true;
        }
        return false;
    }

    public static void setContext(Context context2)
    {
        context = context2;
    }

    public static void setTextMode(boolean textMode)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_KEY, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(TEXT_MODE, textMode);
        editor.apply();
    }

    public static boolean getTextMode()
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_KEY, context.MODE_PRIVATE);
        return sharedPref.getBoolean(TEXT_MODE, false);
    }

    public static void setCachedBrief(int page)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(CACHED_BRIEF, page);
        editor.apply();
    }

    public static int getCachedBrief()
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        return sharedPref.getInt(CACHED_BRIEF, 0);
    }
}
