package com.java.no_36;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

/**
 * 数据库帮助类，创建数据库
 * Created by admin on 2017/9/4.
 */

public class NewsBriefDBHelper extends SQLiteOpenHelper
{
    public static final String TABLE_NAME = "news_brief";
    public final static int VERSION = 2;

    public NewsBriefDBHelper(Context context) {
        super(context, "NewsBrief", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql =
            "CREATE TABLE news_brief(" +
                "lang_type       VARCHAR(50)," +
                "news_class_tag  INTEGER," +
                "news_author     VARCHAR(100)," +
                "news_id         VARCHAR(200)," +
                "news_pictures   VARCHAR(4000)," +
                "news_source     VARCHAR(200)," +
                "news_time       DATETIME," +
                "news_title      VARCHAR(1000)," +
                "news_url        VARCHAR(200)," +
                "news_video      VARCHAR(4000)," +
                "news_intro      VARCHAR(4000)," +
                "score           REAL" +
            ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // TODO Auto-generated method stub
        if (oldVersion == 1 && newVersion == 2)
            db.execSQL("ALTER TABLE news_brief ADD score REAL");
    }
}
