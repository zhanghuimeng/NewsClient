package com.java.no_36;

import com.ns.developer.tagview.entity.*;
import com.ns.developer.tagview.widget.TagCloudLinkView;;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by admin on 2017/9/13.
 * 设置屏蔽列表的类
 */

public class ShieldSetActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean toRefresh = false; // 关键词列表如果更新，就需要刷新（所有的）activity

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shield_set);
        TagCloudLinkView view = (TagCloudLinkView) findViewById(R.id.shield_cloud_view);

        for (String word : CommonUtils.getScreened_keyword_list())
            view.add(new Tag(1, word));

        view.drawTags();

        Button btn = (Button) findViewById(R.id.add_shield_btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) findViewById(R.id.add_shield_btn);
        EditText edt = (EditText) findViewById(R.id.add_shield_edttxt);
        TagCloudLinkView view = (TagCloudLinkView) findViewById(R.id.shield_cloud_view);

        if (btn.getText().equals(this.getString(R.string.add_shield_to_adding))) // 点击了添加
        {
            edt.setVisibility(View.VISIBLE);
            btn.setText(R.string.add_shield_added);
        }
        else // 点击了确认
        {
            String word = edt.getText().toString();
            if (CommonUtils.addScreenedKeyword(word))
            {
                view.add(new Tag(1, word));
                view.drawTags();
                Log.i("shield activity", view.getTags().toString());
                toRefresh = true;
                // view.drawTags();
            }
            edt.setText("");
            edt.setVisibility(View.INVISIBLE);
            btn.setText(R.string.add_shield_to_adding);
        }
    }

    @Override
    protected void onDestroy() // 此时，向activity们发送广播，表示关键词屏蔽已经更新了
    {
        Log.e("shield activity", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        Log.d("shield activity", "onPause");
        super.onPause();
    }
}
