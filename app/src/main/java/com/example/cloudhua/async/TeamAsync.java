package com.example.cloudhua.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cloudhua.adapter.ExpandableAdapter1;
import com.example.cloudhua.enity.Team;
import com.example.cloudhua.enity.User;
import com.example.cloudhua.fragment.ShoppingFragment;
import com.example.cloudhua.ordersys.HomeActivity;
import com.example.cloudhua.ordersys.R;
import com.example.cloudhua.utils.JsonUtil;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudhua on 16-7-28.
 */
public class TeamAsync extends AsyncTask<String ,Void ,String[]> {
    List<String> group = new ArrayList<>();
    List<List<List<String>>> child = new ArrayList<>();
    ExpandableListView expandableListView ;
    Context context;
    boolean isConnect = true;
    public  TeamAsync(Context context ,ExpandableListView expandableListView){
        this.context = context ;
        this.expandableListView = expandableListView ;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... strings) {
        //获取服务端小组信息（小组+各组成员）
        try {
             strings[0] = Utils.getConnection(strings[0]);
             strings[1] = Utils.getConnection(strings[1]);
            return strings;
        }catch (IOException e) {
            isConnect = false ;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);

        /**
         * result[0] 所有组信息
         * {"teams":[
         * {"teamName":"cloudhua","creatorName":"xzh","creatorTEL":"13213213213","creatorID":"0VxLYNEk","teamID":"1"},
         * {"teamName":"xvzonghui","creatorName":"xzh","creatorTEL":"13213213213","creatorID":"0VxLYNEk","teamID":"2"},
         * {"teamName":"FirstTeam","creatorName":"client","creatorTEL":"13288888888","creatorID":"Q993nWiC","teamID":"3"},
         * {"teamName":"SecondTeam","creatorName":"client","creatorTEL":"13288888888","creatorID":"Q993nWiC","teamID":"4"},
         * {"teamName":"ThreeTeam","creatorName":"client","creatorTEL":"13288888888","creatorID":"Q993nWiC","teamID":"5"}]}
         */

        /**
         * result[1] 用户所在组信息
         *{"teams":[{"teamName":"cloudhua","creatorName":"xzh","creatorTEL":"13213213213","creatorID":"0VxLYNEk","tid":"1"},
         * {"teamName":"FirstTeam","creatorName":"client","creatorTEL":"13288888888","creatorID":"Q993nWiC","tid":"3"},
         * {"teamName":"SecondTeam","creatorName":"client","creatorTEL":"13288888888","creatorID":"Q993nWiC","tid":"4"},
         * {"teamName":"ThreeTeam","creatorName":"client","creatorTEL":"13288888888","creatorID":"Q993nWiC","tid":"5"}]}
         */
        String info [][] = null;
        String info2 [][] = null;
        if(result!=null){
            info = JsonUtil.parseFor(result[0],JsonUtil.ParseType.LISTTEAMS);
            info2  = JsonUtil.parseFor(result[1],JsonUtil.ParseType.GETMYTEAMS);
        }
        if(isConnect){
            HomeActivity.handler.sendMessage(new Message()); //将HomeActivity的搜索框置空
            Utils.myTeams.clear();
            Utils.existTeams.clear();
            group.add("我的小组");
            group.add("在线小组");
            if(info!=null){
                User userTemp = Utils.getUser(context.getSharedPreferences(Utils.SPNAME, Activity.MODE_PRIVATE));
                List<String > myTeamId = new ArrayList<>();
                if(info2!=null){     //填充到我的小组列表
                    for (int i = 0; i < info2.length; i++) {
                        Team team = null;
                            myTeamId.add(info2[i][4]);
                            team = new Team(info2[i][4],info2[i][0],new User(info2[i][3],info2[i][1],info2[i][2]),null);
                            Utils.myTeams.add(team);
                        }
                    }
                for (int i = 0; i < info.length; i++) {
                    if(!isMyteam(info[i][4],myTeamId)){  //判断该组是否是我的小组，若不是，填充到在线小组列表
                        Team team = null;
                        team = new Team(info[i][4],info[i][0],new User(info[i][3],info[i][1],info[i][2]),null);
                        Utils.existTeams.add(team);
                    }
                }
            }

            List<List<String>> item1 = new ArrayList<>();
            for (int i = 0; i < Utils.myTeams.size(); i++) {
                List<String> item1_1 = new ArrayList<>();
                item1_1.add(Utils.myTeams.get(i).getName());
                item1_1.add(Utils.myTeams.get(i).getCaptain().getName());
                item1_1.add(Utils.myTeams.get(i).getCaptain().getNumber());
                item1.add(item1_1);
            }
            child.add(item1);
            List<List<String>> item2 = new ArrayList<>();
            for (int i = 0; i < Utils.existTeams.size(); i++) {
                List<String> item2_1 = new ArrayList<>();
                item2_1.add(Utils.existTeams.get(i).getName());
                item2_1.add(Utils.existTeams.get(i).getCaptain().getName());
                item2_1.add(Utils.existTeams.get(i).getCaptain().getNumber());
                item2.add(item2_1);
            }
            child.add(item2);

            expandableListView.setAdapter(new ExpandableAdapter1(context,group,child));

    }else{
            ToastUtil.showToast(context,"小组信息获取失败，请检查网络连接");
        }
        Message msg = new Message();
        msg.arg2 = 5 ;  //标志用于关闭下拉刷新的进度条
        ShoppingFragment.handler.sendMessage(msg);
    }
public boolean isMyteam(String id , List<String > idSet){
    for (int i = 0; i < idSet.size(); i++) {
        if(id.equals(idSet.get(i))){
            return true;
        }
    }
    return false ;
}
}
