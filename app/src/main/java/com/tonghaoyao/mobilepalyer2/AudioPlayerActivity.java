package com.tonghaoyao.mobilepalyer2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonghaoyao.mobilepalyer2.domain.MediaItem;
import com.tonghaoyao.mobilepalyer2.service.MusicPlayerService;
import com.tonghaoyao.mobilepalyer2.utils.LyricUtils;
import com.tonghaoyao.mobilepalyer2.utils.Utils;
import com.tonghaoyao.mobilepalyer2.view.BaseVisualizerView;
import com.tonghaoyao.mobilepalyer2.view.ShowLyricView;


import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by dell1 on 2016-12-08 .
 * 作者: 童浩瑶 on 10:12
 * QQ号: 1339170870
 * 作用:
 */
public class AudioPlayerActivity extends Activity implements View.OnClickListener {
    /**
     * 进度更新
     */
    private static final int PROGRESS = 1;
    /**
     * 显示歌词
     */
    private static final int SHOW_LYRIC = 2;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private IMusicPlayerService service; //服务的代理类, 通过他可以调用服务的方法
    private int position;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlayermode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;
    private ShowLyricView showLyricView;
    private BaseVisualizerView baseVisualizerView;

    private MyReciver reciver;

    private Utils utils;

    /**
     * true:从状态栏进入的: 不需要重新播放
     * false:从播放列表进入的
     */
    private boolean notification;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-12-11 07:37:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */

    private ServiceConnection con = new ServiceConnection() {

        /**
         * 当连接成功的时候回调这个方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);

            if (service != null) {
                try {
                    if (!notification) { //从列表来的数据
                        service.openAudio(position);
                    } else { //从状态栏来的数据
                        //打印是否为主线程
                        System.out.println("onServiceConnected==thread-name: " + Thread.currentThread().getName());
                        showViewData();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开连接的时候回调这个方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (service != null) {
                    service.stop();
                    service = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-12-08 10:57:12 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audioplayer);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlayermode = (Button) findViewById(R.id.btn_audio_playermode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyrc = (Button) findViewById(R.id.btn_lyrc);
        showLyricView = (ShowLyricView) findViewById(R.id.showLyricView);
        baseVisualizerView = (BaseVisualizerView) findViewById(R.id.baseVisualizerView);
        //设置帧动画
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) ivIcon.getBackground();
        rocketAnimation.start();

        btnAudioPlayermode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);

        //设置音频的拖动
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //拖动进度
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC:
                    try {
                        //1.得到当前的进度
                        int currentPosition = service.getCurrentPosition();
                        //2.把进度传入ShowlyricView 控件, 并且计算高亮哪一句

                        showLyricView.setShowNextLyric(currentPosition);

                        //3.实时的发消息
                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                case PROGRESS:
                    try {
                        //1.得到当前进度
                        int currentPosition = service.getCurrentPosition();

                        //2.设置seekBar.setProgress
                        seekbarAudio.setProgress(currentPosition);

                        //3.时间进度更新
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));

                        //4.每秒更新一次
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        findViews();

        getData();

        bindAndStartService();
    }

    private void initData() {
        utils = new Utils();
//        //注册广播
//        reciver = new MyReciver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
//        registerReceiver(reciver, intentFilter);

        //1. EventBus 注册
        EventBus.getDefault().register(this);   //this是当前类

    }

    class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            showData(null);
        }
    }


    //3.EvenBus 订阅方法
    /**
     *  准备好了
     * @param mediaItem
     */
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)  //订阅函数
    public void showData(MediaItem mediaItem) {
        //发消息开始歌词同步
        showLyric();
        showViewData();
        checkPlaymode();
        setupVisualizerFxAndUi();
    }

    public void onEventMainThread(MediaItem mediaItem){
        //发消息开始歌词同步
        showLyric();
        showViewData();
        checkPlaymode();
        setupVisualizerFxAndUi();
    }

    private Visualizer mVisualizer;
     /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi()
    {
        try {
            int audioSessionid = service.getAudioSessionId();
            System.out.println("audioSessionid=="+audioSessionid);
            mVisualizer = new Visualizer(audioSessionid);
            // 参数内必须是2的位数
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            // 设置允许波形表示，并且捕获它
            baseVisualizerView.setVisualizer(mVisualizer);
            mVisualizer.setEnabled(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void showLyric() {
        //解析歌词
        LyricUtils lyricUtils;

        try {
            String path = service.getAudioPath();
            //传歌词文件
            path = path.substring(0, path.lastIndexOf("."));
            File file = new File(path + ".lrc");
            if (!file.exists()) {
                file = new File(path + ".txt");
            }

            lyricUtils = new LyricUtils();
            lyricUtils.readLyricFile(file);  //解析歌词
            showLyricView.setLyrics(lyricUtils.getLyrics());

            if (lyricUtils.isExistsLyric()) {
                handler.sendEmptyMessage(SHOW_LYRIC);
            }else{
                handler.removeMessages(SHOW_LYRIC);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void showViewData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            //设置进度条的最大值
            seekbarAudio.setMax(service.getDuration());

            //发消息
            handler.sendEmptyMessage(PROGRESS);


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.tonghaoyao.mobileplayer_OPENAUDIO");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        //不至于实例化多个服务
        startService(intent);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-12-11 07:37:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnAudioPlayermode) {
            // Handle clicks for btnAudioPlayermode
            setPlaymode();
        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
            if (service != null) {
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioStartPause) {
            // Handle clicks for btnAudioStartPause
            if (service != null) {
                try {
                    if (service.isPlaying()) {
                        //暂停
                        service.pause();
                        //按钮--播放
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } else {
                        //播放
                        service.start();
                        //按钮--暂停
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
            if (service != null) {
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnLyrc) {
            // Handle clicks for btnLyrc
        }
    }

    private void setPlaymode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }

            //保存到服务中
            service.setPlayMode(playmode);

            //设置图片 和 Toast
            showPlaymode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlayermode.setBackgroundResource(R.drawable.btn_audio_playermode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlayermode.setBackgroundResource(R.drawable.btn_audio_playermode_single_selector);
                Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlayermode.setBackgroundResource(R.drawable.btn_audio_playermode_all_selector);
                Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            } else {
                btnAudioPlayermode.setBackgroundResource(R.drawable.btn_audio_playermode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验状态
     */
    private void checkPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlayermode.setBackgroundResource(R.drawable.btn_audio_playermode_normal_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlayermode.setBackgroundResource(R.drawable.btn_audio_playermode_single_selector);
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlayermode.setBackgroundResource(R.drawable.btn_audio_playermode_all_selector);
            } else {
                btnAudioPlayermode.setBackgroundResource(R.drawable.btn_audio_playermode_normal_selector);
            }

            //校验播放和暂停的按钮
            if (service.isPlaying()){
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            }else {
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到数据
     */
    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }
    }

    @Override
    protected void onDestroy() {

        //移除消息
        handler.removeCallbacksAndMessages(null);

//        //取消注册广播
//        if (reciver != null){
//            unregisterReceiver(reciver);
//            //便于垃圾回收器优先回收
//            reciver = null;
//        }

        //2. EvenBus 取消注册
        EventBus.getDefault().unregister(this);

        //解绑服务
        if (con != null) {
            unbindService(con);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVisualizer != null){
            mVisualizer.release();
        }
    }
}
