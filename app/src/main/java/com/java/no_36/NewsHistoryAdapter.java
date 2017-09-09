package com.java.no_36;

/**
 * Created by lwt on 17-9-8.
 */

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class NewsHistoryAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<NewsBriefBean> mDatas;
    private Context mContext;

    // 用构造器获取传递过来的数据
    public NewsHistoryAdapter(Context context, List<NewsBriefBean> listNewsBean)
    {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDatas = listNewsBean;
        this.mContext = context;
    }

    @Override
    public int getCount()
    {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.news_brief_item, null);
            viewHolder = new ViewHolder();
            viewHolder.item_img_icon = (GlideImageView) convertView.findViewById(R.id.item_img_icon);
            ;
            viewHolder.item_tv_des = (TextView) convertView.findViewById(R.id.item_tv_des);
            viewHolder.item_tv_title = (TextView) convertView.findViewById(R.id.item_tv_title);
            viewHolder.item_tv_source = (TextView) convertView.findViewById(R.id.item_tv_source);
            viewHolder.item_tv_type = (TextView) convertView.findViewById(R.id.item_tv_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NewsBriefBean bean = mDatas.get(position);
        if (bean.getNews_pictures().length > 0)
            viewHolder.item_img_icon.setImage_url(bean.getNews_pictures()[0]);
        else
            viewHolder.item_img_icon.setImage_url("");
        viewHolder.item_tv_des.setText(bean.getNews_intro());
        viewHolder.item_tv_title.setText(bean.getNews_title());
        viewHolder.item_tv_source.setText(bean.getNews_source());
        viewHolder.item_tv_type.setText(NewsBriefBean.NEWS_CLASS_TO_STRING[bean.getNews_class_tag()]);
        return convertView;
    }

}

