package com.java.no_36;

/**
 * Created by lwt on 17-9-10.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ListClassifyAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private boolean mshowCheckBox;
    private Context mContext;
    private List<String> mtitles = new ArrayList<String>();
    private Map<String, Boolean> isCheck = new HashMap<String, Boolean>();

    public ListClassifyAdapter(Context mContext, List<String> titles, boolean showCheckBox) {
        super();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mshowCheckBox = showCheckBox;
        this.mtitles = titles;
    }

    @Override
    public int getCount() {
        return mtitles.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mtitles.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String title = mtitles.get(position);
        ViewClassifyHolder viewClassifyHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.classify_item, null);
            viewClassifyHolder = new ViewClassifyHolder();
            viewClassifyHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewClassifyHolder.cbCheckBox = (CheckBox) convertView.findViewById(R.id.cbCheckBox);
            convertView.setTag(viewClassifyHolder);
        } else {
            viewClassifyHolder = (ViewClassifyHolder) convertView.getTag();
        }
        viewClassifyHolder.tvTitle.setText(title);
        if(mshowCheckBox == false) viewClassifyHolder.cbCheckBox.setVisibility(View.INVISIBLE);
        else viewClassifyHolder.cbCheckBox.setVisibility(View.VISIBLE);

        viewClassifyHolder.cbCheckBox
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        isCheck.put(title, isChecked);
                    }
                });

        if (isCheck.get(title) == null) {
            isCheck.put(title, false);
        }
        viewClassifyHolder.cbCheckBox.setChecked(isCheck.get(title));
        return convertView;
    }

    public Map<String, Boolean> getMap() {
        return isCheck;
    }

    public void clearMap() {
        isCheck.clear();
    }

    public void setData(List<String> data) {
        this.mtitles = data;
    }

    public void setState(boolean checkstate) {
        mshowCheckBox = checkstate;
    }

}

class ViewClassifyHolder {
    TextView tvTitle;
    CheckBox cbCheckBox;
}