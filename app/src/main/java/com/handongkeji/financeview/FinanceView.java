package com.handongkeji.financeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.TimeUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.Calendar;
import java.util.List;

import static android.R.attr.max;
import static android.R.attr.x;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class FinanceView extends ViewGroup {

    private String[] months;
    private Paint monthPaint;
    private Rect mBounds = new Rect();
    private Paint mPaint;
    private int monthWidth;
    private float descent;
    private int monthHeight;
    private List<FinanceData> mFinanceList;
    private Rect rect;
    private int round = 5;
    private int textPadding = 10;
    private int paddingTop;


    public FinanceView(@NonNull Context context) {
        this(context, null);
    }

    public FinanceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FinanceView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initMonth();
        initPains();
    }

    private void initPains() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(15);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }


    /**
     * 初始化月份
     */
    private void initMonth() {
        months = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        monthPaint = new Paint();
        monthPaint.setColor(Color.RED);
        monthPaint.setAntiAlias(true);
        monthPaint.setStrokeWidth(2);

        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics());
        Log.i(TAG, "textSize: " + textSize);
        monthPaint.setTextSize(textSize);

        //测量月份所占高度
        monthPaint.getTextBounds(months[0], 0, months[0].length(), mBounds);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        monthWidth = w / 12;
        //文字下坡高度
        descent = monthPaint.getFontMetrics().descent;

        //月份绘制高度
        monthHeight = (int) (getMeasuredHeight() - mBounds.height() - descent);
        paddingTop = getPaddingTop();
        rect = new Rect(0, monthHeight, getMeasuredWidth(), getMeasuredHeight());

    }

    /**
     * 获取最大值
     *
     * @param financeList
     * @return
     */
    public int getMaxMoney(List<FinanceData> financeList) {
        int max = 0;
        if (financeList == null || financeList.size() == 0) {
            return 0;
        }
        for (int i = 0; i < financeList.size(); i++) {
            FinanceData financeData = financeList.get(i);

            int money = financeData.getMoney();
            if (money > max) {
                max = money;
            }
        }

        return max;
    }

    public int getY(int money,int maxValue){
        int y = 0;

        y = (int) ((1 - (double)money / (double)maxValue) * (double)(monthHeight-paddingTop))+paddingTop;

        return y;
    }


    public void setFinanceData(List<FinanceData> financeList) {
        this.mFinanceList = financeList;
        invalidate();
    }

    /**
     * 绘制财务月份图
     *
     * @param canvas
     * @param financeList
     */
    private void drawFinance(Canvas canvas, List<FinanceData> financeList) {
        if (financeList != null && financeList.size() > 0) {
            Path path = new Path();
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new CornerPathEffect(10));

            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

            int maxMoney = getMaxMoney(financeList);

            int maxValue = (maxMoney / 100 + 1) * 100;


            //创建各月份财务组成的path
            //开始
            for (int i = 0; i < financeList.size(); i++) {

                FinanceData financeData = financeList.get(i);

                int month = financeData.getMonth();
                int money = financeData.getMoney();

                int monthCenterX = getMonthCenterX(month);

                int y = getY(money, maxValue);


                if (month == 0) {
                    path.moveTo(monthCenterX, y);
                } else {
                    path.lineTo(monthCenterX, y);
                }
                if (month == currentMonth) {
                    currentMonth = i;
                    break;
                }
            }




            //完成
            canvas.drawPath(path, paint);

            for (int i = 0; i < financeList.size(); i++) {

                FinanceData financeData = financeList.get(i);
                int money = financeData.getMoney();
                int monthCenterX = getMonthCenterX(financeData.getMonth());
                int y = getY(money, maxValue);
                drawMoney(canvas, money,y, monthCenterX);

            }



            if (currentMonth < financeList.size()) {
                FinanceData financeData = financeList.get(currentMonth);
                int month = financeData.getMonth();
                int money = financeData.getMoney();

                int y = getY(money, maxValue);

                drawAlignLine(canvas, month, y);

                int monthCenterX = getMonthCenterX(month);
                drawPoint(canvas, monthCenterX, y);
//                drawMoney(canvas, money,y, monthCenterX);
            }
        }
    }



    /**
     * 绘制当月金额
     *
     * @param canvas
     * @param money
     * @param monthCenterX
     */
    private void drawMoney(Canvas canvas, int y,int money, int monthCenterX) {


        Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.money);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        BitmapDrawable drawable1 = (BitmapDrawable) drawable;

        int x = monthCenterX - monthWidth / 2;

        Bitmap bitmap = drawable1.getBitmap();

        int mBitWidth = bitmap.getWidth();
        int mBitHeight = bitmap.getHeight();
        Rect mSrcRect = new Rect(x, money, x + mBitWidth, mBitHeight);
        Rect mDestRect = new Rect(x, money, x + mBitWidth, mBitHeight);

        String content = "¥" + y;
        Rect rect = new Rect();
        monthPaint.setTextSize(24);


        monthPaint.getTextBounds(content, 0, content.length(), rect);

        int height = rect.height();
        int width = rect.width();

        Path bgPath = new Path();
        int left = monthCenterX - width / 2 - textPadding;
        int right = monthCenterX + width / 2 + textPadding;
        int top = money - height - 2 * textPadding ;


        RectF rf = new RectF();
        rf.set(left, top, right, money);


        monthPaint.setColor(0xffff7d4a);

        monthPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawRoundRect(rf, 5, 5, monthPaint);

        monthPaint.setStyle(Paint.Style.FILL);
        monthPaint.setStrokeWidth(1);
        monthPaint.setColor(0xffffffff);
        monthPaint.setTypeface(Typeface.SANS_SERIF);

        canvas.drawText(content, monthCenterX - width / 2, money - round - textPadding, monthPaint);


        Log.i(TAG, "drawMoney: " + x + "," + mBitHeight + "," + mBitWidth);
