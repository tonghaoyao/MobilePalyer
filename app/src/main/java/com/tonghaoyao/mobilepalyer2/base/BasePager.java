package com.tonghaoyao.mobilepalyer2.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by dell1 on 2016-11-10 .
 */

/*
* 基类,公共类
*   VideoPager
*   AudioPager
*   NetVidioPager
*   NetAudioPager
*    继承BasePager
* */

public abstract class BasePager {
    /*
    *上下文
    * */
    public final Context context;

    public View rootView;
    public boolean isInitData;

    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    /*
    * 强制孩子实现,实现特定的效果
    *
    * */
    public abstract View initView();

    /*
    * 当子页面需要初始化数据的时候,联网请求数据,或者绑定数据的时候要重写该方法
    * */
    public void initData(){}
}
