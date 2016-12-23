package com.tonghaoyao.mobilepalyer2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.tonghaoyao.mobilepalyer2.domain.MediaItem;
import com.tonghaoyao.mobilepalyer2.utils.Utils;
import com.tonghaoyao.mobilepalyer2.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by dell1 on 2016-11-16 .
 * 系统播放器
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    private  boolean isuseSystem = false;
    //视频进度的更新
    private static final int PROGRESS = 1;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLLER = 2;

    /**
     * 显示网速
     */
    private static final int SHOW_SPEED = 3;
    /**
     * 全屏
     */
    private static final int FULL_SCREEN = 1;
    /**
     * 默认屏幕
     */
    private static final int DEFAULT_SCREEN = 2;
    private VideoView videoview;
    private Uri uri;
    private RelativeLayout media_controller;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemtime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;
    private LinearLayout ll_buffer;
    private TextView tv_buffer_netspeed;
    private LinearLayout ll_loading;
    private TextView tv_loading_netspeed;

    private Utils utils;

    //监听电量变化的广播
    private MyReceiver receiver;
    /**
     * 传入进来的数据列表
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * 要播放的列表中的具体位置
     */
    private int position;

    /**
     * 1.定义手势识别器
     */
    private GestureDetector detector;

    /**
     * 是否显示控制面板
     */
    private boolean isshowMedia_controller;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;

    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;

    /**
     * 屏幕的高
     */
    private  int screenHeight = 0;
    /**
     * 真实视频的宽和高
     */
    private int videoWidth;
    private int videoHeight;

    /**
     * 调用声音
     */
    private AudioManager am;

    /**
     * 当前的音量  0-15
     */
    private int currentVoice;

    private int maxVoice;

    /**
     * 是否是静音
     */
    private boolean isMute = false;
    /**
     * 是否是网络URI
     */
    private boolean isNetUri = false;

    //上一次的播放进度
    private int precurrentPosition;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-11-19 18:45:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemtime = (TextView) findViewById(R.id.tv_systemtime);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwitchScreen = (Button) findViewById(R.id.btn_video_switch_screen);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_buffer_netspeed = (TextView) findViewById(R.id.tv_buffer_netspeed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_loading_netspeed = (TextView) findViewById(R.id.tv_loading_netspeed);


        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwitchScreen.setOnClickListener(this);

        //最大音量和SeekBar关联
        seekbarVoice.setMax(maxVoice);
        //设置当前的进度--当前音量
        seekbarVoice.setProgress(currentVoice);

        //开始更新网速,只有网络视频才有网速
        handler.sendEmptyMessage(SHOW_SPEED);

    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-11-19 18:45:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            isMute = !isMute;
            // Handle clicks for btnVoice
            updateVoice(currentVoice, isMute);
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
            showSwitchPlayerDialog();
        } else if (v == btnExit) {
            finish();
            // Handle clicks for btnExit
        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
            playPreVideo();

        } else if (v == btnVideoStartPause) {
            startAndPause();
            // Handle clicks for btnVideoStartPause
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if (v == btnVideoSwitchScreen) {
            // Handle clicks for btnVideoSwitchScreen
            setFullScreenAndDefault();
        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
    }

    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提醒您");
        builder.setMessage("是否切换成万能播放器");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void startAndPause() {
        if (videoview.isPlaying()) {
            //视频在播放 设置为暂停
            videoview.pause();
            //按钮状态设置为播放状态
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            //视频播放
            videoview.start();
            //按钮状态设置为暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        //播放上一个
        position--;
        if (position >= 0) {

            ll_loading.setVisibility(View.VISIBLE);
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());

            //设置按钮状态
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if(mediaItems !=null && mediaItems.size() > 0){
            //播放下一个
            position++;
            if (position < mediaItems.size()){

                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if (uri != null){
            //设置按钮状态--把上一个和下一个按钮设置为黑色且不可点击
            setButtonState();
        }
    }

    private void setButtonState() {
        if(mediaItems != null && mediaItems.size() >0){
            if (mediaItems.size()==1){
                //两个按钮设置灰色
                setEnable(false);
            }else if(mediaItems.size()==2){
                if(position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(true);

                }else if(position == mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoNext.setEnabled(true);
                }

            }else {
                if(position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                }else if(position == mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                }else {
                    setEnable(true);
                }
            }
        }else if (uri != null){
            //两个按钮设置灰色
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        if (isEnable){
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);

            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        }else {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_SPEED:  //显示网速


                    //缓冲进度的更新
                    if (isNetUri){
                        //1.得到网速
                        String netSpeed = utils.getNetSpeed(SystemVideoPlayer.this);

                        //显示网络速度
                        tv_loading_netspeed.setText("玩命加载中..."+netSpeed);
                        tv_buffer_netspeed.setText("缓冲中..."+netSpeed);

                        //2.每两秒秒更新一次
                        removeMessages(SHOW_SPEED);  //移除消息
                        sendEmptyMessageDelayed(SHOW_SPEED, 2000);
                    }else{
                        removeMessages(SHOW_SPEED);  //移除消息
                    }
                    break;
                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    hideMedia_controller();
                    break;
                case PROGRESS:

                    //1.得到当前的视频播放进度
                    int currentPosition = videoview.getCurrentPosition();

                    //2.SeekBar.setProgress(当前进度)
                    seekbarVideo.setProgress(currentPosition);

                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //设置系统时间
                    tvSystemtime.setText(getSystemTime());

                    //缓冲进度的更新
                    if (isNetUri){
                        //只有网络的视频才有缓冲效果
                        int buffer = videoview.getBufferPercentage(); //0-100
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer/100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);



                    }else {
                        //本地视频没有缓冲效果
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    if (isNetUri) {

                        //监听卡
                        if (!isuseSystem && videoview.isPlaying()) {
                            if (videoview.isPlaying()) {
                                int buffer = currentPosition - precurrentPosition;
                                if (buffer < 500) {
                                    //视频就卡了
                                    ll_buffer.setVisibility(View.VISIBLE);
                                } else {
                                    //视频不卡了
                                    ll_buffer.setVisibility(View.GONE);
                                }
                            } else {
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }
                    }else {
                        ll_buffer.setVisibility(View.GONE);
                    }

                    precurrentPosition = currentPosition;

                    //3.每秒更新一次
                    removeMessages(PROGRESS);  //移除消息
                    sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_system_video_player);

        initData();

        findViews();

        setListener();

        //得到播放地址
        getData();

        setData();

        //设置控制面板
//        videoview.setMediaController(new MediaController(this));

    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            //设置视频的名称
            tvName.setText(mediaItem.getName());
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
        } else if (uri != null) {
            //设置视频的名称
            tvName.setText(uri.toString());
            isNetUri = utils.isNetUri(uri.toString());
            videoview.setVideoURI(uri);
        }else{
            Toast.makeText(SystemVideoPlayer.this, "帅哥你没有传递数据", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();  //文件夹,图片浏览器,
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");

        position = getIntent().getIntExtra("position", 0);

    }

    private void initData() {
        utils = new Utils();
        //注册电量广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化的时候监听广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);

        //2.实例化手势识别器, 并且重写双击, 单击, 长按
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){

            /**
             * 长按手势事件
             * @param e
             */
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
            }

            /**
             * 双击事件
             * @param e
             * @return
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setFullScreenAndDefault();
                return super.onDoubleTap(e);
            }

            /**
             * 单击事件
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isshowMedia_controller){
                    //隐藏
                    hideMedia_controller();
                    //把隐藏消息移除
                    handler.removeMessages(HIDE_MEDIACONTROLLER);

                }else {
                    //显示
                    showMedia_controller();
                    //发消息隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                }

                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕的宽和高
        //过时的方法
//        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //新的方法得到屏幕宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    private void setFullScreenAndDefault() {
        if(isFullScreen){
            //默认
            setVideoType(DEFAULT_SCREEN);
        }else{
            //全屏
            setVideoType(FULL_SCREEN);
        }
    }

    public void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    /**
     * 得到系统时间
     *
     * @return
     */
    public String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    public void setVideoType(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN: //全屏
                //1.设置视频画面的大小- 屏幕有多大就多大
                videoview.setVideoSize(screenWidth, screenHeight);
                //2.设置按钮的状态 -- 默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN: //默认
                //1.设置视频画面的大小
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width, height);
                //2.设置按钮的状态 -- 全屏
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_full_selector);
                isFullScreen = false;
                break;
        }
    }


    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0); //0~100
            setBattery(level);
        }
    }

    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //设置SeekBar状态变化的的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());

        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        if(isuseSystem) {
            //监听视频播放卡, 系统的API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoview.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:  //视频卡了,拖动卡
                    //显示缓冲条
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:   //视频卡结束了, 拖动卡结束了
                    //隐藏缓冲条
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                if (progress >0){
                    isMute = false;
                }else {
                    isMute = true;
                }
                updateVoice(progress, isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            handler.sendEmptyMessage(PROGRESS);
        }
    }

    /**
     * 设置音量的大小
     * @param progress
     */
    private void updateVoice(int progress ,boolean isMute) {
        if (isMute){
            //第三个参数 值为1调用系统的音量调节界面, 值为0不调用
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        }else {
            //第三个参数 值为1调用系统的音量调节界面, 值为0不调用
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }

    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当手指滑动的时候,会引起SeekBar进度变化,会回调这个方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的为true, 不是用户引起的为false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //seekTo方法见MediaPlayer.java类图
                videoview.seekTo(progress);
            }

        }

        /**
         * 当手指触碰的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        /**
         * 当手指离开的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
        }
    }

    /**
     * 准备好的监听
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            videoview.start();
            //1.得到视频的总时长, 关联总长度
            int duration = videoview.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));
            //默认隐藏控制面板
            hideMedia_controller();
            //2.发消息
            handler.sendEmptyMessage(PROGRESS);

//            videoview.setVideoSize(200, 200);
//            videoview.setVideoSize(mp.getVideoWidth(), mp.getVideoHeight());

            //屏幕的默认播放
            setVideoType(DEFAULT_SCREEN);

            //把加载页面消失掉
            ll_loading.setVisibility(View.GONE);
//
//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    //拖动完成
//                }
//            });
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//            Toast.makeText(SystemVideoPlayer.this, "播放出错了!", Toast.LENGTH_SHORT).show();
            //1.播放网络视频格式不支持---跳转到万能播放器继续播放
            startVitamioPlayer();
            //2.播放网络视频的时候, 网络中断---1.如果网络确实断了,可以提示用户网络断了; 2.网络断断续续的,重新播放
            //3.播放视频的时候中间有空白---下载做完成
            return true;
        }
    }

    /**
     * a.把数据传入VitamioVideoPlayer播放器
       b.关闭系统播放器
     */
    private void startVitamioPlayer() {
//        //关闭VideoView
//        if (videoview != null){
//            videoview.stopPlayback();
//        }
//
//        Intent intent = new Intent(this, VitamioVideoPlayer.class);
//        if (mediaItems !=null && mediaItems.size() >0){
//            //3.传递列表数据-对象-序列化
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("videolist", mediaItems);
//            intent.putExtras(bundle);
//            intent.putExtra("position", position);
//            startActivity(intent);
//            //Activity之间切换无动画效果
//            overridePendingTransition(0, 0);
//        }else if (uri !=null){
//            intent.setData(uri);
//            startActivity(intent);
//            //Activity之间切换无动画效果
//            overridePendingTransition(0, 0);
//        }else {
//            startActivity(intent);
//            //Activity之间切换无动画效果
//            overridePendingTransition(0, 0);
//        }
//
//        finish(); //关闭页面
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
//            Toast.makeText(SystemVideoPlayer.this, "播放完成了="+uri, Toast.LENGTH_SHORT).show();
            playNextVideo();
        }
    }

    @Override
    protected void onDestroy() {

        //移除所有消息
        handler.removeCallbacksAndMessages(null);

        //释放资源的时候, 先释放子类, 再释放父类
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();
    }

    private float startY;
    private float startX;
    /**
     * 屏幕高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: //手指按下
                //1.按下的时候记住值
                startY = event.getY();
                startX = event.getX();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenWidth, screenHeight);
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE: //手指移动
                //2.移动的时候记录值
                float endX = event.getX();
                float endY = event.getY();
                float distanceY = startY - endY;
                if (endX < screenWidth/2){
                    //左边屏幕调节亮度
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distanceY > FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        Log.e(TAG, "up");
                        setBrightness(20);
                    }
                    if (distanceY < FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        Log.e(TAG, "down");
                        setBrightness(-20);
                    }

                }else {
                    //调节声音
                    //              改变声音 = (滑动屏幕的距离 : 总距离) * 音量的最大值
                    float delta = (distanceY/touchRang)*maxVoice;

                    //              最终音量 = 原来的 + 改变声音
                    int voice = (int) Math.min(Math.max(mVol+delta, 0), maxVoice);
                    if (delta !=0){
                        isMute = false;
                        updateVoice(voice, isMute);
                    }
                }

                break;
            case MotionEvent.ACTION_UP: //手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }

    private Vibrator vibrator;

    /*
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // if (lp.screenBrightness <= 0.1) {
        // return;
        // }
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        }
//        Log.e(TAG, "lp.screenBrightness= " + lp.screenBrightness);
        getWindow().setAttributes(lp);
    }


    /**
     * 显示控制面板
     */
    private void showMedia_controller(){
        media_controller.setVisibility(View.VISIBLE);
        isshowMedia_controller = true;
    }

    /**
     * 隐藏控制面板
     */
    private void hideMedia_controller(){
        media_controller.setVisibility(View.GONE);
        isshowMedia_controller = false;
    }

    /**
     * 监听物理键, 按键声音调节同步, 实现声音的调节大小
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice --;
            updateVoice(currentVoice, false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice ++;
            updateVoice(currentVoice, false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
