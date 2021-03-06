package com.asynchttpclientdemo.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asynchttpclientdemo.R;
import com.asynchttpclientdemo.adapter.JokeIconAdapter;
import com.asynchttpclientdemo.base.BaseFragment;
import com.asynchttpclientdemo.bean.JokeBean;
import com.asynchttpclientdemo.http.HttpResponseHandler;
import com.asynchttpclientdemo.http.HttpUtils;
import com.asynchttpclientdemo.interfaces.LoadMoreListener;
import com.asynchttpclientdemo.model.JokeModel;
import com.asynchttpclientdemo.utils.Constans;
import com.asynchttpclientdemo.utils.Utils;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by guojiadong
 * on 2017/1/5.
 */
@SuppressWarnings({"ResourceAsColor", "deprecation"})
public class IconJokeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private View mView;
    private SwipeRefreshLayout mSwipeReFresh;
    private RecyclerView mRecycler;
    private int page;
    private JokeIconAdapter mAdapter;
    private List<JokeBean> mBeans = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_recycleview, null);
        initView();
        setModel();
        getDataFromGet();
        initLoadMoreListener();
        return mView;
    }

    private void initLoadMoreListener() {
        mRecycler.setOnScrollListener(new LoadMoreListener(mAdapter) {
            @Override
            public void OnLoadMore(boolean isLoad) {
                if (isLoad) {
                    //设置正在加载更多
                    mAdapter.changeMoreStatus(mAdapter.LOADING_MORE);
                    page++;
                    getDataFromGet();
                }
            }
        });
    }

    private void initView() {
        mSwipeReFresh = (SwipeRefreshLayout) mView.findViewById(R.id.main_swipe);
        mRecycler = (RecyclerView) mView.findViewById(R.id.main_recycler);
        mSwipeReFresh.setOnRefreshListener(this);
        mSwipeReFresh.setProgressBackgroundColor(R.color.colorAccent);
        mSwipeReFresh.setColorSchemeColors(Color.YELLOW);
        mRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    private void setModel() {
        mAdapter = new JokeIconAdapter(mActivity, mBeans);
        mRecycler.setAdapter(mAdapter);
    }

    private void getDataFromGet() {
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("pagesize", 10);
        params.put("key", Constans.JOKE_KEY);
        HttpUtils.get(mActivity, "http://japi.juhe.cn/joke/img/text.from?", params, new HttpResponseHandler() {
            @Override
            public void onResponse(String json) {
                Logger.e("json------", json);
                if (Utils.getResultCode(json)) {
                    JokeModel model = new JokeModel(Utils.getResult(json));
                    List<JokeBean> currentList = model.getBeanList();
                    if (page == 1 && mBeans != null) {
                        mBeans.clear();
                        if (currentList != null && !currentList.isEmpty()) {
                            mBeans.addAll(model.getBeanList());
                        }
                    } else {
                        if (currentList != null && !currentList.isEmpty()) {
                            mBeans.addAll(model.getBeanList());
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mSwipeReFresh.isRefreshing()) {
                        mSwipeReFresh.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                super.onFailure(statusCode, headers, responseBody, error);
                if (mSwipeReFresh.isRefreshing()) {
                    mSwipeReFresh.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        getDataFromGet();
    }
}
