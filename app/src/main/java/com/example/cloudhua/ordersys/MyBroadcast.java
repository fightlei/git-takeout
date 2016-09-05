package com.example.cloudhua.ordersys;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by cloudhua on 16-8-2.
 */
public class MyBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("标识", "onReceive: "+"收到广播");
        String byServer = intent.getStringExtra("byServer");
        String byCaptain = intent.getStringExtra("byCaptain");
        String orderBy = intent.getStringExtra("orderBy");
        String deleteBy = intent.getStringExtra("deleteBy");
        if(byServer!=null){
            pushNotification(1,byServer);
        }
        if(byCaptain!=null){
            pushNotification(2,byCaptain);
        }
        if(orderBy!=null){
            pushNotification(3,orderBy);
        }
        if(deleteBy!=null){
            pushNotification(4,deleteBy);
        }

    }
    public void pushNotification(int tag ,String content){
        NotificationManager nm = (NotificationManager)HomeActivity.homeActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.homeActivity);
        builder.setSmallIcon(R.mipmap.git);
        builder.setContentText(content);   //具体内容
        builder.setContentTitle("饭桶");  //大标题
        builder.setDefaults(Notification.DEFAULT_ALL);
        nm.notify(tag,builder.build());
    }
}
