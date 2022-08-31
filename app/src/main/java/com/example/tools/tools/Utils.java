package com.example.tools.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utils {



    private static Toast myToast;

    /**
     * 此处是一个封装的Toast方法，可以取消掉上一次未完成的，直接进行下一次Toast
     * @param context context
     * @param text 需要toast的内容
     */
    public static void toast(Context context, String text){
        if (myToast != null) {
            myToast.cancel();
            myToast=Toast.makeText(context,text,Toast.LENGTH_SHORT);
        }else{
            myToast=Toast.makeText(context,text,Toast.LENGTH_SHORT);
        }
        myToast.show();
    }

    private static OkHttpClient okHttpClient;

    public Utils(){
        okHttpClient=new OkHttpClient().newBuilder()
                .callTimeout(8, TimeUnit.SECONDS)
                .connectTimeout(8,TimeUnit.SECONDS)
                .readTimeout(8,TimeUnit.SECONDS)
                .build();
    }

    private static Utils instance;

    public static Utils getInstance() {
        if (instance==null){
            instance=new Utils();
        }
        return instance;
    }
/////////////////////////////////////////////////////////////////////////////
    //get方法
    public static void get(final String url, final OkhttpCallBack okhttpCallBack){
        try {
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                getInstance();
                Request request = new Request.Builder()
                        .url(url)
                        .method("GET", null)
                        .addHeader("Accept", "application/json")
                        .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        okhttpCallBack.onFail(e.getMessage()+"asdfghjkl");
                        Log.i("asd",e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        okhttpCallBack.onSuccess(response);
                        Log.i("asd","OKHTTP连接chengg");
                    }
             });
            }
        });
        thread.start();} catch (Exception e) {
            e.printStackTrace();
            Log.i("asd","OKHTTP连接失败");
        }
    }


    //get方法
    public static void get_token(final String url, final String token, final OkhttpCallBack okhttpCallBack){
        try {
            final Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    getInstance();
                    Request request = new Request.Builder()
                            .url(url)
                            .method("GET", null)
                            .addHeader("Authorization", token)
                            .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            okhttpCallBack.onFail(e.getMessage()+"asdfghjkl");
                            Log.i("asd",e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            okhttpCallBack.onSuccess(response);
                            Log.i("asd","OKHTTP连接chengg");
                        }
                    });
                }
            });
            thread.start();} catch (Exception e) {
            e.printStackTrace();
            Log.i("asd","OKHTTP连接失败");
        }
    }


//////////////////////////////////////////////////////////////////////
    //////post方法
    // post  json 数据
    public static void post_json(final String url, final JSONObject jsonObject, final OkhttpCallBack okhttpCallBack) throws JSONException {

        try {


        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                 getInstance();


                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, String.valueOf(jsonObject));

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .addHeader("Accept", "application/json")
                            .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                            .addHeader("Content-Type", "application/json")
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            okhttpCallBack.onFail(e.getMessage() + "asdfghjkl");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            okhttpCallBack.onSuccess(response);
                        }
                    });
                }
             });
        thread.start();} catch (Exception e) {
            e.printStackTrace();
            Log.i("asd","OKHTTP连接失败");
        }
    }
