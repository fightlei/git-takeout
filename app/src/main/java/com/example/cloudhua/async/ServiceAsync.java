package com.example.cloudhua.async;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cloudhua.adapter.MemberListAdapter;
import com.example.cloudhua.enity.Order;
import com.example.cloudhua.enity.Product;
import com.example.cloudhua.enity.Team;
import com.example.cloudhua.enity.User;
import com.example.cloudhua.fragment.MyOrderFragment;
import com.example.cloudhua.fragment.ShoppingFragment;
import com.example.cloudhua.ordersys.HomeActivity;
import com.example.cloudhua.ordersys.MainActivity;
import com.example.cloudhua.ordersys.OrderActivity;
import com.example.cloudhua.ordersys.R;
import com.example.cloudhua.ordersys.TeamInfoActivity;
import com.example.cloudhua.utils.JsonUtil;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cloudhua on 16-7-28.
 */
public class ServiceAsync extends AsyncTask<String , Void, String[]> {

    private Team team;
    private Date date;
    private  List<Product> products = new ArrayList<>();
    private  SharedPreferences sharedPreferences;
    private  Context context;
    private int connType ; //1提交订单服务 //2获取所有产品服务 //3创建组服务  //4查询指定组的所有组员
                           //5删除组服务  //6加入组服务   //7取消订单  //8获取该用户的所有订单
                           //9查询某组下的所有订单信息 //10同4,内部数据处理不同 //11删除组员
                           //12退出组服务  //13清空历史订单（只是清除已取消订单和已完成订单）
                           //14完成本组订单  //15组长提醒组员
    private boolean isConnect = true;
    ListView listview ;
    int which ;
    public ServiceAsync(Context context , SharedPreferences sharedPreferences ,
                        List<Product> products, Team team , Date date , int connType) {
        this.connType = connType;
        this.context = context;
        this.sharedPreferences =sharedPreferences;
        this.products = products ;
        this.team = team;
        this.date = date ;

    }
    public ServiceAsync(int connType){
        this.connType = connType;
    }
    public ServiceAsync(Context context ,int connType){
        this.connType = connType;
        this.context = context;
    }
    public ServiceAsync(Context context ,int connType , int which){
        this.connType = connType;
        this.context = context;
        this.which = which ;
    }
    public ServiceAsync(Context context , ListView listview , int connType){
        this.connType = connType;
        this.listview = listview ;
        this.context = context ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... strings) {
        try {
            for (int i = 0; i < strings.length; i++) {
                strings[i] = Utils.getConnection(strings[i]);
            }
        } catch (IOException e) {
            isConnect = false ;
            e.printStackTrace();
        }
        return strings;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        switch (connType){
            /**
             * 向服务器提交订单成功后的处理
             */
        //默认所有请求都成功
            //买了两种商品，下两次单
            //下订单返回：{"stat":"OK"}
            //下订单失败：{"stat":"invalidProductID"}
            case 1:
                if(isConnect&&result!=null) {
                    boolean tag = true;
                    for (int i = 0; i < result.length; i++) {
                        Log.i("slfldsfk", "onPostExecute: "+result[i]);
                        String info[][] = JsonUtil.parseFor(result[i], JsonUtil.ParseType.CREATEORDER);
                        if(!"OK".equals(info[0][0])){
                            tag = false ;
                            break;
                        }
                    }
                    if(tag){
                        User user = Utils.getUser(sharedPreferences);
                        if(OrderActivity.orderActivity!=null)
                        OrderActivity.orderActivity.finish();
                        HomeActivity.tagWhichFragment = 1;
                        //刷新历史订单
                        new ServiceAsync(context,8).execute(Utils.PATH_getUserOrders+"id="+user.getId());
                        ToastUtil.showToast(context,"订单提交成功");
                    }else{
                        ToastUtil.showToast(context,"订单提交失败");
                    }

                }else{
                    ToastUtil.showToast(context,"订单提交失败，请检查网络连接");
                }
                break;
            /**
             * 获取服务器端产品信息后的处理
             */
            case 2:
                //返回数据：{"products":[{"id":"1","price":"3","name":"??????"}]}
                String info_product[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.GETPRODUCTS);
                if(isConnect&&info_product!=null){

                    for (int i = 0; i < info_product.length; i++) {
                        Utils.productList.add(new Product(info_product[i][0],info_product[i][2],Double.parseDouble(info_product[i][1])));
                    }
                }else{
                    //信息获取失败后的处理
                    //5秒后继续获取
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(10000);
                                Log.i("zl", "run: "+"重新开启下载");
                                new ServiceAsync(2).execute(Utils.PATH_getProducts);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
                break;
            /**
             * 创建组后的处理
             */
            case 3:
                //{"stat":"invalidID"}
                //{"stat":"OK"}
                String info[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.CREATETEAM);
                if(isConnect&&info!=null&&"OK".equals(info[0][0])){
                    //需要同时执行加入组操作
                    Message msg = new Message();  //刷新UI
                    msg.arg2 = 4 ;
                    ShoppingFragment.handler.sendMessage(msg);
                    ToastUtil.showToast(context,"创建小组成功");
                }else{
                    ToastUtil.showToast(context,"小组创建失败，请检查网络连接");
                }
                break;
            //获得指定组的所有组员信息
            case 4:
                //数据格式：{"usersInformationBelongsToThisTeam":[{"userID":"Q993nWiC","userName":"client","userTEL":"13288888888"}]}
                List<List<String >> data  = new ArrayList<>();
                String info4[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.GETALLTEAMMEMBERS);
                if(isConnect){
                    if(info4!=null){
                        for (int i = 0; i < info4.length; i++) {
                            List<String> member = new ArrayList<>();
                            member.add(info4[i][0]);  //姓名
                            member.add(info4[i][1]);  //电话
                            member.add("");  //没有权限查询订单和总计，置为空
                            member.add("");
                            data.add(member);
                        }
                        //权限是0,仅能查询组中成员
                        listview.setAdapter(new MemberListAdapter(context,data,0));
                    }
                }else{
                    ToastUtil.showToast(context,"信息获取失败，请检查网络连接");
                }
                break;
            //删除组后的响应
            //数据格式：{"stat":"OK"}
            case 5 :
                if(isConnect){
                    String info5[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.DELETETEAM);
                    if(info5!=null&&"OK".equals(info5[0][0])){
                        ToastUtil.showToast(context,"删除成功");
                        Message message = new Message();
                        message.arg2 = 4;
                        ShoppingFragment.handler.sendMessage(message);
                    }else{
                        ToastUtil.showToast(context,"删除小组失败");
                    }
                }else{
                    ToastUtil.showToast(context,"删除小组失败，请检查网络连接");
                }
                break;
            /**
             * for (int i = 0; i < result.length; i++) {
             Log.i("格式", "onPostExecute: " + result[i]);
             }
             */
            //加入组服务
            //数据格式： {"stat":"OK"}
            case 6:
                if (isConnect) {
                    String info6[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.JOINTEAM);
                    if(info6!=null&&"OK".equals(info6[0][0])){
                        Message msg = new Message();
                        msg.arg2 = 4 ;
                        ShoppingFragment.handler.sendMessage(msg);
                        ToastUtil.showToast(context,"加组成功");
                    }else{
                        ToastUtil.showToast(context,"加入组失败");
                    }
                }else {
                    ToastUtil.showToast(context,"加入组失败，请检查网络连接");
                }
                break;
            //取消订单
            case 7:
                //数据格式：{"stat":"OK"}
                if(isConnect){
                    boolean tag = true ;
                    for (int i = 0; i < result.length; i++) {
                        String info7[][] = JsonUtil.parseFor(result[i], JsonUtil.ParseType.CANCELORDER);
                        if(!"OK".equals(info7[0][0])){
                            tag = false ;
                            break ;
                        }
                    }
                    if(tag){
                        if(which==1){ //组长取消订单 ， 刷新订单详情页面
                            Message msg = new Message();
                            msg.obj = 3 ;
                            TeamInfoActivity.index.clear();
                            TeamInfoActivity.handler.sendMessage(msg);
                            ToastUtil.showToast(context,"删除成功");
                        }else  if(which==2){  //自己取消订单 ，刷新历史订单页面
                            new ServiceAsync(context,8).execute(Utils.PATH_getUserOrders+"id="+Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId());
                            ToastUtil.showToast(context,"取消成功");
                        }

                    }else {
                        ToastUtil.showToast(context,"订单取消失败");
                    }
                }else{
                    ToastUtil.showToast(context,"取消订单失败，请检查网络连接");
                }
                break;
            //请求到的该用户的所有订单信息
            case 8:
                /**
                 * {"orderID":"36","productNum":"1","belongsToTeam":"1","orderTime":"1470216835107","orderStatus":"0","productPrice":"15","productName":"Rice","productID":"1"},
                 */
                String info8[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.GETUSERORDERS);
                if(isConnect){
                    if(info8!=null){
                        Utils.lastOrders.clear();
                        for (int i = 0; i < info8.length; i++) {
                            boolean tag = false ;
                            for (int j = 0; j < Utils.lastOrders.size(); j++) {
                                Order orderTemp = Utils.lastOrders.get(j);
                                if(info8[i][3].equals(orderTemp.getDate())&&info8[i][2].equals(orderTemp.getTeam().getId())&&
                                        (orderTemp.getState()==Integer.parseInt(info8[i][4]))){
                                    Utils.lastOrders.get(j).setId(orderTemp.getId()+"&"+info8[i][0]);
                                    Utils.lastOrders.get(j).getProducts().add(new Product(info8[i][7],info8[i][6],Double.parseDouble(info8[i][5]),Integer.parseInt(info8[i][1])));
                                    tag = true ;
                                    break;
                                }
                            }
                            if(!tag){
                                List<Product> products = new ArrayList<>();
                                products.add(new Product(info8[i][7],info8[i][6],Double.parseDouble(info8[i][5]),Integer.parseInt(info8[i][1])));
                                Order order = new Order(info8[i][0],products,new Team(info8[i][2],Utils.getNameFromID(info8[i][2])),info8[i][3], Integer.parseInt(info8[i][4]));
                                Utils.lastOrders.add(order);
                            }
                        }
                    }
                }else{
                    ToastUtil.showToast(context,"订单信息获取失败，请检查网络连接");
                }
                MyOrderFragment.handler.sendMessage(new Message());
                break;
            case 9:
                //数据格式
                //9查询某组下的所有订单信息
                /**
                 * {"teams":[{"userID":"Q993nWiC","orderID":"48","priductPrice":"15","productNum":"4","userName":"client","orderTime":"1470224587515","productName":"Rice","productID":"1","userTEL":"13288888888"},
                 * {"userID":"Q993nWiC","orderID":"49","priductPrice":"18","productNum":"2","userName":"client","orderTime":"1470224587515","productName":"noodles","productID":"2","userTEL":"13288888888"}]}
                 */
                if(isConnect){
                    String info9[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.GETUSERORDERSBYTEAMID);
                    if(info9!=null){
                        List<List<String >> data9  = new ArrayList<>();
                        Utils.indexOfOrdersOfMyTeam.clear();
                        Utils.ordersOfMyTeam.clear();
                        Utils.productList.get(0).setCount(0);
                        Utils.productList.get(1).setCount(0);
                        for (int i = 0; i < info9.length; i++) {
                            //收集总的订单信息
                            //写死为两中商品，待改进
                            switch (info9[i][7]){
                                case "1":
                                    Utils.productList.get(0).setCount(Utils.productList.get(0).getCount()+Integer.parseInt(info9[i][3]));
                                    break;
                                case "2":
                                    Utils.productList.get(1).setCount(Utils.productList.get(1).getCount()+Integer.parseInt(info9[i][3]));
                                    break;
                            }

                            String content = Utils.ordersOfMyTeam.get(info9[i][0]);
                            //ordersOfMyTeam，用户ID与订单ID的映射
                            //将同一用户ID的订单ID放在同一值内，用&分离
                            if(content!=null){
                                Utils.ordersOfMyTeam.put(info9[i][0],content+info9[i][1]+"&");
                            }else{
                                Utils.indexOfOrdersOfMyTeam.add(info9[i][0]);
                                Utils.ordersOfMyTeam.put(info9[i][0],info9[i][1]+"&");
                            }

                            boolean tag = false ;
                            for (int j = 0; j < data9.size(); j++) {
                                if(data9.get(j).get(4).equals(info9[i][0])){  //对比订单的所有者ID是否相同
                                    tag = true ;
                                    data9.get(j).set(2,data9.get(j).get(2) + "\n"+info9[i][6]+"："+info9[i][3]+"份");
                                    Double total = Double.parseDouble(data9.get(j).get(3))+Double.parseDouble(info9[i][2])*Integer.parseInt(info9[i][3]);
                                    data9.get(j).set(3,total.toString());
                                    break;
                                }
                            }
                            if(!tag){
                                List<String> member = new ArrayList<>();
                                member.add(info9[i][4]);  //姓名
                                member.add(info9[i][8]);  //电话
                                member.add(info9[i][6]+"："+info9[i][3]+"份");
                                String total = String.valueOf(Double.parseDouble(info9[i][2])*Integer.parseInt(info9[i][3]));
                                member.add(total);
                                member.add(info9[i][0]);
                                data9.add(member);
                            }

                        }
                        //权限是1,有删除组员和完成任务权限
                        listview.setAdapter(new MemberListAdapter(context,data9,1));
                        Message msg = new Message() ;
                        msg.obj = 5 ;
                        TeamInfoActivity.handler.sendMessage(msg);
                    }
                }else{
                    ToastUtil.showToast(context,"订单信息获取失败，请检查网络连接");
                }
                break;
            //获得指定组的所有组员信息，组长查看
            case 10:
                //数据格式：{"usersInformationBelongsToThisTeam":[{"userID":"Q993nWiC","userName":"client","userTEL":"13288888888"}]}
                List<List<String >> data10  = new ArrayList<>();
                String info10[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.GETALLTEAMMEMBERS);
                if(isConnect){
                    if(info10!=null){
                        Utils.membersOfMyTeam.clear();
                        String myID = Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId();
                        for (int i = 0; i < info10.length; i++) {
                            if(!myID.equals(info10[i][2])){  //去除组长自己
                                Utils.membersOfMyTeam.add(info10[i][2]);
                                List<String> member = new ArrayList<>();
                                member.add(info10[i][0]);  //姓名
                                member.add(info10[i][1]);  //电话
                                member.add("");  //没有权限查询订单和总计，置为空
                                member.add("");
                                data10.add(member);
                            }
                        }
                        //权限是2,组长管理组员
                        listview.setAdapter(new MemberListAdapter(context,data10,2));
                    }
                }else{
                    ToastUtil.showToast(context,"信息获取失败，请检查网络连接");
                }
                break;
            case 11:
                //删除组员
                //数据格式：{"stat":"OK"}
                if(isConnect){
                    for (int i = 0; i < result.length; i++) {
                        Log.i("删除组员", "onPostExecute: "+result[i]);
                    }
                    TeamInfoActivity.indexOfMembers.clear();
                    Message msg = new Message();
                    msg.obj = 4 ;
                    TeamInfoActivity.handler.sendMessage(msg);
                    ToastUtil.showToast(context,"删除成功");
                }else{
                    ToastUtil.showToast(context,"删除组员失败，请检查网络连接");
                }

                break;
            case 12:
                //退出组
                //数据格式：{"stat":"OK"}
                if(isConnect){
                    String [][]info12 = JsonUtil.parseFor(result[0], JsonUtil.ParseType.QUITFROMTEAM);
                    if(info12!=null&&"OK".equals(info12[0][0])){
                        Message msg = new Message();
                        msg.arg2 = 4 ;
                        ShoppingFragment.handler.sendMessage(msg);
                        ToastUtil.showToast(context,"退出成功");
                    }else{
                        ToastUtil.showToast(context,"退出组失败");
                    }
                }else{
                    ToastUtil.showToast(context,"退出组失败，请检查网络连接");
                }
                break;
            case 13:
                //清空历史记录
                //数据格式：{"stat":"OK"}
                if(isConnect){
                    String [][]info13 = JsonUtil.parseFor(result[0], JsonUtil.ParseType.CLEARALLMYHISTORYORDERS);
                    if(info13!=null&&"OK".equals(info13[0][0])){
                        new ServiceAsync(context,8).execute(Utils.PATH_getUserOrders+"id="+Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId());
                    }else{
                        ToastUtil.showToast(context,"清空失败");
                    }
                }else{
                    ToastUtil.showToast(context,"清空历史记录失败，请检查网络连接");
                }
                break;
            case 14:
                //完成本组订单
                //数据格式：{"stat":"OK"}
                if(isConnect){
                    String info14[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.FINISHTEAMORDER);
                    if(info14!=null&&"OK".equals(info14[0][0])){
                        Message msg = new Message();
                        msg.obj = 3 ;
                        TeamInfoActivity.index.clear();
                        TeamInfoActivity.handler.sendMessage(msg);
                        ToastUtil.showToast(context,"MISSION SUCCESS!");
                    }else{
                        ToastUtil.showToast(context,"请求失败");
                    }
                }else{
                    ToastUtil.showToast(context,"完成任务失败，请检查网络连接");
                }
                break;
            case 15:
                //数据格式：{"stat":"OK"}
                String info15[][] = JsonUtil.parseFor(result[0], JsonUtil.ParseType.NOTIFYALLTEAMMEMBERS);
                if(isConnect&&info15!=null&&"OK".equals(info15[0][0])){
                    ToastUtil.showToast(context,"提醒成功");
                }else{
                    ToastUtil.showToast(context,"提醒失败，请稍后重试");
                }
                break;
        }

    }
}
