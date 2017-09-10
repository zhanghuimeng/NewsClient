package com.java.no_36;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库工具类，封装对数据库进行增删改查的方法
 * Created by admin on 2017/9/4.
 */

public class NewsBriefDBUtils
{
    private NewsBriefDBHelper dbHelper;

    public NewsBriefDBUtils (Context context)
    {
        dbHelper = new NewsBriefDBHelper(context);
    }

    // 保存新闻到数据库中
    public void saveNews(ArrayList<NewsBriefBean> arrayList)
    {
        SQLiteDatabase sqLite = dbHelper.getWritableDatabase();
        for(NewsBriefBean bean : arrayList)
        {
            ContentValues value = new ContentValues();

            value.put("news_id", bean.getNews_id());
            value.put("lang_type", bean.getLang_type());
            value.put("news_class_tag", bean.getNews_class_tag());
            value.put("news_author", bean.getNews_author());
            value.put("news_pictures", joinObject(bean.getNews_pictures(), DELIMITER));
            value.put("news_source", bean.getNews_source());
            value.put("news_time", bean.getNews_time().getTime());
            value.put("news_title", bean.getNews_title());
            value.put("news_url", bean.getNews_url());
            value.put("news_video", joinObject(bean.getNews_video(), DELIMITER));
            value.put("news_intro", bean.getNews_intro());
            value.put("news_isread", bean.getNews_isread());
            sqLite.insert(NewsBriefDBHelper.TABLE_NAME, null, value);
        }
        sqLite.close();
    }


    // 删除数据库数据
    public void deleteNews ()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(NewsBriefDBHelper.TABLE_NAME, null, null);
        db.close();
    }

    // 从数据库中获取存储的行为
    public ArrayList<NewsBriefBean> getNews()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // table, columns, selection, selectionArgs, groupBy, having, orderBy, limit
        Cursor cursor = db.query(NewsBriefDBHelper.TABLE_NAME, null, null, null, null, null, null, null);
        return setBeans(cursor);
    }

    public ArrayList<NewsBriefBean> getTypeNews(int newsClassTag)
    {
        ArrayList<NewsBriefBean> arrayList = new ArrayList<NewsBriefBean>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "news_class_tag=?";
        String[] selectionArgs = new String[]{newsClassTag+""};
        Cursor cursor = db.query(NewsBriefDBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        return setBeans(cursor);
    }

    public void update_isvisit(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("news_isread", 1);
        String[] args = {String.valueOf(id)};
        db.update(NewsBriefDBHelper.TABLE_NAME, values, "news_id=?", args);
    }

    public ArrayList<NewsBriefBean> getHistory() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "news_isread=?";
        String[] selectionArgs = new String[]{"1"};
        Cursor cursor = db.query(NewsBriefDBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        return setBeans(cursor);
    }

    final static String DELIMITER = ",";

    private String joinObject(Object[] array, String delimiter)
    {
        StringBuilder str = new StringBuilder();
        for (Object obj : array)
        {
            str.append(obj.toString());
            str.append(delimiter);
        }
        return str.toString();
    }

    private URL[] splitURL(String str)
    {
        String[] strs = str.split(DELIMITER);
        URL[] urls = new URL[strs.length];
        for (int i = 0; i < strs.length; i++)
        {
            try
            {
                urls[i] = new URL(strs[i]);
            }
            catch (MalformedURLException e)
            {
                urls[i] = null;
            }
        }
        return urls;
    }

    private ArrayList<NewsBriefBean> setBeans(Cursor cursor) {
        ArrayList<NewsBriefBean> arrayList = new ArrayList<NewsBriefBean>();
        if (cursor != null && cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                NewsBriefBean bean = new NewsBriefBean();
                bean.setNews_id(cursor.getString(0));
                bean.setLang_type(cursor.getString(1));
                bean.setNews_class_tag(cursor.getInt(2));
                bean.setNews_author(cursor.getString(3));
                bean.setNews_pictures(cursor.getString(4).split(DELIMITER));
                bean.setNews_source(cursor.getString(5));
                bean.setNews_time(new Date(cursor.getLong(6)));
                bean.setNews_title(cursor.getString(7));
                bean.setNews_url(cursor.getString(8));
                bean.setNews_video(cursor.getString(9).split(DELIMITER));
                bean.setNews_intro(cursor.getString(10));
                bean.setNews_isread(cursor.getInt(11));
                arrayList.add(bean);
            }
        }
        cursor.close();

        return arrayList;
    }

}
