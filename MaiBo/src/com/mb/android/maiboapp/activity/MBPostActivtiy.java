package com.mb.android.maiboapp.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.utils.FileUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.mb.android.maiboapp.BaseNetActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.CommentPicAdaptar;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.FileUtil;
import com.mb.android.maiboapp.utils.MyInputFilter;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.tandy.android.fw2.asynchttp.HttpHelper;
import com.tandy.android.fw2.asynchttp.HttpResponseListener;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * Created by Administrator on 2015/8/31. 发脉搏
 */
public class MBPostActivtiy extends BaseNetActivity implements
		HttpResponseListener ,OnClickListener{
	public static final int REQUEST_DATA_WAY = 0xf1;
	public static final int REQUEST_CODE_CAMERA = 0xf3;
	public static final int REQUEST_CODE_ALUBM = 0xf4;
	public static final int COUNT = 1000;

	private List<String> pathList = new ArrayList<String>();
	private CommentPicAdaptar mAdaptar = null;
	private ImgDel imgDel = null;
	private File mTmpFile = null;
	private int mVisiableType = 0;
	public enum AddType {
		Text, Picture, Camera, Friend, Long;
	}

	private TextView mTitleView, mSubTitleView, mSendView, mCancleView;
	private EditText mInputView;
	private TextView mModeView;
	private ImageView mAtIcon, mModeIcon;
	private GridView mPicGridView;

	private AddType addType = AddType.Text;
	private LinearLayout add_mode_layout;
	private String mUrl = "http://www.maibo.com/";//长脉搏地址
	private TextView txv_article_url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	    LinearLayout custom_acitonbar = (LinearLayout) LayoutInflater
				.from(this).inflate(R.layout.actianbar_post_weibo, null);
	    getSupportActionBar().setCustomView(custom_acitonbar,params);
		mTitleView = (TextView) custom_acitonbar.findViewById(R.id.add_title);
		mSubTitleView = (TextView) custom_acitonbar
				.findViewById(R.id.add_sub_title);
		mSendView = (TextView) custom_acitonbar.findViewById(R.id.add_send);
		mCancleView = (TextView) custom_acitonbar.findViewById(R.id.add_cancle);
		setContentView(R.layout.activity_mb_post);
		if (getIntent().hasExtra("type")) {
			addType = AddType.valueOf(getIntent().getStringExtra("type"));
		}
		if (addType == AddType.Picture) {
			// 进入从相册选择
			selectImage();
		}
		if (addType == AddType.Camera) {
			//拍照
			showCameraAction();
		}
		if (addType == AddType.Friend) {
			mVisiableType = 2;
		}
		mCancleView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		mSendView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				postMB();
			}
		});
		initUI();
		initData();

	}

	private void initData() {
		mTitleView.setText("发脉搏");
		mSubTitleView.setText(UserEntity.getInstance().getUser_name());
		if (addType == AddType.Long) {
			txv_article_url.setVisibility(View.VISIBLE);
			mUrl = getIntent().getStringExtra("url");
			txv_article_url.setText(mUrl);
		}else {
			txv_article_url.setVisibility(View.GONE);
		}
		initMode();
		filter = new MyInputFilter(this, mInputView);
		mInputView.setFilters(new InputFilter[] { filter });
		mAtIcon.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_icon_at:
			filter.goAt();
			break;
		case R.id.add_mode_layout:
			showMode();
			break;
		default:
			break;
		}
		
	}
	private MyInputFilter filter;

	private void initUI() {
		txv_article_url = findView(R.id.txv_article_url);
		mInputView = findView(R.id.add_text_input);
		mModeView = findView(R.id.add_mode_text);
		mModeIcon = findView(R.id.add_mode_icon);
		mAtIcon = findView(R.id.add_icon_at);
		mPicGridView = findView(R.id.add_pic_grid);
		add_mode_layout = findView(R.id.add_mode_layout);
		add_mode_layout.setOnClickListener(this);
		imgDel = new ImgDel();
		pathList.add(R.drawable.icon_pic_add+"");
		mAdaptar = new CommentPicAdaptar(MBPostActivtiy.this, pathList, imgDel);
		mPicGridView.setAdapter(mAdaptar);
	}

	private void initMode() {
		switch (mVisiableType) {
		case 0:
			mModeIcon.setImageResource(R.drawable.icon_public);
			mModeView.setText("公开");
			break;
		case 1:
			mModeIcon.setImageResource(R.drawable.icon_myself);
			mModeView.setText("仅自己可见");
			break;
		case 2:
			mModeIcon.setImageResource(R.drawable.icon_friend);
			mModeView.setText("好友圈");
			break;
		}
	}
	
	private void postMB() {
		String content = "";
		if (addType == AddType.Long) {
			content = mUrl+" "+ mInputView.getText().toString();
		}else {
			content = mInputView.getText().toString();
		}
		if (Helper.isEmpty(pathList) && Helper.isEmpty(content)) {
			ToastHelper.showToast("说点什么吧");
			return;
		}
		if (Helper.isNotEmpty(content) && content.length() > 280) {
			ToastHelper.showToast("内容超过限制字数280");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(MBPostActivtiy.this, "提交中...");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("text", content);
		map.put("visible", String.valueOf(mVisiableType));
		upload(ProjectConstants.Url.POST_MB, map, true,
				pathList.toArray(new String[pathList.size()]));
	}

	/**
	 * 发送upload请求
	 */
	@SuppressWarnings("deprecation")
	public void upload(String url, HashMap<String, String> hashMap,
			boolean isReply, String... files) {
		RequestParams requestParams = generateRequestParams(hashMap);
		if (Helper.isNotEmpty(files)) {
			int size = files.length;
			// 多张图
			for (int i = 0; i < size; i++) {
				try {
					requestParams.put(String.format("img%s", i), new File(
							files[i]));
				} catch (FileNotFoundException e) {
					Log.e(getClass().getName(), "无法上传图片，找不到文件：" + files[i]);
				}
			}
		}
		// 发送String userAgent1, String url, String stringParams,
		// HttpResponseListener listener, Object... extras
		HttpHelper.post(MBPostActivtiy.this, 0, ProjectHelper.getUserAgent1(),
				url, requestParams, this);
	}

	/**
	 * 生成请求参数
	 * 
	 * @return 请求参数
	 */
	private RequestParams generateRequestParams(HashMap<String, String> hashMap) {
		String map = ProjectHelper.mapToJson(hashMap);
		RequestParams params = new RequestParams();
		if (Helper.isNull(map)) {
			return params;
		}
		if (Helper.isNotEmpty(map)) {
			// 获取json对象
			JSONObject json = null;
			try {
				json = new JSONObject(map);
			} catch (JSONException e) {
				Log.e(getClass().getName(),
						"========'jsonParams' is NOT a json string========");
				json = new JSONObject();
			}
			// 获取所有参数
			Iterator<String> jsonKeys = json.keys();
			while (jsonKeys.hasNext()) {
				String paramsKey = jsonKeys.next();
				params.put(paramsKey, String.valueOf(json.opt(paramsKey)));
			}
		}
		return params;
	}

	@Override
	public boolean onStart(int gact, Object... extras) {
		return false;
	}

	@Override
	public boolean onFinish(int gact, Object... extras) {
		return false;
	}

	@Override
	public boolean onSuccess(int gact, int statusCode, Header[] headers,
			String content, Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		CommonEntity entity = JsonHelper.fromJson(content, CommonEntity.class);
		if (Helper.isNull(entity)) {
			return false;
		}
		if ("0".equals(entity.getResuleCode())) {
			LocalBroadcastManager.getInstance(MBPostActivtiy.this)
				.sendBroadcast(new Intent(ProjectConstants.BroadCastAction.REFRESH_MB_LIST));
			ToastHelper.showToast(entity.getResultMessage());
			finish();
		} else {
			ToastHelper.showToast(entity.getResultMessage());
		}
		return false;
	}

	@Override
	public boolean onFailure(int gact, int statusCode, Header[] headers,
			String content, Throwable error, Object... extras) {
		ProgressLoadingHelper.dismissLoadingDialog();
		ToastHelper.showToast("发表失败");
		return false;
	}

	public File getPhotoFile() {
		try {
			return FileUtil.getShareImg(getApplicationContext(),
					System.currentTimeMillis() + ".jpg", true, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class ImgDel implements CommentPicAdaptar.DelImg {

		@Override
		public void delImg(int position) {
			pathList.remove(position);
			mAdaptar.updateList(pathList);
		}

		@Override
		public void addImg() {
			Intent intent = new Intent(getApplicationContext(),
					MenuBottomActivity.class);
			startActivityForResult(intent, REQUEST_DATA_WAY);
		}
	}

	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);

		if (request == REQUEST_DATA_WAY) {
			if (result == MenuBottomActivity.CAMERA) {
				// 进入拍照
				showCameraAction();
			} else if (result == MenuBottomActivity.ALUBM) {
				// 进入从相册选择
				selectImage();
			}
		} else if (request == REQUEST_CODE_CAMERA) {
			handleImageCamera(data);
		} else if (request == REQUEST_CODE_ALUBM) {
			handleImageSelect(data);
		} else if (request == MyInputFilter.CODE_PERSON && result==RESULT_OK) {
			filter.doActivityResult(request, data);
		}
	}
	
	private void showMode(){
  		ListView listView = new ListView(this);//this为获取当前的上下文  
  		listView.setFadingEdgeLength(0);  
  		List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();//建立一个数组存储listview上显示的数据  
  		Map<String, String> nameMap1 = new HashMap<String, String>();  
		nameMap1.put("name", "公开");  
		nameList.add(nameMap1);
		Map<String, String> nameMap2 = new HashMap<String, String>();  
		nameMap2.put("name", "好友圈");  
		nameList.add(nameMap2);
		Map<String, String> nameMap3 = new HashMap<String, String>();  
		nameMap3.put("name", "仅自己");  
		nameList.add(nameMap3);
  		SimpleAdapter adapter = new SimpleAdapter(MBPostActivtiy.this,  
  		        nameList, android.R.layout.simple_list_item_1,  
  		        new String[] { "name" },  
  		        new int[] { android.R.id.text1 });  
  		listView.setAdapter(adapter);  
  		  
  		  
  		final AlertDialog dialog = new AlertDialog.Builder(this)  
  		        .setTitle("选择分享范围").setView(listView)//在这里把写好的这个listview的布局加载dialog中  
  		        .setNegativeButton("取消", new DialogInterface.OnClickListener() {  
  		  
  		            @Override  
  		            public void onClick(DialogInterface dialog, int which) {  
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
  		       if (position == 1) {
					mVisiableType = 2;
				}else if (position == 2) {
					mVisiableType = 1;
				}else {
					mVisiableType = 0;
				}
  		        initMode();
  		    }  
  		}); 
  	}
	
	 /**
     * 图片选择
     */
    private void selectImage() {
        try {
            Intent intent = new Intent(MBPostActivtiy.this, MultiImageSelectorActivity.class);
            // 是否显示拍摄图片
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
            // 最大可选择图片数量
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
            // 选择模式
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            startActivityForResult(intent, REQUEST_CODE_ALUBM);
        }catch (ActivityNotFoundException e) {
        }
    }
    
    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(this.getPackageManager()) != null){
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = FileUtils.createTmpFile(this);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
        }else{
            Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 处理图片选择结果
     * @param data
     */
    private void handleImageSelect(Intent data) {
        if(null == data) {
            return;
        }
        List<String> selecImags = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
        if(null == selecImags || 0 == selecImags.size()) {
            return;
        }
        pathList.addAll(0,selecImags);
		mAdaptar.updateList(pathList);
    }
    
    private void handleImageCamera(Intent data){
    	try {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Uri uri = data.getData();
					File pic = new File(new URI(uri.toString()));
					pathList.add(0,pic.getPath());
					mAdaptar.updateList(pathList);
				}
			} 
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }

}
