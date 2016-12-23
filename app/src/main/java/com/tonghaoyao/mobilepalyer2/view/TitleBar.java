package com.tonghaoyao.mobilepalyer2.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tonghaoyao.mobilepalyer2.R;
import com.tonghaoyao.mobilepalyer2.SearchActivity;

/**
 * Created by dell1 on 2016-11-15 .
 * 自定义标题栏
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {

    //使用View增强兼容性
    private View tv_search;

    private View rl_game;

    private View iv_record;

    private Context context;
    /*
    * 在代码中实例化该类的时候使用这个方法
    * */
    public TitleBar(Context context) {
        this(context, null);
    }

    /**
     * 当我们在布局文件使用该类的时候,Android系统使用这个构造方法实例化该类
     * @param context
     * @param attrs
     */
    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当需要设置样式的时候,可以使用该方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 当布局文件加载完成的时候回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子实例
        tv_search = getChildAt(1);
        rl_game = getChildAt(2);
        //为什么出现java.lang.NullPointerException 报错
//        iv_record = getChildAt(3);

        //设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
//        iv_record.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:  //搜索
//                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,SearchActivity.class);
                context.startActivity(intent);
                break;
            case R.id.rl_game:    //游戏
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                Log.d("七爷", "onClick游戏");
                break;
            case R.id.iv_record:   //播放历史
                Toast.makeText(context, "播放历史", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
