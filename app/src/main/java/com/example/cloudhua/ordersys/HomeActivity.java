package com.example.cloudhua.ordersys;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.cloudhua.async.PushService;
import com.example.cloudhua.async.ServiceAsync;
import com.example.cloudhua.fragment.MyOrderFragment;
import com.example.cloudhua.fragment.ShoppingFragment;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudhua on 16-7-28.
 */
public class HomeActivity extends AppCompatActivity {

    FragmentManager fragmentManager ;
    MyOrderFragment myOrderFragment ;
    ShoppingFragment shoppingFragment ;
    TextView titleInfo , titleMore ;
    TextView bottomHome , bottomOrder ;
    RadioButton rbHome , rbOrder ;
    static EditText etSearch ;
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(etSearch!=null){
                etSearch.setText("");
            }
        }
    };
    public static HomeActivity homeActivity ;
    public static int tagWhichFragment = 0;
    int tag = 0 ; //用于标志当前是哪一个fragment
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = new Intent(this, PushService.class);  //开启轮询服务器
        startService(intent);
        homeActivity = this ;
        titleInfo = (TextView) findViewById(R.id.tv_title_info);
        titleMore = (TextView) findViewById(R.id.btn_title_more);
        etSearch = (EditText) findViewById(R.id.et_search);
        bottomHome = (TextView) findViewById(R.id.tv_bottom_home);
        bottomOrder = (TextView) findViewById(R.id.tv_bottom_order);
        rbHome = (RadioButton) findViewById(R.id.rb_shopping);
        rbOrder = (RadioButton) findViewById(R.id.rb_myorder);
        initData(savedInstanceState);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = etSearch.getText().toString().trim();
                int tag = 1 ;  //搜索框是否为空的标志
                if(content==null||"".equals(content)){
                    tag = 0 ;
                }
                //记录下标
                List<Integer> indexMy = new ArrayList<>();
                List<Integer> indexOnline = new ArrayList<>();
                List<String> group = new ArrayList<>();
                List<List<List<String>>> child = new ArrayList<>();
                group.add("我的小组");
                group.add("在线小组");
                List<List<String>> item1 = new ArrayList<>();
                for (int i = 0; i < Utils.myTeams.size(); i++) {
                    String tname = Utils.myTeams.get(i).getName();
                    String cname = Utils.myTeams.get(i).getCaptain().getName();
                    String cnumber = Utils.myTeams.get(i).getCaptain().getNumber();
                    if(tname.startsWith(content)||cname.startsWith(content)||cnumber.startsWith(content)){
                        indexMy.add(i);
                        List<String> item1_1 = new ArrayList<>();
                        item1_1.add(tname);
                        item1_1.add(cname);
                        item1_1.add(cnumber);
                        item1.add(item1_1);
                    }
                }
                child.add(item1);
                List<List<String>> item2 = new ArrayList<>();
                for (int i = 0; i < Utils.existTeams.size(); i++) {
                    String tname = Utils.existTeams.get(i).getName();
                    String cname = Utils.existTeams.get(i).getCaptain().getName();
                    String cnumber = Utils.existTeams.get(i).getCaptain().getNumber();
                    if(tname.startsWith(content)||cname.startsWith(content)||cnumber.startsWith(content)){
                        indexOnline.add(i);
                        List<String> item2_1 = new ArrayList<>();
                        item2_1.add(tname);
                        item2_1.add(cname);
                        item2_1.add(cnumber);
                        item2.add(item2_1);
                    }
                }
                child.add(item2);
                Message message = new Message();
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("group", (ArrayList<? extends Parcelable>) group);
//                bundle.putParcelable("child", (Parcelable) child);
//                message.setData(bundle);
                message.obj = new Object[]{group,child,indexMy,indexOnline};
                message.arg1 = tag ;
                ShoppingFragment.handler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tagWhichFragment==1){
            etSearch.setVisibility(View.GONE);
            titleInfo.setText("历史订单");
            titleMore.setText("清空");
            titleMore.setBackgroundResource(R.drawable.delete_all);
            rbHome.setBackgroundResource(R.drawable.home_off);
            bottomHome.setTextColor(Color.rgb(169,183,183));
            rbOrder.setBackgroundResource(R.drawable.order_on);
            bottomOrder.setTextColor(Color.rgb(86,171,228));
            FragmentTransaction transaction2 = fragmentManager.beginTransaction();
            transaction2.hide(shoppingFragment);
            transaction2.show(myOrderFragment);
            tag = 2 ;
            transaction2.commit();
        }
        tagWhichFragment=0;
    }

    private void initData(Bundle savedInstanceState) {
        if(savedInstanceState==null){  //判断activity是否发生重建
            shoppingFragment = new ShoppingFragment();
            myOrderFragment = new MyOrderFragment();
            fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.framelayout,shoppingFragment);
            fragmentTransaction.add(R.id.framelayout,myOrderFragment);
            fragmentTransaction.hide(myOrderFragment);
            fragmentTransaction.show(shoppingFragment);
            fragmentTransaction.commit();
        }
        fragmentManager = getFragmentManager();
    }

    public void switch_item(View view){
        switch (view.getId()){
            case R.id.rb_shopping:
            case R.id.ll_bottom_home:
                etSearch.setVisibility(View.VISIBLE);
                titleInfo.setText("小组信息");
                titleMore.setText("更多");
                titleMore.setBackgroundResource(R.drawable.more_operate);
                rbHome.setBackgroundResource(R.drawable.home_on);
                bottomHome.setTextColor(Color.rgb(86,171,228));
                rbOrder.setBackgroundResource(R.drawable.order_off);
                bottomOrder.setTextColor(Color.rgb(169,183,183));
                FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                transaction1.hide(myOrderFragment);
                transaction1.show(shoppingFragment );
                transaction1.commit();
                tag = 1 ;
                break;
            case R.id.rb_myorder:
            case R.id.ll_bottom_order:
                etSearch.setVisibility(View.GONE);
                titleInfo.setText("历史订单");
                titleMore.setText("清空");
                titleMore.setBackgroundResource(R.drawable.delete_all);
                rbHome.setBackgroundResource(R.drawable.home_off);
                bottomHome.setTextColor(Color.rgb(169,183,183));
                rbOrder.setBackgroundResource(R.drawable.order_on);
                bottomOrder.setTextColor(Color.rgb(86,171,228));
                FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                transaction2.hide(shoppingFragment);
                transaction2.show(myOrderFragment);
                transaction2.commit();
                tag = 2 ;
                break;
            case R.id.btn_title_more:
                String title = ((TextView)view).getText().toString().trim();
                if("更多".equals(title)){
                    Utils.showMenu(this,titleMore);
                }
                if("清空".equals(title)){
                    String content ="删除所有历史订单？";
                    for (int i = 0; i < Utils.lastOrders.size(); i++) {
                        if(Utils.lastOrders.get(i).getState()==0){
                            content = "删除所有历史订单？\n不包括未完成订单";
                            break;
                        }
                    }
                    Utils.popuWarningWindow(this,"清空历史订单",content,-1);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(tag==1||tag==0){
                ToastUtil.showToast(HomeActivity.this,"再按一次退出");
                tag = 3 ;
                return true ;
            }
            if(tag==2){
                etSearch.setVisibility(View.VISIBLE);
                titleInfo.setText("小组信息");
                titleMore.setText("更多");
                titleMore.setBackgroundResource(R.drawable.more_operate);
                rbHome.setBackgroundResource(R.drawable.home_on);
                bottomHome.setTextColor(Color.rgb(86,171,228));
                rbOrder.setBackgroundResource(R.drawable.order_off);
                bottomOrder.setTextColor(Color.rgb(169,183,183));
                FragmentTransaction transaction1 = fragmentManager.beginTransaction();
                transaction1.hide(myOrderFragment);
                transaction1.show(shoppingFragment );
                transaction1.commit();
                tag = 1 ;
                return true ;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        etSearch.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(),0);
        return super.dispatchTouchEvent(ev);
    }
}
