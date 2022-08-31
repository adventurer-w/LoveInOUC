package com.example.tools.tools;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

    public static boolean checkMatch(String str, String regex)
    {
        boolean result =true;
        try
        {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            result = matcher.matches();
        } catch (Exception e)
        {
            result = false;
        }
        return result;
    }


    public static Boolean checkLenth(String str) {
        long length=0,normal=0;
        for (int k = 0; k <str.length(); k++) {

            Log.i("asd",str);
            if (Regex.checkMatch(String.valueOf(str.charAt(k)), "[\\s]")) {
                length+=1;
            }else {
                normal+=1;
            }
        }
        Boolean could;
        if (length*4>=normal&&length>6){
            could=true;
        }else {could=false;}
        return could;
    }




    public static Boolean checkPaperLenth(String str) {
        long length=0,normal=0;
        for (int k = 0; k <str.length(); k++) {

            Log.i("asd",str);
            if (Regex.checkMatch(String.valueOf(str.charAt(k)), "[\\s]")) {
                length+=1;
            }else {
                normal+=1;
            }
        }
        Boolean could;
        if (length*4>=normal&&length>36){
            could=true;
        }else {could=false;}
        return could;
    }

//判断是否有连续的回车
    public static Boolean checkContinuous(String str) {

            String regEx = "\\s{4}";
            Pattern pattern = Pattern.compile(regEx);
             Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            return matcher.find();
}



}
