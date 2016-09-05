package com.example.cloudhua.ordersys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.cloudhua.async.MyAsync;
import com.example.cloudhua.async.ServiceAsync;
import com.example.cloudhua.enity.User;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainactivity ;
    private static final String TAG = "MainActivity";
    private User user;
    EditText password, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.activity_main);
        mainactivity = this;
        initView();
        new ServiceAsync(2).execute(Utils.PATH_getProducts);
        SharedPreferences preferences = getSharedPreferences(Utils.SPNAME, Activity.MODE_PRIVATE);
        User user = Utils.getUser(preferences);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("logged",true);
//        editor.commit();
        if(preferences.getBoolean("logged",false)&&user!=null){ //如果已登录
            //记录上一次登录后的历史订单，解决在OrderActivity中访问SharedPreferences，list<order>为空问题
            if(user.getList_order()==null){
                Utils.lastOrders.clear();
            }else{
                Utils.lastOrders = user.getList_order();
            }
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else if(user != null){ //未登录 ,但存在用户信息
            username.setText(user.getName());
            username.setSelection(username.length());
            password.setText(user.getNumber());
            password.setSelection(password.length());
            //记录上一次登录后的历史订单，解决在OrderActivity中访问SharedPreferences，list<order>为空问题
            if(user.getList_order()==null){
                Utils.lastOrders.clear();
            }else{
                Utils.lastOrders = user.getList_order();
            }
            Intent intent1 = getIntent();
            if(intent1!=null){  //如果该界面来自于注销操作
                if(intent1.getBooleanExtra("fromLogOff",false)){
                    HomeActivity.homeActivity.finish();  //finish之前的页面
                }
            }
        }
    }

    private void initView() {
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
    }


    public void login(View view) {
        String name = username.getText().toString().trim();
        if (name == null || name.equals("")) {
            ToastUtil.showToast(this, "姓名不能为空");
        }else{
        Pattern pattern = Pattern.compile("1\\d{10}");
        Matcher matcher = pattern.matcher(password.getText().toString().trim());
        if (matcher.matches()) {
            FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams
                    (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            ProgressBar progressBar = new ProgressBar(this);
            progressBar.setLayoutParams(layoutParams);
            progressBar.setVisibility(View.VISIBLE);
            layout.addView(progressBar);
//            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            lp.alpha = 0.6f;
//            getWindow().setAttributes(lp);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            user = new User(null,username.getText().toString().trim(),
                    password.getText().toString().trim(), null);
            String uname = "";
            try {
                uname = URLEncoder.encode(username.getText().toString().trim(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "login: "+uname);
            new MyAsync(this, progressBar, getSharedPreferences(Utils.SPNAME, Activity.MODE_PRIVATE),user).execute(Utils.PATH_createUser+
                    "username="+uname+"&tel="+password.getText().toString().trim());
        } else {
            ToastUtil.showToast(this, "手机号码格式错误");
        }
    }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        username.clearFocus();
        password.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(),0);
        imm.hideSoftInputFromWindow(username.getWindowToken(),0);
        return super.onTouchEvent(event);
    }
}
