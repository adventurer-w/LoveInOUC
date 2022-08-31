package com.example.tools.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tools.tools.CountDownTimerUtils;
import com.example.tools.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText get_username;
    private EditText get_password_one;
    private EditText get_password_two;
    private EditText get_email;
    private EditText get_verify;
    private Button get_get_verify;
    private Button gogo;
    private ImageView iv_back;
    private int tp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        //用户服务协议
        tp = getIntent().getIntExtra("type", 1);
                iv_back = findViewById(R.id.imageView18);
        get_username = findViewById(R.id.username);
        get_password_one = findViewById(R.id.passwordone);
        get_password_two = findViewById(R.id.passowrdtwo);
        get_email = findViewById(R.id.email);
        get_verify = findViewById(R.id.verify);
        get_get_verify = findViewById(R.id.getverify);
        gogo = findViewById(R.id.gogo);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tp == 1) {
                    Intent intent = new Intent(RegisterActivity.this, InterActivity.class);
                    RegisterActivity.this.startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(intent);
                    finish();
                }
            }
        });
        get_get_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email;
                email = get_email.getText().toString();
                if (!checkEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Gson gson = new Gson();
                            Myemail myemail = new Myemail();
                            myemail.email = email;
                            myemail.usage = 1;

                            MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
                            String requestBody = gson.toJson(myemail);
                            Request request = new Request.Builder()
                                    .url("http://101.43.145.51:10002/itnews/api/reglog/code-reg")
                                    .post(RequestBody.create(mediaType, requestBody))
                                    .build();

                            OkHttpClient okHttpClient = new OkHttpClient();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("1233w", "onFailure: " + e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Log.d("1233w", response.protocol() + " " + response.code() + " " + response.message());
                                    Headers headers = response.headers();
                                    String responseData = response.body().string();
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(responseData);
                                        int code = jsonObject1.getInt("code");
                                        if (code != 1000) {
                                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(RegisterActivity.this, "出错啦，请重试", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(get_get_verify, 60000, 1000);
                                                    mCountDownTimerUtils.start();
                                                    Toast.makeText(RegisterActivity.this, "获取成功", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        RegisterActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(RegisterActivity.this, "无法连接到网络！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        e.printStackTrace();
                                    }
                                    for (int i = 0; i < headers.size(); i++) {
                                        Log.d("1233w", headers.name(i) + ":" + headers.value(i));
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        gogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username;
                final String password;
                final String password2;
                final String email;
                final String string_verify;
                username = get_username.getText().toString();
                password = get_password_one.getText().toString();
                password2 = get_password_two.getText().toString();
                email = get_email.getText().toString();
                string_verify = get_verify.getText().toString();
                if (!checkUsername(username)) {
                    Toast.makeText(RegisterActivity.this, "用户名应为6~12位字母或数字！", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(password2)) {
                    Log.d("passw", password);
                    Log.d("passw2", password2);
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                } else if (!checkPassword(password)) {
                    Toast.makeText(RegisterActivity.this, "密码应为6~12位字母或数字！", Toast.LENGTH_SHORT).show();
                } else if (!checkEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "邮箱格式有误！", Toast.LENGTH_SHORT).show();
                } else if (!checkVerify(string_verify)) {
                    Toast.makeText(RegisterActivity.this, "验证码有误！", Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            int intverify;
                            intverify = Integer.parseInt(string_verify);
                            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                            String requestBody = "\r\n{\r\n    \"username\": \"" + username + "\",\r\n    \"password\": \"" + password + "\",\r\n    \"email\": \"" + email + "\",\r\n    \"verify\": \"" + intverify + "\"\r\n}";

                            Request request = new Request.Builder()
                                    .url("http://101.43.145.51:10002/itnews/api/reglog/all-reg")
                                    .post(RequestBody.create(mediaType, requestBody))
                                    .build();
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Log.d("1233w", request.toString() + "   " + requestBody.toString());
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("1233w", "onFailure: " + e.getMessage());
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Log.d("1233w", response.protocol() + " " + response.code() + " " + response.message());
                                    Headers headers = response.headers();
                                    String responseData = response.body().string();
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(responseData);
                                        int code = jsonObject1.getInt("code");
                                        final String msg = jsonObject1.getString("msg");
                                        if (code != 200) {
                                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        RegisterActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(RegisterActivity.this, "无法连接到网络！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        e.printStackTrace();
                                    }
                                    for (int i = 0; i < headers.size(); i++) {
                                        Log.d("1233w", headers.name(i) + ":" + headers.value(i));
                                    }
                                    Log.d("1233w", "onResponse: " + response.body());
                                }
                            });
                        }
                    }).start();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getfeedback(String responseData) {

    }

    public boolean checkUsername(String str) {
        String regexp = "^[0-9a-zA-Z]{6,12}$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public boolean checkPassword(String str) {
        String regexp = "^[0-9a-zA-Z]{6,12}$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public boolean checkVerify(String str) {
        String regexp = "^[0-9]{4}$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public boolean checkEmail(String str) {
        String regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public class Myemail {
        public String email;
        int usage;
    }

    public class Myname {
        public String name;
    }
}