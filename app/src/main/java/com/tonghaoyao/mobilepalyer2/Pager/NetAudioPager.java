package com.tonghaoyao.mobilepalyer2.Pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tonghaoyao.mobilepalyer2.R;
import com.tonghaoyao.mobilepalyer2.adapter.NetAudioPagerAdapter;
import com.tonghaoyao.mobilepalyer2.base.BasePager;
import com.tonghaoyao.mobilepalyer2.domain.NetAudioPagerData;
import com.tonghaoyao.mobilepalyer2.utils.CacheUtils;
import com.tonghaoyao.mobilepalyer2.utils.Constants;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by dell1 on 2016-11-10 .
 * 网络音乐页面
 */

public class NetAudioPager extends BasePager{

    @ViewInject(R.id.listview)
    private ListView mlistView;

    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;

    /**
     * 页面的数据
     */
    List<NetAudioPagerData.ListBean> datas;

    private NetAudioPagerAdapter adapter;


    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netaudio_pager, null);
        //关联
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        String savaJson = CacheUtils.getString(context, Constants.ALL_RES_URL);
        if (!TextUtils.isEmpty(savaJson)){

            //解析数据
            processData(savaJson);
        }
        //联网
        getDataFromNet();
    }

    /**
     * 解析 JSON数据和显示数据
     * 解析数据 : 1.GsonFormat 生成String对象
     *          2.用Gson 解析数据
     * @param json
     */
    private void processData(String json) {
        NetAudioPagerData data = parsedJson(json);

        datas = data.getList();

        if (datas!=null && datas.size()>0){
            //有数据
            tv_nonet.setVisibility(View.GONE);

            //设置适配器
            adapter = new NetAudioPagerAdapter(context, datas);
            mlistView.setAdapter(adapter);
        }else {
            tv_nonet.setText("没有对应的数据...");
            //没有数据
            tv_nonet.setVisibility(View.VISIBLE);
        }

        pb_loading.setVisibility(View.GONE);
    }

    /**
     * Gson 解析数据
     * @param json
     * @return
     */
    private NetAudioPagerData parsedJson(String json) {
        return new Gson().fromJson(json, NetAudioPagerData.class);
    }


    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //请求数据成功
                CacheUtils.putString(context, Constants.ALL_RES_URL, result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("七爷", "onError=="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("七爷", "onCancelled=="+cex.getMessage());
            }

            @Override
            public void onFinished() {

            }
        });
    }
}
