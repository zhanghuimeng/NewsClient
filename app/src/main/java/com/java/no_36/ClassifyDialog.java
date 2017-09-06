package com.java.no_36;

/**
 * Created by lwt on 17-9-6.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ListView;
import android.widget.Toast;

public class ClassifyDialog {
    Context mcontext;
    private ListView areaCheckListView;
    String mtitle = "关注话题";
    private String[] areas = new String[]{"科技", "教育", "军事", "国内", "社会", "文化", "汽车", "国际", "体育", "财经", "健康", "娱乐" };
    /**
     * 从数据库获取当前选择的内容
     */
    private boolean[] areaState=new boolean[]{false, false, false, false, false,false, false, false, false, false, false,false };

    public ClassifyDialog(Context context) {
        mcontext = context;
    }
    public void createmydialog() {
        AlertDialog ad = new AlertDialog.Builder(mcontext)
                .setTitle(mtitle)
                .setMultiChoiceItems(areas,areaState,new DialogInterface.OnMultiChoiceClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton, boolean isChecked){
                        //点击某个区域
                    }
                }).setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        String s = "您选择了:";
                        for (int i = 0; i < areas.length; i++){
                            if (areaCheckListView.getCheckedItemPositions().get(i)){
                                s += i + ":"+ areaCheckListView.getAdapter().getItem(i)+ "  ";
                            }else{
                                areaCheckListView.getCheckedItemPositions().get(i,false);
                            }
                        }
                        if (areaCheckListView.getCheckedItemPositions().size() > 0){
                            Toast.makeText(mcontext, s, Toast.LENGTH_LONG).show();
                        }else{
                            //没有选择
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).create();
        areaCheckListView = ad.getListView();
        ad.show();
    }
}