////////////////////////////////////////////////////////////
    // post json数据  token数据
    public static void post_jsonObject(final String token,final String url, final String json,  final OkhttpCallBack okhttpCallBack) throws JSONException {

        try {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                getInstance();
                try {
                    JSONObject jsonObject=new JSONObject(json);

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());

                Log.i("asdqq", jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", token)
                        .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                        .addHeader("Content-Type", "application/json")
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        okhttpCallBack.onFail(e.getMessage() + "asdfghjkl");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        okhttpCallBack.onSuccess(response);
                    }

                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });
        thread.start();  } catch (Exception e) {
            e.printStackTrace();
            Log.i("asd","OKHTTP连接失败");
        }
    }


    ///////////////////////////////////////////

    // post json数据  token数据
    public static void post_json(final String token,final String url, final String json,  final OkhttpCallBack okhttpCallBack) throws JSONException {

        try {
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    getInstance();

                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, json);

                        Log.i("asdqq",json);
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization", token)
                                .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                                .addHeader("Content-Type", "application/json")
                                .build();
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                okhttpCallBack.onFail(e.getMessage() + "asdfghjkl");
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                okhttpCallBack.onSuccess(response);
                            }

                        });


                }
            });
            thread.start();  } catch (Exception e) {
            e.printStackTrace();
            Log.i("asd","OKHTTP连接失败");
        }
    }
    /////////////////////////////////////////////////////////////////////
    //post 文件数据
    public static void post_file(final String url, final String filename, final String path, final OkhttpCallBack okhttpCallBack){
        try {

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                getInstance();

                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("img",filename,
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        new File(path)))
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", body)
                        .addHeader("Accept", "application/json")
                        .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        okhttpCallBack.onFail(e.getMessage() + "asdfghjkl");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        okhttpCallBack.onSuccess(response);
                    }
                });
            }
        });
        thread.start(); } catch (Exception e) {
            e.printStackTrace();
            Log.i("asd","OKHTTP连接失败");
        }
    }
//////////////////////////////////////////////////////////////////////////////
    //post 文件数据,token
    public static void post_file(final String token,final String url, final String filename, final String path, final OkhttpCallBack okhttpCallBack){
       try {

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                getInstance();

                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("img",filename,
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        new File(path)))
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", body)
                        .addHeader("Authorization", token)
                        .addHeader("Accept", "application/json")
                        .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        okhttpCallBack.onFail(e.getMessage() + "asdfghjkl");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        okhttpCallBack.onSuccess(response);
                    }
                });
            }
        });
        thread.start();
    } catch (Exception e) {
           e.printStackTrace();
           Log.i("asd","OKHTTP连接失败");
       }
    }
//////////////////////////////////////////////////////////////////
    //put  json数据
public static void put(final String token, final String url, final String json, final OkhttpCallBack okhttpCallBack){
        try {
    Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                getInstance();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, json);
                Request request = new Request.Builder()
                        .url(url)
                        .method("PUT", body)
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", token)
                        .addHeader("User-Agent", "apifox/1.0.26 (https://www.apifox.cn)")
                        .addHeader("Content-Type", "application/json")
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        okhttpCallBack.onFail(e.getMessage() + "asdfghjkl");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        okhttpCallBack.onSuccess(response);
                    }
                });
            }

        });
        thread.start();
} catch (Exception e) {
        e.printStackTrace();
            Log.i("asd","OKHTTP连接失败");
    }
}
////////////////////////////////////////
    //数据返回
    public interface OkhttpCallBack{
        void onSuccess(Response response);
        void onFail(String error);
    }





///////////////////////////////////////////////////////////////////////////////////////////////////
    //时间工具


//获取当前时间
    public static String getTime(){
        long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
        String  str=String.valueOf(time);
        return str;
    }


    // 将字符串转为时间戳
    public static String getTime(String user_time) {
        String re_time = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        }catch (Exception e) {
            // TODO Auto/generated catch block e.printStackTrace();
        }
        return re_time;
    }

    // 将时间戳转为字符串   年
    public static String getStrTime_N(String cc_time) {
        String re_StrTime = null;
        cc_time=iflength(cc_time);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        long lcc_time = Long.parseLong(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    // 将时间戳转为字符串   年  月  日
    public static String getStrTime_NYR(String cc_time) {
        String re_StrTime = null;
        cc_time=iflength(cc_time);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        long lcc_time = Long.parseLong(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    // 将时间戳转为字符串    时 分
    public static String getStrTime_SF(String cc_time) {
        String re_StrTime = null;
        cc_time=iflength(cc_time);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        long lcc_time = Long.parseLong(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }
//将时间戳转为字符串   全时间
    public static String getStrTime_all(String cc_time) {
        String re_StrTime = null;
        cc_time=iflength(cc_time);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long lcc_time = Long.parseLong(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }
 //转换成10位字符
    public static String iflength(String cc_time){
        String time=null;
        if (cc_time.length()==10){time=cc_time;}
        else {time=cc_time.substring(0,cc_time.length()-3);   }
        return time;
///////////////////////////////////////////////////////////////////////////////////////////////////


    }
}
