package com.example.tools.Adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tools.R;

import java.util.List;

/**
 * Created by Administrator on 2018/5/11.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    public GridViewAdapter(Context context, List<String> list)
    {
        this.list=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView==null)
        {
            convertView=View.inflate(context, R.layout.item_grid,null);
            holder=new ViewHolder();
            holder.imageView= (ImageView) convertView.findViewById(R.id.img_appIcon);

            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }




        //placeholder()是图片的占位符，网络还没下载下来的时候占着位置
        //centerCrop()缓存
        Glide.with(context).load(list.get(position)).placeholder(R.mipmap.ic_launcher).centerCrop().into(holder.imageView);

//        /**
//         * 加载图片
//         */
//        if(imageInfo.getBitmap()==null){//如果网络还没下载好
//            holder.imageView.setImageResource(R.mipmap.ic_launcher);
//        }else {
//            holder.imageView.setImageBitmap(imageInfo.getBitmap());
//        }

        return convertView;
    }



    public class ViewHolder
    {
        ImageView imageView;
        TextView textView;
    }
}
