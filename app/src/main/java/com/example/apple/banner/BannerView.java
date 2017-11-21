package com.example.apple.banner;

/**
 * Created by apple on 17/11/21.
 */

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by 550211 on 2017/11/13.
 */

public class BannerView extends FrameLayout {
    private final String TAG = "BannerView";
    private ViewPager mViewPager = null;
    private LinearLayout mLinearPosition = null;
    private BannerHandler mBannerHandler = null;
    private ArrayList<View> mBannerList = new ArrayList<>();
    private Context mContext = null;

    private BannerClickListener bannerClickListener;
    private boolean isMove = false;
    private final static int UPDATE_IMAGE = 0;

    private int mCurrentItem = 0;
    private BannerAdapter mBannerAdapter;
    private int mDelayTime = 2000;//等待切换时间，定时器定时时间
    private boolean isLoop = true;
    private int mSlideSpeed = 500;//设置切换过程中速度
    private boolean isShowNumber = false;
    private boolean isShowPoint = true;

    private TextView textView = null;
    private ImageView imageView = null;
    private ImageView[] views = null;

    public interface BannerClickListener {
        void bannerClick(View view);
    }

    public void setBannerClickListener(BannerClickListener bannerClickListener) {
        this.bannerClickListener = bannerClickListener;
    }

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        initView();
        initBottomLinearLayout();
        this.addView(mViewPager);
        this.addView(mLinearPosition);
    }

    public boolean isShowPoint() {
        return isShowPoint;
    }

    public void setShowPoint(boolean showPoint) {
        isShowPoint = showPoint;
    }

    public boolean isShowNumber() {
        return isShowNumber;
    }

    public void setShowNumber(boolean showNumber) {
        isShowNumber = showNumber;
    }

    public int getmSlideSpeed() {
        return mSlideSpeed;
    }

    public void setmSlideSpeed(int mSlideSpeed) {
        this.mSlideSpeed = mSlideSpeed;
    }

    public int getmDelayTime() {
        return mDelayTime;
    }

    public void setmDelayTime(int mDelayTime) {
        this.mDelayTime = mDelayTime;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        this.isLoop = loop;
        if (isLoop) {
            if (mBannerList != null && mBannerList.size() > 1) {
                mBannerHandler = new BannerHandler(this);
                mBannerHandler.postDelayed(mRunnable, mDelayTime);
            } else {
                mBannerHandler.removeCallbacks(mRunnable);
            }
        }
    }

    public ArrayList<View> getmBannerList() {
        return mBannerList;
    }

    public void setmBannerList(ArrayList<String> list) {
        if (mBannerList != null) {
            mBannerList.clear();
        }

        if (list != null && list.size() > 0) {
            list.add(0, list.get(0));
            list.add(list.size(), list.get(0));

            for (int i = 0; i < list.size(); i++) {
                SimpleDraweeView simpleDraweeView = (SimpleDraweeView) inflate(mContext, R.layout.facebook_image, null);
                simpleDraweeView.setImageURI(Uri.parse(list.get(i)));
                mBannerList.add(simpleDraweeView);
            }
        }

        if (mBannerList != null && mBannerList.size() > 0) {
            mBannerAdapter = new BannerAdapter(mBannerList);
            mViewPager.setAdapter(mBannerAdapter);
// addBottomImage(0);
            addTextView(1);
            addBottomPoint(0);
            setViewPagerSpeed();
            mViewPager.setCurrentItem(1, false);//初始设置第二张
            if (mBannerList.size() > 1) {
                mBannerHandler = new BannerHandler(this);
                mBannerHandler.postDelayed(mRunnable, 0);
            }
        }
    }


    private void initView() {
        mViewPager = new ViewPager(mContext);
//设置viewPager 切换的速度
        setViewPagerSpeed();

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mViewPager.setLayoutParams(layoutParams);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentItem = position;
                textView.setText((position) + "/" + (mBannerList.size() - 2));
            }

            @Override
            public void onPageSelected(int position) {
//直接移除消耗2-6毫秒之间
// mLinearPosition.removeAllViews();
// addTextView(position);
// addBottomImage(position);

//此方法消耗1-2毫秒之间
                for (int i = 0; i < mBannerList.size() - 2; i++) {
                    if ((position - 1) == i) {
                        views[i].setImageResource(R.drawable.btn_radio_on_focused_holo_dark);
                    } else {
                        views[i].setImageResource(R.drawable.btn_radio_off_focused_holo_light);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state != ViewPager.SCROLL_STATE_IDLE)
                    return;
                if (mCurrentItem == mBannerList.size() - 1) {
                    mViewPager.setCurrentItem(1, false);
                }
            }
        });
