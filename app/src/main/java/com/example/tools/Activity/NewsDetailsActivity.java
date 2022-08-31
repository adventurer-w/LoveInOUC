package com.example.tools.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tools.Adapter.CommentAdapter;
import com.example.tools.R;
import com.example.tools.tools.Comments;
import com.example.tools.tools.InputTextMsgDialog;
import com.example.tools.tools.MyData;
import com.example.tools.tools.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;

public class NewsDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private CommentAdapter commentAdapter;
    private boolean like = false, oldLike = false, old_collection = false, collection = false, follow = false;
    private Button btn_like, btn_collection;
    private int id, user_id, page = 1, all_page = 1, size = 9, like_nummber, refresh_num = 0;
    int day, month;
    private TextView Like_num;
    private String email;
    private Boolean refresh = true;
    private String title, writer, photo, info;
    private String token;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyData myData = new MyData(NewsDetailsActivity.this);
        token = myData.load_token();
        refresh_num = 0;
        //点赞
        Log.i("asd", oldLike + "" + like);
        if (like != oldLike) {
            try {
                Utils.post_json(token, "http://101.43.145.51:10002/itnews/api/news/operator/" + id + "/like", "", new Utils.OkhttpCallBack() {
                    @Override
                    public void onSuccess(Response response) {
                        try {
                            JSONObject jsonObject21 = new JSONObject(Objects.requireNonNull(response.body()).string());
                            final String msg2 = jsonObject21.getString("msg");
                            Log.i("asd", msg2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFail(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewsDetailsActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //收藏
        if (collection != old_collection) {
            try {
                Utils.post_json(token, "http://101.43.145.51:10002/itnews/api/news/operator/" + id + "/star", "", new Utils.OkhttpCallBack() {
                    @Override
                    public void onSuccess(Response response) {
                        try {
                            JSONObject jsonObject21 = new JSONObject(Objects.requireNonNull(response.body()).string());
                            final String msg3 = jsonObject21.getString("msg");
                            Log.i("asd", msg3);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewsDetailsActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news_details);
        btn_like = findViewById(R.id.news_like);
        btn_collection = findViewById(R.id.news_collection);
        recyclerView = this.findViewById(R.id.details_recycler);
        smartRefreshLayout = findViewById(R.id.comment_srl);
        Calendar c = Calendar.getInstance();
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        Like_num = findViewById(R.id.like_num);
        recyclerView.setLayoutManager(new LinearLayoutManager(NewsDetailsActivity.this));
        MyData myData = new MyData(NewsDetailsActivity.this);
        token = myData.load_token();
        email = myData.load_email();

        id = getIntent().getIntExtra("id", 0);
        writer = getIntent().getStringExtra("writer");
        user_id = getIntent().getIntExtra("user_id", 0);
        photo = getIntent().getStringExtra("photo");
        info = getIntent().getStringExtra("info");


        List<Comments> list = new ArrayList<>();
        GetData(list);


        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refresh = false;
                page++;
                final List<Comments> list = new ArrayList<>();
                GetComments(list);
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh_num++;
                refresh = true;
                page = 1;
                List<Comments> list = new ArrayList<>();
                GetData(list);
                refreshLayout.finishRefresh();
            }
        });


        //评论
        findViewById(R.id.edittext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputTextMsgDialog inputTextMsgDialog = new InputTextMsgDialog(NewsDetailsActivity.this, R.style.dialog_center);
                inputTextMsgDialog.setmOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {
                    @Override
                    public void onTextSend(String msg) {
                        //点击发送按钮后，回调此方法，msg为输入的值
                        String json = "{\"content\": \"" + msg + "\"}";
                        try {

                            Utils.post_jsonObject(token, "http://101.43.145.51:10002/itnews/api/news/operator/" + id + "/comment", json, new Utils.OkhttpCallBack() {
                                @Override
                                public void onSuccess(Response response) {
                                    try {
                                        Log.i("asd", "bhvbhd");
                                        JSONObject jsonObject21 = new JSONObject(Objects.requireNonNull(response.body()).string());
                                        final String msg1 = jsonObject21.getString("msg");
                                        Log.i("asd1", msg1);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(NewsDetailsActivity.this, msg1, Toast.LENGTH_SHORT).show();
                                                if (msg1.equals("评论成功")) {
                                                    inputTextMsgDialog.clearText();
                                                    inputTextMsgDialog.dismiss();
                                                    DbManager dbManager = null;
                                                    refresh_num++;
                                                    page = 1;
                                                    refresh = true;
                                                    List<Comments> list = new ArrayList<>();
                                                    GetData(list);
//                                                    try {
//                                                        dbManager = x.getDb(((myApplication) getApplicationContext()).getDaoConfig());
//                                                        operation operation = new operation();
//                                                        operation.setTitle(title);
//                                                        operation.setType(4);
//                                                        operation.setDate(month + "月" + day + "日");
//                                                        operation.setRead(1);
//                                                        operation.setEmail(email);
//                                                        operation.setChoice(1);
//                                                        dbManager.save(operation);
//                                                    } catch (DbException e) {
//                                                        e.printStackTrace();
//                                                    }
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        Toast.makeText(NewsDetailsActivity.this, "评sss论失败", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFail(String error) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(NewsDetailsActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //设置评论字数
                inputTextMsgDialog.setMaxNumber(50);
                inputTextMsgDialog.show();
            }
        });


        //点赞
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like) {
                    like = false;
                    btn_like.setBackgroundResource(R.drawable.like_nor);
                    like_nummber = like_nummber - 1;
                    Like_num.setText(like_nummber + "");
                    DbManager dbManager = null;
//                    try {
//                        dbManager = x.getDb(((myApplication) getApplicationContext()).getDaoConfig());
//                        operation operation = new operation();
//                        operation.setTitle(title);
//                        operation.setType(2);
//                        operation.setDate(month + "月" + day + "日");
//                        operation.setRead(1);
//                        operation.setEmail(email);
//                        operation.setChoice(1);
//                        dbManager.save(operation);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    like = true;
                    btn_like.setBackgroundResource(R.drawable.like_fill);
                    like_nummber = like_nummber + 1;
                    Like_num.setText(like_nummber + "");
                    DbManager dbManager = null;
//                    try {
//                        dbManager = x.getDb(((myApplication) getApplicationContext()).getDaoConfig());
//                        operation operation = new operation();
//                        operation.setTitle(title);
//                        operation.setType(2);
//                        operation.setDate(month + "月" + day + "日");
//                        operation.setRead(1);
//                        operation.setEmail(email);
//                        operation.setChoice(0);
//                        dbManager.save(operation);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
                }

            }
        });
        //收藏
        btn_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collection) {
                    collection = false;
                    btn_collection.setBackgroundResource(R.drawable.collection_nor);
                    DbManager dbManager = null;
//                    try {
//                        dbManager = x.getDb(((myApplication) getApplicationContext()).getDaoConfig());
//                        operation operation = new operation();
//                        operation.setTitle(title);
//                        operation.setType(3);
//                        operation.setDate(month + "月" + day + "日");
//                        operation.setRead(1);
//                        operation.setEmail(email);
//                        operation.setChoice(1);
//                        dbManager.save(operation);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }

                } else {
                    collection = true;
                    btn_collection.setBackgroundResource(R.drawable.collection_fill);
                    DbManager dbManager = null;
//                    try {
//                        dbManager = x.getDb(((myApplication) getApplicationContext()).getDaoConfig());
//                        operation operation = new operation();
//                        operation.setTitle(title);
//                        operation.setType(3);
//                        operation.setDate(month + "月" + day + "日");
//                        operation.setRead(1);
//                        operation.setEmail(email);
//                        operation.setChoice(0);
//                        dbManager.save(operation);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
        //返回
        findViewById(R.id.news_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //获取新闻
    public void GetData(final List<Comments> list) {

        try {
            Utils.get_token("http://101.43.145.51:10002/itnews/api/news/info/" + id + "/info-full", token, new Utils.OkhttpCallBack() {
                @Override
                public void onSuccess(Response response) {
                    try {

                        String res = Objects.requireNonNull(response.body()).string();
                        Log.i("asd", res);

                        JSONObject jsonObject1 = new JSONObject(res);
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        Log.i("asd", jsonObject1.getString("msg"));
                        Comments comments = new Comments();
                        title = jsonObject2.getString("title");
                        comments.setTitle(title);
                        comments.setContent(jsonObject2.getString("content"));
                        comments.setAuthor_id(jsonObject2.getInt("author_id"));
                        if (jsonObject2.getInt("isFollow") == 1) {
                            follow = true;
                        }
                        if (jsonObject2.getInt("isFollow") == 0) {
                            follow = false;
                        }

                        Log.i("asd1", jsonObject2.getInt("isFollow") + "");

                        comments.setTag(jsonObject2.getInt("tag"));


                        Log.i("asdq", jsonObject2.getInt("isLike") + "" + jsonObject2.getInt("like_num") + jsonObject2.getInt("isStar") + jsonObject2.getInt("isFollow"));
                        if (refresh_num == 0) {
                            like_nummber = jsonObject2.getInt("like_num");
                            if (jsonObject2.getInt("isLike") == 1) {
                                like = true;
                                oldLike = true;
                            }
                            if (jsonObject2.getInt("isStar") == 1) {
                                collection = true;
                                old_collection = true;
                            }
                        }
                        comments.setComment_num(jsonObject2.getInt("comment_num"));
                        comments.setStar_num(jsonObject2.getInt("star_num"));
                        comments.setPhoto(photo);
                        comments.setAuthor_id(user_id);
                        comments.setInfo(info);
                        comments.setWriter(writer);
                        comments.setFollow(follow);
                        final JSONArray jsonArray = jsonObject2.getJSONArray("pics");
                        if (jsonArray.length() != 0) {
                            List<String> imglist = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                imglist.add(jsonArray.getString(i));
                            }
                            comments.setPics(imglist);
                        }
                        list.add(comments);
                        GetComments(list);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_collection.setEnabled(true);
                                btn_like.setEnabled(true);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String error) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (refresh_num > 0) {
                                btn_collection.setEnabled(false);
                                btn_like.setEnabled(false);
                                Toast.makeText(NewsDetailsActivity.this, "连接失败，请刷新重试", Toast.LENGTH_SHORT).show();

                            } else {
                                Comments error = new Comments();
                                error.setError("error");
                                list.add(error);

                                commentAdapter = new CommentAdapter(NewsDetailsActivity.this, list);
                                recyclerView.setAdapter(commentAdapter);
                            }
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void GetComments(final List<Comments> list) {
        if (page <= all_page) {
            try {
                Utils.get_token("http://101.43.145.51:10002/itnews/api/news/info/" + id + "/comment?page=" + page + "&size=" + size, token, new Utils.OkhttpCallBack() {
                    @Override
                    public void onSuccess(Response response) {
                        try {
                            String data = Objects.requireNonNull(response.body()).string();
                            Log.i("asd", data);
                            JSONObject jsonObject1 = new JSONObject(data);
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data");

                            Log.i("asd", jsonObject1.getString("msg"));

                            if (jsonObject1.getString("msg").equals("暂无评论")) {
                                if (page == 1) {
                                    Comments comments = new Comments();
                                    comments.setNoComments("暂无评论！");
                                    list.add(comments);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(NewsDetailsActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } else {
                                all_page = jsonObject2.getInt("count");

                                if (page == 1) {
                                    Comments comments = new Comments();
                                    comments.setFirst("评论");
                                    list.add(comments);
                                }

                                JSONArray jsonArray2 = jsonObject2.getJSONArray("comments");
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    Comments comments = new Comments();
                                    JSONObject jsonObject3 = jsonArray2.getJSONObject(i);
                                    comments.setComment_writer(jsonObject3.getString("nickname"));
                                    comments.setComment_content(jsonObject3.getString("content"));
                                    String time = jsonObject3.getString("create_time");

                                    time = time.substring(6, 10);
                                    comments.setCreate_time(time);
                                    comments.setPhoto(jsonObject3.getString("avatar"));
                                    list.add(comments);
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btn_collection.setEnabled(true);
                                    btn_like.setEnabled(true);
                                    if (refresh) {
                                        if (refresh_num == 0) {
                                            Like_num.setText(like_nummber + "");
                                            if (like) {
                                                btn_like.setBackgroundResource(R.drawable.like_fill);
                                            } else {
                                                btn_like.setBackgroundResource(R.drawable.like_nor);
                                            }
                                            if (collection) {
                                                btn_collection.setBackgroundResource(R.drawable.collection_fill);
                                            } else {
                                                btn_collection.setBackgroundResource(R.drawable.collection_nor);
                                            }
                                        }
                                        commentAdapter = new CommentAdapter(NewsDetailsActivity.this, list);
                                        recyclerView.setAdapter(commentAdapter);
                                    } else {
                                        commentAdapter.addData(list);
                                    }
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (refresh_num > 0) {
                                    btn_collection.setEnabled(false);
                                    btn_like.setEnabled(false);
                                    Toast.makeText(NewsDetailsActivity.this, "网络走丢了", Toast.LENGTH_SHORT).show();
                                } else {
                                    Comments error = new Comments();
                                    error.setError("error");
                                    list.add(error);
                                    commentAdapter = new CommentAdapter(NewsDetailsActivity.this, list);
                                    recyclerView.setAdapter(commentAdapter);
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(NewsDetailsActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
        }
    }


}