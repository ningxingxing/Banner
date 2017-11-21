package com.example.apple.banner;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BannerView mBannerView;
    private SimpleDraweeView simpleDraweeView;
    private ArrayList<String> mUrlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.sdv);
        Uri uri = Uri.parse("http://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Hukou_Waterfall.jpg/800px-Hukou_Waterfall.jpg")
        ;
        simpleDraweeView.setImageURI(uri);

        mBannerView = (BannerView) findViewById(R.id.banner_view);
        mUrlList.add("http://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Hukou_Waterfall.jpg/800px-Hukou_Waterfall.jpg")
        ;
        mUrlList.add("http://omoml61n3.bkt.clouddn.com/6A5502D1-BD0C-4047-919A-28D311F64665.png");
        mUrlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1490783056273&di=6160d101d31dcf5f44b443ad9c5b2648&imgtype=0&src=http%3A%2F%2Fimg.sc115.com%2Fuploads%2Fallimg%2F110626%2F2011062622383898.jpg")
        ;
        mUrlList.add("http://p0.so.qhmsg.com/sdr/600_900_/t01d43698fbeca29695.jpg");
        mUrlList.add("http://p2.so.qhmsg.com/sdr/599_900_/t019e91b7618003e862.jpg");
        mBannerView.setShowNumber(true);
        mBannerView.setmSlideSpeed(500);
        mBannerView.setmBannerList(mUrlList);
        mBannerView.setmDelayTime(2000);


        mBannerView.setBannerClickListener(new BannerView.BannerClickListener() {
            @Override
            public void bannerClick(View view) {
// scroller.startScroll(0,0,0,200,2000);
                Toast.makeText(getApplication(), "setBannerClickListener", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBannerView.removeHandler();
    }
}