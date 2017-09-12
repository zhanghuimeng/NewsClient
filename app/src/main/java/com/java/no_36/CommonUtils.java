package com.java.no_36;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2017/9/10.
 * 定义一些全局变量
 */

public class CommonUtils
{
    private static final String TEXT_MODE = "TEXT_MODE";
    private static final String CACHED_BRIEF = "CACHED_BRIEF";
    private static final String PREF_KEY = "news_app";

    private static Context context;

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
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
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
