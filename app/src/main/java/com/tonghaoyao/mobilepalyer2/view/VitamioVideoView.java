//package com.tonghaoyao.mobilepalyer2.view;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//
//import io.vov.vitamio.widget.*;
//
///**
// * Created by dell1 on 2016-11-24 .
// * 自定义VitamioVideoView
// */
//
//public class VitamioVideoView extends io.vov.vitamio.widget.VideoView {
//
//    /**
//     * 在代码中创建的时候一般用这个方法
//     * @param context
//     */
//    public VitamioVideoView(Context context) {
//        this(context, null);
//    }
//
//    /**
//     *当这个了在布局文件的时候, 系统通过该构造方法实例化该类
//     * @param context
//     * @param attrs
//     */
//    public VitamioVideoView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    /**
//     * 当需要设置样式的时候调用该方法
//     * @param context
//     * @param attrs
//     * @param defStyleAttr
//     */
//    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    /**
//     * 设置视频的宽和高 \n
//     * by 四七哥哥
//     * @param videoWidth 指定视频的宽
//     * @param videoHeight 指定视频的高
//     */
//    public void setVideoSize(int videoWidth, int videoHeight){
//        ViewGroup.LayoutParams params = getLayoutParams();
//        params.width = videoWidth;
//        params.height = videoHeight;
//        setLayoutParams(params);
//    }
//}
