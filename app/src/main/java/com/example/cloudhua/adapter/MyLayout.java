package com.example.cloudhua.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cloudhua.async.ServiceAsync;
import com.example.cloudhua.fragment.ShoppingFragment;
import com.example.cloudhua.ordersys.OrderActivity;
import com.example.cloudhua.ordersys.R;
import com.example.cloudhua.ordersys.TeamInfoActivity;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

/**
 * Created by cloudhua on 16-8-9.
 */
public class MyLayout extends RelativeLayout implements View.OnClickListener{
    TextView teamName , captainName , captainNumber , memberManager , teamOrder ;
    LinearLayout item ;
    Context context ;
    String teamNameContent ,  captainNameContent ,  captainNumberContent ;
    int i , i1 ;
    boolean isLong , isClick ;
    static boolean hasOpen ;
    static View viewHolder ;
    public MyLayout(Context context) {
        super(context);
        this.context = context ;
        init();
    }
    public MyLayout(Context context ,String teamNameContent , String captainNameContent , String captainNumberContent , int i ,int  i1) {
        super(context);
        this.context = context ;
        this.teamNameContent = teamNameContent ;
        this.captainNameContent = captainNameContent ;
        this.captainNumberContent = captainNumberContent ;
        this.i = i ;
        this.i1 = i1 ;
        init();
    }

