package com.mb.android.maiboapp.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.MBCommentActivtiy;
import com.mb.android.maiboapp.activity.MBRepostActivtiy;
import com.mb.android.maiboapp.activity.WeiboDetailActivity;
import com.mb.android.maiboapp.adapter.HeadWheelAdapter;
import com.mb.android.maiboapp.adapter.WeiboAdapter;
import com.mb.android.maiboapp.adapter.WeiboAdapter.ItemHandler;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.RecommendResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserMbResp;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.OperateDialogHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.widget.WheelView;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

public class SearchWeibosFragment extends BaseNetFragment{
	private PullToRefreshListView mRefreshListView;
    private WeiboAdapter mAdapter;
    private List<WeiboEntity> dataList;
    private String searchKey = "";
    private WheelView mWheelView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.common_listview, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initUI();
		requestAds();
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
	        dataList = new ArrayList<WeiboEntity>();
	        mAdapter = new WeiboAdapter(getActivity(),dataList,0);
	        mRefreshListView.addHeaderView(obtainHeadView());
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
					final WeiboEntity entity = dataList.get(pos);
					if (Helper.isNotEmpty(entity.getMember().getMember_id())&&entity.getMember().getMember_id()
							.equals(UserEntity.getInstance().getMember_id())) {
						OperateDialogHelper.showLoadingDialog(getActivity(), "删除",new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								requestDelete(entity.getId());
								OperateDialogHelper.dismissLoadingDialog();
							}
						});
					}else {
						String operateStr = "";
						if (entity.isFavorite()) {
							operateStr = "取消收藏";
						}else {
							operateStr = "收藏";
						}
						OperateDialogHelper.showLoadingDialog(getActivity(), operateStr,new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if (entity.isFavorite()) {
									requestCollect(entity.getId(),"del");
								}else {
									requestCollect(entity.getId(),"add");
								}
								OperateDialogHelper.dismissLoadingDialog();
							}
						});
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
	        int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
	        if (requestType == 1) {
				CommonEntity entity = JsonHelper.fromJson(response,
						CommonEntity.class);
				if (Helper.isNotNull(entity)) {
					if ("0".equals(entity.getResuleCode())) {
						ToastHelper.showToast(entity.getResultMessage());
					}
					return true;
				}
			}
	        if (requestType == 4) {
				RecommendResp entity = JsonHelper.fromJson(response,RecommendResp.class);
				if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
					mWheelView.initWheel(entity.getData(), new HeadWheelAdapter(getActivity(), entity.getData()));
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
			if (requestType == 3) {
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
	        hideGlobalLoading();
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
			if (requestType == ProjectConstants.RequestType.HEAD) {
				hideGlobalLoading();
				showGlobalError();
			}
			if (requestType == 3 || requestType == 5) {
				ProgressLoadingHelper.dismissLoadingDialog();
				ToastHelper.showToast("操作失败");
			}
			return true;
	    }
	    
	    /**
		 * 获取头部View
		 * @return
		 */
		private View obtainHeadView() {
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_head_wheel, null);
			mWheelView = (WheelView) view.findViewById(R.id.wheelView);
			mWheelView.setListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mWheelView.setVisibility(View.GONE);
				}
			});
			return view;
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
	    	if (requstType == ProjectConstants.RequestType.HEAD) {
				showGlobalLoading();
			}
	    	if (getParentFragment() instanceof SearchFragment) {
				searchKey = ((SearchFragment)getParentFragment()).searchKey;
			}
	        HashMap<String, String> requestMap = new HashMap<String, String>();
	        requestMap.put("k",searchKey);
	        requestMap.put("page", mPageNum + "");
	        requestMap.put("count", mPageSize + "");
	        get(ProjectConstants.Url.SEARCH_MB, requestMap, requstType);
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
	    
	    private void requestDelete(String id) {
			ProgressLoadingHelper.showLoadingDialog(getActivity(), "删除中...");
			HashMap<String, String> requestMap = new HashMap<String, String>();
			requestMap.put("id", id);
			get(ProjectConstants.Url.DELETE_MB, requestMap, 3);
		}
		
		/**
		 * @param id
		 * @param type  add代表收藏，del，代表删除
		 */
		private void requestCollect(String id,String type) {
			ProgressLoadingHelper.showLoadingDialog(getActivity());
			HashMap<String, String> requestMap = new HashMap<String, String>();
			requestMap.put("id", id);
			requestMap.put("type", type);
			get(ProjectConstants.Url.COLLECT_MB, requestMap, 5);
		}
	    
	    private void requestAds() {
			HashMap<String, String> requestMap = new HashMap<String, String>();
			requestMap.put("type", "3");
			get(ProjectConstants.Url.COMMON_ADS, requestMap, 4);
		}
}
