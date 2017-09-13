package com.java.no_36;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lwt on 17-9-9.
 */

public class ClassifyFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView mlistview;
    private Context mContext;
    private View mview;
    private ListClassifyAdapter newsAdapter;

    private SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    private boolean editState; //是否处于编辑状态
    private List<String> mtitles, untitles;
    private FloatingActionButton addItem, deleteItem;
    private Button getOk, getCancle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mContext = getActivity();
        mview = (View) inflater.inflate(R.layout.fragment_classify,container,false);
        mlistview = (ListView) mview.findViewById(R.id.list_classify);
        editState = false;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
        mtitles = new ArrayList<String>();
        untitles = new ArrayList<String>();
        String[] areas = new String[]{"科技", "教育", "军事", "国内", "社会", "文化", "汽车", "国际", "体育", "财经", "健康", "娱乐" };

        for(int i = 0; i < 12; i++) {
            String tmp = "class" + areas[i];
            if(mSharedPreferences.getBoolean(tmp, true))
                mtitles.add(areas[i]);
            else
                untitles.add(areas[i]);
        }

        newsAdapter = new ListClassifyAdapter(mContext, mtitles, editState);
        mlistview.setAdapter(newsAdapter);
        mlistview.setOnItemClickListener(this);

        addItem = (FloatingActionButton) mview.findViewById(R.id.add_class);
        deleteItem = (FloatingActionButton) mview.findViewById(R.id.delete_class);
        getOk = (Button) mview.findViewById(R.id.get_ok);
        getCancle = (Button) mview.findViewById(R.id.get_cancle);

        addItem.setOnClickListener(this);
        deleteItem.setOnClickListener(this);
        getOk.setOnClickListener(this);
        getCancle.setOnClickListener(this);
        getOk.setVisibility(View.INVISIBLE);
        getCancle.setVisibility(View.INVISIBLE);
        return mview;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(editState) {
            if (view.getTag() instanceof ViewClassifyHolder) {
                ViewClassifyHolder holder = (ViewClassifyHolder) view.getTag();
                holder.cbCheckBox.toggle();
            }
        } else {
            String cur_title = (String) newsAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), TypePage.class);
            intent.setData(Uri.parse(cur_title));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_class:
                additems();
                break;
            case R.id.delete_class:
                deleteitems();
                break;
            case R.id.get_ok:
                String tmptext = (String) getOk.getText();
                if(tmptext == "添加")
                    confirmselected(true);
                else if(tmptext == "删除")
                    confirmselected(false);
                break;
            case R.id.get_cancle:
                cancleselected();
                break;
            default:
                break;
        }
    }

    private void additems() {
        if(untitles.size() > 0) {
            editState = true;
            if(isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        newsAdapter.setData(untitles);
                        newsAdapter.setState(editState);
                        newsAdapter.notifyDataSetChanged();
                    }
                });
            }
            getOk.setVisibility(View.VISIBLE);
            getOk.setText("添加");
            getCancle.setVisibility(View.VISIBLE);
            addItem.setVisibility(View.INVISIBLE);
            deleteItem.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(mContext, "您已选中全部分类，不能再添加", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteitems() {
        if(mtitles.size()>0) {
            editState = true;
            if(isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        newsAdapter.setState(editState);
                        newsAdapter.notifyDataSetChanged();
                    }
                });
            }
            getOk.setVisibility(View.VISIBLE);
            getOk.setText("删除");
            getCancle.setVisibility(View.VISIBLE);
            addItem.setVisibility(View.INVISIBLE);
            deleteItem.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(mContext, "您没有选中任何分类，不能再删除", Toast.LENGTH_LONG).show();
        }
    }

    private void confirmselected(boolean selected) {
        Map<String, Boolean> judsel = newsAdapter.getMap();
        if(selected) {
            for (String key : judsel.keySet())
                if(judsel.get(key)) {
                    mEditor.putBoolean("class" + key, true);
                    mtitles.add(0, key);
                    untitles.remove(key);
                }
        } else {
            for (String key : judsel.keySet())
                if(judsel.get(key)) {
                    mEditor.putBoolean("class" + key, false);
                    mtitles.remove(key);
                    untitles.add(key);
                }
        }
        mEditor.commit();
        cancleselected();
    }

    private void cancleselected() {
        newsAdapter.clearMap();
        newsAdapter.setData(mtitles);
        editState = false;
        newsAdapter.setState(editState);
        newsAdapter.notifyDataSetChanged();
        getOk.setVisibility(View.INVISIBLE);
        getCancle.setVisibility(View.INVISIBLE);
        addItem.setVisibility(View.VISIBLE);
        deleteItem.setVisibility(View.VISIBLE);
    }
}
