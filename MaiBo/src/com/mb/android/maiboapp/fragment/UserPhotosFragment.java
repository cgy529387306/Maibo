package com.mb.android.maiboapp.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserProfileActivity;
import com.mb.android.maiboapp.adapter.UserPhotoAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.PhotoResp;
import com.mb.android.maiboapp.entity.PhotosEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

/**
 * 我的相册页面
 */
public class UserPhotosFragment extends BaseNetFragment {

	private PullToRefreshGridView mRefreshListView;
	private UserPhotoAdapter mAdapter;
	private List<PhotosEntity> dataList = new ArrayList<PhotosEntity>();
	private String userId = "";
	private String userName = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_gridview, container, false);
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
        mRefreshListView = findView(R.id.ptr_grid);
        mAdapter = new UserPhotoAdapter(getActivity(),dataList);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(mRefreshListener);
        mRefreshListView.setOnItemClickListener(mItemClickListener);
        initIndicator();
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
        PhotoResp entity = JsonHelper.fromJson(response, PhotoResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			List<PhotosEntity> list = entity.getData().getPhotos();
			// 最后一页停止加载下一页
			if (entity.getResuleCode().equals("0") && list != null) {
				if (requestType == ProjectConstants.RequestType.HEAD) {
					if (Helper.isEmpty(list)) {
						showGlobalEmpty();
					}
					dataList.clear();
				}
				if (Helper.isEmpty(list) && list.size()<mPageSize) {
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
    	return true;
    }
    

    private PullToRefreshBase.OnRefreshListener2<GridView> mRefreshListener = new PullToRefreshBase.OnRefreshListener2<GridView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
        	mPageNum = 0;
			requestData(ProjectConstants.RequestType.HEAD);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
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
//			int pos = position - mRefreshListView.getRefreshableView().getHeaderViewsCount();
//			UserEntity entity = dataList.get(pos);
//			Bundle bundle = new Bundle();
//			bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID, entity.getMember_id());
//			NavigationHelper.startActivity(UserFansActivity.this, UserProfileActivity.class, bundle, false);
		}
	};

    private void requestData(int requestType) {
        HashMap<String, String> requestMap = new HashMap<String, String>();
        if (Helper.isNotEmpty(userId)) {
        	requestMap.put("member_id", userId);
		}
        requestMap.put("user_name", userName);
        requestMap.put("page", mPageNum + "");
        requestMap.put("count", mPageSize + "");
        get(ProjectConstants.Url.ACCOUNT_PHOTOS, requestMap, requestType);
    }
    
    private void initIndicator() {
		ILoadingLayout startLabels = mRefreshListView.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel(getResources().getString(R.string.pull_to_refresh_pull_label));// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel(getResources().getString(R.string.pull_to_refresh_refreshing_label));// 刷新时 
		startLabels.setReleaseLabel(getResources().getString(R.string.pull_to_refresh_release_label));// 下来达到一定距离时，显示的提示

		ILoadingLayout endLabels = mRefreshListView.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel(getResources().getString(R.string.bottom_more_pull));// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel(getResources().getString(R.string.bottom_more_refreshing));// 刷新时
		endLabels.setReleaseLabel(getResources().getString(R.string.bottom_more_refreshing));// 下来达到一定距离时，显示的提示
	} 
}
