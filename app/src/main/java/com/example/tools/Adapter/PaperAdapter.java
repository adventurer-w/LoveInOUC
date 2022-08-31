package com.example.tools.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tools.Activity.NewsDetailsActivity;
import com.example.tools.tools.MyData;
import com.example.tools.R;
import com.example.tools.tools.Utils;
import com.example.tools.tools.MyNews;

import java.util.List;

import okhttp3.Response;

public class PaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private  List<MyNews> list;
    private View inflater;
    private String token;
    private static final int no = 0;
    private static final int yes = 1;
    private static final int net=2;
    public PaperAdapter(Context context,  List<MyNews> list) {
        this.context = context;
        this.list = list;
        MyData myData=new MyData(context);
        token=myData.load_token();
    }


    public void addData( List<MyNews> addList){
        if (addList!=null){
            list.addAll(addList);
            notifyItemRangeChanged(list.size()-addList.size(),addList.size());
        }
    }


    public void removeData(int position) {
        list.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int i) {
       if (list.get(i).getNo()!=null){
           return  no;
       }
        if (list.get(i).getError()!=null){
            return  net;
        } else {
            return  yes;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==yes)
        {
            Log.i("asd","asdfghjkl");
            inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mypaper, parent, false);
            RecyclerView.ViewHolder ViewHolder = new PaperAdapter.ViewHolder(inflater);
            return ViewHolder;}
         if(viewType==net)
        {
            inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_papernonet,parent,false);
            PaperAdapter.netHolder netHolder = new PaperAdapter.netHolder(inflater);
            return  netHolder;
        }
        else
        {
            inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nomypaper,parent,false);
            PaperAdapter.noHolder noHolder = new PaperAdapter.noHolder(inflater);
            return  noHolder;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
      //  int viewType=getItemViewType(i);
        if(holder instanceof ViewHolder)
        {
            if ( list.get(i).getTag()==1){ ( (ViewHolder)holder).paperTag.setText("表白");}
            else if ( list.get(i).getTag()==2){ ((ViewHolder)holder).paperTag.setText("交友");}
           else if ( list.get(i).getTag()==3){ ((ViewHolder)holder).paperTag.setText("约饭");}
           else if ( list.get(i).getTag()==4){ ((ViewHolder)holder).paperTag.setText("吐槽");}
            else{ ((ViewHolder)holder).paperTag.setText("其他");}
            ( (ViewHolder)holder).paperTitle.setText(list.get(i).getMy_title());

            Glide.with(context).load(list.get(i).getImg()).error(R.drawable.error).into(( (ViewHolder)holder).paperImage);

            ( (ViewHolder)holder).delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog textTips = new AlertDialog.Builder(context)
                            .setTitle("删除动态:")
                            .setMessage("您确定要删除该动态吗？")
                            .setNegativeButton("我再想想" ,new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Utils.post_json(token,"http://101.43.145.51:10002/itnews/api/news/operator/"+list.get(i).getId()+"/remove","", new Utils.OkhttpCallBack() {
                                            @Override
                                            public void onSuccess(final Response response) {

                                                ((Activity)context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            removeData(i);
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
                                                        Toast.makeText(context,"失败，过一会再试吧！",Toast.LENGTH_SHORT).show(); }
                                                });
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).create();
                    textTips.show();

                }
            });

            ( (ViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyData myData=new MyData(context);
                    Intent intent=new Intent(context, NewsDetailsActivity.class);
                    intent.putExtra("id",list.get(i).getId());
                    intent.putExtra("user_id",myData.load_id());
                    intent.putExtra("writer",myData.load_name());
                    intent.putExtra("photo",myData.load_pic_url());
                    intent.putExtra("info",myData.load_info());

                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
         return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView paperImage;
            TextView paperTitle;
            TextView paperTag;
            Button delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            paperImage=itemView.findViewById(R.id.item_paper_image);
            paperTitle=itemView.findViewById(R.id.item_paper_title);
            paperTag=itemView.findViewById(R.id.item_paper_tag);

            delete=itemView.findViewById(R.id.delete);
        }
    }
    class noHolder extends RecyclerView.ViewHolder{

        public noHolder (View view){
            super(view);
        }
    }
    class netHolder extends RecyclerView.ViewHolder{

        public netHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
