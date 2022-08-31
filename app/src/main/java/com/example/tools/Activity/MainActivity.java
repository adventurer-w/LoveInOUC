package com.example.tools.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.tools.Fragment.MyPaperFragment;
import com.example.tools.Fragment.NewsFragment;
import com.example.tools.Fragment.UserFragment;
import com.example.tools.tools.MyData;
import com.example.tools.R;
import com.example.tools.tools.Utils;
import com.facebook.stetho.Stetho;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private RelativeLayout news;
    private RelativeLayout paper;
    private RelativeLayout user;
    private String email;
    private ImageView mes;
    private NewsFragment newsFragment = new NewsFragment();
    private MyPaperFragment myPaperFragment = new MyPaperFragment();
    private UserFragment userFragment = new UserFragment();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public void setRedNumber(TextView textView, int num)
    {
        if(num==0)
        {
            textView.setVisibility(View.GONE);
        }
        else if(num>0&&num<=99)
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(num));
        }
        else
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText("99+");
        }
    }
        private void hideFragment(FragmentTransaction transaction){
            if(newsFragment != null){
                transaction.hide(newsFragment);
            }
            if(myPaperFragment != null){
                transaction.hide(myPaperFragment);
            }
            if(userFragment != null){
                transaction.hide(userFragment);
            }
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyData data=new MyData(MainActivity.this);
        email=data.load_email();
        String token =data.load_token();
        Stetho.initializeWithDefaults(this);
        news = findViewById(R.id.news_layout);
        paper = findViewById(R.id.paper_layout);
        user = findViewById(R.id.user_layout);
        news.setSelected(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!newsFragment.isAdded())
        {
            transaction.add(R.id.fragment,newsFragment).show(newsFragment).commit();
        }

        try {
            Utils.get_token("http://101.43.145.51:10002/itnews/api/self/info", token, new Utils.OkhttpCallBack() {
                @Override
                public void onSuccess(Response response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response.body().string());
                        String msg=jsonObject.getString("msg");
                        JSONObject jsonObject1=jsonObject.getJSONObject("data");
                        Log.i("asd",jsonObject1.toString());
                        data.save_name(jsonObject1.getString("nickname"));
                        data.save_pic_url(jsonObject1.getString("avatar"));
                        data.save_info(jsonObject1.getString("info"));
                        data.save_id(jsonObject1.getInt("selfid"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFail(String error) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }



        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                news.setSelected(true);
                paper.setSelected(false);
                user.setSelected(false);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(!newsFragment.isAdded())
                {
                    transaction.add(R.id.fragment,newsFragment);
                }
                hideFragment(transaction);
                transaction.show(newsFragment);
                transaction.commit();
            }
        });
        paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                news.setSelected(false);
                paper.setSelected(true);
                user.setSelected(false);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(!myPaperFragment.isAdded())
                {
                    transaction.add(R.id.fragment,myPaperFragment);
                }
                hideFragment(transaction);
                transaction.show(myPaperFragment);
                transaction.commit();
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                news.setSelected(false);
                paper.setSelected(false);
                user.setSelected(true);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(!userFragment.isAdded())
                {
                    transaction.add(R.id.fragment,userFragment);
                }
                hideFragment(transaction);
                transaction.show(userFragment);
                transaction.commit();
            }
        });
//        HideKeyboard();
        go_update();
    }
    public void HideKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( MainActivity.this.INPUT_METHOD_SERVICE );
        if ( imm.isActive( ) ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );

        }
    }
    private void go_update(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("http://101.43.145.51:10002/itnews/api/appinfo/latest-version")
                        .method("GET", null)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("wsz",responseData);
                    JSONObject jsonObject1 = new JSONObject(responseData);
                    int code = jsonObject1.getInt("code");
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                } catch (IOException | JSONException e) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "请检测网络连接！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("test", "MainonResume");
    }
}