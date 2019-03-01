package com.mb.android.maiboapp.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.android.maiboapp.BaseActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.tandy.android.fw2.utils.PreferencesHelper;

/**
 * Created by Administrator on 2015/8/30.
 */
public class AddMBMenu extends BaseActivity implements View.OnClickListener {

    private TextView addText,addPicture,addFriend,addLong,addCamera;
    private LinearLayout addCancle = null;
    private LinearLayout add = null;
    private RelativeLayout addBg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        addText = findView(R.id.menu_add_text);
        addPicture = findView(R.id.menu_add_pic);
        addCamera = findView(R.id.menu_add_camera);
        addFriend = findView(R.id.menu_add_friend);
        addLong = findView(R.id.menu_add_long);
        addCancle = findView(R.id.menu_add_cancle);
        add = findView(R.id.menu_add_layout);
        addBg = findView(R.id.menu_add_bg);

        addText.setOnClickListener(this);
        addPicture.setOnClickListener(this);
        addCamera.setOnClickListener(this);
        addFriend.setOnClickListener(this);
        addLong.setOnClickListener(this);
        addCancle.setOnClickListener(this);
        addBg.setOnClickListener(this);
        addText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ProjectHelper.startScaleAnimation(addText);
                return false;
            }
        });
        addPicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ProjectHelper.startScaleAnimation(addPicture);
                return false;
            }
        });
        addCamera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ProjectHelper.startScaleAnimation(addCamera);
                return false;
            }
        });
        addFriend.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ProjectHelper.startScaleAnimation(addFriend);
                return false;
            }
        });
        addLong.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ProjectHelper.startScaleAnimation(addLong);
                return false;
            }
        });
        add.setAnimation(AnimationUtils.loadAnimation(this, R.anim.add_menu_in_from_down));
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.menu_add_text:
                if (UserEntity.getInstance().born()){
                    intent = new Intent(getApplicationContext(), MBPostActivtiy.class);
                    intent.putExtra("type", MBPostActivtiy.AddType.Text.toString());
                    startActivity(intent);
                }else {
                	Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
                    NavigationHelper.startActivity(AddMBMenu.this, UserLoginActivity.class, bundle, false);
                }
                break;
            case R.id.menu_add_pic:
                if (UserEntity.getInstance().born()){
                    intent = new Intent(getApplicationContext(), MBPostActivtiy.class);
                    intent.putExtra("type", MBPostActivtiy.AddType.Picture.toString());
                    startActivity(intent);
                }else {
                	Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
                    NavigationHelper.startActivity(AddMBMenu.this, UserLoginActivity.class, bundle, false);
                }
                break;
            case R.id.menu_add_camera:
                if (UserEntity.getInstance().born()){
                    intent = new Intent(getApplicationContext(), MBPostActivtiy.class);
                    intent.putExtra("type", MBPostActivtiy.AddType.Camera.toString());
                    startActivity(intent);
                }else {
                	Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
                    NavigationHelper.startActivity(AddMBMenu.this, UserLoginActivity.class, bundle, false);
                }
                break;
            case R.id.menu_add_friend:
                if (UserEntity.getInstance().born()){
                    intent = new Intent(getApplicationContext(), MBPostActivtiy.class);
                    intent.putExtra("type", MBPostActivtiy.AddType.Friend.toString());
                    startActivity(intent);
                }else {
                	Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
                    NavigationHelper.startActivity(AddMBMenu.this, UserLoginActivity.class, bundle, false);
                }
                break;
            case R.id.menu_add_long:
                if (UserEntity.getInstance().born()){
                    intent = new Intent(getApplicationContext(), MBPostLongActivtiy.class);
                    startActivity(intent);
                }else {
                	Bundle bundle = new Bundle();
                	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
                    NavigationHelper.startActivity(AddMBMenu.this, UserLoginActivity.class, bundle, false);
                }
                break;
            case R.id.menu_add_cancle:
            	boolean isOpen = PreferencesHelper.getInstance().getBoolean(ProjectConstants.Preferences.KEY_IS_OPEN_SOUND,true);
				if (isOpen) {
					showSound();
				}
                onBackPressed();
                break;
            case R.id.menu_add_bg:

                break;
        }
        finish();
    }
    
    private void showSound() {
		MediaPlayer player = MediaPlayer.create(AddMBMenu.this,
				R.raw.radar_pop);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		player.start();
	}
}
