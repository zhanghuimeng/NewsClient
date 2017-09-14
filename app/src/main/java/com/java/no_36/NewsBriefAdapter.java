package com.java.no_36;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.java.no_36.asimplecache.ACache;

import java.util.List;

/**
 * ListView的Adapter
 *
 */
public class NewsBriefAdapter extends BaseAdapter
{
    private LayoutInflater mLayoutInflater;
    private List<NewsBriefBean> mDatas;
    private Context mContext;
    private int selectedPosition = -1;// 选中的位置

    // 用于滑动中不加载数据
    private  boolean scrollState = false;
    public void setScrollState(boolean scrollState) {
        this.scrollState = scrollState;
    }

    // 用构造器获取传递过来的数据
    public NewsBriefAdapter(Context context, List<NewsBriefBean> listNewsBean)
    {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDatas = listNewsBean;
        this.mContext = context;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        NewsBriefBean bean= mDatas.get(position);
        int isread = bean.getNews_isread();
        if (selectedPosition == position) {
            isread = 1;
        }
        if (isread == 1)
        {
            convertView = mLayoutInflater.inflate(R.layout.news_read_brief, null);
            viewHolder = new ViewHolder();
            viewHolder.item_img_icon = (GlideImageView) convertView.findViewById(R.id.item_img_icon);;
            viewHolder.item_tv_des = (TextView) convertView.findViewById(R.id.item_tv_des);
            viewHolder.item_tv_title = (TextView) convertView.findViewById(R.id.item_tv_title);
            viewHolder.item_tv_source = (TextView) convertView.findViewById(R.id.item_tv_source);
            viewHolder.item_tv_type = (TextView) convertView.findViewById(R.id.item_tv_type);
            convertView.setTag(viewHolder);
        }
        else
        {
            convertView = mLayoutInflater.inflate(R.layout.news_brief_item, null);
            viewHolder = new ViewHolder();
            viewHolder.item_img_icon = (GlideImageView) convertView.findViewById(R.id.item_img_icon);;
            viewHolder.item_tv_des = (TextView) convertView.findViewById(R.id.item_tv_des);
            viewHolder.item_tv_title = (TextView) convertView.findViewById(R.id.item_tv_title);
            viewHolder.item_tv_source = (TextView) convertView.findViewById(R.id.item_tv_source);
            viewHolder.item_tv_type = (TextView) convertView.findViewById(R.id.item_tv_type);
            convertView.setTag(viewHolder);
        }

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
class ViewHolder{
    GlideImageView item_img_icon;
    TextView item_tv_des;
    TextView item_tv_title;
    TextView item_tv_source;
    TextView item_tv_type;
}
