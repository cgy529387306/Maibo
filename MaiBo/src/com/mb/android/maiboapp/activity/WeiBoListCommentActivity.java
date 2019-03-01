package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.WeiboAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.HomeMbResp;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 脉搏列表
 */
public class WeiBoListCommentActivity extends BaseSwipeBackActivity {

	private PullToRefreshListView mRefreshListView;
	private WeiboAdapter mAdapter;
	private List<WeiboEntity> dataList;
	private TextView tv_title, tv_menu_center, tv_menu_right;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo_list);
		setCustomTitle("评论列表");
		initUI();
		requestData();
	}

	private void initUI() {
		mRefreshListView = findView(R.id.lsv_common);
		dataList = new ArrayList<WeiboEntity>();
		mAdapter = new WeiboAdapter(WeiBoListCommentActivity.this, dataList,1);
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
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (requestType == 1) {
			CommonEntity entity = JsonHelper.fromJson(response,
					CommonEntity.class);
			if (Helper.isNotNull(entity)) {
				ToastHelper.showToast(entity.getResultMessage());
				if ("0".equals(entity.getResuleCode())) {
					ToastHelper.showToast("赞成功");
				}
				return true;
			}
		}
		HomeMbResp entity = JsonHelper.fromJson(response, HomeMbResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			// 刷新
			if (requestType == ProjectConstants.RequestType.HEAD) {
				dataList.clear();
			}
			List<WeiboEntity> list = entity.getData().getStatues();
			// 最后一页停止加载下一页
			if (entity.getResuleCode().equals("0") && list != null) {

				if (Helper.isEmpty(list) && list.size() < mPageSize) {
					mRefreshListView.enableAutoRefreshFooter(false);
				}
				dataList.addAll(list);
				mAdapter.notifyDataSetChanged();
				mRefreshListView.onRefreshComplete();
			} else {
				ToastHelper.showToast(entity.getResultMessage());
			}
		}
		return true;
	}

	@Override
	public boolean onResponseError(int gact, String response,
			VolleyError error, Object... extras) {
		return true;
	}

	/**
	 * 下拉刷新，上拉加载
	 */
	private PullToRefreshListView.OnRefreshListener mRefreshListener = new PullToRefreshListView.OnRefreshListener() {

		@Override
		public void onRefresh() {
			new Handler().postDelayed(new Runnable() {

				public void run() {
					mRefreshListView.onRefreshComplete();
				}

			}, 2000);
		}

		@Override
		public void onLoadMore() {
		}
	};

	/**
	 * 列表点击事件监听者
	 */
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			NavigationHelper.startActivity(WeiBoListCommentActivity.this,
					WeiboDetailActivity.class, null, false);
		}
	};

	private void requestData() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("since_id", ProjectHelper.getSinceId(dataList) + "");
		requestMap.put("page", mPageNum + "");
		requestMap.put("count", mPageSize + "");
		get(ProjectConstants.Url.HOME_MBS, requestMap, 0);
	}
}
