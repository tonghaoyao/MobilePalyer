package com.tonghaoyao.mobilepalyer2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tonghaoyao.mobilepalyer2.domain.Lyric;
import com.tonghaoyao.mobilepalyer2.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by dell1 on LYRICTEXTSIZE16-12-18 .
 * 作者: 童浩瑶 on 10:17
 * QQ号: 1339170870
 * 作用: 自定义歌词显示控件
 */

public class ShowLyricView extends TextView {

    /**
     * 歌词列表
     */
    private ArrayList<Lyric> lyrics;
    private Paint paint;
    private Paint whitepaint;

    private int width;
    private int height;
    /**
     * 歌词列表中的索引
     */
    private int index;

    /**
     * 每行歌词的高
     */
    private float textHeight;
    /**
     * 当前播放进度
     */
    private float currentPosition;
    /**
     * 高亮显示的时间
     */
    private float sleepTime;
    /**
     * 时间戳: 即什么时刻到高亮哪句歌词
     */
    private float timePoint;

    private float LYRICTEXTSIZE = 16;

    /**
     * 设置歌词列表
     * @param lyrics
     */
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView(Context context) {
        textHeight = DensityUtil.dip2px(context, 18); //对应的像素
        //创建画笔
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(context, LYRICTEXTSIZE));
        paint.setAntiAlias(true);
        //设置居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        whitepaint = new Paint();
        whitepaint = new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setTextSize(DensityUtil.dip2px(context, LYRICTEXTSIZE));
        whitepaint.setAntiAlias(true);
        //设置居中对齐
        whitepaint.setTextAlign(Paint.Align.CENTER);

//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        for(int i=0; i<1000; i++){
//            lyric.setTimePoint(1000*i);
//            lyric.setSleeptime(1500+i);
//            lyric.setContent(i+"aaaaaaaaaaaaa"+i);
//            //把歌词添加到集合中
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0){

            //往上推移
            float plush = 0;
            if (sleepTime == 0){
                plush = 0;
            }else {
                //平移
                //这一句所花的时间 : 休眠时间 = 移动的距离 :　总距离
                //移动的距离 = (这一句所花的时间 : 休眠时间)*总距离
                float delta = ((currentPosition-timePoint)/sleepTime)*LYRICTEXTSIZE;

                //屏幕的坐标 = 行高 + 移动的距离
                plush = LYRICTEXTSIZE + delta;
            }
            canvas.translate(0, -plush);

            //绘制歌词
            //b.绘制当前部分
            String currentText = lyrics.get(index).getContent();
            //canvas 画布
            canvas.drawText(currentText, width/2, height/2, paint);
            //绘制前面部分
            float tempY = height/2; //y轴的中间坐标
            for (int i=index-1; i>=0; i--){

                //每一句歌词
                String preContent = lyrics.get(i).getContent();

                tempY = tempY - textHeight;
                if (tempY <0){
                    break;
                }
                canvas.drawText(preContent, width/2, tempY, whitepaint);
            }
            //绘制后面部分
            tempY = height/2; //y轴的中间坐标
            for (int i=index+1; i<lyrics.size(); i++){
                //每一句歌词
                String nextContent = lyrics.get(i).getContent();

                tempY = tempY + textHeight;
                if (tempY > height){
                    break;
                }
                canvas.drawText(nextContent, width/2, tempY, whitepaint);
            }
        }else{
            //没有歌词
            canvas.drawText("没有歌词...", width/2, height/2, paint);
        }
    }

    /**
     * 根据当前播放的位置计算出高亮哪一句歌词
     * @param currentPosition
     */
    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null && lyrics.size()==0){
            return;
        }
        for(int i=1; i<lyrics.size(); i++){
            if (currentPosition < lyrics.get(i).getTimePoint()){

                int tempIndex = i - 1;

                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()){
                    //当前正在播放的那句歌词
                    index = tempIndex;
                    sleepTime = lyrics.get(index).getSleeptime();
                    timePoint = lyrics.get(index).getTimePoint();
                }
            }
        }

        //重新绘制
        invalidate(); //在主线程中执行
        //如果在子线程中执行
//        postInvalidate();
    }


}
