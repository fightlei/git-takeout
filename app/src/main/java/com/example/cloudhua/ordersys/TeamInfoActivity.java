package com.example.cloudhua.ordersys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cloudhua.adapter.MemberListAdapter;
import com.example.cloudhua.async.ServiceAsync;
import com.example.cloudhua.enity.Team;
import com.example.cloudhua.enity.User;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cloudhua on 16-8-1.
 */
public class TeamInfoActivity extends AppCompatActivity{
    Timer timer ;
    int orderManager ;
    TextView teamname;
    static TextView complete;
    TextView cname;
    TextView cnumber;
    ImageView imageView ;
    static TextView totalOrderInfo;
    static TextView MemberOrOrderInfo;
    static TextView delete ;
    static Team team ;
    static ListView memberList ;
    static Context context;
    static String id ;
    static int tag = 0 ;
    public static List<Integer> index = new ArrayList<>();  //已选订单索引
    public static List<Integer> indexOfMembers = new ArrayList<>();  //已选成员索引
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(delete!=null){
                delete.setVisibility(View.VISIBLE);
            }
            if(msg.obj!=null) {
                tag = (int) msg.obj;
                if(tag==2){  //组长查看组员时的勾选情况
                    if(msg.arg2==1){  //勾选触发
                        indexOfMembers.add(msg.arg1);
                    }else if(msg.arg2==2){  //未勾选触发
                        if(index.size()<=1){
                            delete.setVisibility(View.GONE);
                        }
                        indexOfMembers.remove(new Integer(msg.arg1));
                    }
                }else if(tag==3){  //刷新订单详情
                    if(totalOrderInfo!=null&&MemberOrOrderInfo!=null&&context!=null&&delete!=null){
                        delete.setVisibility(View.GONE);
                        totalOrderInfo.setText("总订单：");
                        MemberOrOrderInfo.setText("订单详情");
                        new ServiceAsync(context,memberList,9).execute(Utils.PATH_getUserOrdersByTeamId+"id="+id+"&tid="+team.getId());
                    }
                }else if(tag==4){  //刷新组员信息
                    if(totalOrderInfo!=null&&MemberOrOrderInfo!=null&&context!=null&&delete!=null) {
                        delete.setVisibility(View.GONE);
                        totalOrderInfo.setVisibility(View.GONE);
                        MemberOrOrderInfo.setText("成员信息");
                        complete.setVisibility(View.GONE); //不具备完成任务功能
                        new ServiceAsync(context, memberList, 10).execute(Utils.PATH_getAllTeamMembers + "id=" + id + "&tid=" + team.getId());
                    }
                }else if(tag == 5){  //获得该组所有订单信息后，填写总订单信息
                    if(totalOrderInfo!=null){
                        StringBuffer stringBuffer = new StringBuffer();
                        double price = 0 ;
                        for (int i = 0; i < Utils.productList.size(); i++) {
                            if(Utils.productList.get(i).getCount()>0){
                                stringBuffer.append(Utils.productList.get(i).getName()+"："+Utils.productList.get(i).getCount()+"份 ");
                                price += (Utils.productList.get(i).getCount()*Utils.productList.get(i).getPrice());
                            }
                            totalOrderInfo.setText("总订单："+stringBuffer+"\n总计："+price+"元");
                        }
                    }
                }
            }else{     //组长查看订单时的勾选情况
                if(msg.arg2==1){  //勾选触发
                    index.add(msg.arg1);
                }else if(msg.arg2==2){  //未勾选触发
                    if(index.size()<=1){
                        delete.setVisibility(View.GONE);
                    }
                    index.remove(new Integer(msg.arg1));
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_info);
        initView();
        context = this ;
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",-1);
        int authority = intent.getIntExtra("authority",-1);
        orderManager = authority ;
        if(position!=-1){

            team = Utils.myTeams.get(position);
            if(team!=null){
            id = Utils.getUser(getSharedPreferences(Utils.SPNAME,0)).getId();
            if(authority==1){  //组长管理订单权限
                totalOrderInfo.setText("总订单：");
                MemberOrOrderInfo.setText("订单详情");
                new ServiceAsync(this,memberList,9).execute(Utils.PATH_getUserOrdersByTeamId+"id="+id+"&tid="+team.getId());
            }else if(authority==0){  //组员权限
                totalOrderInfo.setVisibility(View.GONE);
                MemberOrOrderInfo.setText("成员列表");
                complete.setVisibility(View.GONE);  //没有完成任务权限
                new ServiceAsync(this,memberList,4).execute(Utils.PATH_getAllTeamMembers+"id="+id+"&tid="+team.getId());
            }else if(authority==2){  //组长管理组员权限
                totalOrderInfo.setVisibility(View.GONE);
                MemberOrOrderInfo.setText("成员信息");
                complete.setVisibility(View.GONE); //不具备完成任务功能
                new ServiceAsync(this,memberList,10).execute(Utils.PATH_getAllTeamMembers+"id="+id+"&tid="+team.getId());
            }

                teamname.setText("小组名称："+team.getName());
                cname.setText("组长姓名："+team.getCaptain().getName());
                cnumber.setText("组长电话："+team.getCaptain().getNumber());
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(orderManager==1){  //组长管理订单权限
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                new ServiceAsync(TeamInfoActivity.this,memberList,9).execute(Utils.PATH_getUserOrdersByTeamId+"id="+id+"&tid="+team.getId());
            }
        };
        timer.schedule(task,0,10000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        index.clear();
        indexOfMembers.clear();
        tag = 0 ;
    }

    private void initView() {
        teamname = (TextView) findViewById(R.id.tv_detail_teamname);
        cname = (TextView) findViewById(R.id.tv_detail_cname);
        cnumber = (TextView) findViewById(R.id.tv_detail_cnumber);
        delete = (TextView) findViewById(R.id.tv_delete);
        MemberOrOrderInfo = (TextView) findViewById(R.id.tv_memberinfo_orderinfo);
        totalOrderInfo = (TextView) findViewById(R.id.tv_total_orderinfo);
        complete = (TextView) findViewById(R.id.tv_complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.ordersOfMyTeam.size()>0){
                    String [] position = new String[]{Utils.getUser(getSharedPreferences(Utils.SPNAME,0)).getId(),team.getId()};
                    Utils.popuWarningWindow(TeamInfoActivity.this,"完成任务","确定已完成本组所有订单？",position);
                }else{
                    ToastUtil.showToast(TeamInfoActivity.this,"组内还没有订单哦！");
                }
            }
        });
        delete.setVisibility(View.GONE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tag==2){  //删除组员
                    if(indexOfMembers.size()<=0){
                        ToastUtil.showToast(TeamInfoActivity.this,"你还没有选择哦！");
                    }else
                    Utils.popuWarningWindow(TeamInfoActivity.this,"删除组员","确定删除已勾选的组员？\n将同时删除其在本组的订单",team.getId());
                }else {  //删除该组员的订单
                    if(index.size()<=0){
                        ToastUtil.showToast(TeamInfoActivity.this,"你还没有选择哦！");
                    }else
                    Utils.popuWarningWindow(TeamInfoActivity.this,"取消订单","确定删除已勾选的订单？",-2);
                }
            }
        });
        memberList = (ListView) findViewById(R.id.lv_detail_member);
        imageView = (ImageView) findViewById(R.id.iv_btn_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamInfoActivity.this.finish();
            }
        });
    }
}
