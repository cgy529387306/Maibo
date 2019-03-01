package com.mb.android.maiboapp.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.utils.FileUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.mb.android.maiboapp.BaseNetActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.LongMbResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.FileUtil;
import com.mb.android.maiboapp.utils.ImageFactory;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.tandy.android.fw2.asynchttp.HttpHelper;
import com.tandy.android.fw2.asynchttp.HttpResponseListener;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * Created by Administrator on 2015/8/31. 发长脉搏
 */
public class MBPostLongActivtiy extends BaseNetActivity implements
		HttpResponseListener{
	public static final int REQUEST_DATA_WAY = 0xf1;
	public static final int REQUEST_CODE_CAMERA = 0xf3;
	public static final int REQUEST_CODE_ALUBM = 0xf4;
	public static final int COUNT = 1000;

	private List<String> pathList = new ArrayList<String>();

	private File mTmpFile = null;
	private TextView mTitleView, mSubTitleView, mSendView, mCancleView;
	private EditText mTitleInput,mContentInput;
	private ImageView mAddPic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		LinearLayout custom_acitonbar = (LinearLayout) LayoutInflater
				.from(this).inflate(R.layout.actianbar_post_weibo, null);
		getSupportActionBar().setCustomView(custom_acitonbar);
		mTitleView = (TextView) custom_acitonbar.findViewById(R.id.add_title);
		mSubTitleView = (TextView) custom_acitonbar
				.findViewById(R.id.add_sub_title);
		mSendView = (TextView) custom_acitonbar.findViewById(R.id.add_send);
		mCancleView = (TextView) custom_acitonbar.findViewById(R.id.add_cancle);
		setContentView(R.layout.activity_mb_post_long);
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
		mTitleView.setText("编辑长脉搏");
		mSubTitleView.setText(UserEntity.getInstance().getUser_name());
	}

	private void initUI() {
		mTitleInput = findView(R.id.add_title_input);
		mContentInput = findView(R.id.add_content_input);
		mAddPic = findView(R.id.add_icon_pic);
		mAddPic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				Intent intent = new Intent(getApplicationContext(),
						MenuBottomActivity.class);
				startActivityForResult(intent, REQUEST_DATA_WAY);
			}
		});
	}

	private void postMB() {
		final String title = mTitleInput.getText().toString();
		final String content = mContentInput.getText().toString();
		if (Helper.isEmpty(title)) {
			ToastHelper.showToast("请输入标题");
			return;
		}
		if (Helper.isEmpty(content)) {
			ToastHelper.showToast("请输入内容");
			return;
		}
		if (Helper.isNotEmpty(content) && content.length() > 280) {
			ToastHelper.showToast("内容超过限制字数280");
			return;
		}
		ProgressLoadingHelper.showLoadingDialog(MBPostLongActivtiy.this, "提交中...");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", title);
		map.put("content", content);
		upload(ProjectConstants.Url.POST_MB_LONG, map, true,
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
		HttpHelper.post(MBPostLongActivtiy.this, 0, ProjectHelper.getUserAgent1(),
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
		LongMbResp entity = JsonHelper.fromJson(content, LongMbResp.class);
		if (Helper.isNull(entity)) {
			return false;
		}
		if ("0".equals(entity.getResuleCode())) {
			ToastHelper.showToast(entity.getResultMessage());
			Intent intent = new Intent(getApplicationContext(), MBPostActivtiy.class);
            intent.putExtra("type", MBPostActivtiy.AddType.Long.toString());
            intent.putExtra("url", entity.getData().getUrl());
            startActivity(intent);
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
		} 
	}

	private void addImageBetweentext(Bitmap bmp) {
		int i = 0;
		String str = "{img" + i + "}";
		Drawable drawable = new BitmapDrawable(bmp);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		int selectionCursor = mContentInput.getSelectionStart();
		mContentInput.getText().insert(selectionCursor, str);
		selectionCursor = mContentInput.getSelectionStart();

		SpannableStringBuilder builder = new SpannableStringBuilder(
				mContentInput.getText());
		builder.setSpan(new ImageSpan(drawable),
				selectionCursor - str.length(), selectionCursor,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mContentInput.setText(builder);
		mContentInput.setSelection(selectionCursor);
	}
	
    /**
     * 图片选择
     */
    private void selectImage() {
        try {
            Intent intent = new Intent(MBPostLongActivtiy.this, MultiImageSelectorActivity.class);
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
        for (int i = 0; i < selecImags.size(); i++) {
        	Bitmap bm = ImageFactory.getImageThumbnail(selecImags.get(i));
			addImageBetweentext(bm);
		}
    }
    
    private void handleImageCamera(Intent data){
    	try {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Uri uri = data.getData();
					File pic = new File(new URI(uri.toString()));
					Bitmap bm = (Bitmap) bundle.get("data");
					pathList.add(pic.getPath());
					addImageBetweentext(bm);
				}
			} else {
				Bitmap bm = ImageFactory.getImageThumbnail(
						mTmpFile.getPath());
				pathList.add(mTmpFile.getPath());
				addImageBetweentext(bm);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }
	
}