//设置页面切换动画效果
        mViewPager.setPageTransformer(true, new ScrollOffsetTransformer());

        mViewPager.setOnTouchListener(new OnTouchListener() {
            float downX = 0;
            float downY = 0;
            float lastX, lastY = 0;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        isMove = false;
                        mBannerHandler.removeCallbacks(mRunnable);
                        downX = motionEvent.getX();
                        downY = motionEvent.getY();
                        break;

                    case MotionEvent.ACTION_MOVE://手指移动时移除定时器
                        isMove = true;
                        lastX = motionEvent.getX();
                        lastY = motionEvent.getY();

                        break;

                    case MotionEvent.ACTION_UP:
                        if (isMove == false) {//点击添加监听回调
                            if (bannerClickListener != null) {
                                bannerClickListener.bannerClick(view);
                            }
                        } else {
                            if ((lastX - downX) < -50) {//左滑的时候，显示第1张
                                if (mCurrentItem == mBannerList.size() - 1) {
                                    mViewPager.setCurrentItem(0, false);
                                }
                            } else if ((lastX - downX) > 50) {//右滑的时候显示倒数第二张
                                if (mCurrentItem == 0) {
                                    mViewPager.setCurrentItem(mBannerList.size() - 2, false);
                                }
                            }
                        }
                        mBannerHandler.removeCallbacks(mRunnable);
                        mBannerHandler.postDelayed(mRunnable, 2000);
                        break;
                }

                return false;
            }
        });
    }

    private void setViewPagerSpeed() {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
            field.set(mViewPager, scroller);
//设置mSlideSpeed时间要小于定时器时间
            if (mDelayTime > 500 && mSlideSpeed >= mDelayTime - 500) {
                mSlideSpeed = mDelayTime - 500;
            } else if (mDelayTime <= 500 && mSlideSpeed >= 500) {
                mSlideSpeed = mDelayTime - 100;
            }
            scroller.setmDuration(mSlideSpeed);
        } catch (Exception e) {
            Log.e(TAG, "error=" + e.getMessage());
        }
    }
    /**
     * 添加底部显示哪张图片
     *
     * @param position
     */
    private void addBottomImage(int position) {
        for (int i = 0; i < mBannerList.size() - 2; i++) {
//SimpleDraweeView imageView = (SimpleDraweeView) LayoutInflater.from(getContext()).inflate(R.layout.facebook_image, mLinearPosition, false);
            imageView = new ImageView(mContext);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.width = 50;
            layoutParams.leftMargin = 100;
            imageView.setLayoutParams(layoutParams);

            if (isShowPoint) {
                imageView.setVisibility(VISIBLE);
            } else {
                imageView.setVisibility(GONE);
            }

            if (i == (position - 1)) {
                imageView.setImageResource(R.drawable.btn_radio_on_focused_holo_dark);
            } else {
                imageView.setImageResource(R.drawable.btn_radio_off_focused_holo_light);
            }
            mLinearPosition.addView(imageView);
        }
    }

    /**
     * 底部圆圈实现
     *
     * @param position
     */
    private void addBottomPoint(int position) {
        views = new ImageView[mBannerList.size()];
        for (int i = 0; i < mBannerList.size() - 2; i++) {
            views[i] = new ImageView(mContext);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.width = 50;
            layoutParams.leftMargin = 100;
            views[i].setLayoutParams(layoutParams);

            if (isShowPoint) {
                views[i].setVisibility(VISIBLE);
            } else {
                views[i].setVisibility(GONE);
            }

            if (i == (position - 1)) {
                views[i].setImageResource(R.drawable.btn_radio_on_focused_holo_dark);
            } else {
                views[i].setImageResource(R.drawable.btn_radio_off_focused_holo_light);
            }
            mLinearPosition.addView(views[i]);
        }

    }

    /**
     * 文字显示哪张图片
     */
    private void addTextView(int position) {
        textView = new TextView(mContext);
        if (position > (mBannerList.size() - 2)) {
            position = mBannerList.size() - 2;
        }
        if (isShowNumber) {
            textView.setVisibility(VISIBLE);
        } else {
            textView.setVisibility(GONE);
        }
        textView.setText((position) + "/" + (mBannerList.size() - 2));
        mLinearPosition.addView(textView);
    }

    private void initBottomLinearLayout() {
        mLinearPosition = new LinearLayout(getContext());
        mLinearPosition.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 100);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        mLinearPosition.setLayoutParams(layoutParams);
    }

    private static class BannerHandler extends Handler {

        private WeakReference weakReference;

        public BannerHandler(BannerView parent) {
            super(Looper.getMainLooper());
            weakReference = new WeakReference(parent);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case UPDATE_IMAGE:
//mViewPager.setCurrentItem(mCurrentItem, true);
                    BannerView parent = (BannerView) weakReference.get();
                    if (parent != null) {
                        parent.mViewPager.setCurrentItem(parent.mCurrentItem, true);
                    }
                    break;
            }
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if (mCurrentItem == mBannerList.size() - 1) {
                mCurrentItem = 0;
            } else {
                mCurrentItem++;
            }

            mBannerHandler.postDelayed(this, mDelayTime);
            mBannerHandler.sendEmptyMessage(UPDATE_IMAGE);
        }
    };


    /**
     * 移除定时器
     */
    public void removeHandler() {
        if (mBannerHandler != null) {
            mBannerHandler.removeCallbacksAndMessages(null);
            mBannerHandler = null;
        }
    }

}