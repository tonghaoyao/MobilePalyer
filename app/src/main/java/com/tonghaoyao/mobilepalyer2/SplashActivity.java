package com.tonghaoyao.mobilepalyer2;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.Window;
        import android.view.WindowManager;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler hander = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //去除title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后才执行到这里
                //执行在主线程中
                startMainActivity();
                Log.e(TAG,"当前线程名称=="+Thread.currentThread().getName());
            }
        },2000);
    }

    /*
    * 只调用一次startMainActivity() 或者使用单例模式
    * */
    private boolean isStartMain = false;
    /*
    * 跳转到主页面,并且把当前页面关闭掉
    * */
    private void startMainActivity() {
        if (!isStartMain) {
            isStartMain = true;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //关闭当前界面
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent=="+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        hander.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
