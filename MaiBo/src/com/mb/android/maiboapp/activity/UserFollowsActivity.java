package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.UserFollowAdapter;
import com.mb.android.maiboapp.adapter.UserFollowAdapter.ItemHandler;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserFollowResp;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.tandy.android.fw2.utils.DialogHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * Created by cgy on 15/8/31.
 */
public class UserFollowsActivity extends BaseSwipeBackActivity {
    private PullToRefreshListView mRefreshListView;
    private UserFollowAdapter mAdapter;
    private List<UserEntity> dataList = new ArrayList<UserEntity>();
    private String userId;
    private String userName;
    private int postion = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Helper.isNotEmpty(getIntent())) {
        	userId = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_USER_ID);
        	userName = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_USER_NAME);
		}else if (isNetworkAvailable) {
			userId = UserEntity.getInstance().getMember_id();
			userName = UserEntity.getInstance().getUser_name();
		}
        setCustomTitle("关注");
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

        mAdapter = new UserFollowAdapter(UserFollowsActivity.this,dataList);
		// 添加头部
		View headerView = new View(this);
		headerView.setMinimumHeight(20);
		if (Helper.isNotNull(headerView)) {
			mRefreshListView.addHeaderView(headerView);
		}
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.enableAutoRefreshFooter(true);
        mRefreshListView.getRefreshableView().setDividerHeight(2);
        mRefreshListView.setOnRefreshListener(mRefreshListener);
        mRefreshListView.setOnItemClickListener(mItemClickListener);
        mAdapter.setItemHandler(new ItemHandler() {
			
			@Override
			public void onFollow(int pos) {
				postion = pos;
				DialogHelper.showConfirmDialog(UserFollowsActivity.this, "取消关注","确定不再关注此人?", 
		    			true, R.string.dialog_positive, mPositiveListener, R.string.dialog_negative, null);
			}
		});
    }
    
    private DialogInterface.OnClickListener mPositiveListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			ProgressLoadingHelper.showLoadingDialog(UserFollowsActivity.this);
			UserEntity entity = dataList.get(postion);
			HashMap<String, String> requestMap = new HashMap<String, String>();
	        requestMap.put("user_name", entity.getUser_name());
	        requestMap.put("member_id", entity.getMember_id());
	        get(ProjectConstants.Url.FOLLOWS_DEL, requestMap, 1);
		}
	};

    @Override
    public boolean onResponseSuccess(int gact, String response,
                                     Object... extras) {
        if (super.onResponseSuccess(gact, response, extras)) {
            return true;
        }
        hideGlobalLoading();
        int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
        if (requestType == 1) {
        	ProgressLoadingHelper.dismissLoadingDialog();
			CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
			if (Helper.isNotEmpty(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					mRefreshListView.setRefreshing();
				}else {
					ToastHelper.showToast(entity.getResultMessage());
				}
				return true;
			}
		}
        UserFollowResp entity = JsonHelper.fromJson(response, UserFollowResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			List<UserEntity> list = entity.getData().getFriends();
			// 最后一页停止加载下一页
			if (entity.getResuleCode().equals("0") && list != null) {
				if (requestType == ProjectConstants.RequestType.HEAD) {
					if (Helper.isEmpty(list)) {
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
    	int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
    	if (requestType == 1) {
    		ProgressLoadingHelper.dismissLoadingDialog();
		}
    	if (requestType == ProjectConstants.RequestType.HEAD) {
    		hideGlobalLoading();
    		showGlobalError();
		}
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
			UserEntity entity = dataList.get(pos);
			Bundle bundle = new Bundle();
			bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID, entity.getMember_id());
			bundle.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, entity.getUser_name());
			NavigationHelper.startActivity(UserFollowsActivity.this, UserProfileActivity.class, bundle, false);
		}
	};
	
    private void requestData(int requestType) {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("member_id", userId);
        requestMap.put("user_name", userName);
        requestMap.put("page",mPageNum+"");
        requestMap.put("count",mPageSize+"");
        get(ProjectConstants.Url.ACCOUNT_FOLLOWS, requestMap, requestType);
    }

}
