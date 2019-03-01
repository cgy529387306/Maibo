package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.ArticleAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.ArticleEntity;
import com.mb.android.maiboapp.entity.ArticleResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

/**
 * 长脉搏列表
 * Created by cgy on 15/8/31.
 */
public class UserLongMbActivity extends BaseSwipeBackActivity {
	private PullToRefreshListView mRefreshListView;
    private ArticleAdapter mAdapter;
    private List<ArticleEntity> dataList;
    private int postion = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomTitle("文章");
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
        dataList = new ArrayList<ArticleEntity>();
        mAdapter = new ArticleAdapter(UserLongMbActivity.this,dataList);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.enableAutoRefreshFooter(true);
        mRefreshListView.getRefreshableView().setDividerHeight(20);
        mRefreshListView.setOnRefreshListener(mRefreshListener);
        mRefreshListView.setOnItemClickListener(mItemClickListener);
    }

    @Override
    public boolean onResponseSuccess(int gact, String response,
                                     Object... extras) {
        if (super.onResponseSuccess(gact, response, extras)) {
            return true;
        }
        hideGlobalLoading();
        int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
        ArticleResp entity = JsonHelper.fromJson(response, ArticleResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			List<ArticleEntity> list = entity.getData().getStatues();
			// 最后一页停止加载下一页
			if (entity.getResuleCode().equals("0")) {
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
	
	/**
	 * 列表点击事件监听者
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int pos = position - mRefreshListView.getRefreshableView().getHeaderViewsCount();
			Bundle bundle=new Bundle();
			bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, ProjectConstants.Url.URL_ARTICLE_DETAIL+dataList.get(pos).getId());
			bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, false);
			NavigationHelper.startActivity(UserLongMbActivity.this,
					BaseWebViewActivity.class, bundle, false);
		}
	};

    private void requestData(int requstType) {
    	String userId = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_USER_ID);
    	String userName = getIntent().getStringExtra(ProjectConstants.BundleExtra.KEY_USER_NAME);
        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("user_name", userName);
        requestMap.put("uid", userId);
        requestMap.put("page", mPageNum + "");
        requestMap.put("count", mPageSize + "");
        get(ProjectConstants.Url.URL_ARTICLE, requestMap, requstType);
    }
    
}
