package com.example.tools.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tools.Adapter.MixAdapter;
import com.example.tools.tools.MyData;
import com.example.tools.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyCollections extends AppCompatActivity {

    private TextView head;
    private ImageView back;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private static int i = 0;
    private List<Map<String, Object>> list = new ArrayList<>();
    private int flag = 0;
    private String responseData = "";
    private String tp_url;
    private int type;
    private boolean once=false;
    private String token;
    private int len;
    private MixAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_collection);

        head = findViewById(R.id.chat_name);
        back = findViewById(R.id.chat_back);
        recyclerView = findViewById(R.id.recyclerview);
        refreshLayout = findViewById(R.id.new_srl1);


        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        String name = bd.getString("name");
        head.setText(name);
        once = false;
        type = bd.getInt("num");   // 1:collection 2:history good  3:fans  4:attentions;
        switch (type) {
            case 1:
                tp_url = "star-news-ids";
                break;
            case 2:
                tp_url = "like-news-ids";
                break;
            case 3:
                tp_url = "fans-ids";
                break;
            case 4:
                tp_url = "followee-ids";
                break;
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);  //避免滑动卡顿
        list.clear();
        adapter = new MixAdapter(MyCollections.this, list);
        adapter.myNotifyDataSetChange();
        MyData myData = new MyData(MyCollections.this);
        token = myData.load_token();
        once = false;
        wzy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        adapter = new MixAdapter(MyCollections.this, list);
        adapter.myNotifyDataSetChange();
        i = 0;
        once = false;
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("http://101.43.145.51:10002/itnews/api/self/" + tp_url)
                        .method("GET", null)
                        .addHeader("Authorization", token)
                        .build();
                Response response = client.newCall(request).execute();
                responseData = response.body().string();
                getfeedback(responseData);
            } catch (IOException e) {
                list.clear();
                adapter = new MixAdapter(MyCollections.this, list);
                adapter.myNotifyDataSetChange();
                responseData = "";
                flag = 0;
                Map map2 = new HashMap();
                map2.put("type", 3);
                list.add(map2);
                Log.d("1233", String.valueOf(map2));
                MyCollections.this.runOnUiThread(() -> {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MyCollections.this));
                    recyclerView.setAdapter(new MixAdapter(MyCollections.this, list));
                });

                e.printStackTrace();
            }
        }).start();
    }

    public void wzy() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                flag = 0;
                i = 0;
                list.clear();
                adapter = new MixAdapter(MyCollections.this, list);
                adapter.myNotifyDataSetChange();
                responseData = "";
                onResume();
                refreshlayout.finishRefresh(1000/*,false*/);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getfeedback(responseData);
                refreshlayout.finishLoadMore(500/*,false*/);//传入false表示加载失败
            }
        });
    }

    public void getfeedback(String responseData) {
        if (responseData != "") {
            switch (type) {
                case 1:
                case 2:
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        if (jsonObject.getInt("code") == 1000) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject1.getJSONArray("news");
                            Log.d("12332l", "" + jsonArray.length());
                            len = jsonArray.length();
                            for (int j = 0; i < jsonArray.length() && j < 8; i++, j++) {
                                Log.d("1233i", "1:" + i);
                                Log.d("1233i", "leng:" + jsonArray.length());
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                Log.d("12332", "动态消息"+jsonObject2.toString());
                                int news_id = jsonObject2.getInt("id");
                                int tag_type = jsonObject2.getInt("tag_type");
                                String title = jsonObject2.getString("title");
                                JSONArray jsonArray22 = jsonObject2.getJSONArray("news_pics_set");
                                String news_pic = "no";
                                if (jsonArray22.length() != 0) {
                                    news_pic = jsonArray22.getString(0);
                                }

                                JSONObject jsonObject24 = jsonObject2.getJSONObject("author");
                                int author_id = jsonObject24.getInt("id");
                                String username = jsonObject24.getString("username");
                                String nickname = jsonObject24.getString("nickname");
                                String user_pic = jsonObject24.getString("avatar");
                                String info = jsonObject24.getString("info");
                                Map map = new HashMap();

                                map.put("news_id", news_id);
                                map.put("tag_type", tag_type);
                                map.put("news_pic", news_pic);
                                map.put("author_id", author_id);
                                map.put("title", title);
                                map.put("username", username);
                                map.put("nickname", nickname);
                                map.put("user_pic", user_pic);
                                map.put("type", 2);
                                map.put("info",info);
                                list.add(map);
                            }

                            if (i == jsonArray.length()) {
                                MyCollections.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(once){
                                            Toast.makeText(MyCollections.this, "到底了~", Toast.LENGTH_SHORT).show();
                                        }else {
                                            once = true;
                                        }
                                    }
                                });
                            }
                        } else if (jsonObject.getInt("code") == 6666) {
                            list.clear();
                            adapter = new MixAdapter(MyCollections.this, list);
                            adapter.myNotifyDataSetChange();
                            responseData = "";
                            Map map2 = new HashMap();
                            map2.put("type", 0);
                            Log.d("12332", type + "资料");
                            switch (type) {
                                case 1:
                                    map2.put("text", "您还没有收藏任何动态");
                                    break;
                                case 2:
                                    map2.put("text", "您没有任何浏览记录");
                                    break;
                                case 3:
                                    map2.put("text", "还没有人关注您，发发动态吧");
                                    break;
                                case 4:
                                    map2.put("text", "您没有关注任何人");
                                    break;
                            }
                            list.add(map2);
                        }

                        Objects.requireNonNull(MyCollections.this).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (flag != 666) {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MyCollections.this));
                                    recyclerView.setAdapter(new MixAdapter(MyCollections.this, list));
                                    Log.d("12332", "2here");
                                }
                                if (i == len) {
                                    flag = 666;
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                case 4:
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        if (jsonObject.getInt("code") == 1000) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject1.getJSONArray("users");
                            len = jsonArray.length();
                            for (int j = 0; i < jsonArray.length() && j < 8; i++, j++) {
                                Log.d("1233i", "1:" + i);
                                Log.d("1233i", "leng:" + jsonArray.length());
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                Log.d("12332", "人物"+jsonObject2.toString());
                                int peo_id = jsonObject2.getInt("id");
                                String info = jsonObject2.getString("info");
                                String name = jsonObject2.getString("nickname");
                                String head_url = jsonObject2.getString("avatar");
                                int isMutual = jsonObject2.getInt("isFollow");
                                Map map = new HashMap();

                                map.put("peo_id", peo_id);
                                map.put("info", info);
                                map.put("name", name);
                                map.put("head_url", head_url);
                                map.put("type", 1);
                                map.put("isMutual", isMutual);
                                list.add(map);
                            }
                            if (i == jsonArray.length()) {
                                MyCollections.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(once){
                                            Toast.makeText(MyCollections.this, "到底了~", Toast.LENGTH_SHORT).show();
                                        }else {
                                            once = true;
                                        }
                                    }
                                });
                            }
                        } else if (jsonObject.getInt("code") == 6666) {
                            list.clear();
                            adapter = new MixAdapter(MyCollections.this, list);
                            adapter.myNotifyDataSetChange();
                            responseData = "";
                            Map map2 = new HashMap();
                            map2.put("type", 0);
                            Log.d("12332", type + "资料");
                            switch (type) {
                                case 1:
                                    map2.put("text", "您还没有收藏任何动态");
                                    break;
                                case 2:
                                    map2.put("text", "您没有点赞任何动态");
                                    break;
                                case 3:
                                    map2.put("text", "还没有人关注您，发发动态吧");
                                    break;
                                case 4:
                                    map2.put("text", "您没有关注任何人");
                                    break;
                            }
                            list.add(map2);
                        }

                        Objects.requireNonNull(MyCollections.this).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (flag != 666) {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MyCollections.this));
                                    recyclerView.setAdapter(new MixAdapter(MyCollections.this, list));
                                    Log.d("12332", "2here");
                                }
                                if (i == len) {
                                    flag = 666;
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
