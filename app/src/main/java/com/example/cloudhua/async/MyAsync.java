package com.example.cloudhua.async;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.cloudhua.enity.Order;
import com.example.cloudhua.enity.User;
import com.example.cloudhua.ordersys.HomeActivity;
import com.example.cloudhua.ordersys.MainActivity;
import com.example.cloudhua.utils.JsonUtil;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cloudhua on 16-7-28.
 */
public class MyAsync extends AsyncTask<String , Void, String> {

    private Context context;
    private ProgressBar progressBar ;
    private User user ;
    private boolean isConnect = true;
    private SharedPreferences sharedPreferences ;
    public MyAsync(Context context , ProgressBar progressBar , SharedPreferences sharedPreferences , User user) {
        this.user = user;
        this.sharedPreferences = sharedPreferences;
        this.progressBar = progressBar ;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return Utils.getConnection(strings[0]);
        } catch (IOException e) {
            isConnect = false ;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //返回数据形式：{"id":"Q993nWiC","stat":"OK"}
//        Log.i("信息","信息"+result);
//        Log.i("注册信息", user.getId()+" "+user.getName()+" "+user.getNumber());
        String info[][] = JsonUtil.parseFor(result, JsonUtil.ParseType.CREATEUSER);
        if(isConnect) {
            if(info!=null&&"OK".equals(info[0][0])){
                user.setId(info[0][1]);
                Utils.saveUser(sharedPreferences, user);
                Log.i("注册信息", user.getId()+" "+user.getName()+" "+user.getNumber());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("logged",true);
                editor.commit();
                Intent intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
                MainActivity.mainactivity.finish();
            }else{
                progressBar.setVisibility(View.GONE);
                ToastUtil.showToast(context,"服务器错误");
            }

        }else{
            progressBar.setVisibility(View.GONE);
            ToastUtil.showToast(context,"登录失败，请检查网络连接");
        }
    }
}
