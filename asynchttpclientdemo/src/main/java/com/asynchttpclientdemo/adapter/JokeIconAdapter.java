package com.asynchttpclientdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asynchttpclientdemo.R;
import com.asynchttpclientdemo.activity.JokeDetailActivity;
import com.asynchttpclientdemo.bean.JokeBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by guojiadong
 * on 2017/1/5.
 */

public class JokeIconAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private Context mContext;
    private List<JokeBean> mBeans;
    private LayoutInflater mInflater;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    public JokeIconAdapter(Context context, List<JokeBean> beans) {
        this.mContext = context;
        this.mBeans = beans;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                View view = mInflater.inflate(R.layout.item_joke_icon, parent, false);
                return new ViewHolder(view);
            case TYPE_FOOTER:
                View footView = mInflater.inflate(R.layout.item_loadmore, parent, false);
                return new FooterViewHolder(footView);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            JokeBean bean = getJokeBean(position);
            setViewData((ViewHolder) holder, bean);
        }else if(holder instanceof FooterViewHolder){
            setFooterViewData((FooterViewHolder) holder);
        }
    }

    private void setFooterViewData(FooterViewHolder holder){
        switch (mLoadMoreStatus) {
            case PULLUP_LOAD_MORE:
                holder.loadTv.setText("上拉加载更多...");
                break;
            case LOADING_MORE:
                holder.loadTv.setText("正加载更多...");
                break;
            case NO_LOAD_MORE:
                //隐藏加载更多
                holder.loadBox.setVisibility(View.GONE);
                break;

        }
    }

    private void setViewData(ViewHolder holder, JokeBean bean) {
        if (holder == null || bean == null) {
            return;
        }
        holder.contentTv.setText(bean.content);
        Glide.with(mContext).load(bean.url).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).
                into(holder.icon);
        holder.viewBox.setTag(bean);
        holder.viewBox.setOnClickListener(this);
    }

    private JokeBean getJokeBean(int position) {
        return mBeans == null || mBeans.isEmpty() ? null : mBeans.get(position);
    }

    @Override
    public int getItemCount() {
        return mBeans == null || mBeans.isEmpty() ? 0 : mBeans.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onClick(View view) {
        Object object= view.getTag();
        JokeBean bean = null;
        if(object != null){
            bean = (JokeBean) object;
        }
        switch (view.getId()){
            case R.id.item_joke_icon_box:
                Intent intent = new Intent();
                intent.setClass(mContext, JokeDetailActivity.class);
                intent.putExtra("type_bean",bean);
                mContext.startActivity(intent);
                break;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contentTv;
        ImageView icon;
        View viewBox;
        public ViewHolder(View itemView) {
            super(itemView);
            contentTv = (TextView) itemView.findViewById(R.id.item_content);
            icon = (ImageView) itemView.findViewById(R.id.item_icon);
            viewBox = itemView.findViewById(R.id.item_joke_icon_box);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView loadTv;
        private View loadBox;

        public FooterViewHolder(View v) {
            super(v);
            loadTv = (TextView) v.findViewById(R.id.item_loadmoer_tv);
            loadBox = v.findViewById(R.id.item_loadmoer_box);
        }
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }
}
