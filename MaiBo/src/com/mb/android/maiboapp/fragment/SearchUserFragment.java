package com.mb.android.maiboapp.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserProfileActivity;
import com.mb.android.maiboapp.adapter.UserFriendAdapter;
import com.mb.android.maiboapp.adapter.UserFriendAdapter.ItemHandler;
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

public class SearchUserFragment extends BaseNetFragment {
	private PullToRefreshListView mRefreshListView;
	private UserFriendAdapter mAdapter;
	private List<UserEntity> dataList = new ArrayList<UserEntity>();
	private String searchKey = "";
	private GroupDao groupDao;
	private List<GroupEntity> groupList = new ArrayList<GroupEntity>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.common_listview, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		groupDao = new GroupDao();
		groupList = groupDao.getList();
		initUI();
		requestData(ProjectConstants.RequestType.HEAD);
	}
	
	@Override
	public void globalReload() {
		super.globalReload();
		requestData(ProjectConstants.RequestType.HEAD);
	}
	
	public void reload(){
		requestData(ProjectConstants.RequestType.HEAD);
	}

	private void initUI() {
		mRefreshListView = findView(R.id.lsv_common);

		mAdapter = new UserFriendAdapter(getActivity(), dataList);

		mRefreshListView.setAdapter(mAdapter);
		mRefreshListView.enableAutoRefreshFooter(true);
		mRefreshListView.getRefreshableView().setDividerHeight(2);
		mRefreshListView.setOnRefreshListener(mRefreshListener);
		mRefreshListView.setOnItemClickListener(mItemClickListener);
		mAdapter.setItemHandler(new ItemHandler() {

			@Override
			public void onFollow(int pos) {
				UserEntity entity = dataList.get(pos);
				showGroup(entity);
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
	    	if (requestType == ProjectConstants.RequestType.HEAD) {
				showGlobalLoading();
			}
	    	if (getParentFragment() instanceof SearchFragment) {
				searchKey = ((SearchFragment)getParentFragment()).searchKey;
			}
	        HashMap<String, String> requestMap = new HashMap<String, String>();
	        requestMap.put("k", searchKey);
	        requestMap.put("page", mPageNum + "");
	        requestMap.put("count", mPageSize + "");
	        get(ProjectConstants.Url.SEARCH_USER, requestMap, requestType);
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
	  	
	  	private void showGroup(final UserEntity entity){
	  		ListView listView = new ListView(getActivity());//this为获取当前的上下文  
	  		listView.setFadingEdgeLength(0);  
	  		  
	  		List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();//建立一个数组存储listview上显示的数据  
	  		for (int m = 0; m < groupList.size(); m++) {//initData为一个list类型的数据源  
	  		    Map<String, String> nameMap = new HashMap<String, String>();  
	  		    nameMap.put("name", groupList.get(m).getName());  
	  		    nameList.add(nameMap);  
	  		}  
	  		SimpleAdapter adapter = new SimpleAdapter(getActivity(),  
	  		        nameList, android.R.layout.simple_list_item_1,  
	  		        new String[] { "name" },  
	  		        new int[] { android.R.id.text1 });  
	  		listView.setAdapter(adapter);  
	  		  
	  		  
	  		final AlertDialog dialog = new AlertDialog.Builder(getActivity())  
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
					ProgressLoadingHelper.showLoadingDialog(getActivity());
					HashMap<String, String> requestMap = new HashMap<String, String>();
			        requestMap.put("user_name", entity.getUser_name());
			        requestMap.put("member_id", entity.getMember_id());
			        requestMap.put("groupid", groupEntity.getId());
					get(ProjectConstants.Url.FOLLOWS_ADD, requestMap, 1);
	  		    }  
	  		}); 
	  	}
	  	
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
				NavigationHelper.startActivity(getActivity(), UserProfileActivity.class, bundle, false);
			}
		};

}
