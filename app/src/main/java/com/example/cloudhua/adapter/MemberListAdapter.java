package com.example.cloudhua.adapter;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.cloudhua.ordersys.R;
import com.example.cloudhua.ordersys.TeamInfoActivity;

import java.util.List;

/**
 * Created by cloudhua on 16-8-2.
 */
public class MemberListAdapter extends BaseAdapter{
    List<List<String>> data ;
    Context context;
    int authority;
    public MemberListAdapter(Context context , List<List<String>> data ,int authority){
        this.context = context ;
        this.data = data;
        this.authority = authority ;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.item_lv_memberlist,null);
        TextView tv_member_name = (TextView) view1.findViewById(R.id.tv_member_name);
        TextView tv_member_number = (TextView) view1.findViewById(R.id.tv_member_number);
        TextView tv_member_order = (TextView) view1.findViewById(R.id.tv_member_order);
        TextView tv_member_total = (TextView) view1.findViewById(R.id.tv_member_total);
        CheckBox cb_select = (CheckBox) view1.findViewById(R.id.cb_select);
        if(authority==0){  //组员权限
            cb_select.setVisibility(View.GONE);
            tv_member_name.setTextSize(18);
            tv_member_name.setPadding(10,20,10,20);
            tv_member_number.setTextSize(18);
            tv_member_number.setPadding(10,20,10,20);
            tv_member_order.setVisibility(View.GONE);
            tv_member_total.setVisibility(View.GONE);
        }else if(authority==1){  //组长查看订单权限
            cb_select.setTag(i);
            cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Message msg = new Message();
                    msg.arg1 = (int)compoundButton.getTag() ;
                    if(b){
                        msg.arg2 = 1 ;

                    }else{
                        msg.arg2 = 2 ;
                    }
                    TeamInfoActivity.handler.sendMessage(msg);
                }
            });

            tv_member_order.setText(data.get(i).get(2));
            tv_member_total.setText("总计："+data.get(i).get(3)+"元");
        }else if(authority==2){  //组长管理组员
            tv_member_name.setTextSize(18);
            tv_member_name.setPadding(10,20,10,20);
            tv_member_number.setTextSize(18);
            tv_member_number.setPadding(10,20,10,20);
            tv_member_order.setVisibility(View.GONE);
            tv_member_total.setVisibility(View.GONE);
            cb_select.setTag(i);
            cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Message msg = new Message();
                    msg.obj = 2;
                    msg.arg1 = (int)compoundButton.getTag() ;
                    if(b){
                        msg.arg2 = 1 ;

                    }else{
                        msg.arg2 = 2 ;
                    }
                    TeamInfoActivity.handler.sendMessage(msg);
                }
            });
        }
        tv_member_name.setText(data.get(i).get(0));
        tv_member_number.setText(data.get(i).get(1));

        return view1;
    }
}
