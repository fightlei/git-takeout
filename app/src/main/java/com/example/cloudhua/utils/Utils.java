package com.example.cloudhua.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.cloudhua.async.ServiceAsync;
import com.example.cloudhua.enity.Order;
import com.example.cloudhua.enity.Product;
import com.example.cloudhua.enity.Team;
import com.example.cloudhua.enity.User;
import com.example.cloudhua.ordersys.HomeActivity;
import com.example.cloudhua.ordersys.MainActivity;
import com.example.cloudhua.ordersys.R;
import com.example.cloudhua.ordersys.TeamInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cloudhua on 16-7-28.
 */
public class Utils {
    //创建用户
    public static final String PATH_createUser = "http://xvzonghui.top:8080/api/user/createUser?";
    //public static final String PATH_createUser = "http://www.baidu.com?" ;
    //查询所有商品
    public static final String PATH_getProducts = "http://xvzonghui.top:8080/api/service/getProducts?";
    //public static final String PATH_getProducts = "http://www.baidu.com?" ;
    //创建订单//加入组
    public static final String PATH_createOrder = "http://xvzonghui.top:8080/api/order/createOrder?";
    public static final String PATH_joinTeam = "http://xvzonghui.top:8080/api/user/joinTeam?";
    //public static final String PATH_createOrder = "http://www.baidu.com?" ;
    //public static final String PATH_joinTeam = "http://www.baidu.com?" ;

    //创建组
    public static final String PATH_createTeam = "http://xvzonghui.top:8080/api/team/createTeam?";
    //public static final String PATH_createTeam = "http://www.baidu.com?" ;
    //查询所有组
    public static final String PATH_listTeams = "http://xvzonghui.top:8080/api/team/listTeams?";
    //查询用户所属组
    public static final String PATH_getMyTeam = "http://xvzonghui.top:8080/api/user/getMyTeam?";

    //查看某组下的所有用户的用户ID
    public static final String PATH_getAllTeamMembers = "http://xvzonghui.top:8080/api/team/getAllTeamMembers?";

    //查询某用户的所有订单
    public static final String PATH_getUserOrders = "http://xvzonghui.top:8080/api/order/getUserOrders?";
    //查看某个创建者名下的组信息
    public static final String PATH_getAllTeamsBelongsToCreator = "http://xvzonghui.top:8080/api/team/getAllTeamsBelongsToCreator?";
    //查询某组下的所有订单消息
    public static final String PATH_getUserOrdersByTeamId = "http://xvzonghui.top:8080/api/order/getUserOrdersByTeamID?";
    //删除组
    public static final String PATH_deleteTeam = "http://xvzonghui.top:8080/api/team/deleteTeam?";
    //取消订单
    public static final String PATH_cancelOrder = "http://xvzonghui.top:8080/api/order/cancelOrder?";
    //删除组员
    public static final String PATH_deleteUserFromTeam = "http://xvzonghui.top:8080/api/user/deleteUserFromTeam?";
    //退出组
    public static final String PATH_quitFromTeam = "http://xvzonghui.top:8080/api/team/quitFromTeam?";
    //清空历史订单
    public static final String PATH_clearAllMyHistoryOrders = "http://xvzonghui.top:8080/api/user/clearAllMyHistoryOrders?";
    //完成任务
    public static final String PATH_finishTeamOrder = "http://xvzonghui.top:8080/api/order/finishTeamOrder?";
    //提醒
    public static final String PATH_notifyAllTeamMembers = "http://xvzonghui.top:8080/api/notify/notifyAllTeamMembers?";



