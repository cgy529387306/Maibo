package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.MsgGoodsAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.MessageGoodsResp;
import com.mb.android.maiboapp.entity.MessageGoodsResp.MsgGoodEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;

/**
 * 我的赞 Created by cgy on 15/8/31.
 */
public class MessageGoodsActivity extends BaseSwipeBackActivity {
	private PullToRefreshListView mRefreshListView;
	private MsgGoodsAdapter mAdapter;
	private List<MsgGoodEntity> dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("赞");
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
		dataList = new ArrayList<MsgGoodEntity>();
		mAdapter = new MsgGoodsAdapter(MessageGoodsActivity.this, dataList);
		mRefreshListView.setAdapter(mAdapter);
		mRefreshListView.enableAutoRefreshFooter(true);
		mRefreshListView.getRefreshableView().setDividerHeight(20);
		mRefreshListView.setOnRefreshListener(mRefreshListener);
	}

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		hideGlobalLoading();
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		MessageGoodsResp entity = JsonHelper.fromJson(response, MessageGoodsResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotEmpty(entity.getData())) {
			List<MsgGoodEntity> list = entity.getData().getMsgGoods();
			if (entity.getResuleCode().equals("0") && list != null) {
				if (requestType == ProjectConstants.RequestType.HEAD) {
					if (Helper.isEmpty(list)) {
						showGlobalEmpty();
					}
					dataList.clear();
				}
				if (Helper.isEmpty(list) || list.size() < mPageSize) {
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
	
	private void requestData(int requstType) {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("user_name", UserEntity.getInstance().getUser_name());
		requestMap.put("uid", UserEntity.getInstance().getMember_id());
		requestMap.put("page", mPageNum + "");
		requestMap.put("count", mPageSize + "");
		get(ProjectConstants.Url.MESSAGE_GOOD, requestMap, requstType);
	}
}
