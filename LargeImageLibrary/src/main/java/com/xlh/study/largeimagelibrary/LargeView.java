package com.xlh.study.largeimagelibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author: Watler Xu
 * time:2020/4/13
 * description:
 * version:0.0.1
 */
public class LargeView extends View implements GestureDetector.OnGestureListener,
        View.OnTouchListener {

    Rect mRect;

    BitmapFactory.Options mOptions;

    GestureDetector mGestureDetector;

    Scroller mScroller;

    int mImageWidth, mImageHeight;

    int mViewWidth, mViewHeight;

    BitmapRegionDecoder mBitmapRegionDecoder;

    float mScale;

    Bitmap mBitmap;

    Matrix mMatrix;

    public LargeView(Context context) {
        this(context, null);
    }

    public LargeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LargeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    // 第1步，设置LargeView所需成员变量
    private void init(Context context) {

        mRect = new Rect();
        // 内存复用
        mOptions = new BitmapFactory.Options();
        // 手势识别
        mGestureDetector = new GestureDetector(context, this);
        // 滚动类
        mScroller = new Scroller(context);
        // 设置触摸
        setOnTouchListener(this);
    }

    // 第2步，设置图片，得到图片的信息
    public void setImage(InputStream is) {
        // 获取图片的宽高
        // 没有将整个图片加载进内存
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;

        // 开启复用
        mOptions.inMutable = true;
        // 设置图片像素格式
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        mOptions.inJustDecodeBounds = false;

        try {
            // 区域解码器
            mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        requestLayout();

    }

    // 第3步，开始测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        // 确定图片的加载区域
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mImageWidth;
        // 得到图片加载的具体高度
        // 根据图片的宽度，以及view的宽度，计算缩放因子
        mScale = mViewWidth / (float) mImageWidth;
        mRect.bottom = (int) (mViewHeight / mScale);

    }

    // 第4步，开始绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmapRegionDecoder == null) {
            return;
        }

        // 内存复用 (复用的bitmap必须跟即将解码的bitmap尺寸一样)
        mOptions.inBitmap = mBitmap;
        // 指定解码区域
        mBitmap = mBitmapRegionDecoder.decodeRegion(mRect, mOptions);
        // 得到矩阵进行缩放，得到view的大小
        mMatrix = new Matrix();
        mMatrix.setScale(mScale, mScale);
        canvas.drawBitmap(mBitmap, mMatrix, null);

    }

    // 第5步，处理点击事件
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // 直接将事件交给手势事件
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    // 第6步
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        // 如果移动没有停止，强行停止
        if(!mScroller.isFinished()){
            mScroller.forceFinished(true);
        }
        return true;
    }

    // 第7步，处理滑动事件
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
        // 上下移动时，mRect需要改变显示区域
        mRect.offset(0,(int)distanceY);
        // 移动的时候，处理到达顶部和底部的情况
        if(mRect.bottom > mImageHeight){
            mRect.bottom = mImageHeight;
            mRect.top = mImageHeight-(int)(mViewHeight/mScale);
            Log.e("LargeView","mImageHeight:"+mImageHeight+"  mViewHeight/mScale:"+mViewHeight/mScale);
        }
        if(mRect.top < 0){
            mRect.top = 0;
            mRect.bottom = (int)(mViewHeight/mScale);
        }
        // 重绘
        invalidate();
        return false;
    }

    // 第8步，处理惯性问题
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
        mScroller.fling(0,mRect.top,0,(int)-velocityY,0,0,0,mImageHeight-(int)(mViewHeight/mScale));
        return false;
    }

    //第9步，处理计算结果
    @Override
    public void computeScroll() {
        if(mScroller.isFinished()){
            return;
        }
        // 为true,滑动还没有结束
        if(mScroller.computeScrollOffset()){
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mRect.top+(int)(mViewHeight/mScale);
            invalidate();
        }
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }



}