    public static final String SPNAME = "userinfo";
    public static List<Team> myTeams = new ArrayList<>();
    public static List<Team> existTeams = new ArrayList<>();
    public static List<Order> lastOrders = new ArrayList<>();
    public static List<Product> productList = new ArrayList<>();
    public static List<String> membersOfMyTeam = new ArrayList<>();
    public static Map<String,String> ordersOfMyTeam = new HashMap<>();
    public static List<String > indexOfOrdersOfMyTeam = new ArrayList<>();
    static { //测试用例 产品列表
        productList.add(new Product("15元菜品",15));
        productList.add(new Product("18元菜品",18));
    }
    public static void saveUser(SharedPreferences sharedPreferences, User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream toByte = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(toByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (oos != null) {
            try {
                oos.writeObject(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //对byte[]进行Base64编码
        String base64 = Base64.encodeToString(toByte.toByteArray(), Base64.DEFAULT);
        editor.putString("KEY", base64);
        editor.commit();
    }

    public static User getUser(SharedPreferences sharedPreferences) {
        String base64 = sharedPreferences.getString("KEY", null);
        if (base64 != null) {
            byte[] data = Base64.decode(base64, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                if (ois != null) {
                    return (User) ois.readObject();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static List<Team> JsonParse(String json){
        try {
            List<Team> teams = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("teams");
            //解析所有小组
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject teamObject1 = jsonArray.getJSONObject(i);
                JSONObject teamObject = teamObject1.getJSONObject("team");
                JSONArray membersArray = teamObject.getJSONArray("members");
                //解析小组成员
                User members [] = new User[membersArray.length()];
                for (int j = 0; j < membersArray.length(); j++) {
                    JSONObject memberObject = membersArray.getJSONObject(j);
                    User member = new User(memberObject.getString("mname"),memberObject.getString("mnumber"),null);
                    members[j]=member;
                }
                //解析组长
                JSONObject captainObject = teamObject.getJSONObject("captain");
                User captain = new User(captainObject.getString("cname"),captainObject.getString("cnumber"),null);
                Team team = new Team(teamObject.getString("tid"),teamObject.getString("tname"),captain,members);
                teams.add(team);
            }
            return teams;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static String getNameFromID(String id){
        for (int i = 0; i < myTeams.size(); i++) {
            if(myTeams.get(i).getId().equals(id)){
                return  myTeams.get(i).getName();
            }
        }
        for (int i = 0; i < existTeams.size(); i++) {
            if(existTeams.get(i).getId().equals(id)){
                return  existTeams.get(i).getName();
            }
        }
        return null;
    }
    public static String getCaptainIdFromID(String id){
        for (int i = 0; i < myTeams.size(); i++) {
            if(myTeams.get(i).getId().equals(id)){
                return  myTeams.get(i).getCaptain().getId();
            }
        }
        for (int i = 0; i < existTeams.size(); i++) {
            if(existTeams.get(i).getId().equals(id)){
                return  existTeams.get(i).getCaptain().getId();
            }
        }
        return null;
    }
    public static String getConnection(String path) throws IOException{
        String content = "";
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(5000);
        conn.connect();
        InputStream inputStream = conn.getInputStream();
        byte data[] = new byte[1024];
        int count = 0 ;
        while((count=inputStream.read(data))!=-1){
            content+=new String(data,0,count);
        }
        if(conn.getResponseCode()!=200){
            throw new IOException();
        }
        return content;
    }
    public static void confirmOrderWindow(final Context context , final SharedPreferences preferences , final List<Product> products, final Team team){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("确认订单");
        StringBuffer bufferInfo = new StringBuffer();
        bufferInfo.append("确认下单吗？"+"\n");
        double total = 0;
        for (int i = 0; i < products.size(); i++) {
            total+=products.get(i).getPrice()*products.get(i).getCount();
            bufferInfo.append(products.get(i).getName()+":"+products.get(i).getCount()+"份");
            bufferInfo.append(",");
        }
        bufferInfo.deleteCharAt(bufferInfo.length()-1);
        bufferInfo.append("\n");
        bufferInfo.append("共计:"+total+"元");
        builder.setMessage(bufferInfo);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下单
                String teamID = team.getId();
                String path[] = new String[products.size()];
                //请求创建订单
                //购买几种商品就产生几条订单
                long time = System.currentTimeMillis();
                for (int j = 0; j < path.length; j++) {
                    path[j] = PATH_createOrder+"userID="+getUser(preferences).getId()+"&productID="+products.get(j).getId()
                    +"&productNum="+products.get(j).getCount()+"&tid="+teamID+"&time="+time;
                }
                //异步任务下单
                    new ServiceAsync(context,preferences,products,team,
                            new Date(System.currentTimeMillis()),1).execute(path);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }
    public static void cancelOrderPopwindow(final Context context, View v , final int position , String... tag ){
        View popView = LayoutInflater.from(context).inflate(R.layout.item_popwindow,null);
        LinearLayout linearLayout = (LinearLayout) popView.findViewById(R.id.ll_container_tv);
        final PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT,true);
        for (int i = 0; i < tag.length; i++) {
            TextView textVIew = new TextView(context);
            textVIew.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textVIew.setPadding(14,14,14,14);
            textVIew.setTextColor(Color.rgb(255,255,255));
            textVIew.setText(tag[i]);
            linearLayout.addView(textVIew);
            if (i<tag.length-1){
                ImageView imageview = new ImageView(context);
                imageview.setBackgroundColor(Color.WHITE);
                imageview.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT));
                linearLayout.addView(imageview);
            }
            textVIew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (((TextView)view).getText().toString().trim()){
                        case "取消订单":
                            popuWarningWindow(context,"取消订单","该订单尚未完成，确定取消？",position);
                            break;
                        case "删除记录":
                            popuWarningWindow(context,"删除记录","确定删除该条历史订单？",position);
                            break;
                        case "删除该组":
                            popuWarningWindow(context,"删除小组","确定删除该小组？\n将同时解散所有小组成员以及删除成员们的未完成订单",position);
                            break;
                        case "组内订单":
                            Intent intent = new Intent(context, TeamInfoActivity.class);
                            intent.putExtra("position",position);
                            intent.putExtra("authority",1);
                            context.startActivity(intent);
                            break;
                        case "提醒一下":
                            String id_remind = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                            new ServiceAsync(context,15).execute(PATH_notifyAllTeamMembers+"id="+id_remind+"&tid="+myTeams.get(position).getId());
//                            Intent intent1 = new Intent("android.intent.action.MY_BROADCAST");
//                            context.sendBroadcast(intent1);
                            break;
                        case "详细信息":
                            Intent intent3 = new Intent(context, TeamInfoActivity.class);
                            intent3.putExtra("position",position);
                            intent3.putExtra("authority",0);
                            context.startActivity(intent3);
                            break;
                        case "成员管理":
                            Intent intent4 = new Intent(context, TeamInfoActivity.class);
                            intent4.putExtra("position",position);
                            intent4.putExtra("authority",2);  //组长管理成员
                            context.startActivity(intent4);
                            break;
                        case "退出该组":
                            String idquit = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                            new ServiceAsync(context,12).execute(PATH_quitFromTeam+"id="+idquit+"&tid="+myTeams.get(position).getId());
                            break;
                        case "加入":
                            new ServiceAsync(context,6).execute(Utils.PATH_joinTeam+"id="+
                                    Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId()+"&tid="+Utils.existTeams.get(position).getId());
                            break;
                    }
                    if(popupWindow!=null){
                        popupWindow.dismiss();
                    }
                }
            });
        }
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //弹窗宽高
        int height = popView.getMeasuredHeight();
        int width = popView.getMeasuredWidth();
        //获取父控件的位置
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,(location[0]+v.getWidth()/2)-width/2,location[1]);
        popupWindow.update();
    }
    public static void showMenu(final Context context , View view){
        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.getMenuInflater().inflate(R.menu.main,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getTitle().toString()){
                    case "创建组":
                        showDialogCreateTeam(context);
                        break;
                    case "个人信息":
                        User user = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0));
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setIcon(R.mipmap.ic_launcher);
                        builder1.setTitle("个人信息");
                        View view3 = LayoutInflater.from(context).inflate(R.layout.item_popu_more,null) ;
                        LinearLayout linearLayout1 = (LinearLayout) view3.findViewById(R.id.ll_container_more);
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                        TextView textView = new TextView(context);
                        textView.setLayoutParams(params);
                        textView.setText("ID："+user.getId());
                        TextView textView1 = new TextView(context);
                        textView1.setLayoutParams(params);
                        textView1.setText("姓名："+user.getName());
                        TextView textView2 = new TextView(context);
                        textView2.setLayoutParams(params);
                        textView2.setText("电话号码："+user.getNumber());
                        linearLayout1.addView(textView);
                        linearLayout1.addView(textView1);
                        linearLayout1.addView(textView2);
                        builder1.setView(view3);
                        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder1.show();
                        break;
                    case "注销":
                        SharedPreferences.Editor editor = context.getSharedPreferences(Utils.SPNAME,0).edit();
                        editor.putBoolean("logged",false);
                        editor.commit();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("fromLogOff",true);
                        context.startActivity(intent);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    public static void showDialogCreateTeam(final Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("创建组");
        View view2 = LayoutInflater.from(context).inflate(R.layout.item_popu_more,null) ;
        LinearLayout linearLayout = (LinearLayout) view2.findViewById(R.id.ll_container_more);
        final EditText editText = new EditText(context);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        editText.setHint("小组名称");
        linearLayout.addView(editText);
        builder.setView(view2);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String content = editText.getText().toString().trim();
                if(content==null||"".equals(content)){
                    showDialogCreateTeam(context);
                    ToastUtil.showToast(context,"小组名称不能为空");
                }else{
                    new ServiceAsync(context,3).execute(PATH_createTeam+"id="+getUser(context.getSharedPreferences(SPNAME,0)).getId()+"&name="+content);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
    public static void popuWarningWindow(final Context context , final String title , final String content , final Object position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(title){
                    case "清空历史订单":
                        String idClear = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                        new ServiceAsync(context,13).execute(PATH_clearAllMyHistoryOrders+"id="+idClear);
                        //清空操作
                        break;
                    case "删除小组":
                        String id = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                        new ServiceAsync(context,5).execute(Utils.PATH_deleteTeam+"id="+id+"&tid="+Utils.myTeams.get((int)position).getId());
                        //删除我的小组 Utils.myTeams position
                        break;
                    case "取消订单":
                        String idCancel = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                        if(-2==(int)position){  //组长取消该成员订单
                            String orderIDS[][] = new String[TeamInfoActivity.index.size()][];
                            for (int k = 0; k < TeamInfoActivity.index.size(); k++) {
                                orderIDS[k] = ordersOfMyTeam.get(indexOfOrdersOfMyTeam.get(TeamInfoActivity.index.get(k))).split("&");
                            }
                            List<String> paths = new ArrayList<>();
                            for (int m = 0; m < orderIDS.length; m++) {
                                for (int n = 0; n < orderIDS[m].length; n++) {
                                    paths.add(PATH_cancelOrder+"id="+idCancel+"&orderID="+orderIDS[m][n]);
                                }
                            }
                            new ServiceAsync(context,7,1).execute((String [])paths.toArray(new String[paths.size()]));
                        }else{  //自己取消订单
                            String oids[] = Utils.lastOrders.get((int)position).getId().split("&");
                            String path[] = null;
                            if(oids!=null) {
                                path = new String[oids.length];
                                for (int j = 0; j < oids.length; j++) {
                                    //参数有待确定
                                    path[j] = PATH_cancelOrder+"id="+idCancel+"&orderID="+oids[j];
                                }
                            }
                            new ServiceAsync(context,7,2).execute(path);
                        }
                        break;
                    case "完成任务":
                        String [] data = (String [])position;
                        new ServiceAsync(context,14).execute(PATH_finishTeamOrder+"id="+data[0]+"&tid="+data[1]);
                        break;
                    case "删除组员":
                        //position传来的是组ID
                        String idCaptaion = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                        String deletePath[] = new String[TeamInfoActivity.indexOfMembers.size()];
                        for (int x = 0; x < deletePath.length; x++) {
                            String deleteID = membersOfMyTeam.get(TeamInfoActivity.indexOfMembers.get(x));
                            deletePath[x] = PATH_deleteUserFromTeam+"id="+idCaptaion+"&userID="+deleteID+"&tid="+position;
                        }
                        new ServiceAsync(context,11).execute(deletePath);
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

}
