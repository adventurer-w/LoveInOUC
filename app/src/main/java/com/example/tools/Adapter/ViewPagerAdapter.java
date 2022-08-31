package com.example.tools.Adapter;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tools.R;

import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private List<String> img;
    private Context context;
    public ViewPagerAdapter(Context context, List<String> img) {
        this.context=context;
        this.img=img;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vp_item, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("asd",img.get(i));
            }
        });
        Glide.with(context)
                .load(Objects.requireNonNull(img.get(i)))
                .error(R.drawable.error)
                .apply(bitmapTransform(new RoundedCornersTransformation(42, 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(holder.imageView);
    }



    @Override
    public int getItemCount() {
        return img.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.viewpager_img);

        }
    }
}
