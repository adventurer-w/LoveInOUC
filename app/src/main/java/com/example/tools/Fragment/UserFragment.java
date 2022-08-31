package com.example.tools.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.tools.Activity.ChangeActivity;
import com.example.tools.Activity.FaceActivity;
import com.example.tools.Activity.InterActivity;
import com.example.tools.Activity.MyCollections;
import com.example.tools.tools.MyData;
import com.example.tools.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserFragment extends Fragment {

    private ConstraintLayout attentions;
    private ConstraintLayout fans;
    private TextView tv_attentions_num;
    private TextView tv_fans_num;
    private TextView logout;
    private TextView tv_name;
    private Button change;
    private ConstraintLayout history;
    private ConstraintLayout collection;
    private ConstraintLayout face;
    private ImageView iv_head;
    private String name;
    private String info;
    private String gender;
    private int fans_num;
    private int follow_num;
    private String avatar;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attentions = view.findViewById(R.id.attention);
        fans = view.findViewById(R.id.fans);
        iv_head = view.findViewById(R.id.imageView);
        tv_attentions_num = view.findViewById(R.id.textView4);
        tv_fans_num = view.findViewById(R.id.fn);
        logout = view.findViewById(R.id.textView16);
        change = view.findViewById(R.id.button);
        history = view.findViewById(R.id.constraintLayout2);
        collection = view.findViewById(R.id.constraintLayout3);
        change = view.findViewById(R.id.button);
        tv_name = view.findViewById(R.id.textView2);
        face = view.findViewById(R.id.constraintLayout33);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyData myData = new MyData(getActivity());
                myData.save_check(false);
                myData.save_xx(false);
                myData.save_token("error");
                startActivity(new Intent(getActivity(), InterActivity.class));
                getActivity().finish();
            }
        });
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCollections.class);
                Bundle bd = new Bundle();
                bd.putString("name", "收藏");
                bd.putInt("num", 1);
                intent.putExtras(bd);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCollections.class);
                Bundle bd = new Bundle();
                bd.putString("name", "历史点赞");
                bd.putInt("num", 2);
                intent.putExtras(bd);
                startActivity(intent);
            }
        });
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FaceActivity.class);
                startActivity(intent);
            }
        });
        fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCollections.class);
                Bundle bd = new Bundle();
                bd.putString("name", "粉丝");
                bd.putInt("num", 3);
                intent.putExtras(bd);
                startActivity(intent);
            }
        });
        attentions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCollections.class);
                Bundle bd = new Bundle();
                bd.putString("name", "关注");
                bd.putInt("num", 4);
                intent.putExtras(bd);
                startActivity(intent);
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangeActivity.class));
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangeActivity.class));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        final MyData myData = new MyData(getContext());
        final String my_token = myData.load_token();
        if (myData.load_xx()) {
            tv_fans_num.setText(myData.load_fans() + "");
            tv_attentions_num.setText("" + myData.load_attentions());
            tv_name.setText(myData.load_name());
            Glide.with(getContext()).load(myData.load_pic_url())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(iv_head);
        }
        if (my_token != "NO") {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "http://101.43.145.51:10002/itnews/api/self/info";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(url)
                            .get()
                            .addHeader("Authorization", my_token)
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("1233gg123", "onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            Log.d("1233gg123", "onResponse: " + responseData);
                            try {
                                JSONObject jsonObject1 = new JSONObject(responseData);
                                int code = jsonObject1.getInt("code");
                                final String msg = jsonObject1.getString("msg");
                                if (code != 1000) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                                    name = jsonObject2.getString("nickname");
                                    if (name.length() > 6) {
                                        name = name.substring(0, 6);
                                    }
                                    info = jsonObject2.getString("info");
                                    gender = jsonObject2.getString("gender");
                                    fans_num = jsonObject2.getInt("fans_num");
                                    follow_num = jsonObject2.getInt("follow_num");
                                    avatar = jsonObject2.getString("avatar_90x90");
                                    myData.save_attentions(follow_num);
                                    myData.save_info(info);
                                    myData.save_name(name);
                                    myData.save_fans(fans_num);
                                    myData.save_sex(gender);
                                    myData.save_pic_url(avatar);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_fans_num.setText("" + fans_num);
                                            tv_attentions_num.setText("" + follow_num);
                                            tv_name.setText(name);
                                            Glide.with(getContext()).load(avatar)
                                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                                    .into(iv_head);
                                            myData.save_xx(true);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        }

    }


}