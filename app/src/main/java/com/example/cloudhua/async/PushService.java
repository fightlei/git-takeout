package com.example.cloudhua.async;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cloudhua.ordersys.HomeActivity;
import com.example.cloudhua.ordersys.MyBroadcast;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cloudhua on 16-8-4.
 */
public class PushService extends Service {
    //请求通知URL 参数：id=D5IGE3hF
    private final String GETNOTIFY = "http://xvzonghui.top:8080/api/notify/getNotify";
    //无消息时数据
    //{"stat":"sleep"}
    //被从组中删除 数据  您已被xx从xx组中移出
    //"deleted by team":[[{"teamName":"haha","creatorID":"D5IGE3hF","tid":"4"}]],"stat":"alive"}
    //{"deleted by team":[[{"teamName":"second","creatorID":"4cNxEYur","tid":"2"}]],"stat":"alive"}
    //混合型： {"msg2creator":"该吃晚餐了，请提醒组员订餐","deleted by team":[[{"teamName":"haha","creatorID":"D5IGE3hF","tid":"4"}]],"stat":"alive"}
    //混合被山订单：{"msg2creator":"该吃晚餐了，请提醒组员订餐","your order is canceled":[{"userID":"4cNxEYur","time":"1470652235754","orderID":"121","productNum":"1","tid":"4","productID":"2"},{"userID":"4cNxEYur","time":"1470652235754","orderID":"120","productNum":"1","tid":"4","productID":"1"}],"stat":"alive"}
    //被提醒数据  xx提醒您午餐时间到了，记得下单哦
    //{"you are notified by":[[{"teamName":"second","creatorID":"4cNxEYur","tid":"2"}]],"stat":"alive"}
    //多次被提醒的数据格式
    //{"you are notified by":[[{"teamName":"second","creatorID":"4cNxEYur","tid":"2"}]],"stat":"alive"}//组长自己也被提醒
    //{"you are notified by":[[{"teamName":"second","creatorID":"4cNxEYur","tid":"2"}],[{"teamName":"second","creatorID":"4cNxEYur","tid":"2"}]],"stat":"alive"}
    //订单被删除，回馈数据
    //{"your order is canceled":[{"userID":"D5IGE3hF","time":"1470648307811","orderID":"119","productNum":"1","tid":"2","productID":"2"},{"userID":"D5IGE3hF","time":"1470648307811","orderID":"118","productNum":"2","tid":"2","productID":"1"},{"userID":"D5IGE3hF","time":"1470468031409","orderID":"113","productNum":"1","tid":"2","productID":"2"},{"userID":"D5IGE3hF","time":"1470468031409","orderID":"112","productNum":"1","tid":"2","productID":"1"}],"stat":"alive"}
    //通知组长催组员
    //{"msg2creator":"该吃晚餐了，请提醒组员订餐","stat":"alive"}
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final String id = Utils.getUser(getSharedPreferences(Utils.SPNAME,0)).getId();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    URL url = new URL(GETNOTIFY+"?id="+id);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setConnectTimeout(5000);
                    connection.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String tag = br.readLine();
                    if(tag!=null)
                    parseNotify(tag);
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
            }
        };
        timer.schedule(timerTask,0,5000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void parseNotify(String json) {
        JSONObject jsonObject ;
        String state = null;
        JSONArray deleteBy = null;
        JSONArray dBy[] = null;
        JSONArray orderBy = null;
        JSONArray byCaptain = null;
        JSONArray byC[] = null;
        String byServer = null;
        try {
            jsonObject = new JSONObject(json) ;
            state = jsonObject.getString("stat");
            if(json.contains("deleted by team")){
            deleteBy = jsonObject.getJSONArray("deleted by team");  //两层数组
            if(deleteBy!=null){
                dBy = new JSONArray[deleteBy.length()];
                for (int i = 0; i < deleteBy.length(); i++) {
                    dBy[i] = deleteBy.getJSONArray(i);
                }
            }
            }
            if(json.contains("your order is canceled"))
            orderBy = jsonObject.getJSONArray("your order is canceled");  //一层数组
            if(json.contains("you are notified by")){
                byCaptain = jsonObject.getJSONArray("you are notified by");  //两层数组
                if(byCaptain!=null){
                    byC = new JSONArray[byCaptain.length()] ;
                    for (int i = 0; i < byCaptain.length(); i++) {
                        byC[i] = byCaptain.getJSONArray(i);
                    }
                }
            }
            if(json.contains("msg2creator"))
            byServer = jsonObject.getString("msg2creator");
        } catch (JSONException e) {
        }

        if("sleep".equals(state)){
            return ;
        }else
        if("alive".equals(state)){
            if(dBy!=null){  //组员被移出组  //您已被移出xx小组
                //{"deleted by team":[[{"teamName":"second","creatorID":"4cNxEYur","tid":"2"}]],"stat":"alive"}
                for (int i = 0; i < dBy.length; i++) {
                    if(dBy[i]!=null){
                        try {
                            JSONObject object = dBy[i].getJSONObject(0);
                            String teamName = object.getString("teamName");
                            pushNotify("deleteBy","您已被移出"+teamName+"小组");
                        } catch (JSONException e) {
                        }
                    }
                }
            }
            if(orderBy!=null){   //组员订单被删除  //你在xx小组的订单已被取消
                //{"your order is canceled":[{"userID":"D5IGE3hF","time":"1470648307811","orderID":"119","productNum":"1","tid":"2","productID":"2"},{"userID":"D5IGE3hF","time":"1470648307811","orderID":"118","productNum":"2","tid":"2","productID":"1"},{"userID":"D5IGE3hF","time":"1470468031409","orderID":"113","productNum":"1","tid":"2","productID":"2"},{"userID":"D5IGE3hF","time":"1470468031409","orderID":"112","productNum":"1","tid":"2","productID":"1"}],"stat":"alive"}
                    try {
                        JSONObject object = orderBy.getJSONObject(0);

                        String tid = object.getString("tid");
                        String creatorId = Utils.getCaptainIdFromID(tid);
                        String userId = object.getString("userID") ;
                        if(creatorId!=null&&!creatorId.equals(userId)){
                            String teamName = Utils.getNameFromID(tid);
                            if(teamName!=null){
                                pushNotify("orderBy","您在"+teamName+"小组的订单已被取消");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
            if(byC!=null){  //组员被组长提醒下单  //xx小组组长提醒您点餐时间到了
                //{"you are notified by":[[{"teamName":"second","creatorID":"4cNxEYur","tid":"2"}]],"stat":"alive"}
                for (int i = 0; i < byC.length; i++) {
                    if(byC[i]!=null){
                        try {

                            JSONObject object = byC[i].getJSONObject(0);
                            String creatorId = object.getString("creatorID");
                            String id = Utils.getUser(getSharedPreferences(Utils.SPNAME,0)).getId();
                            if(creatorId!=null&&!creatorId.equals(id)){
                                String teamName = object.getString("teamName");
                                pushNotify("byCaptain",teamName+"小组组长提醒您点餐时间到了");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if(byServer!=null){   //组长被服务器定时提醒
                //{"msg2creator":"该吃晚餐了，请提醒组员订餐","stat":"alive"}
                pushNotify("byServer","饭桶提醒您点餐时间将近，请提醒组员下单");
            }
        }
    }
    public void pushNotify(String key , String content){
        if(key!=null&&content!=null){
            Intent intent = new Intent("android.intent.action.MY_BROADCAST");
            intent.putExtra(key,content);
            getApplicationContext().sendBroadcast(intent);
        }

    }

}
