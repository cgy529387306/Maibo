package com.mb.android.maiboapp.activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.entity.LoginResp;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.UserInfoResp;
import com.mb.android.maiboapp.utils.LogHelper;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.UserAgentHelper;
import com.mb.android.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.AppHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 我的资料
 *
 * @author cgy
 */
public class UserInfoActivity extends BaseSwipeBackActivity {

	private RelativeLayout rel_user_avater;
	private LinearLayout lin_user_name, lin_user_sex, lin_user_introduction;
	private TextView txv_user_name, txv_user_sex, txv_user_introduction;
	private CircleImageView imv_user_avater;
	private int selectItem = 0;
	private String mTempFilePath = AppHelper.getBaseCachePath()
			.concat(String.valueOf(System.currentTimeMillis())).concat(".jpg");
	private Dialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("基本资料");
		setContentView(R.layout.activity_user_info);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestData();
	}

	private void initViews() {
		rel_user_avater = findView(R.id.rel_user_avater);
		lin_user_name = findView(R.id.lin_user_name);
		lin_user_sex = findView(R.id.lin_user_sex);
		lin_user_introduction = findView(R.id.lin_user_introduction);
		txv_user_name = findView(R.id.txv_user_name);
		txv_user_sex = findView(R.id.txv_user_sex);
		txv_user_introduction = findView(R.id.txv_user_introduction);
		imv_user_avater = findView(R.id.imv_user_avater);
		rel_user_avater.setOnClickListener(mClickListener);
		lin_user_name.setOnClickListener(mClickListener);
		lin_user_sex.setOnClickListener(mClickListener);
		lin_user_introduction.setOnClickListener(mClickListener);
	}

	@Override
	public boolean onResponseSuccess(int gact, String response,
									 Object... extras) {
		int requestType = extras.length != 0 ? (Integer) extras[0] : 0;
		if (super.onResponseSuccess(gact, response, extras)) {
			return true;
		}
		if (requestType == 1) {
			CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
			if (Helper.isNull(entity)) {
				return false;
			}
			ToastHelper.showToast(entity.getResultMessage());
			return true;
		}
		UserInfoResp entity = JsonHelper.fromJson(response, UserInfoResp.class);
		if (Helper.isNotNull(entity) && Helper.isNotNull(entity.getData())) {
			fillData(entity.getData());
			UserEntity.getInstance().born(entity.getData());
//			LocalBroadcastManager.getInstance(UserInfoActivity.this).sendBroadcast(
//							new Intent(ProjectConstants.BroadCastAction.REFRESH_USERINFO));
		}
		return true;
	}

	@Override
	public boolean onResponseError(int gact, String response,
								   VolleyError error, Object... extras) {
		return true;
	}

	private void fillData(UserEntity userEntity) {
		if(Helper.isNotEmpty(userEntity.getAvatar_large())) {
			ImageLoader.getInstance().displayImage(userEntity.getAvatar_large(), imv_user_avater);
		}
		if (Helper.isEmpty(userEntity.getUser_name())) {
			txv_user_name.setText(userEntity.getPhone());
		} else {
			txv_user_name.setText(userEntity.getUser_name());
		}
		UserEntity.getInstance().getMember_id();
		txv_user_introduction.setText(userEntity.getIntro());
		if ("0".equals(userEntity.getGender())) {
			txv_user_sex.setText("男");
		} else if ("1".equals(userEntity.getGender())) {
			txv_user_sex.setText("女");
		} else if("2".equals(userEntity.getGender())){
			txv_user_sex.setText("未知");
		}
		if ("0".equals(UserEntity.getInstance().getGender())) {
			selectItem = 0;
		} else {
			selectItem = 1;
		}
	}


	//begin region 上传头像
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case 1:
				// 相册
				cropPickedImage(data.getData());
				break;
			case 2:
				// 拍照
				File file = new File(mTempFilePath);
				if (!file.exists()) {
					return;
				}
				cropPickedImage(Uri.parse("file://".concat(mTempFilePath)));
				break;
			case 3:
				new SubmitUserAvatarTask(mTempFilePath).execute();
				break;
			default:
				break;
		}
	}

	private void showUserAvatarPickDialog() {
		dismissDialog();
		AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
		builder.setTitle("头像选取");
		builder.setItems(new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						// 选取相册图片
						pickImageFromGallery();
						break;
					case 1:
						// 拍照获取图片
						pickImageFromCamera();
						break;
					default:
						break;
				}
			}
		}).show();
	}

	/**
	 * 相册图片选取
	 */
	private void pickImageFromGallery() {
		try {
			Intent intentFromGallery = new Intent();
			// 设置文件类型
			intentFromGallery.setType("image/*");
			intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentFromGallery, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拍照获取图片
	 */
	private void pickImageFromCamera() {
		try {
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			// 图片缓存的地址
			mTempFilePath = AppHelper.getBaseCachePath()
					.concat(String.valueOf(System.currentTimeMillis()))
					.concat(".jpg");
			File file = new File(mTempFilePath);
			Uri uri = Uri.fromFile(file);
			// 设置图片的存放地址
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 裁剪选取的图片
	 *
	 * @param uri
	 */
	private void cropPickedImage(Uri uri) {
		try {

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("crop", "true");// 可裁剪
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("scale", true);

			// 图片缓存的地址
			mTempFilePath = AppHelper.getBaseCachePath()
					.concat(String.valueOf(System.currentTimeMillis()))
					.concat(".jpg");
			File file = new File(mTempFilePath);
			Uri uriCache = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCache);

			intent.putExtra("outputFormat", "JPEG");// 图片格式
			intent.putExtra("noFaceDetection", true);// 取消人脸识别
			intent.putExtra("return-data", false);

			startActivityForResult(intent, 3);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据地址获取图片Bitmap
	 *
	 * @param picPath
	 */
	private Bitmap getBitmapFromPath(String picPath) {
		Bitmap result = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		result = BitmapFactory.decodeFile(picPath, opts);
		int scaleHeight = (int) (opts.outHeight / (float) 200);
		int scaleWidth = (int) (opts.outWidth / (float) 200);
		if (scaleHeight <= 0 && scaleWidth <= 0) {
			opts.inSampleSize = 1;
		} else {
			if (scaleHeight > scaleWidth) {
				opts.inSampleSize = scaleHeight;
			} else {
				opts.inSampleSize = scaleWidth;
			}
		}
		opts.inJustDecodeBounds = false;
		result = BitmapFactory.decodeFile(picPath, opts);
		return result;
	}

	/**
	 * 显示上传头像对话框
	 */
	private void showSubmitAvatarDialog() {
		mDialog = ProgressDialog.show(UserInfoActivity.this, "头像上传", "上传中…");
	}

	/**
	 * 隐藏对话框
	 */
	private void dismissDialog() {
		if (Helper.isNotNull(mDialog) && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	/**
	 * 头像上传异步任务
	 *
	 * @author cgy
	 */
	class SubmitUserAvatarTask extends AsyncTask<Void, Void, HttpResponse> {

		private String mUserAvatar;

		public SubmitUserAvatarTask(String avatarUrl) {
			this.mUserAvatar = avatarUrl;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dismissDialog();
			showSubmitAvatarDialog();
		}

		@Override
		protected HttpResponse doInBackground(Void... params) {
			HttpResponse response = null;
			if (Helper.isEmpty(params)) {
				return response;
			}
			try {
				HttpPost request = new HttpPost(
						ProjectConstants.Url.ACCOUNT_SETAVATAR);
				// 待上传头像
				FileBody img_file = new FileBody(new File(mUserAvatar));
				MultipartEntity mpEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				mpEntity.addPart("avatar", img_file);
				request.setEntity(mpEntity);
				request.addHeader("User-Agent1", ProjectHelper.getUserAgent1());
				HttpParams httpParameters = new BasicHttpParams();
				HttpClient httpClient = new DefaultHttpClient(httpParameters);
				response = httpClient.execute(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(HttpResponse result) {
			super.onPostExecute(result);
			// 隐藏对话框
			dismissDialog();

			if (Helper.isNotNull(result)
					&& result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String callBack = null;
				try {
					callBack = EntityUtils.toString(result.getEntity());
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String decodeResult = UserAgentHelper.simpleDecode("0", 0, callBack);
				LogHelper.e(decodeResult);
				LoginResp entity = JsonHelper.fromJson(decodeResult,LoginResp.class);
				if (Helper.isNotEmpty(entity)){
					if ("0".equals(entity.getResuleCode())){
						imv_user_avater.setImageBitmap(getBitmapFromPath(mUserAvatar));
						ToastHelper.showToast(entity.getResultMessage());
						UserEntity.getInstance().born(entity.getData());
						LocalBroadcastManager.getInstance(UserInfoActivity.this).sendBroadcast(new Intent(ProjectConstants.BroadCastAction.CHANGE_USER_BLOCK));
						Toast.makeText(UserInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
					}else {
						ToastHelper.showToast(entity.getResultMessage());
					}
				}
			}
		}
	}
	//end region 上传头像

	/**
	 * 点击事件
	 */
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.rel_user_avater:
					showUserAvatarPickDialog();
					break;

				case R.id.lin_user_name:
					NavigationHelper.startActivity(UserInfoActivity.this, UserEditNameActivity.class, null, false);
					break;

				case R.id.lin_user_sex:
					selectSex();
					break;
				case R.id.lin_user_introduction:
					NavigationHelper.startActivity(UserInfoActivity.this, UserEditIntrodutionActivity.class, null, false);
					break;

				default:
					break;
			}
		}
	};

	private void requestData() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("member_id", UserEntity.getInstance().getMember_id());
		get(ProjectConstants.Url.SHOW_USER_INFO, requestMap);
	}


	/**
	 * 性别选择
	 */
	private void selectSex() {
		new AlertDialog.Builder(UserInfoActivity.this).setTitle("请选择")
				.setSingleChoiceItems(new String[]{"男", "女"}, selectItem,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								HashMap<String, String> requestMap = new HashMap<String, String>();
								if (which == 0) {
									selectItem = 0;
									txv_user_sex.setText("男");
									requestMap.put("gender", "0");
								} else {
									selectItem = 1;
									txv_user_sex.setText("女");
									requestMap.put("gender", "1");
								}
								get(ProjectConstants.Url.ACCOUNT_UPDATE, requestMap, 1);
								dialog.dismiss();
							}
						}
				).setNegativeButton("取消", null).show();
	}
}
