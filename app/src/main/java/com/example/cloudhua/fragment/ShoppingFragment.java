package com.example.cloudhua.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cloudhua.adapter.ExpandableAdapter1;
import com.example.cloudhua.async.TeamAsync;

import com.example.cloudhua.ordersys.OrderActivity;
import com.example.cloudhua.ordersys.R;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cloudhua on 16-7-28.
 */
public class ShoppingFragment extends Fragment {
    static ExpandableListView elvTeamInfo ;
    static Context context;
    public static List<Integer> indexMy ;  //记录了搜索后各项内容在搜索前的数据集中的下标
    public static List<Integer> indexOnline ;
    static SwipeRefreshLayout refreshLayout ;
    public static Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.arg2==4||msg.arg2==5){  //4用于更新UI，在加入组，删除组，退出组等操作后
                if(msg.arg2==5){
                    if(refreshLayout!=null){
                        refreshLayout.setRefreshing(false);
                    }
                }else
                if(context!=null){
                    String id = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                    String path = Utils.PATH_listTeams+"id="+id;
                    String path1 = Utils.PATH_getMyTeam+"id="+id;
                    new TeamAsync(context,elvTeamInfo).execute(new String[]{path,path1});
                }
            }else{  //来自于搜索框内容变化
                Object[] data = (Object[]) msg.obj;
                List<String> group = (List<String>)data[0];
                List<List<List<String>>> child = (List<List<List<String>>>)data[1];
                indexMy = (List<Integer>)data[2];
                indexOnline = (List<Integer>)data[3];
                elvTeamInfo.setAdapter(new ExpandableAdapter1(context,group,child));
                if (msg.arg1==1) {
                    elvTeamInfo.expandGroup(0);
                    elvTeamInfo.expandGroup(1);
                }
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_shopping,container,false);
        context = getActivity();
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_teamsrefresh);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        elvTeamInfo = (ExpandableListView) view.findViewById(R.id.elv_team_info);
        //得到所有组信息
        String id = Utils.getUser(getActivity().getSharedPreferences(Utils.SPNAME,0)).getId();
        String path = Utils.PATH_listTeams+"id="+id;
        String path1 = Utils.PATH_getMyTeam+"id="+id;
        List<String > group = new ArrayList<>();
        group.add("我的小组");
        group.add("在线小组");
        List<List<List<String >>> child = new ArrayList<>();
        child.add(new ArrayList<List<String>>());
        child.add(new ArrayList<List<String>>());
        elvTeamInfo.setAdapter(new ExpandableAdapter1(context,group,child));
        new TeamAsync(getActivity(),elvTeamInfo).execute(new String[]{path,path1});
//        elvTeamInfo.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
//                if(view.getTag()!=null&&(int)view.getTag()==0){  //点击了已加入的小组，可以下单
//                    Intent intent = new Intent(getActivity(), OrderActivity.class);
//                    if(indexMy!=null&&indexMy.size()>0&&i==0){
//                        i1 = indexMy.get(i1);
//                    }
//                    if(indexOnline!=null&&indexOnline.size()>0&&i==1){
//                        i1 = indexOnline.get(i1);
//                    }
//                    intent.putExtra("position",i1);
//                    //i+1=1点击我的小组时的响应
//                    //i+1=2通过在线小组点击
//                    intent.putExtra("type",i+1);
//                    startActivity(intent);
//                }else{
//                    ToastUtil.showToast(context,"请先加入该组才能完成下单");
//                }
//
//                return false;
//            }
//        });
//        elvTeamInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(view.getTag()!=null){  //只对子列表做处理
//                    i -- ;
//                    if((int)view.getTag()==0){   //只能对我的小组进行删除和提醒操作
//
//                        if(indexMy!=null&&indexMy.size()>0){
//                            i = indexMy.get(i);
//                        }
//                        if(Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId().equals(Utils.myTeams.get(i).getCaptain().getId())){
//                            //是组长
//                            Utils.cancelOrderPopwindow(context,view,i,new String[]{"1删除该组","成员管理","组内订单","1提醒一下"});
//                        }else{
//                            //不是组长
//                            Utils.cancelOrderPopwindow(context,view,i,new String[]{"1退出该组","详细信息"});
//                        }
//
//                    }else if((int)view.getTag()==1){  //对在线小组
//                        i -- ;
//                        if(elvTeamInfo.isGroupExpanded(0)){  //我的小组是否展开
//                            if(indexOnline!=null&&indexOnline.size()>0){
//                                i = i - indexMy.size();
//                                i = indexOnline.get(i);
//                            }else{
//                                i = i - Utils.myTeams.size();
//                            }
//                        }
//                        Utils.cancelOrderPopwindow(context,view,i,new String[]{"加入"});
//                    }
//                }
//
//                return true;
//            }
//        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String id = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                String path = Utils.PATH_listTeams+"id="+id;
                String path1 = Utils.PATH_getMyTeam+"id="+id;
                new TeamAsync(context,elvTeamInfo).execute(new String[]{path,path1});
            }
        });
        return view;
    }





    @Override
    public void onPause() {
        super.onPause();
    }
}