    public void init(){
        LayoutInflater.from(context).inflate(R.layout.item_elv_teaminfo_slide,this,true);
        item = (LinearLayout) findViewById(R.id.ll_item_slide);
        item.setTag(R.id.tag_first,i);
        item.setTag(R.id.tag_third,i1);
        teamName = (TextView) findViewById(R.id.tv_teamname_slide);
        captainName = (TextView) findViewById(R.id.tv_captain_name_slide);
        captainNumber = (TextView) findViewById(R.id.tv_captain_number_slide);
        memberManager = (TextView) findViewById(R.id.tv_btn_member_manager);
        teamOrder = (TextView) findViewById(R.id.tv_btn_team_order);

        if(i==1){
            if(ShoppingFragment.indexOnline.size()>0){
                i1 = ShoppingFragment.indexOnline.get(i1);
            }
            teamOrder.setText("  加  入  ");
            memberManager.setVisibility(View.GONE);
        }
        if(i==0){
            if(ShoppingFragment.indexMy.size()>0){
                i1 = ShoppingFragment.indexMy.get(i1);
            }
            if(!Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId().equals(Utils.myTeams.get(i1).getCaptain().getId())){
                //不是组长
                memberManager.setVisibility(View.GONE);
                teamOrder.setText("详细信息");
            }else{
                teamName.setTextColor(Color.RED);
            }
        }
        teamName.setText(teamNameContent);
        captainName.setText("组长姓名："+captainNameContent);
        captainNumber.setText("组长电话："+captainNumberContent);

        item.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return handleTouch(view, motionEvent);
            }
        });
        item.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(isLong) {
                    if(i==0){  //我的小组
                        if(Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId().equals(Utils.myTeams.get(i1).getCaptain().getId())){
                            //是组长
                            Utils.cancelOrderPopwindow(context,view,i1,new String[]{"删除该组","提醒一下"});
                        }else{
                            //不是组长
                            Utils.cancelOrderPopwindow(context,view,i1,new String[]{"退出该组"});
                        }
                    }else if(i==1){  //在线小组

                    }
                }
                return true ;
            }
        });
        memberManager.setOnClickListener(this);
        item.setOnClickListener(this);
        teamOrder.setOnClickListener(this);
    }

    boolean tag = true ; //标志action_down后是否还处理action_up
    boolean result = false , isOpen = false ;
    int downX ;
    private  boolean handleTouch(View v, MotionEvent motionEvent){

        int bottomWidth = memberManager.getWidth()+teamOrder.getWidth();

        switch (motionEvent.getAction()){

            case MotionEvent.ACTION_DOWN:
                isLong = true ;
                isClick = true ;
                tag = true ;
                downX =(int) motionEvent.getRawX();
                if(hasOpen){
                    if(viewHolder!=null){
                        ObjectAnimator ooa = ObjectAnimator.ofFloat(viewHolder,"translationX",-360,0).setDuration(0);
                        ooa.start();
                        isOpen = false ;
                        result = false ;
                        hasOpen = false ;
                        viewHolder.setTag(R.id.tag_second,false);
                        viewHolder.setTranslationX(0);
                        viewHolder = null;
                    }
                    hasOpen = false ;
                    tag = false ;
                    return true ;

                }

                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) motionEvent.getRawX() - downX;
                if(dx!=0){
                    isClick = false ;
                    isLong = false;
                }

                if(tag) {

                    //移动距离

                    if (v.getTag(R.id.tag_second) == null) {
                        v.setTag(R.id.tag_second,false);
                    }
                    if ((boolean) v.getTag(R.id.tag_second)) {  //是打开状态
                        if (dx > 0 && dx < bottomWidth) {
                            v.setTranslationX(dx - bottomWidth);
                            result = true;
                        }
                    } else {  //关闭状态
                        if (dx < 0 && Math.abs(dx) < bottomWidth) {
                            v.setTranslationX(dx);
                            result = true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(tag) {
                    float alreadyDx = v.getTranslationX();
                    //关闭
                    if (alreadyDx != 0) {
                        if (alreadyDx > -bottomWidth / 2) {
                            if (viewHolder != null)
                                viewHolder.setTag(R.id.tag_second,false);
                            viewHolder = null;
                            hasOpen = false;
                            result = false;
                            isOpen = false;
                            ObjectAnimator aq = ObjectAnimator.ofFloat(v, "translationX", alreadyDx, 0).setDuration(100);
                            aq.start();

                        }
                        //打开
                        else {  //实现打开

                            viewHolder = v;
                            hasOpen = true;
                            isOpen = true;
                            result = true;
                            ObjectAnimator bq = ObjectAnimator.ofFloat(v, "translationX", alreadyDx, -bottomWidth).setDuration(100);
                            bq.start();
                            viewHolder.setTag(R.id.tag_second,true);

                        }
                    }
                }
                break;
        }

        return  false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_item_slide:
                if(isClick){
                    Log.i("lei", "onClick: "+i+"     "+i1);
                    if(i==0){ //我的小组
                        Intent intent = new Intent(context, OrderActivity.class);
//                        if(ShoppingFragment.indexMy!=null&&ShoppingFragment.indexMy.size()>0&&i==0){
//                            i1 = ShoppingFragment.indexMy.get(i1);
//                        }
//                        if(ShoppingFragment.indexOnline!=null&&ShoppingFragment.indexOnline.size()>0&&i==1){
//                            i1 = ShoppingFragment.indexOnline.get(i1);
//                        }
                        intent.putExtra("position",i1);
                        //i+1=1点击我的小组时的响应
                        //i+1=2通过在线小组点击
                        intent.putExtra("type",i+1);
                        context.startActivity(intent);
                    }else if(i==1){
                        ToastUtil.showToast(context,"请先加入该组才能完成下单");
                    }
                }

                break;
            case R.id.tv_btn_member_manager:
                //成员管理
                Intent intent4 = new Intent(context, TeamInfoActivity.class);
                intent4.putExtra("position",i1);
                intent4.putExtra("authority",2);  //组长管理成员
                context.startActivity(intent4);
                break;
            case R.id.tv_btn_team_order:
                String content = teamOrder.getText().toString();
                if(content.equals("  加  入  ")){
                    new ServiceAsync(context,6).execute(Utils.PATH_joinTeam+"id="+
                            Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId()+"&tid="+Utils.existTeams.get(i1).getId());
                }else if(content.equals("组内订单")){
                    Intent intent = new Intent(context, TeamInfoActivity.class);
                    intent.putExtra("position",i1);
                    intent.putExtra("authority",1);
                    context.startActivity(intent);
                }else if(content.equals("详细信息")){
                    Intent intent3 = new Intent(context, TeamInfoActivity.class);
                    intent3.putExtra("position",i1);
                    intent3.putExtra("authority",0);
                    context.startActivity(intent3);
                }
                break;
        }
    }
}
