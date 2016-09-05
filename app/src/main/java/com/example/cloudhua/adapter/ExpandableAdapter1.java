package com.example.cloudhua.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.cloudhua.ordersys.R;

import java.util.List;

/**
 * Created by cloudhua on 16-8-1.
 */
public class ExpandableAdapter1 extends BaseExpandableListAdapter {

    Context context;
    List<String> group;
    List<List<List<String>>> child;
    public ExpandableAdapter1(Context context , List<String> group,List<List<List<String>>> child){
        this.group = group ;
        this.child = child ;
        this.context = context;
    }
    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return child.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return group.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return child.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
//        View view1 =
//        TextView textview = (TextView) view1.findViewById(R.id.tv_info);
//        textview.setText(group.get(i));
//        return view1;
        AbsListView.LayoutParams layoutParams = new  AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        TextView text = new  TextView(context);
        text.setBackgroundColor(Color.WHITE);
        text.setLayoutParams(layoutParams);
        // Center the text vertically
        text.setGravity(Gravity.CENTER);
        // Set the text starting position
        text.setTextSize(18);
        text.setPadding(20 , 20 , 20 , 20 );
        text.setText(group.get(i));
        return  text;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
//        View view1 = LayoutInflater.from(context).inflate(R.layout.item_elv_teaminfo_slide,null);
//        TextView teamName = (TextView)view1.findViewById(R.id.tv_teamname_slide);
//        TextView captainName = (TextView)view1.findViewById(R.id.tv_captain_name_slide);
//        TextView captainNumber = (TextView)view1.findViewById(R.id.tv_captain_number_slide);
//        teamName.setText(child.get(i).get(i1).get(0));
//        captainName.setText("组长姓名："+child.get(i).get(i1).get(1));
//        captainNumber.setText("组长电话："+child.get(i).get(i1).get(2));
//        view1.setTag(i); //此值在设置expandableListView中使用长按事件，判断是父item还是子item
        return new MyLayout(context,child.get(i).get(i1).get(0),child.get(i).get(i1).get(1),child.get(i).get(i1).get(2),i,i1);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}

