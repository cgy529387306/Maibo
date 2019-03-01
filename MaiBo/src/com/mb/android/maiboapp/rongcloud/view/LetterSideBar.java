package com.mb.android.maiboapp.rongcloud.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 字母侧边栏
 * 
 * @author Administrator
 *
 */
public class LetterSideBar extends View {
	private final static int HIDE_DIALOG_TEXT = 0;

	private int delayMillis = 2000;// 弹出的提示停留时间

	private char[] l; // bar上显示的
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private TextView mDialogText;// 点击按钮弹出的提示
	private float m_nItemHeight = 15; // bar上每项的文字绘制在每个子项上的位置
	private float ItemHeight = 15; // bar上每项的高度，默认15
	private int textSize = 20;// bar上的文字的字体大小
	private Paint paint;
	private int PaddingTop = 0;

	private Handler mHandler;

	public LetterSideBar(Context context) {
		super(context);
		init();
	}

	public LetterSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LetterSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 初始化一些对象
	 */
	@SuppressLint("HandlerLeak")
	private void init() {
		l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
				'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
				'X', 'Y', 'Z' }; // 初始化bar上要显示的文字
		paint = new Paint();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HIDE_DIALOG_TEXT:// 使提示隐藏
					mDialogText.setVisibility(View.GONE);
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	/**
	 * 设置器要控制的listview
	 * 
	 * @param _list
	 *            设置器要控制的listview
	 */
	public void setListView(ListView _list) {
		list = _list;
		sectionIndexter = (SectionIndexer) _list.getAdapter();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// system.out.println("widthMeasureSpec :"+widthMeasureSpec);
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * 设置提示
	 * 
	 * @param mDialogText
	 */
	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = (int) (i / ItemHeight); // 得到点击的项的索引
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			mDialogText.setVisibility(View.VISIBLE);// 显示提示
			mDialogText.setText("" + l[idx]);// 设置提示内容
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(l[idx]);// 得到listview上的索引
			if (position == -1) {
				return true;
			}
			list.smoothScrollToPositionFromTop(position, 0, 100);// 把listview上指向的项置顶
		} else {
			mHandler.sendEmptyMessageDelayed(HIDE_DIALOG_TEXT, delayMillis);// 发送隐藏提示消息
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		// 文字的绘制坐标是左下角开始的
		// 下边的paint.setTextAlign(Paint.Align.CENTER)设置了横向居中，所以这里就变成从中间开始绘制。所以用中间值作为X坐标
		// 而且这个坐标也是比较好计算的数值
		float widthCenter = getMeasuredWidth() / 2; // 计算文字绘制的开始的位置（X轴）
		float HeightCenter = getMeasuredHeight() / l.length; // 每项的高度，为了每项大小一样并布满整个bar
		ItemHeight = HeightCenter;// 设置每项的高度
		m_nItemHeight = (HeightCenter + textSize) / 2;// 计算文字绘制的开始位置（Y轴）

		// 虽然X，Y是float型的，但是像素点肯定是整数的，所以绘制的时候肯定会取整
		int HeightPadding = (int) ((HeightCenter + textSize) % 2);// 计算出每项取整后舍去的大小*2的整数
		if (HeightPadding > 0) {
			PaddingTop = HeightPadding * l.length / 4;// 计算出总共舍去的大小后计算出上下padding的大小，好让bar上的文字垂直居中显示
		}
		if ((int) ItemHeight < textSize)// 字体大小超过最大值，设置字体大小为最大值
		{
			this.textSize = (int) ItemHeight;
		}
		paint.setColor(0xff595c61);
		paint.setTextSize(textSize);
		paint.setTextAlign(Paint.Align.CENTER);
		for (int i = 0; i < l.length; i++) { // 绘制每个文字
			canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight
					+ (i * HeightCenter) + PaddingTop, paint);
		}
		super.onDraw(canvas);
	}

	/**
	 * 设置字大小，最大不可超过每项的最大高度(最大高度view会自己计算，超过最大值会在绘制的时候取最大值)
	 * 
	 * @param textSize
	 */
	public void setTextSize(int textSize) {
		if ((int) ItemHeight > textSize) {
			this.textSize = textSize;
		} else {
			this.textSize = (int) ItemHeight;
		}
	}

	/**
	 * 测量控件宽度
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = (int) textSize + getPaddingLeft() + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);// 60,480
			}
		}
		// system.out.println("measureWidth :" + result + " "+getPaddingLeft()+
		// "  " + getPaddingRight());
		return result;
	}

	/**
	 * 测量控件高度
	 * 
	 * @param measureSpec
	 * @return
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		int mAscent = (int) paint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-mAscent + paint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		// system.out.println("measureHeight :" + result);
		return result;
	}

}
