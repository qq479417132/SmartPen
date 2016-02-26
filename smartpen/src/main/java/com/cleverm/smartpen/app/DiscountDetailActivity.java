package com.cleverm.smartpen.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.bean.DiscountAdInfo;
import com.cleverm.smartpen.bean.DiscountInfo;
import com.cleverm.smartpen.bean.DiscountRollInfo;
import com.cleverm.smartpen.ui.banner.BGABanner;
import com.cleverm.smartpen.ui.banner.NoTouchBGABanner;
import com.cleverm.smartpen.util.QuickUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by xiong,An android project Engineer,on 2016/2/22.
 * Data:2016-02-22  17:54
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class DiscountDetailActivity extends BaseBackActivity {

    public static final String INTENT_NAME = "DiscountDetailActivity";

    private Activity mContext;

    private boolean isMoreInfo = true;

    DiscountInfo info;
    NoTouchBGABanner vpImage;
    TextView tvDiscountDetailTime;
    TextView tvDiscountDetailDesc;
    TextView tvDiscountDetailTitle;

    List<DiscountRollInfo> rollDetailList;
    List<DiscountAdInfo> advertisementList;

    ImageView ivDiscountDetailImage1;
    ImageView ivDiscountDetailImage2;
    ImageView ivDiscountDetailImage3;

    ImageView ivDisountImage;


    @Override
    protected void onCreate() {
        mContext = this;
        initIntent();
        initContent();
        initView();
        initBanner();
        initData();
    }

    @Override
    protected ImageView getBackResId() {
        return (ImageView) findViewById(R.id.ivClose);
    }



    private void initContent() {
        if (info.getTitle() == null && info.getDescriptionText() == null) {
            setContentView(R.layout.image_discount_detail);
            isMoreInfo = false;
        } else {
            setContentView(R.layout.activity_discount_detail);
            isMoreInfo = true;
        }
    }


    private void initIntent() {
        Intent intent = getIntent();
        info = (DiscountInfo) intent.getSerializableExtra(DiscountDetailActivity.INTENT_NAME);
    }


    private void initView() {
        if (isMoreInfo) {
            vpImage = (NoTouchBGABanner) findViewById(R.id.BGADetailImage);
            tvDiscountDetailTime = (TextView) findViewById(R.id.tvDiscountDetailTime);
            tvDiscountDetailTitle = (TextView) findViewById(R.id.tvDiscountDetailTitle);
            tvDiscountDetailDesc = (TextView) findViewById(R.id.tvDiscountDetailDesc);

            ivDiscountDetailImage1 = (ImageView) findViewById(R.id.ivDiscountDetailImage1);
            ivDiscountDetailImage2 = (ImageView) findViewById(R.id.ivDiscountDetailImage2);
            ivDiscountDetailImage3 = (ImageView) findViewById(R.id.ivDiscountDetailImage3);
        } else {
            ivDisountImage = (ImageView) findViewById(R.id.ivDisountImage);
        }

    }

    private void initBanner() {
        //轮播
        rollDetailList = info.getRollDetailList();
        //广告
        advertisementList = info.getAdvertisementList();
    }


    private void initData() {
        if (isMoreInfo) {
            tvDiscountDetailTitle.setText(info.getTitle() + "");
            tvDiscountDetailDesc.setText(info.getDescriptionText() + "");
            //处理时间区间
            if (QuickUtils.isSameDay(info.getStartTime(), info.getEndTime())) {
                tvDiscountDetailTime.setText("活动时间：" + QuickUtils.timeStamp2Date(info.getEndTime().toString(), "yyyy-MM-dd"));
            } else {
                tvDiscountDetailTime.setText("活动时间：" + QuickUtils.timeStamp2Date(info.getStartTime().toString(), "yyyy-MM-dd") + "至" + QuickUtils.timeStamp2Date(info.getEndTime().toString(), "yyyy-MM-dd"));
            }
            handlerAd();
            handlerBanner();
        } else {
            //读取rollDetailList的第一个数据
            if (rollDetailList.size() > 0) {
                Picasso.with(this).load(QuickUtils.spliceUrl(info.getRollDetailList().get(0).getPictruePath())).into(ivDisountImage);
            } else {
                ivDisountImage.setImageResource(R.mipmap.discount_background);
            }
        }
    }


    private void handlerAd() {
        if (advertisementList.size() == 1) {
            ivDiscountDetailImage1.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(0).getPictruePath())).into(ivDiscountDetailImage1);
        } else if (advertisementList.size() == 2) {
            ivDiscountDetailImage1.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(0).getPictruePath())).into(ivDiscountDetailImage1);
            ivDiscountDetailImage2.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(1).getPictruePath())).into(ivDiscountDetailImage2);
        } else if (advertisementList.size() == 3) {
            ivDiscountDetailImage1.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(0).getPictruePath())).into(ivDiscountDetailImage1);
            ivDiscountDetailImage2.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(1).getPictruePath())).into(ivDiscountDetailImage2);
            ivDiscountDetailImage3.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(2).getPictruePath())).into(ivDiscountDetailImage3);
        }
    }

    private void handlerBanner() {
        vpImage.setTransitionEffect(BGABanner.TransitionEffect.Default);
        vpImage.setPageChangeDuration(3000);

        List<View> views = QuickUtils.getViews(mContext, rollDetailList.size());
        vpImage.setViews(views);

        for (int i = 0; i < views.size(); i++) {
            //通过给ImageView外套了一个RL解决在ViewPager中图片显示不全的BUG
            View rootView = views.get(i);
            ImageView view = (ImageView) rootView.findViewById(R.id.ivDisountImage);
            Picasso.with(this).load(QuickUtils.spliceUrl(rollDetailList.get(i).getPictruePath())).placeholder(R.mipmap.discount_background).into(view);

        }
    }

    @Override
    protected void onBack() {
        finish();
    }
}
