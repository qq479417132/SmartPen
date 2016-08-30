package com.cleverm.smartpen.ui.lucky;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cleverm.smartpen.R;

/**
 * Created by xiong,An android project Engineer,on 29/8/2016.
 * Data:29/8/2016  上午 11:04
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class LuckyView extends RelativeLayout {

    private static final int DEF_MIN_WIDTH = 200;
    private static final int DEF_MIN_HEIGHT = 200;

    private static final int PRIZE_COUNT = 8;
    private Runnable mLuckyRunnable;
    private ImageView mViewStart;


    private Handler mHandler = new Handler(getContext().getMainLooper());
    private LuckyItem[] mLuckyItem = new LuckyItem[PRIZE_COUNT];

    private int mWidth, mHeight;

    public LuckyView(Context context) {
        this(context,null);
    }

    public LuckyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
        initType(context, attrs);
    }



    private void initView() {
        RelativeLayout.inflate(getContext(), R.layout.util_lucky_draw_view, this);
        mLuckyRunnable = new LuckyRunnable(mHandler,mLuckyItem,luckyDrawRotation());

        mViewStart = (ImageView) findViewById(R.id.util_lucky_view_start);
        mViewStart.setOnClickListener(onClickStart());

        mLuckyItem[0] = (LuckyItem) findViewById(R.id.util_lucky_view_1);
        mLuckyItem[1] = (LuckyItem) findViewById(R.id.util_lucky_view_2);
        mLuckyItem[2] = (LuckyItem) findViewById(R.id.util_lucky_view_3);
        mLuckyItem[3] = (LuckyItem) findViewById(R.id.util_lucky_view_4);
        mLuckyItem[4] = (LuckyItem) findViewById(R.id.util_lucky_view_5);
        mLuckyItem[5] = (LuckyItem) findViewById(R.id.util_lucky_view_6);
        mLuckyItem[6] = (LuckyItem) findViewById(R.id.util_lucky_view_7);
        mLuckyItem[7] = (LuckyItem) findViewById(R.id.util_lucky_view_8);
    }



    @SuppressWarnings("deprecation")
    private void initType(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LuckyView);
        Drawable startSrc = typedArray.getDrawable(R.styleable.LuckyView_startSrc);
        Drawable startBackgroud = typedArray.getDrawable(R.styleable.LuckyView_startBackgroud);
        Drawable prizeBackgroud = typedArray.getDrawable(R.styleable.LuckyView_prizeBackgroud);
        typedArray.recycle();

        mViewStart.setImageDrawable(startSrc);
        if(startBackgroud==null){
            mViewStart.setBackgroundResource(R.drawable.lucky_draw_btn_selector);
        }else{
            mViewStart.setBackgroundDrawable(startBackgroud);
        }
        for(int i =0;i<mLuckyItem.length;i++){
            if(prizeBackgroud==null){
                mLuckyItem[i].setBackgroundResource(R.drawable.lucky_draw_item_selector);
            }else{
                mLuckyItem[i].setBackgroundDrawable(prizeBackgroud);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getMeasuredWidth()!=0&&getMeasuredHeight()!=0){
            initMeasureView(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    private void initMeasureView(int width, int height) {

        mWidth = width;
        mHeight = height;

        // 如果大小小于最小值，则重新设置大小为最小值
        if (width < DEF_MIN_WIDTH || height < DEF_MIN_HEIGHT) {
            if (width < DEF_MIN_WIDTH) {
                mWidth = DEF_MIN_WIDTH;
            }
            if (height < DEF_MIN_HEIGHT) {
                mHeight = DEF_MIN_HEIGHT;
            }
            setMeasuredDimension(mWidth, mHeight);
            return;
        }

        // 设置 奖品Imte及开始按钮的大小
        float size = (float) (((width < height ? width : height) - dp2px(8) * 2.0) / 3.0);
        ViewGroup.LayoutParams lp = mViewStart.getLayoutParams();
        lp.width = (int) size;
        lp.height = (int) size;
        mViewStart.setLayoutParams(lp);
        for (int i = 0; i < mLuckyItem.length; i++) {
            lp = mLuckyItem[i].getLayoutParams();
            lp.width = (int) size;
            lp.height = (int) size;
            mLuckyItem[i].setLayoutParams(lp);
        }


    }

    private OnLuckyRotationListener luckyDrawRotation() {
        return new OnLuckyRotationListener() {
            @Override
            public void startRotation() {
                mViewStart.setEnabled(false);
                setKeepScreenOn(true);
                if (mOnLuckyDrawListener != null) {
                    mOnLuckyDrawListener.stop();
                }
            }

            @Override
            public void stopRotation() {
                mViewStart.setEnabled(true);
                setKeepScreenOn(false);
                if (mOnLuckyDrawListener != null) {
                    mOnLuckyDrawListener.start();
                }
            }
        };
    }

    private OnClickListener onClickStart() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        };
    }

    private void start() {
        new Thread(mLuckyRunnable).start();
    }


    private class LuckyRunnable implements Runnable{


        private View[] mItems;
        private Handler mMainHandelr;
        private OnLuckyRotationListener mRotationListener;

        private final int MIN_TIME_INTERVAL = 200;		// 最小时间间隔
        private final int MAX_TIME_INTERVAL = 800;		// 最大时间间隔
        private int mTimeInterval = 800;               // 时间间隔
        private int mEnd;                               // 停止位置
        private final int ROTATION_COUNT = 3;          // 高速旋转的圈数
        private boolean isStop;                         // 是否停止状态
        private boolean isDeceleration;                 // 是否开始减速




        LuckyRunnable(Handler handler,View[] items,OnLuckyRotationListener listener){
            this.mMainHandelr=handler;
            this.mItems=items;
            this.mRotationListener=listener;
            this.mTimeInterval=MAX_TIME_INTERVAL;
            this.isDeceleration=false;
        }



        @Override
        public void run() {
            start();
            stop();
        }

        private void start(){
            isStop=false;
            firstRotationPrize();
            while(!isStop){
                rotationPrize();
            }
        }

        /**
         * 首轮的旋转
         */
        private void firstRotationPrize() {
            if (mRotationListener != null) {
                mMainHandelr.post(new Runnable() {

                    @Override
                    public void run() {
                        mRotationListener.startRotation();
                    }
                });
            }
            int position = -1;
            for (int i = 0; i < mItems.length; i++) {
                if (mItems[i].isSelected()) {
                    position = i;
                    break;
                }
            }
            for (int i = (position == -1 ? 0 : position); i < mItems.length; i++) {
                nextPrize(mItems[i]);
            }
        }


        private void nextPrize(final View v) {
            if (!isDeceleration) {
                mTimeInterval -= mTimeInterval / 8;
                if (mTimeInterval < MIN_TIME_INTERVAL) {
                    mTimeInterval = MIN_TIME_INTERVAL;
                }
            } else {
                mTimeInterval += mTimeInterval / 8;
                if (mTimeInterval > 2 * MAX_TIME_INTERVAL) {
                    mTimeInterval = 2 * MAX_TIME_INTERVAL;
                }
            }
            // 线程休眠，造成
            try {
                Thread.sleep(mTimeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 旋转到下一个item
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    cleanSelected();
                    v.setSelected(true);
                }
            });
        }

        private void cleanSelected() {
            for (int i = 0; i < mItems.length; i++) {
                mItems[i].setSelected(false);
            }
        }



        //只有搜索到后自动停止时才执行
        private void stop(){
            for (int i = 0; i < ROTATION_COUNT; i++) {
                rotationPrize();
            }
            // 停止旋转
            isDeceleration = true;
            int count = 0;
            while (true) {
                nextPrize(mItems[count++%mItems.length]);
                if (mTimeInterval >= (MAX_TIME_INTERVAL / 2)) {
                    break;
                }
            }
            lastRotationPrize(mEnd);
        }

        public void stop(int end) {
            isStop = true;
            mEnd = end;
        }

        private void rotationPrize() {
            for (int i = 0; i < mItems.length; i++) {
                nextPrize(mItems[i]);
            }
        }

        /**
         * 最后一轮转动，并转到指定的项上
         */
        private void lastRotationPrize(int end) {
            // 获取当前所在位置
            int position = 0;
            for (int i = 0; i < mItems.length; i++) {
                if (mItems[i].isSelected()) {
                    position = i;
                    break;
                }
            }
            if (position < end) {
                // 如果当前位置在停止位置后，则旋转到停止位置停止
                for (int i = ++position; i <= (end < mItems.length-1 ? end : mItems.length-1); i++) {
                    nextPrize(mItems[i]);
                }
            } else {
                // 如新当前位置在停止位置前，则先旋转到0,再继续旋转到停止位置停止
                for (int i = ++position; i < mItems.length; i++) {
                    nextPrize(mItems[i]);
                }
                for (int i = 0; i <= (end < mItems.length-1 ? end : mItems.length-1); i++) {
                    nextPrize(mItems[i]);
                }
            }
            if (mRotationListener != null) {
                mMainHandelr.post(new Runnable() {

                    @Override
                    public void run() {
                        clean();
                        mRotationListener.stopRotation();
                    }
                });
            }
        }

        private void clean() {
            isDeceleration = false;
            isStop = false;
            mTimeInterval = MAX_TIME_INTERVAL;
        }


    }


    private OnLuckyDrawListener mOnLuckyDrawListener;

    public void setOnLuckyDrawListener(OnLuckyDrawListener listener){
        this.mOnLuckyDrawListener=listener;
    }



    public interface OnLuckyDrawListener{
        void start();
        void stop();
    }

    private interface OnLuckyRotationListener{
        void startRotation();
        void stopRotation();
    }


    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }



}
