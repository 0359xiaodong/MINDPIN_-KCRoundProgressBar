package com.taotao.kcroundprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

/**
 * An indicator of progress, similar to Android's ProgressBar. Can be used in
 * 'spin mode' or 'increment mode'
 * 
 * @author Todd Davies
 * 
 *         Licensed under the Creative Commons Attribution 3.0 license see:
 *         http://creativecommons.org/licenses/by/3.0/
 */
public class ProgressWheel extends View {
	

	// Sizes (with defaults)
	private int layout_height = 0;
	private int layout_width = 0;
	private int fullRadius = 100;
	private int circleRadius = 80;
	
	private int barLength = 60;
	private int barWidth = 10;
	private int borderWidth = 0;
	private int bgWidth;
	private int textSize = 18;
	private int start_angle = -90;

	// Padding (with defaults)
	private int paddingTop = 5;
	private int paddingBottom = 5;
	private int paddingLeft = 5;
	private int paddingRight = 5;

	// Colors (with defaults)
	private int barColor = 0x87ceeb;
	private int bgColor =  0xaa888888;
	private int borderColor;
	private int textColor = 0xFF000000;

	// Paints
	private Paint barPaint = new Paint();
	private Paint bgPaint = new Paint();
	private Paint borderPaint = new Paint();
	private Paint textPaint = new Paint();

	// Rectangles
	private RectF rectBounds;
	private RectF barBounds;
	private RectF borderBounds;
	private RectF bgBounds;
	
	private float text_height;
	private boolean show_text = true;
	

	// Animation
	// The amount of pixels to move the bar by on each draw
	private int rollSpeed = 2;
	// The number of milliseconds to wait inbetween each draw
	private int delayMillis = 0;
	private boolean loading;
	private Handler rollHandler = new Handler() {
		/**
		 * This is the code that will increment the progress variable and so
		 * spin the wheel
		 */
		@Override
		public void handleMessage(Message msg) {
			if (!loading_mode) return;
			    invalidate();
				angle += rollSpeed;
				if (angle > 360) {
					angle = 0;
				}
				rollHandler.sendEmptyMessageDelayed(0, delayMillis);
		}
	};
	
	
	private int angle = 0;
	boolean loading_mode = false;

	// Other
	private String text = "";
	private String[] splitText = {};

