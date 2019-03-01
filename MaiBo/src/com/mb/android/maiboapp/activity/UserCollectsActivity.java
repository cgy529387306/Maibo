package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

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
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.tandy.android.fw2.utils.DialogHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 我的收藏
 * Created by cgy on 15/8/31.
 */
public class UserCollectsActivity extends BaseSwipeBackActivity {
	private PullToRefreshListView mRefreshListView;
    private WeiboAdapter mAdapter;
    private List<WeiboEntity> dataList;
    private int postion = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomTitle("收藏");
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
        mAdapter = new WeiboAdapter(UserCollectsActivity.this,dataList,1);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.enableAutoRefreshFooter(true);
        mRefreshListView.getRefreshableView().setDividerHeight(20);
        mRefreshListView.setOnRefreshListener(mRefreshListener);
        mRefreshListView.setOnItemClickListener(mItemClickListener);
        mRefreshListView.getRefreshableView().setOnItemLongClickListener(mItemLongClickListener);  
        mAdapter.setItemHandler(new ItemHandler() {
			private Bundle bundle = new Bundle();

			@Override
			public void onRepost(int pos) {

				bundle.putString("id", dataList.get(pos).getId());
				NavigationHelper.startActivity(UserCollectsActivity.this,
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
				NavigationHelper.startActivity(UserCollectsActivity.this,
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
        if (requestType == 5) {
			ProgressLoadingHelper.dismissLoadingDialog();
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
				if (Helper.isEmpty(list) && list.size()<mPageSize) {
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
    	int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
    	if (requestType == 5) {
			ProgressLoadingHelper.dismissLoadingDialog();
		}
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
	
	private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {  
  	  
        @Override  
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
                int pos, long arg3) {  
        	postion = pos - mRefreshListView.getRefreshableView().getHeaderViewsCount();
        	DialogHelper.showConfirmDialog(UserCollectsActivity.this, "取消收藏","确定取消收藏?", 
	    			true, R.string.dialog_positive, mPositiveListener, R.string.dialog_negative, null);
            return true;  
        }  
    };
    
    private DialogInterface.OnClickListener mPositiveListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			WeiboEntity entity = dataList.get(postion);
        	requestCollect(entity.getId());
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
			NavigationHelper.startActivity(UserCollectsActivity.this,
					WeiboDetailActivity.class, bundle, false);
		}
	};

    private void requestData(int requstType) {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("user_name", UserEntity.getInstance().getUser_name());
        requestMap.put("uid", UserEntity.getInstance().getMember_id());
        requestMap.put("page", mPageNum + "");
        requestMap.put("count", mPageSize + "");
        get(ProjectConstants.Url.ACCOUNT_COLLECT, requestMap, requstType);
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
    
    /**
	 * @param id
	 * @param type  add代表收藏，del，代表删除
	 */
	private void requestCollect(String id) {
		ProgressLoadingHelper.showLoadingDialog(this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		requestMap.put("type", "del");
		get(ProjectConstants.Url.COLLECT_MB, requestMap, 5);
	}
}
