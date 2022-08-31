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


public class FocusNewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private SmartRefreshLayout smartRefreshLayout;
    private String token;
    private int page=1,size=9,o_page=1,refresh_num=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_focus_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        smartRefreshLayout= view.findViewById(R.id.new_srl2);
        recyclerView = view.findViewById(R.id.new_recy2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyData data=new MyData(getContext());
        token=data.load_token();

//刷新与加载
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                page++;
                List<Data> list=new ArrayList<>();
                GetNews(list, false);
                refreshLayout.finishLoadMore();
            }
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                smartRefreshLayout.setEnableLoadMore(true);
                List<Data> list=new ArrayList<>();
                page=1;
                refresh_num++;
                GetNews(list, true);
                refreshLayout.finishRefresh();
            }
        });
        List<Data> list=new ArrayList<>();

        GetNews(list,true);

        Log.i("asd",list.size()+"");
    adapter=new NewsAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);
}

    public void GetNews(final List<Data> list, final Boolean refresh){
        if (page<=o_page) {
            Utils.get_token("http://101.43.145.51:10002/itnews/api/news/recommend/follow?page="+page+"&size="+size,token, new Utils.OkhttpCallBack() {
                @Override
                public void onSuccess(Response response) {
                    try {
                        JSONObject jsonObject21 = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONObject jsonObject22 = jsonObject21.getJSONObject("data");
                        Log.i("asd", jsonObject22.toString());
                        o_page = jsonObject22.getInt("count");
                        JSONArray jsonArray21 = jsonObject22.getJSONArray("news");
                        Log.i("asd", jsonObject21.getString("msg"));                        if (jsonObject21.getString("msg").equals("数据库暂无相关数据")){
                            page=1;o_page=1;
                            Data data211 = new Data();
                            data211.setNoData("暂无关注");
                            list.add(data211);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new NewsAdapter(getContext(), list);
                                    recyclerView.setAdapter(adapter);
                                }
                            });
                        }else {

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

                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
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
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),"没有更多了",Toast.LENGTH_SHORT).show();
                    smartRefreshLayout.setEnableLoadMore(false);

                }
            });
        }
    }

}