package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView.OnRefreshListener;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.CommentsAdapter;
import com.mb.android.maiboapp.adapter.GoodsAdapter;
import com.mb.android.maiboapp.adapter.RepostAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommentsEntity;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.HomeMbResp;
import com.mb.android.maiboapp.entity.MbCommentsResp;
import com.mb.android.maiboapp.entity.MbDetailResp;
import com.mb.android.maiboapp.entity.MbGoodsResp;
import com.mb.android.maiboapp.entity.PhotosEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.TextLinkUtil;
import com.mb.android.maiboapp.utils.TimeUtil;
import com.mb.android.widget.GridListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 脉搏详情
 * 
 * @author cgy
 * 
 */
public class WeiboDetailActivity extends BaseSwipeBackActivity {
	private TextView txv_weibo_detail_repost, txv_weibo_detail_comment,
			txv_weibo_detail_good;
	private TextView txv_repost, txv_comment, txv_good,txv_empty_hint;
	private TextView txv_weibo_content, txv_weibo_time, txv_user_name;// 内容，时间，用户名
	private View line_detail_repost,line_detail_comment,line_detail_good;
	private ImageView imv_user_avater;// 头像
	private ImageView imv_weibo_collect;// 收藏？
	private ImageView imv_weibo_good;
	private LinearLayout lin_bottom_btn;
	private GridListView grd_item;
	private FrameLayout frame_relay;
	private PullToRefreshListView mRefreshListView;
	private RelativeLayout tabView;
	private View stickyView;
	private View topView;

	private List<WeiboEntity> list_repost;
	private List<CommentsEntity> list_comments;
	private List<UserEntity> list_goods;
	private List<WeiboEntity> data_repost;
	private List<CommentsEntity> data_comments;
	private List<UserEntity> data_goods;

	private CommentsAdapter coommentsAdapter;
	private GoodsAdapter goodsAdapter;
	private RepostAdapter repostAdapter;
	private WeiboEntity weibo;
	private MbDetailResp resp;
	private HomeMbResp resp_repost;
	private MbCommentsResp resp_comments;
	private MbGoodsResp resp_goods;