	/**
	 * The constructor for the ProgressWheel
	 * 
	 * @param context
	 * @param attrs
	 */
	public ProgressWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		//parsePadding(context, attrs);
		parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ProgressWheel));
		
	}
	
	
	

	// ----------------------------------
	// Setting up stuff
	// ----------------------------------

	
	/**
	 * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of
	 * the view, because this method is called after measuring the dimensions of
	 * MATCH_PARENT & WRAP_CONTENT. Use this dimensions to setup the bounds and
	 * paints.
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		// Share the dimensions
		layout_width = w;
		layout_height = h;
        Log.d(VIEW_LOG_TAG, "view_width = " + w + " view_height = " + h);
		setupBounds();
		setupPaints();
		
		
		
		
	}
	

	/**
	 * Set the properties of the paints we're using to draw the progress wheel
	 */
	private void setupPaints() {
		barPaint.setColor(barColor);
		barPaint.setAntiAlias(true);
		barPaint.setStyle(Style.STROKE);
		barPaint.setStrokeWidth(barWidth);

		borderPaint.setColor(borderColor);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(borderWidth);

		bgPaint.setColor(bgColor);
		bgPaint.setAntiAlias(true);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(bgWidth);

		textPaint.setColor(textColor);
		textPaint.setStyle(Style.FILL);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);
		
		text_height = getFontHeight(textSize);
	}
	
	public float getFontHeight(float fontSize)
	{
	    Paint paint = new Paint();
	    paint.setTextSize(fontSize);
	    FontMetrics fm = paint.getFontMetrics();
	    return (float) Math.ceil(fm.descent - fm.ascent);
	}

	/**
	 * Set the bounds of the component
	 */
	private void setupBounds() {
		// Width should equal to Height, find the min value to steup the circle
		int minValue = Math.min(layout_width, layout_height);

		// Calc the Offset if needed
		int xOffset = layout_width - minValue;
		int yOffset = layout_height - minValue;

		// Add the offset
		
		paddingTop = this.getPaddingTop() + (yOffset / 2);
		paddingBottom = this.getPaddingBottom() + (yOffset / 2);
		paddingLeft = this.getPaddingLeft() + (xOffset / 2);
		paddingRight = this.getPaddingRight() + (xOffset / 2);
		Log.i(VIEW_LOG_TAG, "padding:" + paddingLeft + " / " + paddingTop + " / " + paddingRight + " / " + paddingBottom);

		rectBounds = new RectF(paddingLeft, paddingTop,
				this.getLayoutParams().width - paddingRight, this.getLayoutParams().height
						- paddingBottom);
		
		Log.i(VIEW_LOG_TAG, "before inset RECT: " + rectBounds);
        
		if(borderWidth > 0){
			float hw = borderWidth/2.0f;
			borderBounds = new RectF(rectBounds.left + hw, rectBounds.top + hw, 
					rectBounds.right - hw, rectBounds.bottom - hw);
			
			rectBounds.inset(2*borderWidth, 2*borderWidth);
		}
		
		if(bgWidth > 0){
			float hw = bgWidth/2.0f;
			bgBounds = new RectF(rectBounds.left + hw, rectBounds.top + hw, 
					rectBounds.right - hw, rectBounds.bottom - hw);
		}
		
		float hw = barWidth/2.0f;
		barBounds = new RectF(rectBounds.left + hw, rectBounds.top + hw, 
				rectBounds.right - hw, rectBounds.bottom - hw);
		
		Log.i(VIEW_LOG_TAG, "RECT: " + rectBounds + " <> " + barBounds);
		
		Log.d(VIEW_LOG_TAG, "bar_width = " + barWidth + " rim_width = " + borderWidth);
		fullRadius = (this.getLayoutParams().width - paddingRight - barWidth) / 2;
		circleRadius = (fullRadius - barWidth) + 1;
	}

	/**
	 * Parse the attributes passed to the view from the XML
	 * 
	 * @param a
	 *            the attributes to parse
	 */
	private void parseAttributes(TypedArray a) {
		int tem = a.getInt(R.styleable.ProgressWheel_progress, angle);
		angle = 360*tem/max_value;
		
		show_text = a.getBoolean(R.styleable.ProgressWheel_show_text, show_text);
		max_value = a.getInt(R.styleable.ProgressWheel_max_value, max_value);
		
		loading_mode = a.getBoolean(R.styleable.ProgressWheel_loading_mode, loading_mode);
		
		start_angle = a.getInteger(R.styleable.ProgressWheel_start_angle, start_angle);
		
		barWidth = (int) a.getDimension(R.styleable.ProgressWheel_bar_width, barWidth);
		bgWidth = (int) a.getDimension(R.styleable.ProgressWheel_bar_bg_width, barWidth);

		borderWidth = (int) a.getDimension(R.styleable.ProgressWheel_border_width, borderWidth);

		rollSpeed = (int) a.getDimension(R.styleable.ProgressWheel_roll_speed, rollSpeed);

		delayMillis = (int) a.getInteger(R.styleable.ProgressWheel_roll_delay_millis, delayMillis);
		if (delayMillis < 0) {
			delayMillis = 0;
		}

		barColor = a.getColor(R.styleable.ProgressWheel_bar_color, barColor);
		borderColor = a.getColor(R.styleable.ProgressWheel_border_color, barColor);
		bgColor = a.getColor(R.styleable.ProgressWheel_bar_bg_color, bgColor);

		barLength = (int) a.getDimension(R.styleable.ProgressWheel_bar_length, barLength);

		textSize = (int) a.getDimension(R.styleable.ProgressWheel_text_size, textSize);

		textColor = (int) a.getColor(R.styleable.ProgressWheel_text_color, textColor);

		// if the text is empty , so ignore it
		if (a.hasValue(R.styleable.ProgressWheel_text)) {
			setText(a.getString(R.styleable.ProgressWheel_text));
		}

		

		// Recycle
		a.recycle();
	}

	// ----------------------------------
	// Animation stuff
	// ----------------------------------

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.v(VIEW_LOG_TAG, "---onDraw----" + angle);
		// Draw the rim
		if(borderWidth > 0){
			canvas.drawArc(borderBounds, 360, 360, false, borderPaint);
		}
		
		//draw the bg
		if(bgWidth > 0){
			canvas.drawArc(bgBounds, 360, 360, false, bgPaint);
		}
				
		// Draw the bar
		if (loading_mode) {
			canvas.drawArc(barBounds, angle + start_angle, barLength, false, barPaint);
		} else {
			canvas.drawArc(barBounds, start_angle, angle, false, barPaint);
		}
		
		if(show_text){
			int offsetNum = 1;
			for (String s : splitText) {
				float offset = textPaint.measureText(s) / 2;
				canvas.drawText(s, this.getWidth() / 2.0f - offset, 
						this.getHeight() / 2.0f + (text_height*offsetNum)/4.0f ,
						textPaint);
				offsetNum++;
			}
		}
		
	}

	/**
	 * Reset the count (in increment mode)
	 */
	public void reset() {
		angle = 0;
		setText("0%");
		postInvalidate();
	}

	/**
	 * Turn off spin mode
	 */
	public void stopRoll() {
		if(!loading_mode) return;
		loading = false;
		rollHandler.removeMessages(0);
	}
	
	
	/**
	 * Puts the view on spin mode
	 */
	public void startRoll() {
		if(!loading_mode) return;
		loading = true;
		rollHandler.sendEmptyMessage(0);
	}

	
	private int max_value = 100;
	
	/**
	 * 设置进度当前值
	 */
	public void setProgress(int i) {
		if(loading_mode) return;
		if(i < 0) i = 0;
		if(i > max_value) i = max_value;
		angle = 360*i/max_value;
		setText(Math.round(((float) angle / 360) * 100) + "%");
		postInvalidate();
	}
	
	public int getProgress() {
		return angle*max_value/360;
	}
	
	private Scroller mScroller = new Scroller(getContext());
	private static final int Smooth_Duration = 1000;
	/**
	 * 设置进度当前值,运行这个方法后组件的数字从正在显示的值平滑渐变的变化到这个值
	 */
	public void setProgressSmooth(int i) {
		if(loading_mode) return;
		if(i < 0) i = 0;
		if(i > max_value) i = max_value;
		int temPrpgress = 360*i/max_value;
		if(temPrpgress == angle) return;
		Log.i(VIEW_LOG_TAG, "tem:" + temPrpgress + " / " + angle);
		if(!mScroller.isFinished()) mScroller.abortAnimation();
		mScroller.startScroll(angle, 0, temPrpgress - angle, 0, Smooth_Duration);
	    postInvalidate();
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
	
		if(mScroller.computeScrollOffset()){
			
			angle = mScroller.getCurrX();
			Log.v(VIEW_LOG_TAG, "progress = " + angle);
			setText(Math.round(((float) angle / 360) * 100) + "%");
			invalidate();
		}else{
			
		}
	}
	


	/**
	 * Set the text in the progress bar Doesn't invalidate the view
	 * 
	 * @param text
	 *            the text to show ('\n' constitutes a new line)
	 */
	public void setText(String text) {
		if(!show_text) return;
		this.text = text;
		splitText = this.text.split("\n");
	}
	
	
	
	
	
	
	
	
	
	
	//=======================================get set============================
	
    public boolean isLoading() {
		return loading;
	}
	
	public int getBarLength() {
		return barLength;
	}




	public void setBarLength(int barLength) {
		this.barLength = barLength;
	}




	public int getBarWidth() {
		return barWidth;
	}




	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}




	public int getBorderWidth() {
		return borderWidth;
	}




	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}




	public int getBgWidth() {
		return bgWidth;
	}




	public void setBgWidth(int bgWidth) {
		this.bgWidth = bgWidth;
	}




	public int getTextSize() {
		return textSize;
	}




	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}




	public int getStart_angle() {
		return start_angle;
	}



    /**
     * 设置起始角度
     * @param start_angle
     */
	public void setStart_angle(int start_angle) {
		this.start_angle = start_angle;
	}




	public int getBarColor() {
		return barColor;
	}



    /**
     * 设置前景色
     * @param barColor
     */
	public void setBarColor(int barColor) {
		this.barColor = barColor;
	}




	public int getBgColor() {
		return bgColor;
	}



    /**
     * 设置背景色
     * @param bgColor
     */
	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}




	public int getBorderColor() {
		return borderColor;
	}




	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}




	public int getTextColor() {
		return textColor;
	}




	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}




	public boolean isShow_text() {
		return show_text;
	}



    /**
     * 设置是否显示进度条组件中间的数字
     * @param show_text
     */
	public void setShow_text(boolean show_text) {
		this.show_text = show_text;
	}




	public int getRollSpeed() {
		return rollSpeed;
	}




	public void setRollSpeed(int rollSpeed) {
		this.rollSpeed = rollSpeed;
	}




	public int getDelayMillis() {
		return delayMillis;
	}




	public void setDelayMillis(int delayMillis) {
		this.delayMillis = delayMillis;
	}




	public int getAngle() {
		return angle;
	}




	public void setAngle(int angle) {
		this.angle = angle;
	}




	public boolean isLoading_mode() {
		return loading_mode;
	}




	public void setLoading_mode(boolean loading_mode) {
		this.loading_mode = loading_mode;
	}




	public int getMax_value() {
		return max_value;
	}



    /**
     * 设置进度最大值
     * @param max_value
     */
	public void setMax_value(int max_value) {
		this.max_value = max_value;
	}




	public String getText() {
		return text;
	}




	public static int getSmoothDuration() {
		return Smooth_Duration;
	}




	

}
