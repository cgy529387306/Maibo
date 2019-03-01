package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.WeiboAdapter;
import com.mb.android.maiboapp.adapter.WeiboAdapter.ItemHandler;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserMbResp;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 我的评论
 * Created by cgy on 15/8/31.
 */
public class UserCommentsActivity extends BaseSwipeBackActivity {
	private PullToRefreshListView mRefreshListView;
    private WeiboAdapter mAdapter;
    private List<WeiboEntity> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomTitle("我的点评");
        setContentView(R.layout.common_listview);
        initUI();
        showGlobalLoading();
        requestData(ProjectConstants.RequestType.HEAD);
    }
    
    @Override
	public void globalReload() {
		super.globalReload();
		requestData(ProjectConstants.RequestType.HEAD);
	}

    private void initUI() {
        mRefreshListView = findView(R.id.lsv_common);
        dataList = new ArrayList<WeiboEntity>();
        mAdapter = new WeiboAdapter(UserCommentsActivity.this,dataList,1);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.enableAutoRefreshFooter(true);
        mRefreshListView.getRefreshableView().setDividerHeight(20);
        mRefreshListView.setOnRefreshListener(mRefreshListener);
        mRefreshListView.setOnItemClickListener(mItemClickListener);
        
        mAdapter.setItemHandler(new ItemHandler() {
			private Bundle bundle = new Bundle();

			@Override
			public void onRepost(int pos) {

				bundle.putString("id", dataList.get(pos).getId());
				NavigationHelper.startActivity(UserCommentsActivity.this,
						MBRepostActivtiy.class, bundle, false);
			}

			@Override
			public void onGood(int pos) {
				WeiboEntity entity = dataList.get(pos);
				if (Helper.isNotNull(entity)) {
					requestGood(dataList.get(pos).getId(), entity.getIsPraise());
				}
			}

			@Override
			public void onComment(int pos) {
				bundle.putString("id", dataList.get(pos).getId());
				NavigationHelper.startActivity(UserCommentsActivity.this,
						MBCommentActivtiy.class, bundle, false);
			}
			
			@Override
			public void onDelete(int pos) {
				
			}
		});
    }

    @Override
    public boolean onResponseSuccess(int gact, String response,
                                     Object... extras) {
        if (super.onResponseSuccess(gact, response, extras)) {
            return true;
        }
        hideGlobalLoading();
        int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (requestType == 1) {
			CommonEntity entity = JsonHelper.fromJson(response,
					CommonEntity.class);
			if (Helper.isNotNull(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					requestData(ProjectConstants.RequestType.HEAD);
				}
				ToastHelper.showToast(entity.getResultMessage());
				return true;
			}
		}
        UserMbResp entity = JsonHelper.fromJson(response, UserMbResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			List<WeiboEntity> list = entity.getData().getStatues();
			// 最后一页停止加载下一页
			if (entity.getResuleCode().equals("0") && list != null) {
				if (requestType == ProjectConstants.RequestType.HEAD) {
					if (Integer.parseInt(entity.getData().getTotal_number()) == 0) {
						showGlobalEmpty();
					}
					dataList.clear();
				}
				if (Helper.isEmpty(list) || list.size()<mPageSize) {
					mRefreshListView.enableAutoRefreshFooter(false);
				}
				dataList.addAll(list);
				mAdapter.notifyDataSetChanged();
				mRefreshListView.onRefreshComplete();
			}
		}
        return true;
    }

    @Override
    public boolean onResponseError(int gact, String response,
                                   VolleyError error, Object... extras) {
    	hideGlobalLoading();
		showGlobalError();
    	return true;
    }


    /**
	 * 下拉刷新，上拉加载
	 */
	private OnRefreshListener mRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			mPageNum = 0;
			requestData(ProjectConstants.RequestType.HEAD);
		}

		@Override
		public void onLoadMore() {
			mPageNum++;
			requestData(ProjectConstants.RequestType.BOTTOM);
		}
	};


	/**
	 * 列表点击事件监听者
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int pos = position - mRefreshListView.getRefreshableView().getHeaderViewsCount();
			Bundle bundle=new Bundle();
			bundle.putString("id", dataList.get(pos).getId());
			NavigationHelper.startActivity(UserCommentsActivity.this,
					WeiboDetailActivity.class, bundle, false);
		}
	};

    private void requestData(int requstType) {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("user_name", UserEntity.getInstance().getUser_name());
        requestMap.put("uid", UserEntity.getInstance().getMember_id());
        requestMap.put("page", mPageNum + "");
        requestMap.put("count", mPageSize + "");
        get(ProjectConstants.Url.ACCOUNT_COMMENT, requestMap, requstType);
    }
    
    private void requestGood(String id, int isPraise) {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		// 大于0取消赞，小于等于0赞
		if (isPraise == 0) {
			requestMap.put("praise_id", "0");
		} else {
			requestMap.put("praise_id", "1");
		}
		get(ProjectConstants.Url.GOOD_MB, requestMap, 1);
	}
}
