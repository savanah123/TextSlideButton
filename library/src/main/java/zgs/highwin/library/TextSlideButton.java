package zgs.highwin.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Created by zgsHighwin on 2016/6/7.
 * TextSlideButton用于多种状态的选择
 */
public class TextSlideButton extends View {

    private float mChangeX = 0;

    /**
     * 输入的文本
     */
    private String[] mTexts;

    /**
     * 文本的长度
     */
    private int textCount;

    private int mMargin = 10;
    /**
     * 外部矩形画笔
     */
    private Paint mOutPaint;

    /**
     * 内部选择的方块
     */
    private Paint mChoosePaint;

    /**
     * 获取测量的总宽度
     */
    private int mMeasureWidth;

    /**
     * 获取测量的总高度
     */
    private int mMeasureHeight;

    /**
     * 绘制文本画笔
     */
    private Paint[] mTextPaints;

    /**
     * 文本的矩形框
     */
    private Rect[] mTextRects;

    /**
     * 上一次滑动的位置
     */
    private float mLastLocation = 0;

    /**
     * 上一次滑块的坐标
     */
    private float mLastX = 0;
    /**
     * 白色方块的移动速度
     */
    private int mRectSpeed = 45;
    /**
     * 判断是否正在移动
     */
    private boolean mIsMove = false;
    /**
     * TExt
     * 滑块的位置
     */
    private int[] mLocation = new int[]{0};

    /**
     * 设置外部实体开始的颜色
     */
    private int mOutRectFColor = Color.parseColor("#ff6000");

    /**
     * 设置内部滑块开始的颜色
     */
    private int mChooseRectFColor = Color.WHITE;

    /**
     * 初始选中的颜色
     */
    private int mChooseTextColor = Color.parseColor("#ff6000");

    /**
     * 初始未被选中的颜色
     */
    private int mUnChooseTextColor = Color.BLACK;

    /**
     * 选中的文本的大小
     */
    private int mChooseTextSize = 80;

    /**
     * 未选中的文本的大小
     */
    private int mUnChooseTextSize = 50;
    /**
     * 未被选中的文本的颜色
     */
    private int[] mUnChooseTextColorArrys;
    /**
     * 选中的文本的颜色
     */
    private int[] mChooseTextColorArrys;

    /**
     * 判断是否是第一次获取onMeasure的高度值
     */
    private boolean mIsFirstClick;
    //private int mInputLocation;

    public TextSlideButton(Context context) {
        this(context, null);
    }

