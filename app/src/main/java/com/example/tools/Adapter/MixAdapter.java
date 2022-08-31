package com.example.tools.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.tools.Activity.NewsDetailsActivity;
import com.example.tools.R;
import com.example.tools.tools.MyData;
import com.example.tools.tools.Utils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;

public class MixAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Map<String, Object>> list;
    public Context context;
    public final int VIEW_NO = 0;  // 没有内容
    public final int VIEW_PEO = 1;  // 用户列表
    public final int VIEW_NEWS = 2; // 新闻列表
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 2000;
    private boolean type;
    private int focus;
    private String email;
    private int month;
    private int day;
    private String token;

    public MixAdapter(Context context, List<Map<String, Object>> list){
        this.context = context;
        this.list = list;
        MyData myData = new MyData(context);
        token = myData.load_token();
        email=myData.load_email();
        Calendar c=Calendar.getInstance();
        month=c.get(Calendar.MONTH)+1;
        day=c.get(Calendar.DAY_OF_MONTH);
        Log.d("12332","制造成功");
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("12332",Integer.valueOf(list.get(position).get("type").toString())+"拓扑");
        return Integer.valueOf(list.get(position).get("type").toString());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Log.d("12332", viewType+"");
        if (viewType == VIEW_NEWS) {
            Log.d("12332", "动态");
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new NewsViewHolder(view);
        } else if(viewType == VIEW_NO){
            Log.d("12332", "没有");
            view = LayoutInflater.from(context).inflate(R.layout.item_nofans, parent, false);
            return new NoViewHolder(view);
        }else if(viewType == VIEW_PEO){
            Log.d("12332","绑定成功");
            view = LayoutInflater.from(context).inflate(R.layout.item_people, parent, false);
            return new PeoHolder(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.item_papernonet, parent, false);
            return new NetHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoViewHolder) {
            NoViewHolder viewHolder = (NoViewHolder) holder;
            viewHolder.textView.setText(list.get(position).get("text").toString());
            Log.d("12332", "00");
        } else if(holder instanceof NewsViewHolder){
            int tptp = Integer.valueOf(list.get(position).get("tag_type").toString());
            NewsViewHolder viewHolder = (NewsViewHolder) holder;
            viewHolder.tv_title.setText((list.get(position).get("title").toString()));
            viewHolder.tv_writer.setText((list.get(position).get("nickname").toString()));
            if (tptp == 1) {
                viewHolder.news_type.setText("表白");
            } else if (tptp == 2) {
                viewHolder.news_type.setText("交友");
            } else if (tptp== 3) {
                viewHolder.news_type.setText("约饭");
            } else if (tptp == 4) {
                viewHolder.news_type.setText("吐槽");
            } else {
                viewHolder.news_type.setText("其他");
            }

            Glide.with(context).load(list.get(position).get("news_pic").toString()).centerCrop().into(viewHolder.iv_pic);
            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, NewsDetailsActivity.class);
                    intent.putExtra("id",Integer.valueOf(list.get(position).get("news_id").toString()));
                    intent.putExtra("user_id",Integer.valueOf(list.get(position).get("author_id").toString()));
                    intent.putExtra("writer",list.get(position).get("username").toString());
                    intent.putExtra("photo",list.get(position).get("user_pic").toString());
                    intent.putExtra("info",list.get(position).get("info").toString());
                    context.startActivity(intent);
                }
            });
        }else if(holder instanceof  NetHolder){

        }else if(holder instanceof  PeoHolder) {
            PeoHolder viewHolder = (PeoHolder) holder;
            focus = Integer.valueOf(list.get(position).get("isMutual").toString());
            viewHolder.tv_info.setText(list.get(position).get("info").toString());
            viewHolder.tv_name.setText(list.get(position).get("name").toString());
            Glide.with(context).load(list.get(position).get("head_url").toString())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(viewHolder.iv_head);
            if (focus == 1){
                viewHolder.button.setBackgroundResource(R.drawable.button_focus);
                viewHolder.button.setText("已关注");
                viewHolder.button.setTextColor(context.getResources().getColor(R.color.hotpink));
            }else {  viewHolder.button.setBackgroundResource(R.drawable.btn_focus_fill);
                viewHolder.button.setText("关注");
                viewHolder.button.setTextColor(context.getResources().getColor(R.color.white));
            }
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() - lastClickTime >= FAST_CLICK_DELAY_TIME) {
                        type=true;
                        try {
                            Utils.post_json(token,"http://101.43.145.51:10002/itnews/api/users/"+list.get(position).get("peo_id").toString()+"/operator/follow","", new Utils.OkhttpCallBack() {
                                @Override
                                public void onSuccess(final Response response) {

                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {try {
                                            JSONObject jsonObject21 = new JSONObject(Objects.requireNonNull(response.body()).string());
                                            final String msg2 = jsonObject21.getString("msg");
                                            Log.i("asd", msg2);
                                            Toast.makeText(context,msg2,Toast.LENGTH_SHORT).show();
                                            if (msg2.equals("关注成功")){
                                                String write=list.get(position).get("name").toString();
                                                viewHolder.button.setBackgroundResource(R.drawable.button_focus);
                                                viewHolder.button.setText("已关注");
                                                viewHolder.button.setTextColor(context.getResources().getColor(R.color.hotpink));

                                            }else {  String write=list.get(position).get("name").toString();
                                                viewHolder.button.setBackgroundResource(R.drawable.btn_focus_fill);
                                                viewHolder.button.setText("关注");
                                                viewHolder.button.setTextColor(context.getResources().getColor(R.color.white)); }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        }  });
                                }

                                @Override
                                public void onFail(String error) {

                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,"关注失败，过一会再试吧！",Toast.LENGTH_SHORT).show(); }
                                    });
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        lastClickTime = System.currentTimeMillis();
                    } else {
                        if (type){type=false;
                            Toast.makeText(context,"操作频繁，过一会再试吧！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void myNotifyDataSetChange(){
        notifyDataSetChanged();
    }


    class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_writer;
        private TextView news_type;
        private ImageView iv_pic;
        private RelativeLayout relativeLayout;
        NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.item_news_title);
            tv_writer = itemView.findViewById(R.id.textView6);
            news_type = itemView.findViewById(R.id.textView12);
            iv_pic = itemView.findViewById(R.id.imageView2);
            relativeLayout = itemView.findViewById(R.id.relativelayout);

        }
    }
    class NoViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        NoViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview567);
        }
    }
    class NetHolder extends RecyclerView.ViewHolder {

        NetHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    class PeoHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_info;
        ImageView iv_head;
        Button button;
        PeoHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.textView);
            tv_info = itemView.findViewById(R.id.textView7);
            iv_head = itemView.findViewById(R.id.imageView16);
            button = itemView.findViewById(R.id.details_btn);
        }
    }
}
