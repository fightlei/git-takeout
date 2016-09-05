package com.example.cloudhua.ordersys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.cloudhua.async.ServiceAsync;
import com.example.cloudhua.enity.Product;
import com.example.cloudhua.enity.Team;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cloudhua on 16-7-27.
 */
public class OrderActivity extends AppCompatActivity{
    public static OrderActivity orderActivity ;
    Button left_add , left_sub , right_add , right_sub ;
    EditText left_count  , right_count ;
    ImageView imageView ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initVIew();
        orderActivity = this ;
    }

    private void initVIew() {
        left_count = (EditText) findViewById(R.id.left_count);
        right_count = (EditText) findViewById(R.id.right_count);
        imageView = (ImageView) findViewById(R.id.iv_btn_orderback);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderActivity.this.finish();
            }
        });
    }
    public void operate(View view){
        switch(view.getId()){
            case R.id.left_add:
                left_count.setText((Integer.parseInt(left_count.getText().toString().trim())+1)+"");
                left_count.setSelection(left_count.getText().length());
                break;
            case R.id.left_sub:
                int count = Integer.parseInt(left_count.getText().toString().trim());
                if(count>1||(count==1&&Integer.parseInt(right_count.getText().toString().trim())>0)){
                    left_count.setText((count-1)+"");
                }
                left_count.setSelection(left_count.getText().length());
                break;
            case R.id.right_add:
                right_count.setText((Integer.parseInt(right_count.getText().toString().trim())+1)+"");
                right_count.setSelection(right_count.getText().length());
                break;
            case R.id.right_sub:
                int count_right = Integer.parseInt(right_count.getText().toString().trim());
                if(count_right>1||(count_right==1&&Integer.parseInt(left_count.getText().toString().trim())>0)){
                    right_count.setText((count_right-1)+"");
                }
                right_count.setSelection(right_count.getText().length());
                break;
        }

    }
    public void confirm(View view){
        //有待处理
        //现在默认是15 , 18两种商品
        List<Product> products = new ArrayList<>();
        int count ;
        if((count=Integer.parseInt(left_count.getText().toString().trim()))>0)
        products.add(new Product("1",Utils.productList.get(0).getName(),Utils.productList.get(0).getPrice(),count));
        if((count=Integer.parseInt(right_count.getText().toString().trim()))>0)
        products.add(new Product("2",Utils.productList.get(1).getName(),Utils.productList.get(1).getPrice(),count));
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",-1);
        int type = intent.getIntExtra("type",-1);
        Team team = null;
        switch(type){
            case 1: //处理通过点击我的小组创建订单事件
                team = Utils.myTeams.get(position);
                break;
            case 2: //处理通过点击在线小组创建订单事件
                team = Utils.existTeams.get(position);
                break;
        }
        SharedPreferences preferences = getSharedPreferences(Utils.SPNAME, 0);
        Utils.confirmOrderWindow(this,preferences,products,team);
    }
}
