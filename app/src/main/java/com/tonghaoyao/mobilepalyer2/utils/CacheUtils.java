package com.tonghaoyao.mobilepalyer2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.tonghaoyao.mobilepalyer2.service.MusicPlayerService;

/**
 * Created by dell1 on 2016-12-07 .
 * 作者: 童浩瑶 on 19:55
 * QQ号: 1339170870
 * 作用: 缓存工具类
 */


public class CacheUtils {

    /**
     * 保存播放模式
     * @param context
     * @param key
     * @param values
     */
    public static void putPlaymode(Context context, String key, int values){
        SharedPreferences sharedPreferences = context.getSharedPreferences("atqiye", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, values).commit();
    }

    public static int getPlaymode(Context context, String key){

        SharedPreferences sharedPreferences = context.getSharedPreferences("atqiye", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.REPEAT_NORMAL);
    }


    /**
     * 保存数据
     * @param context
     * @param key
     * @param values
     */
    public static void putString (Context context, String key, String values){

        SharedPreferences sharedPreferences = context.getSharedPreferences("atqiye", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, values).commit();
    }

    /**
     * 得到缓存的数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key){

        SharedPreferences sharedPreferences = context.getSharedPreferences("atqiye", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

}