    public TextSlideButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSlideButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextSlideButton, defStyleAttr, 0);
        mChooseRectFColor = typedArray.getColor(R.styleable.TextSlideButton_TextSlideButton_chooseRectColor, Color.WHITE);
        mUnChooseTextSize = typedArray.getInteger(R.styleable.TextSlideButton_TextSlideButton_UnChooseTextSize, 40);
        mUnChooseTextColor = typedArray.getColor(R.styleable.TextSlideButton_TextSlideButton_UnChooseTextColor, Color.WHITE);
        mChooseTextSize = typedArray.getInteger(R.styleable.TextSlideButton_TextSlideButton_ChooseTextSize, 50);
        mChooseTextColor = typedArray.getColor(R.styleable.TextSlideButton_TextSlideButton_ChooseTextColor, Color.parseColor("#ff9500"));
        mMargin = typedArray.getInteger(R.styleable.TextSlideButton_TextSlideButton_margin, 2);
        mOutRectFColor = typedArray.getColor(R.styleable.TextSlideButton_TextSlideButton_OutRectColor, Color.parseColor("#ff9500"));
        mRectSpeed = typedArray.getInteger(R.styleable.TextSlideButton_TextSlideButton_RectSpeed, 50);
        typedArray.recycle();

        mOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutPaint.setStrokeJoin(Paint.Join.ROUND);
        mOutPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutPaint.setDither(true);
        mOutPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mOutPaint.setColor(mOutRectFColor);

        mChoosePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChoosePaint.setStrokeJoin(Paint.Join.ROUND);
        mChoosePaint.setStrokeCap(Paint.Cap.ROUND);
        mChoosePaint.setDither(true);
        mChoosePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mChoosePaint.setColor(mChooseRectFColor);
    }

    /**
     * 获取白色方块的移动速度
     *
     * @return
     */
    public int getRectSpeed() {
        return mRectSpeed;
    }

    /**
     * 设置白色方块的移动速度
     *
     * @param mRectSpeed
     */
    public void setRectSpeed(int mRectSpeed) {
        this.mRectSpeed = mRectSpeed;
    }

    /**
     * 设置方块的位置
     *
     * @param location
     */
    public void setChooseRectLocation(int location) {
        mLocation[0] = location;
        if (!mIsFirstClick) {
            postInvalidate();
        } else {
            mChangeX = mLocation[0] * mMeasureWidth / textCount;
            mLastX = mLastLocation * (mMeasureWidth / textCount);
            if (mLocation[0] > mLastLocation) {
                moveToRight();
            } else {
                moveToLeft();
            }
        }
    }

    /**
     * 设置文本内容
     *
     * @param texts
     */
    public void setTexts(String[] texts) {
        if (texts != null && texts.length != 0) {
            textCount = texts.length;
            this.mTexts = texts;
            mTextPaints = new Paint[textCount];
            mTextRects = new Rect[textCount];
            for (int i = 0; i < textCount; i++) {
                mTextPaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
                mTextPaints[i].setStrokeJoin(Paint.Join.ROUND);
                mTextPaints[i].setStrokeCap(Paint.Cap.ROUND);
                mTextPaints[i].setDither(true);
                mTextPaints[i].setStyle(Paint.Style.FILL_AND_STROKE);
                mTextPaints[i].setColor(mUnChooseTextColor);
                mTextPaints[i].setTextSize(mUnChooseTextSize);
            }
            for (int i = 0; i < textCount; i++) {
                mTextRects[i] = new Rect();
            }

            for (int i = 0; i < textCount; i++) {
                mTextPaints[i].getTextBounds(mTexts[i], 0, mTexts[i].length(), mTextRects[i]);
            }

            mUnChooseTextColorArrys = new int[textCount];
            mChooseTextColorArrys = new int[textCount];
            for (int i = 0; i < textCount; i++) {
                mUnChooseTextColorArrys[i] = mUnChooseTextColor;
                mChooseTextColorArrys[i] = mChooseTextColor;
            }
        }
    }

    /**
     * 获取TextSlideButton的文本内容
     *
     * @return
     */
    public String[] getTexts() {
        return mTexts;
    }


    /**
     * 设置指定位置的文本的内容
     *
     * @param location
     * @param text
     */
    public void setLocationText(int location, String text) {
        if (location > textCount - 1) {
            location = textCount - 1;
        }
        mTexts[location] = text;
    }

    /**
     * 设置未选择文本的颜色
     *
     * @param chooseColors
     */
    public void setUnChooseTextColor(int... chooseColors) {
        if (chooseColors.length < textCount) {
            for (int i = 0; i < chooseColors.length; i++) {
                mUnChooseTextColorArrys[i] = chooseColors[i];
            }
        } else {
            for (int i = 0; i < textCount; i++) {
                mUnChooseTextColorArrys[i] = chooseColors[i];
            }
        }
    }

    /**
     * 设置指定未选择文本颜色
     *
     * @param location
     * @param color
     */
    public void setPointUnChooseTextColor(int location, int color) {
        if (location > textCount - 1) {
            location = textCount - 1;
        }
        mUnChooseTextColorArrys[location] = color;
    }

    /**
     * 设置选中文本的颜色
     *
     * @param chooseColor
     */
    public void setChooseTextColor(int... chooseColor) {
        if (chooseColor.length < textCount) {
            for (int i = 0; i < chooseColor.length; i++) {
                mChooseTextColorArrys[i] = chooseColor[i];
            }
        } else {
            for (int i = 0; i < textCount; i++) {
                mChooseTextColorArrys[i] = chooseColor[i];
            }
        }
    }

    /**
     * 设置指定文本的颜色
     *
     * @param location
     * @param color
     */
    public void setPointChooseTextColor(int location, int color) {
        if (location > textCount - 1) {
            location = textCount - 1;
        }
        mChooseTextColorArrys[location] = color;
    }

    /**
     * 获取文本的总数
     *
     * @return
     */
    public int getTextCount() {
        return textCount;
    }

    /**
     * 获取当前滑块的位置
     *
     * @return
     */
    public int[] getLocation() {
        return mLocation;
    }

    /**
     * 设置边距大小
     *
     * @param mMargin
     */
    public void setMargin(int mMargin) {
        this.mMargin = mMargin;
    }

    /**
     * 设置选中的滑块的颜色
     *
     * @param mChooseRectFColor
     */
    public void setChooseRectFColor(int mChooseRectFColor) {
        this.mChooseRectFColor = mChooseRectFColor;
    }

    /**
     * 获取选中文本的字体颜色
     *
     * @return
     */
    public int getChooseTextColor() {
        return mChooseTextColor;
    }

    /**
     * 设置选中字体的颜色
     *
     * @param mChooseTextColor
     */
    public void setChooseTextColor(int mChooseTextColor) {
        this.mChooseTextColor = mChooseTextColor;
    }


    public void setUnChooseTextSize(int mUnChooseTextSize) {
        this.mUnChooseTextSize = mUnChooseTextSize;
    }

    public void setChooseTextSize(int mChooseTextSize) {
        this.mChooseTextSize = mChooseTextSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightsize = MeasureSpec.getSize(heightMeasureSpec);
        int realWidth;
        int realHeight;

        int eachRealWidth = 0;
        int eachMaxLength = 0;
        int[] textLengths = new int[textCount];

        for (int i = 0; i < textCount; i++) {
            textLengths[i] = (int) mTextPaints[i].measureText(mTexts[i]);
        }

        if (widthMode == MeasureSpec.EXACTLY) {  //如果是match_parent和固定的尺寸的话
            realWidth = widthSize;
            eachRealWidth = realWidth / textCount;
            eachMaxLength = eachRealWidth;
            for (int i = 0; i < textCount; i++) {
                if (textLengths[i] > eachMaxLength) {
                    eachMaxLength = textLengths[i];
                }
            }
            realWidth = eachMaxLength * textCount;
        } else { //一般是为wrap_content做的处理
            realWidth = getWidth() <= 0 ? 100 : getWidth() + mTextRects[0].left * textCount * textCount * 2;  //如果是wrap_content长度加上50
            eachRealWidth = realWidth / textCount;
            eachMaxLength = eachRealWidth;

            for (int i = 0; i < textCount; i++) {
                if (textLengths[i] > eachMaxLength) {
                    eachMaxLength = textLengths[i];
                }
            }

            realWidth = ((Math.abs(mTextRects[0].top) + Math.abs(mTextRects[0].bottom))  + eachMaxLength) * textCount;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            realHeight = heightsize;
        } else {
            realHeight = getHeight() <= 0 ? 50 : (int) (getHeight() + mTextRects[0].width() * 0.5);
        }

        if (!mIsFirstClick) {
            mChangeX = mLocation[0] * realWidth / textCount;
            mLastLocation = mLocation[0];
            mLastX = mChangeX;
            mMeasureWidth = realWidth;
            mIsFirstClick = true;
        }
        setMeasuredDimension(realWidth, realHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();

        RectF outRectF = new RectF(0, 0, mMeasureWidth, mMeasureHeight); //外面最大的圆角矩形

        RectF chooseRectF = new RectF(mMargin + mChangeX, mMargin, mMeasureWidth / (textCount) - mMargin + mChangeX, mMeasureHeight - mMargin);  //里面小的白色圆角矩形

        float halfTextWidth = (float) mMeasureWidth / (textCount * 2);

        canvas.drawRoundRect(outRectF, getMeasuredHeight() / 2, getMeasuredHeight() / 2, mOutPaint);
        canvas.drawRoundRect(chooseRectF, (mMeasureHeight - mMargin * 2) / 2, (mMeasureHeight - mMargin * 2) / 2, mChoosePaint);
        if (!mIsMove) {
            for (int i = 0; i < textCount; i++) {
                if (mLocation[0] == i) {
                    mTextPaints[i].setColor(mChooseTextColorArrys[i]);
                    mTextPaints[i].setTextSize(mChooseTextSize);
                } else {
                    mTextPaints[i].setColor(mUnChooseTextColorArrys[i]);
                    mTextPaints[i].setTextSize(mUnChooseTextSize);
                }
            }
        } else {
            for (int i = 0; i < textCount; i++) {
                mTextPaints[i].setColor(mUnChooseTextColorArrys[i]);
                mTextPaints[i].setTextSize(mUnChooseTextSize);
            }
        }

        for (int i = 0; i < textCount; i++) {
            mTextPaints[i].getTextBounds(mTexts[i], 0, mTexts[i].length(), mTextRects[i]);
        }
        for (int i = 0; i < textCount; i++) {
            canvas.drawText(mTexts[i], -mTextRects[i].left + halfTextWidth - mTextRects[i].width() / 2 + 2 * i * halfTextWidth, mMeasureHeight / 2 - mTextRects[i].height() / 2 - mTextRects[i].top, mTextPaints[i]);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        int action = event.getAction();
        float downX = 0;
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
                mIsMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsMove) {
                    mIsMove = true;
                    double eventValue = (event.getX() + mMargin - mMeasureWidth / (2 * textCount)) / (mMeasureWidth / textCount);
                    BigDecimal bigDecimal = new BigDecimal(eventValue).setScale(0, BigDecimal.ROUND_HALF_UP);
                    mLocation[0] = bigDecimal.intValue();
                    Log.i("TextSlideButton", "mLocation[0]:" + mLocation[0]);
                    if (mLocation[0] > textCount - 1) {
                        mLocation[0] = textCount - 1;
                    }

                    if (mLocation[0] < 0) {
                        mLocation[0] = 0;
                    }
                    if (mOnGetLocationListener != null) {
                        mOnGetLocationListener.showLocation(mLocation[0], mTexts[mLocation[0]]);
                    }
                    mChangeX = mLocation[0] * mMeasureWidth / textCount;
                    mLastX = mLastLocation * (mMeasureWidth / textCount);
                    float currentX = event.getX();
                    //在上一次的右边
                    if (currentX > mLastX) {
                        moveToRight();
                    } else {
                        moveToLeft();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 滑到左测
     */
    private void moveToLeft() {
        final float increase = mChangeX;
        mChangeX = mLastX;
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                        mChangeX -= mRectSpeed;
                        if (mChangeX <= increase) {

                            mChangeX = increase;
                            mLastLocation = mLocation[0];
                            mLastX = mChangeX;
                            mIsMove = false;
                            postInvalidate();
                            break;
                        }
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 滑到右侧
     */
    private void moveToRight() {
        final float increase = mChangeX;
        mChangeX = mLastX;
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                        mChangeX += mRectSpeed;
                        if (mChangeX >= increase) {
                            mChangeX = increase;
                            mLastLocation = mLocation[0];
                            mLastX = mChangeX;
                            mIsMove = false;
                            postInvalidate();
                            break;
                        }
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    private OnGetLocationListener mOnGetLocationListener;

    /**
     * 用于获取初当前滑块的位置和文本的信息
     */
    public interface OnGetLocationListener {
        /**
         * 获取当前的位置信息
         *
         * @param location 当前的位置信息
         * @param text     当前位置的文本内容
         */
        void showLocation(float location, String text);
    }

    public void setOnGetLocationListener(OnGetLocationListener mOnGetLocationListener) {
        this.mOnGetLocationListener = mOnGetLocationListener;
    }

}
