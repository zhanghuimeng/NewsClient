package com.java.no_36;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class ShieldPage extends AppCompatActivity {

    private Button btnAdd = null;
    private Button btnReset = null;

    private EditText nameET = null;

    private int entity_id = 0;
    private String entity_name = "";

    private TableLayout table = null;
    private int orders = 0; // 用户记录最大的orders值

    List<String> keywords = new ArrayList<String>();
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        config = getSharedPreferences("config", MODE_PRIVATE);
        int themeId = getThemeId();
        if (themeId != 0) {
            setTheme(themeId);
        }
        setContentView(R.layout.activity_shield_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 实例化按钮并设置监听器.
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnReset = (Button) findViewById(R.id.btnReset);

        btnAdd.setOnClickListener(listener);
        btnReset.setOnClickListener(listener);

        // 实例化EditText
        nameET = (EditText) findViewById(R.id.name);

        // Intent intent = getIntent();

        entity_id = 1; // intent.getIntExtra(CrmConstances.ID, 0);

        nameET.setText(entity_name);

        table = (TableLayout) findViewById(R.id.tableLayout);

    }

    OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnAdd:
                    // 组名称不能为空.
                    if (TextUtils.isEmpty(nameET.getText().toString().trim())) {
                        toastShow("请输入关键词");
                        nameET.requestFocus(); // 设定焦点
                        break;
                    }

                    String entityNameET = nameET.getText().toString().trim();
                    keywords.add(entityNameET);
                    addRow();
                    break;
                case R.id.btnReset:
                    // 若为添加则重置为空,若为修改则重置为打开前的数据.
                    nameET.setText(entity_name);
                    table.removeAllViews();
                    keywords = new ArrayList<String>();
                    toastShow("重置成功,请重新输入");
                    // setResult(CrmConstances.RESULT_FAILURE);
                    break;
                default:
                    break;
            }

        }
    };

    public void toastShow(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void addRow()
    {
        TableRow tableRow = new TableRow(this);
        TextView textView = new TextView(this);
        Button button = new Button(this);

        textView.setText(nameET.getText().toString().trim());
        button.setText("删除");
        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttom_shape));
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TableRow tableRow = (TableRow) view.getParent();
                table.removeView(tableRow);
            }
        });
        tableRow.addView(textView);
        tableRow.addView(button);

        table.addView(tableRow);
    }

    private int getThemeId() {
        return config.getInt("theme_id", R.style.APPTheme_DayTheme);
    }
}
