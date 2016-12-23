package com.tonghaoyao.mobilepalyer2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonghaoyao.mobilepalyer2.Pager.AudioPager;
import com.tonghaoyao.mobilepalyer2.base.BasePager;

import java.util.ArrayList;

/**
 * Created by dell1 on 2016-11-14 .
 */
public class MyFragment extends Fragment {
    private View rootView;
    private int position;
    private ArrayList<BasePager> basePagers;
    public MyFragment(int position, ArrayList<BasePager> basePagers){
        this.position = position;
        this.basePagers = basePagers;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BasePager basePager = getBasePager();
        if (basePager != null){
            //各个页面的视图
            return basePager.rootView;
        }
        return null;
    }


    /*
    * 根据位置得到对应的页面
    *
    * */
    public BasePager getBasePager() {

        BasePager basePager = basePagers.get(position);
        if(basePager !=null && !basePager.isInitData){
            basePager.initData();      //联网请求或者绑定数据
            basePager.isInitData = true;
        }
        return basePager;
    }
}
