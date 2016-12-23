package com.tonghaoyao.mobilepalyer2.Pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tonghaoyao.mobilepalyer2.R;
import com.tonghaoyao.mobilepalyer2.SystemVideoPlayer;
import com.tonghaoyao.mobilepalyer2.adapter.NetVideoPagerAdapter;
import com.tonghaoyao.mobilepalyer2.adapter.VideoPagerAdapter;
import com.tonghaoyao.mobilepalyer2.base.BasePager;
import com.tonghaoyao.mobilepalyer2.domain.MediaItem;
import com.tonghaoyao.mobilepalyer2.utils.CacheUtils;
import com.tonghaoyao.mobilepalyer2.utils.Constants;
import com.tonghaoyao.mobilepalyer2.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dell1 on 2016-11-10 .
 * 网络视频页面
 */

public class NetVideoPager extends BasePager{

    @ViewInject(R.id.listview)
    private XListView mListView;

    @ViewInject(R.id.tv_nonet)
    private TextView mTv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mProgressBar;

    /**
     * 装数据集合
     */
    private ArrayList<MediaItem> mediaItems;

    private NetVideoPagerAdapter adapter;

    /**
     * 是否已经加载更多
     */
    private boolean isLoadMore = false;

    public NetVideoPager(Context context) {
        super(context);
    }

    /**
     * 初始化网络视频界面
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netvideo_pager, null);

        //第一个参数是NetVideoPager.this, 第二个参数: 布局, 这个方法的作用是把类和布局关联起来
        x.view().inject(this, view);
        //设置mListView的item的点击事件
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new MyXListViewListener());
        return view;
    }
    

    class MyXListViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {
            getDataFromNet();

        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        //联网请求
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            /**
             * 联网成功
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                isLoadMore = true;
                //主线程
                processData(result);
            }

            /**
             * 联网失败
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                isLoadMore = false;
            }
        });
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //3.传递列表数据-对象-序列化
            Intent intent = new Intent(context, SystemVideoPlayer.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position-1);
            context.startActivity(intent);

        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.d("七爷", "网络视频页面的数据被初始化了");
        String saveJson = CacheUtils.getString(context, Constants.NET_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        getDataFromNet();

    }

    private void getDataFromNet() {
        //联网请求
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            /**
             * 联网成功,网络数据加载成功
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context, Constants.NET_URL, result);
                //主线程
                processData(result);
            }

            /**
             * 联网失败
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showData();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void processData(String json) {

        if (!isLoadMore){
            mediaItems = parseJson(json);

            showData();
        }else {
            //加载更多
            //要把得到更多的数据,添加到原来的集合中
//            ArrayList<MediaItem> moreDatas = parseJson(json);

            isLoadMore = false;
            mediaItems.addAll(parseJson(json));

            //刷新适配器
            adapter.notifyDataSetChanged();

            onLoad();

        }

    }

    private void showData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //有数据
            //设置适配器
            adapter = new NetVideoPagerAdapter(context, mediaItems);
            mListView.setAdapter(adapter);
            onLoad();
            //把文本隐藏
            mTv_nonet.setVisibility(View.GONE);
        } else {
            //没有数据
            //文本显示
            mTv_nonet.setVisibility(View.VISIBLE);
        }

        //ProgressBar隐藏
        mProgressBar.setVisibility(View.GONE);
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("更新时间:"+getSystemTime());
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

    /**
     * 解析json数据:
     * 1.用系统接口解析json数据
     * 2.使用第三方解析工具(Gson, fastjson)
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() >= 0) {

                for (int i=0; i<jsonArray.length(); i++){

                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

                    if (jsonObjectItem != null) {

                        MediaItem mediaItem = new MediaItem();

                        String movieName = jsonObjectItem.optString("movieName"); //name
                        mediaItem.setName(movieName);

                        String videoTitle = jsonObjectItem.optString("videoTitle"); //Desc
                        mediaItem.setDesc(videoTitle);

                        String coverImg = jsonObjectItem.optString("coverImg"); //ImageUrl
                        mediaItem.setImageUrl(coverImg);

                        String hightUrl = jsonObjectItem.optString("hightUrl"); //data
                        mediaItem.setData(hightUrl);

                        //把数据添加到集合中
                        mediaItems.add(mediaItem);

                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mediaItems;
    }
}
