package com.example.tools.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tools.Adapter.NewsAdapter;
import com.example.tools.tools.MyData;
import com.example.tools.R;
import com.example.tools.tools.Utils;
import com.example.tools.tools.Data;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;


public class RecommendFragment extends Fragment {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private SmartRefreshLayout smartRefreshLayout;

    private String token;
    private int page=1,size=5,o_page=1,refresh_num=0;
    private Boolean refresh=true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        smartRefreshLayout= view.findViewById(R.id.new_srl1);
        recyclerView = view.findViewById(R.id.new_recy1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyData data=new MyData(getContext());
        token=data.load_token();
//刷新加载
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refresh=false;
                page++;
                List<Data> list=new ArrayList<>();
                GetNews(list,refresh);
                refreshLayout.finishLoadMore();
            }
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                smartRefreshLayout.setEnableLoadMore(true);
                refresh=true;
                refresh_num++;
                page=1;o_page=1;
                List<Data> list=new ArrayList<>();
                GetData(list);
                refreshLayout.finishRefresh();
            }
        });
//新闻列表
        List<Data> list=new ArrayList<>();
        GetData(list);

    }


    //获取轮播图及新闻
    public void GetData(final List<Data> list){
    //轮播图
        Utils.get("http://101.43.145.51:10002/itnews/api/get-img-lunbo", new Utils.OkhttpCallBack() {
            @Override
            public void onSuccess(Response response) {
                try {
                    JSONObject jsonObject1=new JSONObject(Objects.requireNonNull(response.body()).string());
                    JSONObject jsonObject2=jsonObject1.getJSONObject("data");
                    final JSONArray jsonArray=new JSONArray("[\"https:\\/\\/s3.bmp.ovh\\/imgs\\/2022\\/08\\/30\\/7a44d60768a98475.jpeg\",\"http:\\/\\/101.43.145.51:10002\\/media\\/up_image\\/newspics2022-08-27_094404.696246.jpeg\",\"http:\\/\\/101.43.145.51:10002\\/media\\/up_image\\/newspics2022-08-27_094507.996474.jpeg\",\"https:\\/\\/s3.bmp.ovh\\/imgs\\/2022\\/08\\/30\\/c275feaf1bf22b00.jpeg\"]");
//                    final JSONArray jsonArray=jsonObject2.getJSONArray("pics");
                    Log.i("wzyy",jsonArray.toString());
                    List<String> img=new ArrayList<>();
                    img.add(jsonArray.getString(jsonArray.length()-1));
                    for (int i=0;i<jsonArray.length();i++){
                        img.add(jsonArray.getString(i));
                    }
                    img.add(jsonArray.getString(0));
                    final Data data=new Data();
                    data.setPics(img);
                    Log.i("asd",img.toString());
                    list.add(data);
                    Log.i("asd","轮播图加载完成");
//新闻数据
                    GetNews(list,refresh);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (refresh_num>=1){
                            Toast.makeText(getContext(),"网络走丢了",Toast.LENGTH_SHORT).show();
                        }else {
                        Data dataerror=new Data();
                        dataerror.setError("error");
                        list.add(dataerror);
                        adapter=new NewsAdapter(getContext(),list);
                        recyclerView.setAdapter(adapter);}
                    }
                });
            }
        });
    }

    public void GetNews(final List<Data> list, final Boolean refresh){

        if (page<=o_page) {
            Utils.get("http://101.43.145.51:10002/itnews/api/news/recommend/v4?page=" + page + "&size=" + size, new Utils.OkhttpCallBack() {
                @Override
                public void onSuccess(Response response) {
                    try {
                        JSONObject jsonObject21 = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONObject jsonObject22 = jsonObject21.getJSONObject("data");
                        o_page = jsonObject22.getInt("count");
                        JSONArray jsonArray21 = jsonObject22.getJSONArray("news");
                        if (jsonArray21.length()==0){
                            Data data211 = new Data();
                            data211.setNoData("暂无新闻");
                            list.add(data211);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new NewsAdapter(getContext(), list);
                                    recyclerView.setAdapter(adapter);
                                }
                            });

                        }else {
                        Log.i("asd", jsonObject21.getString("msg"));
                        for (int i = 0; i < jsonArray21.length(); i++) {
                            Data data21 = new Data();
                            JSONObject jsonObject23 = jsonArray21.getJSONObject(i);
                            data21.setTitle(jsonObject23.getString("title"));
                            data21.setNews_Id(jsonObject23.getInt("id"));
                            JSONArray jsonArray22 = jsonObject23.getJSONArray("news_pics_set");
                            if (jsonArray22.length() != 0) {
                                data21.setNews_pics_set(jsonArray22.getString(0));
                            }
                            data21.setTag(jsonObject23.getInt("tag_type"));
                            JSONObject jsonObject24 = jsonObject23.getJSONObject("author");
                          //  Log.i("asd",jsonObject24.toString());
                            data21.setWriter_id(jsonObject24.getInt("id"));
                            data21.setInfo(jsonObject24.getString("info"));
                            data21.setWriter(jsonObject24.getString("nickname"));
                            data21.setPhoto(jsonObject24.getString("avatar"));
                            list.add(data21);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (refresh) {
                                    adapter = new NewsAdapter(getContext(), list);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    adapter.addData(list);
                                }
                            }
                        });}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String error) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!refresh){page--;}
                            if (refresh_num>=1){
                                Toast.makeText(getContext(),"网络走丢了",Toast.LENGTH_SHORT).show();
                            }else {
                            Data dataerror = new Data();
                            dataerror.setError("error");
                            list.add(dataerror);
                            adapter = new NewsAdapter(getContext(), list);
                            recyclerView.setAdapter(adapter);}
                        }
                    });
                }
            });


        }else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),"没有更多了",Toast.LENGTH_SHORT).show();
                    smartRefreshLayout.setEnableLoadMore(false);

                }
            });
        }
    }

}