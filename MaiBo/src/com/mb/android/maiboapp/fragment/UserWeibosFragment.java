package com.mb.android.maiboapp.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.MBCommentActivtiy;
import com.mb.android.maiboapp.activity.MBRepostActivtiy;
import com.mb.android.maiboapp.activity.UserProfileActivity;
import com.mb.android.maiboapp.activity.WeiboDetailActivity;
import com.mb.android.maiboapp.adapter.WeiboAdapter;
import com.mb.android.maiboapp.adapter.WeiboAdapter.ItemHandler;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserMbResp;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.impl.ScrollStateImpl;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 我的脉搏页面
 */
public class UserWeibosFragment extends BaseNetFragment {
	    private PullToRefreshListView mRefreshListView;
	    private WeiboAdapter mAdapter;
	    private List<WeiboEntity> dataList;
	    private String userId = "";
	    private String userName = "";
	    @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.common_listview, container, false);
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			if (getActivity() instanceof UserProfileActivity) {
	    		userId = ((UserProfileActivity)getActivity()).getUserId();
	    		userName = ((UserProfileActivity)getActivity()).getUserName();
			}else {
				userId = UserEntity.getInstance().getMember_id();
				userId = UserEntity.getInstance().getUser_name();
			}
			initUI(view);
		    showGlobalLoading();
		    requestData(ProjectConstants.RequestType.HEAD);
		}

	    private void initUI(View view) {
	        mRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lsv_common);
	        dataList = new ArrayList<WeiboEntity>();
	        mAdapter = new WeiboAdapter(getActivity(),dataList,1);
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
					NavigationHelper.startActivity(getActivity(),
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
					NavigationHelper.startActivity(getActivity(),
							MBCommentActivtiy.class, bundle, false);
				}
				
				@Override
				public void onDelete(int pos) {
					
				}
			});
	        mRefreshListView.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (firstVisibleItem!=0 && visibleItemCount == totalItemCount - firstVisibleItem) {
						((UserProfileActivity) getActivity()).onHide();;
					} else {
						((UserProfileActivity) getActivity()).onShow();
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
				CommonEntity entity = JsonHelper.fromJson(response,
						CommonEntity.class);
				if (Helper.isNotNull(entity)) {
					ToastHelper.showToast(entity.getResultMessage());
					return true;
				}
			}
	        UserMbResp entity = JsonHelper.fromJson(response, UserMbResp.class);
			if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
				List<WeiboEntity> list = entity.getData().getStatues();
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
				NavigationHelper.startActivity(getActivity(),
						WeiboDetailActivity.class, bundle, false);
			}
		};

	    private void requestData(int requstType) {
	        HashMap<String, String> requestMap = new HashMap<String, String>();
	        if (Helper.isNotEmpty(userId)) {
	        	requestMap.put("member_id", userId);
			}
	        requestMap.put("user_name", userName);
	        requestMap.put("page", mPageNum + "");
	        requestMap.put("count", mPageSize + "");
	        get(ProjectConstants.Url.ACCOUNT_MB, requestMap, requstType);
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
