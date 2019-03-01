package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.UserFansAdapter;
import com.mb.android.maiboapp.adapter.UserFansAdapter.ItemHandler;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.db.GroupDao;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.GroupEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserFollowResp;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * Created by cgy on 15/8/31.
 */
public class UserFriendsActivity extends BaseSwipeBackActivity {
    private PullToRefreshListView mRefreshListView;
    private UserFansAdapter mAdapter;
    private List<UserEntity> dataList = new ArrayList<UserEntity>();
    private List<GroupEntity> groupList = new ArrayList<GroupEntity>();
    private String userId;
    private String userName;
    private GroupDao groupDao;
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
        groupDao = new GroupDao();
        groupList = groupDao.getList();
        setCustomTitle("新的好友");
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

        mAdapter = new UserFansAdapter(UserFriendsActivity.this,dataList);
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
				UserEntity entity = dataList.get(pos);
				if (entity.getFollow_state() == 2) {
					showGroup(entity);
				}else if (entity.getFollow_state() == 3) {
					ProgressLoadingHelper.showLoadingDialog(UserFriendsActivity.this);
					HashMap<String, String> requestMap = new HashMap<String, String>();
			        requestMap.put("user_name", entity.getUser_name());
			        requestMap.put("member_id", entity.getMember_id());
				    get(ProjectConstants.Url.FOLLOWS_DEL, requestMap, 1);
				}
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
        	ProgressLoadingHelper.dismissLoadingDialog();
			CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
			if (Helper.isNotEmpty(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					mRefreshListView.setRefreshing();
				}
				ToastHelper.showToast(entity.getResultMessage());
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
    	if (requestType == 1) {
    		ProgressLoadingHelper.dismissLoadingDialog();
    		ToastHelper.showToast("关注失败");
		}
    	if (requestType == ProjectConstants.RequestType.HEAD) {
    		hideGlobalLoading();
    		showGlobalError();
		}
    	return true;
    }
    

    private void requestData(int requestType) {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("user_name", userName);
        requestMap.put("member_id", userId);
        requestMap.put("page", mPageNum + "");
        requestMap.put("count", mPageSize + "");
        get(ProjectConstants.Url.ACCOUNT_NEW_FRIENDS, requestMap, requestType);
    }

	private void showGroup(final UserEntity entity){
		if (Helper.isEmpty(groupList)) {
			return;
		}
  		ListView listView = new ListView(this);//this为获取当前的上下文  
  		listView.setFadingEdgeLength(0);  
  		List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();//建立一个数组存储listview上显示的数据  
  		for (int m = 0; m < groupList.size(); m++) {//initData为一个list类型的数据源  
  		    Map<String, String> nameMap = new HashMap<String, String>();  
  		    nameMap.put("name", groupList.get(m).getName());  
  		    nameList.add(nameMap);  
  		}  
  		SimpleAdapter adapter = new SimpleAdapter(UserFriendsActivity.this,  
  		        nameList, android.R.layout.simple_list_item_1,  
  		        new String[] { "name" },  
  		        new int[] { android.R.id.text1 });  
  		listView.setAdapter(adapter);  
  		  
  		  
  		final AlertDialog dialog = new AlertDialog.Builder(this)  
  		        .setTitle("选择分组").setView(listView)//在这里把写好的这个listview的布局加载dialog中  
  		        .setNegativeButton("取消", new DialogInterface.OnClickListener() {  
  		  
  		            @Override  
  		            public void onClick(DialogInterface dialog, int which) {  
  		                // TODO Auto-generated method stub  
  		                dialog.cancel();  
  		            }  
  		        }).create();  
  		dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击  
  		dialog.show();  
  		listView.setOnItemClickListener(new OnItemClickListener() {//响应listview中的item的点击事件  
  		  
  		    @Override  
  		    public void onItemClick(AdapterView<?> arg0, View arg1, int position,  
  		            long arg3) {  
  		        dialog.cancel();  
  		        GroupEntity groupEntity = groupList.get(position);
				ProgressLoadingHelper.showLoadingDialog(UserFriendsActivity.this);
				HashMap<String, String> requestMap = new HashMap<String, String>();
		        requestMap.put("user_name", entity.getUser_name());
		        requestMap.put("member_id", entity.getMember_id());
		        requestMap.put("groupid", groupEntity.getId());
				get(ProjectConstants.Url.FOLLOWS_ADD, requestMap, 1);
  		    }  
  		}); 
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
			NavigationHelper.startActivity(UserFriendsActivity.this, UserProfileActivity.class, bundle, false);
		}
	};
}
