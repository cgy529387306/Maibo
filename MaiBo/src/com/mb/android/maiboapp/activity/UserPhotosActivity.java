package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.UserPhotoAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.PhotoResp;
import com.mb.android.maiboapp.entity.PhotosEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * Created by cgy on 15/8/31.
 */
public class UserPhotosActivity extends BaseSwipeBackActivity {
    private PullToRefreshGridView mRefreshListView;
    private UserPhotoAdapter mAdapter;
    private List<PhotosEntity> dataList = new ArrayList<PhotosEntity>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomTitle("相册");
        setContentView(R.layout.common_gridview);
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
        mAdapter = new UserPhotoAdapter(UserPhotosActivity.this,dataList);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(mRefreshListener);
        mRefreshListView.setOnItemClickListener(mItemClickListener);
        initIndicator();
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
				if (Helper.isEmpty(list) || list.size()<mPageSize) {
					
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
        requestMap.put("member_id", UserEntity.getInstance().getMember_id());
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