//        canvas.drawBitmap(bitmap,mSrcRect,mDestRect,mPaint);
    }

    /**
     * 绘制小圆点
     *
     * @param canvas
     * @param monthCenterX
     * @param money
     */
    private void drawPoint(Canvas canvas, int monthCenterX, int money) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1);
        canvas.drawCircle(monthCenterX, money, round, paint);

    }

    static class FinanceData {
        int month;
        int money;

        public FinanceData() {
        }

        public FinanceData(int month, int money) {
            this.month = month;
            this.money = money;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredDimension(widthMeasureSpec), getMeasuredDimension(heightMeasureSpec));
    }

    private int getMeasuredDimension(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (measureSpec == MeasureSpec.UNSPECIFIED) {
            mode = MeasureSpec.AT_MOST;
            size = 100;
        }
        return size;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    /**
     * 获取月份中线x坐标
     *
     * @param monthIndex
     * @return
     */
    private int getMonthCenterX(int monthIndex) {
        int x = getLeft() + monthIndex * monthWidth + monthWidth / 2;
        return x;
    }


    /**
     * 绘制虚线
     *
     * @param canvas
     * @param monthIndex
     * @param money
     */
    private void drawAlignLine(Canvas canvas, int monthIndex, int money) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(5);

        Path path = new Path();

        int x = getMonthCenterX(monthIndex);
        path.moveTo(x, money);
        path.lineTo(x, monthHeight);

        path.addCircle(0, 0, 3, Path.Direction.CCW);

        paint.setPathEffect(new PathDashPathEffect(path, 12, 0, PathDashPathEffect.Style.ROTATE));

        canvas.drawPath(path, paint);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawRect(rect, mPaint);

        for (int i = 0; i < months.length; i++) {
            String month = months[i];

            monthPaint.getTextBounds(month, 0, month.length(), mBounds);

            //当前月份之前所占宽度总和
            int frontWidth = monthWidth * i;
            //文字做边距
            int left = (monthWidth - mBounds.right) / 2;

            canvas.drawText(month, frontWidth + left, getMeasuredHeight() - descent, monthPaint);

        }
        drawFinance(canvas, mFinanceList);
    }


    private static final String TAG = "FinanceView";

}
