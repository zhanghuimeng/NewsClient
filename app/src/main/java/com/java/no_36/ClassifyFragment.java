package com.java.no_36;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.View;

/**
 * Created by lwt on 17-9-9.
 */

public class ClassifyFragment extends Fragment {
    private ListView mlistview;
    private Context mContext;
    private View mview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mview = (View) inflater.inflate(R.layout.fragment_classify,container,false);
        mlistview = (ListView) mview.findViewById(R.id.list_classify);


        return mview;
    }

}
