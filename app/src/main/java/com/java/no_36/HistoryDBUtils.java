package com.java.no_36;

/**
 * Created by lwt on 17-9-13.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
public class HistoryDBUtils {
    private NewsBriefDBHelper dbHelper;
    final static String DELIMITER = ",";


    public HistoryDBUtils (Context context)
    {
        dbHelper = new NewsBriefDBHelper(context);
    }

    public void saveHistorys(String id) {
        SQLiteDatabase sqLite = dbHelper.getWritableDatabase();

        ContentValues value = new ContentValues();

        value.put("news_id", id);
        sqLite.insert(NewsBriefDBHelper.HISTORY_TABLE_NAME, null, value);
        sqLite.close();
    }

    public void deleteHistorys ()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(NewsBriefDBHelper.HISTORY_TABLE_NAME, null, null);
        db.close();
    }

    public ArrayList<NewsBriefBean> getHistorys() {
        ArrayList<NewsBriefBean> arrayList = new ArrayList<NewsBriefBean>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // table, columns, selection, selectionArgs, groupBy, having, orderBy
        Cursor cursor = db.query(NewsBriefDBHelper.HISTORY_TABLE_NAME, null, null, null, null, null, "read_time DESC");
        if (cursor != null && cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                NewsBriefBean bean = new NewsBriefBean();
                String id = cursor.getString(0);
                String selection = "news_id=?";
                String[] selectionArgs = new String[]{id};
                Cursor cursor1 = db.query(NewsBriefDBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                if(cursor1 != null && cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    bean.setNews_id(cursor1.getString(0));
                    bean.setLang_type(cursor1.getString(1));
                    bean.setNews_class_tag(cursor1.getInt(2));
                    bean.setNews_author(cursor1.getString(3));
                    bean.setNews_pictures(cursor1.getString(4).split(DELIMITER));
                    bean.setNews_source(cursor1.getString(5));
                    bean.setNews_time(new Date(cursor1.getLong(6)));
                    bean.setNews_title(cursor1.getString(7));
                    bean.setNews_url(cursor1.getString(8));
                    bean.setNews_video(cursor1.getString(9).split(DELIMITER));
                    bean.setNews_intro(cursor1.getString(10));
                    bean.setNews_isread(cursor1.getInt(11));
                }
                cursor1.close();
                arrayList.add(bean);
            }
        }
        cursor.close();
        return arrayList;
    }

}
