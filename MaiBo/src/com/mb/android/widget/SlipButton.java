package com.mb.android.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.mb.android.maiboapp.R;

/**
 * 滑动切换按钮
 * 
 * @author cgy
 * 
 */
public class SlipButton extends View implements OnTouchListener {

	/**
	 * 记录当前按钮是否打开,true为打开,flase为关闭
	 */
	private boolean mNowChoose = false;
	/**
	 * 记录用户是否在滑动的变量
	 */
	private boolean mOnSlip = false;
	private float mDownX, mNowX;// 按下时的x,当前的x,
	private Rect mBtnOn, mBtnOff;// 打开和关闭状态下,游标的Rect

	private boolean mIsChgLsnOn = false;
	private OnChangedListener mChangedListener;

	private Bitmap mBmBgOn, mBmBgOff, mSlipBtn;

	public SlipButton(Context context) {
		super(context);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// 载入图片资源
		mBmBgOn = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg_slip_btn_on);
		mBmBgOff = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg_slip_btn_off);
		mSlipBtn = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg_slip_btn);
		// 获得需要的Rect数据
		mBtnOn = new Rect(0, 0, mSlipBtn.getWidth(), mSlipBtn.getHeight());
		mBtnOff = new Rect(mBmBgOff.getWidth() - mSlipBtn.getWidth(), 0,
				mBmBgOff.getWidth(), mSlipBtn.getHeight());
		setOnTouchListener(this);// 设置监听器,也可以直接复写OnTouchEvent
	}

	@Override
	protected void onDraw(Canvas canvas) {// 绘图函数
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x;
		// 是否是在滑动状态,
		if (mOnSlip) {
			// 滑动到前半段与后半段的背景不同,在此做判断
			if (mNowX < (mBmBgOn.getWidth() / 2)) {
				// 画出关闭时的背景
				canvas.drawBitmap(mBmBgOff, matrix, paint);
			}else {
				// 画出打开时的背景
				canvas.drawBitmap(mBmBgOn, matrix, paint);
			}			
			// 是否划出指定范围,不能让游标跑到外头,必须做这个判断
			if (mNowX >= mBmBgOn.getWidth()) {
				// 减去游标1/2的长度...
				x = mBmBgOn.getWidth() - mSlipBtn.getWidth() / 2;
			} else {
				x = mNowX - mSlipBtn.getWidth() / 2;
			}
		} else {// 非滑动状态
			if (mNowChoose) {
				// 根据现在的开关状态设置画游标的位置
				x = mBtnOff.left;
				// 画出打开时的背景
				canvas.drawBitmap(mBmBgOn, matrix, paint);
			} else {
				x = mBtnOn.left;
				// 画出关闭时的背景
				canvas.drawBitmap(mBmBgOff, matrix, paint);
			}
		}
		if (x < 0) {
			// 对游标位置进行异常判断...
			x = 0;
		} else if (x > mBmBgOn.getWidth() - mSlipBtn.getWidth()) {
			x = mBmBgOn.getWidth() - mSlipBtn.getWidth();
		}
		canvas.drawBitmap(mSlipBtn, x, 0, paint);// 画出游标.
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction())// 根据动作来执行代码
		{
		case MotionEvent.ACTION_MOVE:// 滑动
			mNowX = event.getX();
			break;
		case MotionEvent.ACTION_DOWN:// 按下
			if (event.getX() > mBmBgOn.getWidth() || event.getY() > mBmBgOn.getHeight())
				return false;
			mOnSlip = true;
			mDownX = event.getX();
			mNowX = mDownX;
			break;
		case MotionEvent.ACTION_UP:// 松开
			mOnSlip = false;
			boolean LastChoose = mNowChoose;
			if (event.getX() >= (mBmBgOn.getWidth() / 2))
				mNowChoose = true;
			else
				mNowChoose = false;
			if (mIsChgLsnOn && (LastChoose != mNowChoose))// 如果设置了监听器,就调用其方法..
				mChangedListener.OnChanged(mNowChoose);
			break;
		default:

		}
		invalidate();// 重画控件
		return true;
	}
	
	/**
	 * 开光状态设置
	 * @param isChecked 是否选中
	 */
	public void setState(boolean isChecked) {
		boolean LastChoose = mNowChoose;
		mOnSlip = false;
		mNowChoose = isChecked;
		invalidate();
		if(null  == mChangedListener) {
			return;
		}
		if (mIsChgLsnOn && (LastChoose != mNowChoose)) {
			mChangedListener.OnChanged(mNowChoose);
		}
	}

	public void SetOnChangedListener(OnChangedListener l) {// 设置监听器,当状态修改的时候
		mIsChgLsnOn = true;
		mChangedListener = l;
	}

	public interface OnChangedListener {
		abstract void OnChanged(boolean CheckState);
	}
}
