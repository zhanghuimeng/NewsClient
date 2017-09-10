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
    public static final String COLLECT_TABLE_NAME = "news_collect";

    public NewsBriefDBHelper(Context context) {
        super(context, "NewsBrief", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql =
            "CREATE TABLE news_brief(" +
                    "news_id         VARCHAR(200) primary key," +
                "lang_type       VARCHAR(50)," +
                "news_class_tag  INTEGER," +
                "news_author     VARCHAR(100)," +
                "news_pictures   VARCHAR(4000)," +
                "news_source     VARCHAR(200)," +
                "news_time       DATETIME," +
                "news_title      VARCHAR(1000)," +
                "news_url        VARCHAR(200)," +
                "news_video      VARCHAR(4000)," +
                "news_intro      VARCHAR(4000)," +
                "news_isread     INTEGER," +
                    "score           REAL" +

            ");";
        db.execSQL(sql);

        String collectsql =
                "CREATE TABLE news_collect(" +
                        "news_id         VARCHAR(200) primary key," +
                        "collect_time    DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");";
        db.execSQL(collectsql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
