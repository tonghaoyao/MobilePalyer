package com.tonghaoyao.mobilepalyer2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tonghaoyao.mobilepalyer2.R;
import com.tonghaoyao.mobilepalyer2.domain.MediaItem;
import com.tonghaoyao.mobilepalyer2.domain.SearchBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell1 on 2016-11-16 .
 * VideoPager 的适配器
 */

public class SearchAdapter extends BaseAdapter {

    //使用工具utils将毫秒化为分钟显示

    private Context context;
    private final List<SearchBean.ItemData> mediaItems;

    public SearchAdapter(Context context, List<SearchBean.ItemData> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;

    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_netvideo_pager, null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHoder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);

            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }

        //根据position的到列表中对应的数据
        SearchBean.ItemData mediaItem = mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getItemTitle());
        viewHoder.tv_desc.setText(mediaItem.getKeywords());

        //1使用xUtils得到网络图片
//        x.image().bind(viewHoder.iv_icon, mediaItem.getImageUrl());

        //2.使用Glide请求网络图片
//        Glide.with(context).load(mediaItem.getImageUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHoder.iv_icon);

        //3.使用Picasso请求网络图片
        Picasso.with(context).load(mediaItem.getItemImage().getImgUrl1())
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHoder.iv_icon);

        return convertView;
    }


    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }
}