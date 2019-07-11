package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;
    private static final float DEFAULT_NEEDLE_STROKE_WIDTH = 0.015f;

    private static final String [] HOUR_TEXT_VALUES = {"01", "02", "03", "04", "05", "06",
                                                        "07", "08", "09", "10", "11", "12"};

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int secondNeedleLength;
    private int hoursNeedleLength;
    private int minutesNeedleLength;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();     // 设置的宽度?
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        size = Math.min(widthWithoutPadding, heightWithoutPadding);         // 正方形?
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = Math.min(getWidth(), getHeight());

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        hoursNeedleLength = (int) (mRadius * 0.45f);
        minutesNeedleLength = (int) (mRadius * 0.55f);
        secondNeedleLength = (int) (mRadius * 0.7f);


        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }
    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.1f);
        textPaint.setColor(numbersColor);
        textPaint.setTextAlign(Paint.Align.CENTER);

        int rText = (int)(mRadius * 0.75);
        Rect rect = new Rect();
        for (int i = 0; i < 12; i ++) {
            String num = HOUR_TEXT_VALUES[(i + 11) % 12];
            int centerX = mCenterX + (int)(rText * Math.sin(Math.toRadians(360 * i / 12)));
            int centerY = mCenterY - (int)(rText * Math.cos(Math.toRadians(360 * i / 12)));
            textPaint.getTextBounds(num, 0, num.length(), rect);
            int baseLineY = (int)(centerY + rect.height()/2);
            canvas.drawText(num, centerX, baseLineY, textPaint);
        }
    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {

        // set paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);    // 填充内部和描边
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_NEEDLE_STROKE_WIDTH);

        // get system's time
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // draw hour needle
        paint.setColor(this.hoursNeedleColor);
        int degree = (360 * hour / 12 + 30 * minute / 60) % 360;
        int stopX = mCenterX + (int)(this.hoursNeedleLength * Math.sin(Math.toRadians(degree)));
        int stopY = mCenterY - (int)(this.hoursNeedleLength * Math.cos(Math.toRadians(degree)));
        canvas.drawLine(mCenterX, mCenterY, stopX, stopY, paint);

        // draw minute needle
        paint.setColor(this.minutesNeedleColor);
        stopX = mCenterX + (int)(this.minutesNeedleLength * Math.sin(Math.toRadians(360 * minute / 60)));
        stopY = mCenterY - (int)(this.minutesNeedleLength * Math.cos(Math.toRadians(360 * minute / 60)));
        canvas.drawLine(mCenterX, mCenterY, stopX, stopY, paint);

        // draw second needle
        paint.setColor(this.secondsNeedleColor);
        paint.setStrokeWidth(mWidth * DEFAULT_NEEDLE_STROKE_WIDTH * 0.5f);
        stopX = mCenterX + (int)(this.secondNeedleLength * Math.sin(Math.toRadians(360 * second / 60)));
        stopY = mCenterY - (int)(this.secondNeedleLength * Math.cos(Math.toRadians(360 * second / 60)));
        canvas.drawLine(mCenterX, mCenterY, stopX, stopY, paint);

    }


    private void drawCenter(Canvas canvas) {

        Paint centerPaint = new Paint();
        centerPaint.setStyle(Paint.Style.FILL);

        float innerRadius = mRadius * 0.02f;
        float outerRadius = mRadius * 0.04f;

        // draw outer circle
        centerPaint.setColor(centerOuterColor);
        canvas.drawCircle(mCenterX, mCenterY, outerRadius, centerPaint);

        // draw inner circle
        centerPaint.setColor(centerInnerColor);
        canvas.drawCircle(mCenterX, mCenterY, innerRadius, centerPaint);
    }

    // 开放一个方法用于外部的调用，改变自定义 view 的属性
    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();       // repaint the view
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

    public void updateTime() {
        invalidate();
    }

}