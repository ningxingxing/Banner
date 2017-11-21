package com.example.apple.banner;

/**
 * Created by apple on 17/11/21.
 */

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 550211 on 2017/11/13.
 */

public class BannerAdapter extends PagerAdapter {
    private final String TAG = "BannerAdapter";

    private ArrayList<View> mBannerList;


    public BannerAdapter(ArrayList<View> mBannerList) {
        this.mBannerList = mBannerList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mBannerList.get(position));//删除页卡
    }

    @Override
    public int getCount() {
// return Integer.MAX_VALUE;
        return mBannerList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
// ViewGroup parent = (ViewGroup) mBannerList.get(position%mBannerList.size()).getParent();
// if (parent != null) {
// parent.removeView(mBannerList.get(position%mBannerList.size()));
// }
// container.addView(mBannerList.get(position%mBannerList.size()));
// // Log.e(TAG,"position="+position);
// return mBannerList.get(position%mBannerList.size());

        position %= mBannerList.size();
        if (position < 0) {
            position = mBannerList.size() + position;
        }
        ImageView view = (ImageView) mBannerList.get(position);
// 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
// add listeners here if necessary

        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
