package com.example.tools.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
// 使用安卓的SharedPreferences来储存文本信息

public class MyData {
    private Context context;
    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static MyData mSharedPreferencesUtil;

    @SuppressLint("CommitPrefEdits")
    public MyData(Context context) {
        this.context = context;
        mPreferences = this.context.getSharedPreferences("MY_DATA", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }
    public static MyData getInstance(Context context) {
        if (mSharedPreferencesUtil == null) mSharedPreferencesUtil = new MyData(context);
        return mSharedPreferencesUtil;
    }
    public void save_check(Boolean check) {mEditor.putBoolean("CHECK", check);mEditor.commit();}
    public void save_xx(Boolean xx) {mEditor.putBoolean("XX", xx);mEditor.commit();}
    public void save_sex(String sex) {mEditor.putString("SEX",sex);mEditor.commit();}
    public void save_token(String token) {mEditor.putString("TOKEN", token);mEditor.commit();}
    public void save_name(String name){mEditor.putString("NAME",name);mEditor.commit();}
    public void save_email(String email){mEditor.putString("EMAIL",email);mEditor.commit();}
    public void save_info(String info){mEditor.putString("INFO",info);mEditor.commit();}
    public void save_fans(int num){mEditor.putInt("FANS",num);mEditor.commit();}
    public void save_attentions(int num){mEditor.putInt("ATTENTIONS",num);mEditor.commit();}
    public void save_id(int my){mEditor.putInt("MY",my);mEditor.commit();}
    public void save_pic_url(String url){mEditor.putString("URL",url);mEditor.commit();}

    public String load_pic_url(){return  mPreferences.getString("URL","NO");}
    public String load_token() {
        return mPreferences.getString("TOKEN", "NO");
    }
    public String load_sex() {
        return mPreferences.getString("SEX", "未知");
    }
    public Boolean load_xx() {
        return mPreferences.getBoolean("XX", false);
    }
    public String load_name(){
        return mPreferences.getString("NAME","暂无昵称");
    }
    public String load_info(){
        return mPreferences.getString("INFO","写点什么吧");
    }
    public int load_fans(){return mPreferences.getInt("FANS",0);}
    public int load_attentions(){return mPreferences.getInt("ATTENTIONS",0);}
    public int load_id(){return mPreferences.getInt("MY",1);}
    public String load_email(){
        return mPreferences.getString("EMAIL","NO");
    }
}