	private String id;
	private int type = 2;// 当前列表类型 2评论 1转发 3赞

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo_detail);
		setCustomTitle("脉搏正文");
		id = getIntent().getExtras().get("id").toString();
		initHeader();
		showGlobalLoading();
		requestData();
		initViews();
		showComment();
	}

	private void initHeader() {
		stickyView = View.inflate(this, R.layout.view_weibo_head_tab, null);
		topView = View.inflate(this, R.layout.view_weibo_head, null);
		// 脉搏操作
		txv_repost = (TextView) stickyView.findViewById(R.id.txv_repost);
		txv_comment = (TextView) stickyView.findViewById(R.id.txv_comment);
		txv_good = (TextView) stickyView.findViewById(R.id.txv_good);
		txv_empty_hint = (TextView) stickyView.findViewById(R.id.txv_empty_hint);
		line_detail_repost = stickyView.findViewById(R.id.line_detail_repost);
		line_detail_comment = stickyView.findViewById(R.id.line_detail_comment);
		line_detail_good = stickyView.findViewById(R.id.line_detail_good);
		txv_repost.setOnClickListener(mClickListener);
		txv_comment.setOnClickListener(mClickListener);
		txv_good.setOnClickListener(mClickListener);
		// 脉搏内容
		grd_item = (GridListView) topView.findViewById(R.id.grd_item);
		frame_relay = (FrameLayout) topView.findViewById(R.id.frame_relay);
		txv_user_name = (TextView) topView.findViewById(R.id.txv_user_name);
		txv_weibo_time = (TextView) topView.findViewById(R.id.txv_weibo_time);
		txv_weibo_content = (TextView) topView
				.findViewById(R.id.txv_weibo_content);
		imv_user_avater = (ImageView) topView
				.findViewById(R.id.imv_user_avater);
		imv_user_avater.setOnClickListener(mClickListener);
		imv_weibo_collect = (ImageView) topView
				.findViewById(R.id.imv_weibo_collect);
		imv_weibo_good = (ImageView) findViewById(R.id.imv_weibo_good);
		lin_bottom_btn = (LinearLayout) findViewById(R.id.lin_bottom_btn);
		imv_weibo_collect.setImageResource(R.drawable.ic_weibo_collect);
		imv_weibo_collect.setOnClickListener(mClickListener);
		txv_weibo_content.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                CharSequence text = ((TextView) v).getText();
                Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
                TextView widget = (TextView) v;
                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP ||
                        action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);

                    ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);

                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget);
                        }
                        ret = true;
                    }
                }
                return ret;
            }
        });
	}

	@SuppressLint("NewApi")
	private void initViews() {

		LinearLayout lin_weibo_repost = findView(R.id.lin_weibo_repost);
		LinearLayout lin_weibo_comment = findView(R.id.lin_weibo_comment);
		LinearLayout lin_weibo_good = findView(R.id.lin_weibo_good);

		tabView = (RelativeLayout) findViewById(R.id.tabView);
		lin_weibo_repost.setOnClickListener(mClickListener);
		lin_weibo_comment.setOnClickListener(mClickListener);
		lin_weibo_good.setOnClickListener(mClickListener);

		txv_weibo_detail_repost = findView(R.id.txv_weibo_detail_repost);
		txv_weibo_detail_comment = findView(R.id.txv_weibo_detail_comment);
		txv_weibo_detail_good = findView(R.id.txv_weibo_detail_good);

		txv_weibo_detail_repost.setOnClickListener(mClickListener);
		txv_weibo_detail_comment.setOnClickListener(mClickListener);
		txv_weibo_detail_good.setOnClickListener(mClickListener);

		imv_weibo_good.setOnClickListener(mClickListener);

		mRefreshListView = findView(R.id.lsv_common);

		list_goods = new ArrayList<UserEntity>();
		list_repost = new ArrayList<WeiboEntity>();
		list_comments = new ArrayList<CommentsEntity>();
		data_goods = new ArrayList<UserEntity>();
		data_repost = new ArrayList<WeiboEntity>();
		data_comments = new ArrayList<CommentsEntity>();

		coommentsAdapter = new CommentsAdapter(data_comments, this);
		mRefreshListView.setAdapter(coommentsAdapter);
		mRefreshListView.enableAutoRefreshFooter(true);
		mRefreshListView.getRefreshableView().setDividerHeight(2);
		mRefreshListView.setOnRefreshListener(mRefreshListener);
		mRefreshListView.setOnItemClickListener(mItemClickListener);
		mRefreshListView.addHeaderView(topView);
		mRefreshListView.addHeaderView(stickyView);
		mRefreshListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem >= mRefreshListView.getRefreshableView()
						.getHeaderViewsCount() - 1) {
					tabView.setVisibility(View.VISIBLE);
				} else {
					tabView.setVisibility(View.GONE);
				}
				if (firstVisibleItem!=0 && visibleItemCount == totalItemCount - firstVisibleItem) {
					if (lin_bottom_btn.getVisibility() == View.VISIBLE) {
						Animation am = AnimationUtils.loadAnimation(
								WeiboDetailActivity.this, R.anim.out_to_down);
						lin_bottom_btn.startAnimation(am);
						lin_bottom_btn.setVisibility(View.GONE);
					}
				} else {
					if (lin_bottom_btn.getVisibility() == View.GONE) {
						Animation am = AnimationUtils.loadAnimation(
								WeiboDetailActivity.this, R.anim.in_from_down);
						lin_bottom_btn.startAnimation(am);
						lin_bottom_btn.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lin_weibo_repost:
				repostAction();
				break;
			case R.id.lin_weibo_comment:
				commentsAction();
				break;
			case R.id.lin_weibo_good:
				goodsAction();
				break;
			case R.id.imv_user_avater:
				Bundle bundle = new Bundle();
				bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID, resp.getData().getMember().getMember_id());
				bundle.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, resp.getData().getMember().getUser_name());
				NavigationHelper.startActivity(WeiboDetailActivity.this, UserProfileActivity.class, bundle, false);
				break;
			case R.id.txv_weibo_detail_repost:
				showRepost();
				break;
			case R.id.txv_weibo_detail_comment:
				showComment();
				break;
			case R.id.txv_weibo_detail_good:
				showGood();
				break;
			case R.id.txv_repost:
				showRepost();
				break;
			case R.id.txv_comment:
				showComment();
				break;
			case R.id.txv_good:
				showGood();
				break;
			case R.id.imv_weibo_collect:
				if (weibo.isFavorite()) {
					requestCollect("del");
				}else {
					requestCollect("add");
				}
				break;
			default:
				break;
			}
		}

	};

	private void repostAction() {
		Bundle bundle = new Bundle();
		bundle.putString("id", id);
		NavigationHelper.startActivity(WeiboDetailActivity.this,
				MBRepostActivtiy.class, bundle, false);
	}

	private void commentsAction() {
		Bundle bundle = new Bundle();
		bundle.putString("id", id);
		NavigationHelper.startActivity(WeiboDetailActivity.this,
				MBCommentActivtiy.class, bundle, false);
	}

	private void goodsAction() {
		ProjectHelper.startScaleAnimation(imv_weibo_good);
		requestGood(id);
		int num = Integer.parseInt(txv_good.getTag().toString());
		boolean isPraise = (Boolean) imv_weibo_good.getTag();
		if (isPraise) {
			imv_weibo_good.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_weibo_good));
			txv_good.setText("赞 " + (num - 1) + "");
			txv_good.setTag(num - 1);
			txv_weibo_detail_good.setText("赞 " + (num - 1) + "");
			imv_weibo_good.setTag(false);
		} else {
			imv_weibo_good.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_weibo_good_selected));
			txv_good.setText("赞 " + (num + 1) + "");
			txv_good.setTag(num + 1);
			txv_weibo_detail_good.setText("赞 " + (num + 1) + "");
			imv_weibo_good.setTag(true);
		}
	}

	private void showRepost() {
		txv_weibo_detail_repost.setSelected(true);
		txv_weibo_detail_comment.setSelected(false);
		txv_weibo_detail_good.setSelected(false);
		txv_repost.setSelected(true);
		txv_comment.setSelected(false);
		txv_good.setSelected(false);
		line_detail_repost.setVisibility(View.VISIBLE);
		line_detail_comment.setVisibility(View.GONE);
		line_detail_good.setVisibility(View.GONE);
		mPageNum = 0;
		type = 1;
		requestRepost();
	}

	private void showComment() {
		txv_weibo_detail_repost.setSelected(false);
		txv_weibo_detail_comment.setSelected(true);
		txv_weibo_detail_good.setSelected(false);
		txv_repost.setSelected(false);
		txv_comment.setSelected(true);
		txv_good.setSelected(false);
		line_detail_repost.setVisibility(View.GONE);
		line_detail_comment.setVisibility(View.VISIBLE);
		line_detail_good.setVisibility(View.GONE);
		mPageNum = 0;
		type = 2;
		requestComment();
	}

	private void showGood() {
		txv_weibo_detail_repost.setSelected(false);
		txv_weibo_detail_comment.setSelected(false);
		txv_weibo_detail_good.setSelected(true);
		txv_repost.setSelected(false);
		txv_comment.setSelected(false);
		txv_good.setSelected(true);
		line_detail_repost.setVisibility(View.GONE);
		line_detail_comment.setVisibility(View.GONE);
		line_detail_good.setVisibility(View.VISIBLE);
		mPageNum = 0;
		type = 3;
		requestGood();
	}

	private void initDetail(MbDetailResp resp) {
		weibo = resp.getData();
		if (Helper.isNotEmpty(weibo)) {
			if ("0".equals(resp.getResuleCode())) {

				txv_comment.setText("评论 " + weibo.getComment_num());
				txv_good.setText("赞 " + weibo.getPraise_num());
				txv_good.setTag(weibo.getPraise_num());
				txv_repost.setText("转发 " + weibo.getRelay_num());
				
				txv_weibo_detail_comment
						.setText("评论 " + weibo.getComment_num());
				txv_weibo_detail_good.setText("赞 " + weibo.getPraise_num());
				txv_weibo_detail_repost.setText("转发 " + weibo.getRelay_num());

				txv_user_name.setText(weibo.getMember().getUser_name());
				txv_weibo_time.setText(TimeUtil.getTimeStr(weibo.getAdd_time()));
//				txv_weibo_content.setText(weibo.getContent());
				TextLinkUtil.handleMemoContent(txv_weibo_content,
						weibo.getContent(), WeiboDetailActivity.this);
				ImageLoader.getInstance().displayImage(
						weibo.getMember().getAvatar_hd(), imv_user_avater);
				List<PhotosEntity> photos = weibo.getPhotos();
				WeiboEntity relay = weibo.getRelay();
				if (relay != null && relay.getId() != null) {
					View view = LayoutInflater.from(this).inflate(
							R.layout.view_weibo_relay, null);
					frame_relay.addView(view);
					final String id = relay.getId();
					frame_relay.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Bundle bundle = new Bundle();
							bundle.putString("id", id);
							NavigationHelper.startActivity(
									WeiboDetailActivity.this,
									WeiboDetailActivity.class, bundle, false);
						}
					});
					TextView txv_weibo_content1 = (TextView) view
							.findViewById(R.id.txv_weibo_content1);
					GridListView grd_item1 = (GridListView) view
							.findViewById(R.id.grd_item1);
					txv_weibo_content1.setText("@"+relay.getMember().getUser_name()
							+ ": " + relay.getContent());
					TextLinkUtil.handleMemoContent(txv_weibo_content1,
							"@"+relay.getMember().getUser_name()
							+ ": " + relay.getContent(), WeiboDetailActivity.this);
					grd_item.setVisibility(View.GONE);
					List<PhotosEntity> photos2 = relay.getPhotos();
					grd_item1.setData(photos2);
				} else {
					if (photos != null && photos.size() > 0) {
						grd_item.setData(photos);
					} else {
						grd_item.setVisibility(View.GONE);
					}
				}
				// 设置赞
				if (weibo.getIsPraise() > 0) {
					imv_weibo_good.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_weibo_good_selected));
					imv_weibo_good.setTag(true);
				} else {
					imv_weibo_good.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_weibo_good));
					imv_weibo_good.setTag(false);
				}
				if (weibo.isFavorite()) {
					imv_weibo_collect.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_weibo_collect_selected));
				} else {
					imv_weibo_collect.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_weibo_collect));
				}

			} else {
				ToastHelper.showToast(resp.getResultMessage());
			}
		}
	}

	@Override
	public boolean onResponseSuccess(int gact, String response,
			Object... extras) {
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		hideGlobalLoading();
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
		switch (requestType) {
		case 0:
			resp = JsonHelper.fromJson(response, MbDetailResp.class);
			if (Helper.isNotEmpty(resp)) {
				if ("0".equals(resp.getResuleCode())) {
					initDetail(resp);
				}else {
					ToastHelper.showToast(resp.getResultMessage());
				}
			}
			break;
		case 1:
			mRefreshListView.onRefreshComplete();
			resp_repost = JsonHelper.fromJson(response, HomeMbResp.class);
			list_repost = resp_repost.getData().getStatues();
			if (list_repost == null) {
				list_repost = new ArrayList<WeiboEntity>();
			}
			if ("0".equals(entity.getResuleCode())) {
				if (repostAdapter == null) {
					repostAdapter = new RepostAdapter(data_repost,
							WeiboDetailActivity.this);
				}
				if (list_repost.size() < 20) {
					mRefreshListView.enableAutoRefreshFooter(false);
				}
				if (mPageNum == 0) {
					data_repost.clear();
				}
				data_repost.addAll(list_repost);
				if (Helper.isEmpty(data_repost)) {
					txv_empty_hint.setText("还没有人转发");
					txv_empty_hint.setVisibility(View.VISIBLE);
				}else {
					txv_empty_hint.setVisibility(View.GONE);
				}
				mRefreshListView.setAdapter(repostAdapter);
				repostAdapter.notifyDataSetChanged();
			} else {
				ToastHelper.showToast(entity.getResultMessage());
			}
			break;
		case 2:
			mRefreshListView.onRefreshComplete();
			resp_comments = JsonHelper.fromJson(response, MbCommentsResp.class);
			list_comments = resp_comments.getData().getComments();
			if (list_comments == null) {
				list_comments = new ArrayList<CommentsEntity>();
			}
			if ("0".equals(entity.getResuleCode())) {
				if (coommentsAdapter == null) {
					coommentsAdapter = new CommentsAdapter(data_comments,
							WeiboDetailActivity.this);
				}
				if (list_comments.size() < 20) {
					mRefreshListView.enableAutoRefreshFooter(false);
				}
				if (mPageNum == 0) {
					data_comments.clear();
				}
				data_comments.addAll(list_comments);
				if (Helper.isEmpty(data_comments)) {
					txv_empty_hint.setText("还没有人评论");
					txv_empty_hint.setVisibility(View.VISIBLE);
				}else {
					txv_empty_hint.setVisibility(View.GONE);
				}
				mRefreshListView.setAdapter(coommentsAdapter);
				coommentsAdapter.notifyDataSetChanged();
			} else {
				ToastHelper.showToast(entity.getResultMessage());
			}
			break;
		case 3:
			mRefreshListView.onRefreshComplete();
			resp_goods = JsonHelper.fromJson(response, MbGoodsResp.class);
			list_goods = resp_goods.getData().getMember();
			if (list_goods == null) {
				list_goods = new ArrayList<UserEntity>();
			}
			if ("0".equals(entity.getResuleCode())) {
				if (goodsAdapter == null) {
					goodsAdapter = new GoodsAdapter(WeiboDetailActivity.this,
							data_goods);
				}
				if (list_goods.size() < 20) {
					mRefreshListView.enableAutoRefreshFooter(false);
				}
				if (mPageNum == 0) {
					data_goods.clear();
				}
				data_goods.addAll(list_goods);
				if (Helper.isEmpty(data_goods)) {
					txv_empty_hint.setText("还没有人赞");
					txv_empty_hint.setVisibility(View.VISIBLE);
				}else {
					txv_empty_hint.setVisibility(View.GONE);
				}
				mRefreshListView.setAdapter(goodsAdapter);
				goodsAdapter.notifyDataSetChanged();
			} else {
				ToastHelper.showToast(entity.getResultMessage());
			}
			break;
		case 4:
			if (Helper.isNotNull(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					LocalBroadcastManager.getInstance(WeiboDetailActivity.this)
					.sendBroadcast(new Intent(ProjectConstants.BroadCastAction.REFRESH_MB_LIST));
				}
				ToastHelper.showToast(entity.getResultMessage());
				return true;
			}
			break;
		case 5:
			ProgressLoadingHelper.dismissLoadingDialog();
			if (Helper.isNotNull(entity)) {
				if ("0".equals(entity.getResuleCode())) {
					requestData();
					LocalBroadcastManager.getInstance(WeiboDetailActivity.this)
					.sendBroadcast(new Intent(ProjectConstants.BroadCastAction.REFRESH_MB_LIST));
				}
				ToastHelper.showToast(entity.getResultMessage());
				return true;
			}
			break;
		default:
			break;
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
		if (requestType == 4) {
			ToastHelper.showToast("赞失败");
		}
		if (requestType == 5) {
			ProgressLoadingHelper.dismissLoadingDialog();
		}
		return true;
	}

	private void requestData() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		get(ProjectConstants.Url.MB_DETAIL, requestMap, 0);
	}

	private void requestRepost() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		requestMap.put("page", mPageNum + "");
		requestMap.put("count", mPageSize + "");
		get(ProjectConstants.Url.MB_REPOSTS, requestMap, 1);
	}

	private void requestComment() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		requestMap.put("page", mPageNum + "");
		requestMap.put("count", mPageSize + "");
		get(ProjectConstants.Url.MB_COMMENTS, requestMap, 2);
	}

	private void requestGood() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		requestMap.put("page", mPageNum + "");
		requestMap.put("count", mPageSize + "");
		get(ProjectConstants.Url.MB_GOODS_LIST, requestMap, 3);
	}

	/**
	 * 脉搏点赞
	 * 
	 * @param id
	 */
	private void requestGood(String id) {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		requestMap.put("praise_id", "0");
		get(ProjectConstants.Url.GOOD_MB, requestMap, 4);
	}

	// /**
	// * 获取头部添加View
	// *
	// * @return
	// */
	// private View obtainHeaderView() {
	// View view =
	// LayoutInflater.from(WeiboDetailActivity.this).inflate(R.layout.header_weibo_detail,
	// null);
	// placeView = view.findViewById(R.id.placeView);
	// LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams)
	// placeView.getLayoutParams(); //取控件textView当前的布局参数
	// linearParams.height = topView.getMeasuredHeight();// 控件的高强制设成20
	// linearParams.width = AppHelper.getScreenWidth();
	// placeView.setLayoutParams(linearParams);
	// return view;
	// }

	/**
	 * 下拉刷新，上拉加载
	 */
	private OnRefreshListener mRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			mPageNum = 0;
			requestData();
			switch (type) {
			case 1:
				showRepost();
				break;
			case 2:
				showComment();
				break;
			case 3:
				showGood();
				break;
			default:
				break;
			}
		}

		@Override
		public void onLoadMore() {
			mPageNum++;
			switch (type) {
			case 1:
				showRepost();
				break;
			case 2:
				showComment();
				break;
			case 3:
				showGood();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 列表点击事件监听者
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO 列表点击
		}
	};
	
	/**
	 * @param type  add代表收藏，del，代表删除
	 */
	private void requestCollect(String type) {
		ProgressLoadingHelper.showLoadingDialog(this);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("id", id);
		requestMap.put("type", type);
		get(ProjectConstants.Url.COLLECT_MB, requestMap, 5);
	}
}
