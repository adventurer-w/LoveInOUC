package com.example.tools.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tools.tools.MyData;
import com.example.tools.R;
import com.example.tools.tools.Utils;

import org.json.JSONObject;

import okhttp3.Response;

public class WelcomeActivity extends AppCompatActivity {

    private MyData myData;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_welcome);

        myData=new MyData(WelcomeActivity.this);
        token=myData.load_token();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ;
        try {
            Utils.get_token("http://101.43.145.51:10002/itnews/api/self/info", token, new Utils.OkhttpCallBack() {
                @Override
                public void onSuccess(Response response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response.body().string());
                        String msg=jsonObject.getString("msg");
                        Thread.sleep(1200);//等待时间
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (msg.equals("一切正常")){
                                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                                    finish();
                                    }else {
                                        startActivity(new Intent(WelcomeActivity.this,InterActivity.class));
                                        finish();
                                    }}
                            });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFail(String error) {
                    try {
                        Thread.sleep(2000);//等待时间
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}