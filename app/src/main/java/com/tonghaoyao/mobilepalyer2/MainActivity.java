package com.tonghaoyao.mobilepalyer2;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tonghaoyao.mobilepalyer2.Pager.AudioPager;
import com.tonghaoyao.mobilepalyer2.Pager.NetAudioPager;
import com.tonghaoyao.mobilepalyer2.Pager.NetVideoPager;
import com.tonghaoyao.mobilepalyer2.Pager.VideoPager;
import com.tonghaoyao.mobilepalyer2.base.BasePager;

import java.util.ArrayList;

/**
 * Created by dell1 on 2016-11-09 .
 */
public class MainActivity extends FragmentActivity {

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;

    /*
    * 页面的集合
    * */
    private ArrayList<BasePager> basePagers;

    /*
    * 选中的位置
    * */
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);


        basePagers = new ArrayList<>();
        //添加本地视频页面
        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new NetAudioPager(this));

        //设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyonCheckChangeListener());
        //默认选中首页
        rg_bottom_tag.check(R.id.rb_video);
    }



    class MyonCheckChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default:          //默认本地视频界面
                    position = 0;
                    break;
                case R.id.rb_audio:  //本地音乐界面
                    position = 1;
                    break;
                case R.id.rb_net_video:  //网络视频界面
                    position = 2;
                    break;
                case R.id.rb_net_audio:  //网络音乐界面
                    position = 3;
                    break;
            }

            //把页面添加到Fragment中
            setFragmet();
        }
    }

    /*
    * 把页面添加到Fragment中
    * */
    private void setFragmet() {
        //1.得到FragmentManager
        FragmentManager manager = getSupportFragmentManager();
//        getSupportFragmentManager

        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
        MyFragment mf= new MyFragment(position,basePagers);
//        Log.d("七爷", "执行到FragmentTransaction");
        ft.replace(R.id.fl_main_content, mf);
        //4.提交事务
        ft.commit();

    }

    /**
     * 是否已经退出
     */
    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (position != 0) {  //如果不是第一个界面
                rg_bottom_tag.check(R.id.rb_video);  //返回第一界面
                return true;  //不执行退出操作
            }else if (!isExit){
                isExit = true;
                Toast.makeText(MainActivity.this,"再按一次退出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
                return true;   //不执行退出操作
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
