package com.cleverm.smartpen.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.adapter.DiscountDetailAdapter;
import com.cleverm.smartpen.adapter.DiscountDetailPagerAdapter;
import com.cleverm.smartpen.bean.DiscountAdInfo;
import com.cleverm.smartpen.bean.DiscountInfo;
import com.cleverm.smartpen.bean.DiscountRollInfo;
import com.cleverm.smartpen.ui.CircleIndicator;
import com.cleverm.smartpen.ui.ListViewForScrollView;
import com.cleverm.smartpen.ui.banner.BGABanner;
import com.cleverm.smartpen.ui.banner.NoTouchBGABanner;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.StatisticsUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

    private boolean isDeprecated=true;

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


    ListViewForScrollView lvAdList;
    ViewPager vpView;
    CircleIndicator ciIndicator;


    /**
     * 该类不做主事件统计中去,做二级广告id的统计
     * @return
     */
    @Override
    protected int onGetEventId() {
        return StatisticsUtil.ERROR_AND_NOE_STAISTICS;
    }

    @Override
    protected String onGetDesc() {
        return "";
    }

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

            vpView= (ViewPager) findViewById(R.id.vpView);
            lvAdList= (ListViewForScrollView) findViewById(R.id.lvAdList);
            ciIndicator= (CircleIndicator) findViewById(R.id.ciIndicator);

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
                tvDiscountDetailTime.setText("活动时间：" + QuickUtils.timeStamp2DateNoSec(info.getEndTime().toString(), "yyyy-MM-dd"));
            } else {
                tvDiscountDetailTime.setText("活动时间：" + QuickUtils.timeStamp2DateNoSec(info.getStartTime().toString(), "yyyy-MM-dd") + "至" + QuickUtils.timeStamp2DateNoSec(info.getEndTime().toString(), "yyyy-MM-dd"));
            }
            if(isDeprecated){
                handlerNewAd();
                handlerNewBanner();
            }else{
                handlerAd();
                handlerBanner();
            }

        } else {
            //读取rollDetailList的第一个数据
            if (rollDetailList.size() > 0) {
                QuickUtils.displayImage(QuickUtils.spliceUrl(info.getRollDetailList().get(0).getPictruePath(),info.getRollDetailList().get(0).getQiniuPath()),ivDisountImage);
                //Picasso.with(this).load(QuickUtils.spliceUrl(info.getRollDetailList().get(0).getPictruePath())).into(ivDisountImage);
            } else {
                ivDisountImage.setImageResource(R.mipmap.discount_background);
            }
        }
        
        scrollToTop();
        
        
    }




    private void handlerNewAd() {
        DiscountDetailAdapter detailAdapter = new DiscountDetailAdapter(this, advertisementList);
        lvAdList.setAdapter(detailAdapter);
    }

    private void handlerNewBanner() {
        List<View> view_s = QuickUtils.getViews(mContext, rollDetailList.size());
        vpView.setAdapter(new DiscountDetailPagerAdapter(view_s));
        if(view_s.size()>1){
            ciIndicator.setViewPager(vpView);
        }


        for(int i =0 ;i < view_s.size();i++){
            View rootView = view_s.get(i);
            ImageView view = (ImageView) rootView.findViewById(R.id.ivDisountImage);
            QuickUtils.displayImage(QuickUtils.spliceUrl(rollDetailList.get(i).getPictruePath(),rollDetailList.get(i).getQiniuPath()), view);
            //Picasso.with(this).load(QuickUtils.spliceUrl(rollDetailList.get(i).getPictruePath())).placeholder(R.mipmap.discount_background).into(view);
        }
    }


    @Deprecated
    private void handlerAd() {
        if (advertisementList.size() == 1) {
            ivDiscountDetailImage1.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(0).getPictruePath(),advertisementList.get(0).getQiniuPath())).into(ivDiscountDetailImage1);
        } else if (advertisementList.size() == 2) {
            ivDiscountDetailImage1.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(0).getPictruePath(),advertisementList.get(0).getQiniuPath())).into(ivDiscountDetailImage1);
            ivDiscountDetailImage2.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(1).getPictruePath(), advertisementList.get(1).getQiniuPath())).into(ivDiscountDetailImage2);
        } else if (advertisementList.size() == 3) {
            ivDiscountDetailImage1.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(0).getPictruePath(),advertisementList.get(0).getQiniuPath())).into(ivDiscountDetailImage1);
            ivDiscountDetailImage2.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(1).getPictruePath(),advertisementList.get(1).getQiniuPath())).into(ivDiscountDetailImage2);
            ivDiscountDetailImage3.setVisibility(View.VISIBLE);
            Picasso.with(this).load(QuickUtils.spliceUrl(advertisementList.get(2).getPictruePath(),advertisementList.get(2).getQiniuPath())).into(ivDiscountDetailImage3);
        }


    }

    @Deprecated
    private void handlerBanner() {
        vpImage.setTransitionEffect(BGABanner.TransitionEffect.Default);
        vpImage.setPageChangeDuration(3000);

        List<View> views = QuickUtils.getViews(mContext, rollDetailList.size());
        vpImage.setViews(views);

        for (int i = 0; i < views.size(); i++) {
            //通过给ImageView外套了一个RL解决在ViewPager中图片显示不全的BUG
            View rootView = views.get(i);
            ImageView view = (ImageView) rootView.findViewById(R.id.ivDisountImage);
            Picasso.with(this).load(QuickUtils.spliceUrl(rollDetailList.get(i).getPictruePath(),rollDetailList.get(i).getQiniuPath())).placeholder(R.mipmap.discount_background).into(view);

        }

    }

    private void scrollToTop() {
        /*if (info.getTitle() != null && info.getDescriptionText() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.svTop).scrollTo(0, 0);
                }
            }, 100);
        }*/
    }

    @Override
    protected void onBack() {
        finish();
    }
}
