package com.example.tools.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.tools.Activity.NewsDetailsActivity;
import com.example.tools.R;
import com.example.tools.tools.Data;
import com.example.tools.tools.SquareLayout;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Data> list;
    private static final int ITEM_PAGER = 0;
    private static final int ITEM_NEWS = 1;
    private static final int ITEM_ERROR = 2;
    private static final int ITEM_NO = 3;
    private ViewPagerAdapter viewPagerAdapter;

    public NewsAdapter(Context context, List<Data> list) {
        this.context = context;
        this.list = list;
    }

    public void addData(List<Data> addList) {
        if (addList != null) {
            list.addAll(addList);
            notifyItemRangeChanged(list.size() - addList.size(), addList.size());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view_news = null;
        RecyclerView.ViewHolder holder = null;

        if (i == ITEM_NO) {
            view_news = LayoutInflater.from(context).inflate(R.layout.item_nofocused, viewGroup, false);
            holder = new NoHolder(view_news);
        }
        if (i == ITEM_NEWS) {            view_news = LayoutInflater.from(context).inflate(R.layout.item_news, viewGroup, false);
            holder = new NewsHolder(view_news);
        }
        if (i == ITEM_PAGER) {
            view_news = LayoutInflater.from(context).inflate(R.layout.item_viewpager2, viewGroup, false);
            holder = new PagerHolder(view_news);
        }
        if (i == ITEM_ERROR) {
            view_news = LayoutInflater.from(context).inflate(R.layout.item_papernonet, viewGroup, false);
            holder = new ErrorHolder(view_news);
        }
        assert holder != null;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int i) {

        //?????????
        if (holder instanceof NoHolder) {
            ((NoHolder) holder).textView.setText(list.get(i).getNoData());
            if (list.get(i).getNoData().equals("????????????")) {
                ((NoHolder) holder).squareLayout.setBackgroundResource(R.drawable.nonews);
            }
        }
        //??????
        if (holder instanceof NewsHolder) {
            ((NewsHolder) holder).title.setText(list.get(i).getTitle());
            ((NewsHolder) holder).writer.setText(list.get(i).getWriter());
            if (list.get(i).getTag() == 1) {
                ((NewsHolder) holder).like_num.setText("??????");
            } else if (list.get(i).getTag() == 2) {
                ((NewsHolder) holder).like_num.setText("??????");
            } else if (list.get(i).getTag() == 3) {
                ((NewsHolder) holder).like_num.setText("??????");
            } else if (list.get(i).getTag() == 4) {
                ((NewsHolder) holder).like_num.setText("??????");
            } else {
                ((NewsHolder) holder).like_num.setText("??????");
            }
            Glide.with(context)
                    .load(list.get(i).getNews_pics_set())
                    .error(R.drawable.error)
                    .into(((NewsHolder) holder).imageView);

            ((NewsHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewsDetailsActivity.class);
                    intent.putExtra("id", list.get(i).getNew_Id());
                    intent.putExtra("user_id", list.get(i).getWriter_id());
                    intent.putExtra("writer", list.get(i).getWriter());
                    intent.putExtra("photo", list.get(i).getPhoto());
                    intent.putExtra("info", list.get(i).getInfo());
                    context.startActivity(intent);
                }
            });
        }
        //?????????
        if (holder instanceof PagerHolder) {
            List<String> img = new ArrayList<String>();
            img = list.get(i).getPics();
            viewPagerAdapter = new ViewPagerAdapter(context, img);

            ((PagerHolder) holder).viewPager2.setOffscreenPageLimit(1);
            //????????????
            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(10));
            compositePageTransformer.addTransformer(new TransFormer());
            //????????????
            ((PagerHolder) holder).viewPager2.setPageTransformer(compositePageTransformer);
            View recyclerView = ((PagerHolder) holder).viewPager2.getChildAt(0);
            if (recyclerView != null && recyclerView instanceof RecyclerView) {
                recyclerView.setPadding(100, 0, 100, 0);
                ((RecyclerView) recyclerView).setClipToPadding(false);
            }


            ((PagerHolder) holder).viewPager2.setAdapter(viewPagerAdapter);
            ((PagerHolder) holder).viewPager2.setCurrentItem(1);
            // ????????????
            final List<String> finalImg = img;
            ((PagerHolder) holder).viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                int currentPosition;

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                    // ViewPager.SCROLL_STATE_IDLE ??????????????????????????????????????????????????????????????????????????????????????????
                    // ????????????????????? setCurrentItem ???????????????????????????????????????????????????
                    if (state != ViewPager.SCROLL_STATE_IDLE) return;

                    // ????????????????????????????????????????????????????????????????????????
                    if (currentPosition == 0) {
                        ((PagerHolder) holder).viewPager2.setCurrentItem(finalImg.size() - 2, false);

                    } else if (currentPosition == finalImg.size() - 1) {
                        // ???????????????????????????,??????????????????????????????????????????
                        ((PagerHolder) holder).viewPager2.setCurrentItem(1, false);
                    }
                }
            });
            viewPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int i) {

        if (list.get(i).getNoData() != null) {
            return ITEM_NO;
        } else if (list.get(i).getPics() != null) {
            Log.i("asd", "???????????????");
            return ITEM_PAGER;
        } else if (list.get(i).getError() != null) {
            Log.i("asd", "assss");
            return ITEM_ERROR;
        } else {
            return ITEM_NEWS;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PagerHolder extends RecyclerView.ViewHolder {

        public ViewPager2 viewPager2;
        public LinearLayout linearLayout;

        public PagerHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.recy_pager);
            linearLayout = itemView.findViewById(R.id.viewpager_lin);


        }
    }


    public static class NewsHolder extends RecyclerView.ViewHolder {

        TextView title, writer, like_num;
        ImageView imageView;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_news_title);
            writer = itemView.findViewById(R.id.textView6);
            like_num = itemView.findViewById(R.id.textView12);
            imageView = itemView.findViewById(R.id.imageView2);
        }
    }

    public static class ErrorHolder extends RecyclerView.ViewHolder {


        public ErrorHolder(@NonNull View itemView) {
            super(itemView);


        }
    }

    public static class NoHolder extends RecyclerView.ViewHolder {


        TextView textView;
        SquareLayout squareLayout;

        public NoHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.no_focused);
            squareLayout = itemView.findViewById(R.id.no_focused_ing);
        }
    }

    //?????????????????????
    static class TransFormer implements ViewPager2.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {

            if (position >= -1.0f && position <= 0.0f) {
                //?????????????????????????????????????????????
                page.setScaleX(1 + position * 0.1f);
                page.setScaleY(1 + position * 0.2f);
            } else if (position > 0.0f && position < 1.0f) {
                //?????????????????????????????????????????????
                page.setScaleX(1 - position * 0.1f);
                page.setScaleY(1 - position * 0.2f);
            } else {
                //????????????View????????????
                page.setScaleX(0.9f);
                page.setScaleY(0.8f);
            }
        }
    }

}
