package com.example.cloudhua.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.cloudhua.async.ServiceAsync;
import com.example.cloudhua.enity.Product;
import com.example.cloudhua.enity.Team;
import com.example.cloudhua.ordersys.OrderActivity;
import com.example.cloudhua.ordersys.R;
import com.example.cloudhua.utils.ToastUtil;
import com.example.cloudhua.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by cloudhua on 16-7-28.
 */
public class MyOrderFragment extends Fragment {
    ListView historyOrder ;
    static Context context;
    SharedPreferences sharedPreferences ;
    static HistoryAdapter historyAdapter ;
    static SwipeRefreshLayout swipeRefreshLayout ;
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(historyAdapter!=null)
            historyAdapter.notifyDataSetChanged();
            if(swipeRefreshLayout!=null){
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view =  inflater.inflate(R.layout.item_myorder,container,false);
        sharedPreferences = getActivity().getSharedPreferences(Utils.SPNAME,0);
        historyOrder = (ListView) view.findViewById(R.id.lv_history_order);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        historyAdapter = new HistoryAdapter(getActivity()) ;
        historyOrder.setAdapter(historyAdapter);
        new ServiceAsync(context,8).execute(Utils.PATH_getUserOrders+"id="+Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId());
//        historyOrder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(Utils.lastOrders.get(i).getState()==0){
//                    Utils.cancelOrderPopwindow(context,view,i,"取消订单");
//                }
//                return false;
//            }
//        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ServiceAsync(context,8).execute(Utils.PATH_getUserOrders+"id="+Utils.getUser(context.getSharedPreferences(Utils.SPNAME,0)).getId());
            }
        });
        return view;
    }
    class HistoryAdapter extends BaseAdapter{

        Context context ;
        LayoutInflater inflater ;
        public HistoryAdapter(Context context){
            this.context = context;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return Utils.lastOrders.size();
        }

        @Override
        public Object getItem(int i) {
            return Utils.lastOrders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View view1 = inflater.inflate(R.layout.item_ls_history,null);
            TextView tv_product_info = (TextView) view1.findViewById(R.id.tv_product_info);
            List<Product> products = Utils.lastOrders.get(i).getProducts();
            StringBuffer productInfo = new StringBuffer();
            double price = 0 ;
            for (int j = 0; j < products.size(); j++) {
                productInfo.append(products.get(j).getName()+":");
                productInfo.append(products.get(j).getCount()+"份");
                price += products.get(j).getCount()*products.get(j).getPrice();
                productInfo.append("  ");
            }
            tv_product_info.setText(productInfo);
            TextView tv_total_price = (TextView) view1.findViewById(R.id.tv_total_price);
            tv_total_price.setText("总计："+price+"元");
            TextView tv_date = (TextView) view1.findViewById(R.id.tv_date);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = Utils.lastOrders.get(i).getDate();
            tv_date.setText(format.format(new Date(Long.parseLong(date))));
            TextView teamBelongs = (TextView) view1.findViewById(R.id.tv_teambelong);
            teamBelongs.setText(Utils.lastOrders.get(i).getTeam().getName());
            Button button_cancel = (Button) view1.findViewById(R.id.btn_cancel_order);
            TextView tv_state = (TextView) view1.findViewById(R.id.tv_state);
            int state = Utils.lastOrders.get(i).getState();
//            if(state==0){
//                tv_state.setTextColor(Color.RED);
//                tv_state.setText("待处理");
//            }else if(state==1){
//                tv_state.setTextColor(Color.GREEN);
//                tv_state.setText("已完成");
//            }else if(state==2){
//                tv_state.setTextColor(Color.parseColor("#FEB016"));
//                tv_state.setText("已取消");
//            }
            if(state==0){
                tv_state.setBackgroundResource(R.drawable.wait_order);
                button_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.popuWarningWindow(context,"取消订单","该订单尚未完成，确定取消？",i);
                    }
                });
            }else if(state==1){
                button_cancel.setVisibility(View.GONE);
                tv_state.setBackgroundResource(R.drawable.complete_order);

            }else if(state==2){
                button_cancel.setVisibility(View.GONE);
                tv_state.setBackgroundResource(R.drawable.cancel_order);
            }
            Button btn_again = (Button) view1.findViewById(R.id.btn_again);
            btn_again.setTag(i);
            btn_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //处理再次购买事件
                    final Button btn = (Button) view;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle ("选择小组") ;
                    builder.setIcon(R.mipmap.ic_launcher);
                    int i = 0;
                    String[] teamName = new String[Utils.myTeams.size()];
                    for (; i < Utils.myTeams.size() ; i++) {
                        teamName[i] = Utils.myTeams.get(i).getName();
                    }
                    builder.setItems(teamName, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int type = -1 , position = -1;
                            Team team = Utils.myTeams.get(i);
                            Utils.confirmOrderWindow(context,sharedPreferences,Utils.lastOrders.get((int)btn.getTag()).getProducts(),team);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,getResources().getDisplayMetrics().heightPixels/2);
                }
            });
            return view1;
    }
}
    }
