package com.mindpin.android;

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
import com.mindpin.android.kcroundprogressbar.R;

/**
 * An indicator of progress, similar to Android's ProgressBar. Can be used in
 * 'spin mode' or 'increment mode'
 * 
 * @author Todd Davies
 * 
 *         Licensed under the Creative Commons Attribution 3.0 license see:
 *         http://creativecommons.org/licenses/by/3.0/
 */
public class KCRoundProgressBar extends View {
	

	// Sizes (with defaults)
	private int layout_height = 0;
	private int layout_width = 0;
	private int fullRadius = 100;
	private int circleRadius = 80;
	
	private int fg_length = 60;
	private int fg_width = 10;
	private int border_width = 0;
	private int bg_width;
	private int text_size = 18;
	private int start_angle = -90;

	// Padding (with defaults)
	private int paddingTop = 5;
	private int paddingBottom = 5;
	private int paddingLeft = 5;
	private int paddingRight = 5;

	// Colors (with defaults)
	private int fg_color = 0x87ceeb;
	private int bg_color =  0xaa888888;
	private int border_color;
	private int text_color = 0xFF000000;

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
	private boolean text_display = true;
	

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
	public KCRoundProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		//parsePadding(context, attrs);
		parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.KCRoundProgressBar));
		
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
		barPaint.setColor(fg_color);
		barPaint.setAntiAlias(true);
		barPaint.setStyle(Style.STROKE);
		barPaint.setStrokeWidth(fg_width);

		borderPaint.setColor(border_color);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(border_width);

		bgPaint.setColor(bg_color);
		bgPaint.setAntiAlias(true);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(bg_width);

		textPaint.setColor(text_color);
		textPaint.setStyle(Style.FILL);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(text_size);
		
		text_height = getFontHeight(text_size);
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
        
		if(border_width > 0){
			float hw = border_width/2.0f;
			borderBounds = new RectF(rectBounds.left + hw, rectBounds.top + hw, 
					rectBounds.right - hw, rectBounds.bottom - hw);
			
			rectBounds.inset(2*border_width, 2*border_width);
		}
		
		if(bg_width > 0){
			float hw = bg_width/2.0f;
			bgBounds = new RectF(rectBounds.left + hw, rectBounds.top + hw, 
					rectBounds.right - hw, rectBounds.bottom - hw);
		}
		
		float hw = fg_width/2.0f;
		barBounds = new RectF(rectBounds.left + hw, rectBounds.top + hw, 
				rectBounds.right - hw, rectBounds.bottom - hw);
		
		Log.i(VIEW_LOG_TAG, "RECT: " + rectBounds + " <> " + barBounds);
		
		Log.d(VIEW_LOG_TAG, "bar_width = " + fg_width + " rim_width = " + border_width);
		fullRadius = (this.getLayoutParams().width - paddingRight - fg_width) / 2;
		circleRadius = (fullRadius - fg_width) + 1;
	}

	/**
	 * Parse the attributes passed to the view from the XML
	 * 
	 * @param a
	 *            the attributes to parse
	 */
	private void parseAttributes(TypedArray a) {
		int tem = a.getInt(R.styleable.KCRoundProgressBar_progress, angle);
		angle = 360*(tem - min)/(max - min);
		
		text_display = a.getBoolean(R.styleable.KCRoundProgressBar_text_display, text_display);
		max = a.getInt(R.styleable.KCRoundProgressBar_max_value, max);
		min = a.getInt(R.styleable.KCRoundProgressBar_min_value, min);
		
		loading_mode = a.getBoolean(R.styleable.KCRoundProgressBar_loading_mode, loading_mode);
		
		start_angle = a.getInteger(R.styleable.KCRoundProgressBar_start_angle, start_angle);
		
		fg_width = (int) a.getDimension(R.styleable.KCRoundProgressBar_fg_width, fg_width);
		bg_width = (int) a.getDimension(R.styleable.KCRoundProgressBar_bg_width, fg_width);
    
		border_width = (int) a.getDimension(R.styleable.KCRoundProgressBar_border_width, border_width);

		rollSpeed = (int) a.getDimension(R.styleable.KCRoundProgressBar_roll_speed, rollSpeed);

		delayMillis = (int) a.getInteger(R.styleable.KCRoundProgressBar_roll_delay_millis, delayMillis);
		if (delayMillis < 0) {
			delayMillis = 0;
		}

		fg_color = a.getColor(R.styleable.KCRoundProgressBar_fg_color, fg_color);
		border_color = a.getColor(R.styleable.KCRoundProgressBar_border_color, fg_color);
		bg_color = a.getColor(R.styleable.KCRoundProgressBar_bg_color, bg_color);

		fg_length = (int) a.getDimension(R.styleable.KCRoundProgressBar_fg_length, fg_length);

		text_size = (int) a.getDimension(R.styleable.KCRoundProgressBar_text_size, text_size);

		text_color = (int) a.getColor(R.styleable.KCRoundProgressBar_text_color, text_color);

		// if the text is empty , so ignore it
		if (a.hasValue(R.styleable.KCRoundProgressBar_text)) {
			setText(a.getString(R.styleable.KCRoundProgressBar_text));
		}

		

		// Recycle
		a.recycle();
	}

	// ----------------------------------
	// Animation stuff
	// ----------------------------------

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Log.v(VIEW_LOG_TAG, "---onDraw----" + angle);
		// Draw the rim
		if(border_width > 0){
			canvas.drawArc(borderBounds, 360, 360, false, borderPaint);
		}
		
		//draw the bg
		if(bg_width > 0){
			canvas.drawArc(bgBounds, 360, 360, false, bgPaint);
		}
				
		// Draw the bar
		if (loading_mode) {
			canvas.drawArc(barBounds, angle + start_angle, fg_length, false, barPaint);
		} else {
			canvas.drawArc(barBounds, start_angle, angle, false, barPaint);
		}
		
		if(text_display){
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

	
	private int max = 100;
	private int min = 0;
	
	/**
	 * 设置进度当前值
	 */
	public void set_current(int i) {
		if(loading_mode) return;
		if(i < min) i = min;
		if(i > max) i = max;
		angle = 360*(i - min)/(max - min);
		setText(Math.round(((float) angle / 360) * 100) + "%");
		postInvalidate();
	}
	
	public int getProgress() {
		return angle*(max - min)/360;
	}
	
	private Scroller mScroller = new Scroller(getContext());
	private static final int Smooth_Duration = 1000;
	/**
	 * 设置进度当前值,运行这个方法后组件的数字从正在显示的值平滑渐变的变化到这个值
	 */
	public void set_current_smooth(int i) {
		if(loading_mode) return;
		if(i < min) i = min;
		if(i > max) i = max;
		int temPrpgress = 360*(i - min)/(max - min);
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
			//Log.v(VIEW_LOG_TAG, "progress = " + angle);
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
		if(!text_display) return;
		this.text = text;
		splitText = this.text.split("\n");
	}
	
	
	
	
	
	
	
	
	
	
	//=======================================get set============================
	
    public boolean isLoading() {
		return loading;
	}
	
	public int get_fg_length() {
		return fg_length;
	}




	public void set_fg_length(int barLength) {
		this.fg_length = barLength;
	}




	public int get_fg_width() {
		return fg_width;
	}




	public void set_fg_width(int barWidth) {
		this.fg_width = barWidth;
	}




	public int get_border_width() {
		return border_width;
	}




	public void set_border_width(int borderWidth) {
		this.border_width = borderWidth;
	}




	public int get_bg_width() {
		return bg_width;
	}




	public void set_bg_width(int bgWidth) {
		this.bg_width = bgWidth;
	}




	public int get_text_size() {
		return text_size;
	}




	public void set_text_size(int textSize) {
		this.text_size = textSize;
	}




	public int get_start_angle() {
		return start_angle;
	}



    /**
     * 设置起始角度
     * @param start_angle
     */
	public void set_start_angle(int start_angle) {
		this.start_angle = start_angle;
	}




	public int get_fg_color() {
		return fg_color;
	}



    /**
     * 设置前景色
     * @param barColor
     */
	public void set_fg_color(int barColor) {
		this.fg_color = barColor;
	}




	public int get_bg_color() {
		return bg_color;
	}



    /**
     * 设置背景色
     * @param bgColor
     */
	public void set_bg_color(int bgColor) {
		this.bg_color = bgColor;
	}




	public int get_border_color() {
		return border_color;
	}




	public void set_border_color(int borderColor) {
		this.border_color = borderColor;
	}




	public int get_text_color() {
		return text_color;
	}




	public void set_text_color(int textColor) {
		this.text_color = textColor;
	}




	public boolean is_text_display() {
		return text_display;
	}



    /**
     * 设置是否显示进度条组件中间的数字
     * @param show_text
     */
	public void set_text_display(boolean show_text) {
		this.text_display = show_text;
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

	public int get_min() {
		return min;
	}



    /**
     * 设置进度最大值
     * @param max_value
     */
	public void set_min(int max_value) {
		this.min = min;
	}


	public int get_max() {
		return max;
	}



    /**
     * 设置进度最大值
     * @param max_value
     */
	public void set_max(int max_value) {
		this.max = max_value;
	}




	public String getText() {
		return text;
	}




	public static int getSmoothDuration() {
		return Smooth_Duration;
	}




	

}
