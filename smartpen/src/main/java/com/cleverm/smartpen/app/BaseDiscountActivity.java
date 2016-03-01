package com.cleverm.smartpen.app;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.bean.DiscountInfo;
import com.cleverm.smartpen.ui.banner.BGABanner;
import com.cleverm.smartpen.util.AlgorithmUtil;
import com.cleverm.smartpen.util.DownloadUtil;
import com.cleverm.smartpen.util.FileCacheUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.ServiceUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiong,An android project Engineer,on 2016/3/1.
 * Data:2016-03-01  15:57
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public abstract class BaseDiscountActivity extends BaseBackActivity implements View.OnClickListener {


    private Activity mContext;
    private boolean isDisountArea;
    private BGABanner vpImage;
    private ArrayList<String> images;
    private ImageView ivLeft;
    private ImageView ivRight;
    TextView tvDiscountNum;


    @Override
    protected void onCreate() {
        setContentView(R.layout.activity_discount);
        mContext = this;
        initIntent();
        initView();
        initDate();
        initClick();
    }

    @Override
    protected ImageView getBackResId() {
        return (ImageView) findViewById(R.id.ivClose);
    }

    /**
     * 是否是优惠专区
     */
    private void initIntent() {
        isDisountArea = getSonDiscountArea();
    }

    protected abstract boolean getSonDiscountArea();


    private void initView() {
        vpImage = (BGABanner) findViewById(R.id.banner_main_default);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        tvDiscountNum = (TextView) findViewById(R.id.tvDiscountNum);
    }

    private void initDate() {
        if (FileCacheUtil.get(this).getAsString(DownloadUtil.DISOUNT_JSON) != null && FileCacheUtil.get(this).getAsString(DownloadUtil.DISOUNT_HEADOFFICE_JSON) != null) {//从File缓存中读取json串
            if (isDisountArea) {
                String json = FileCacheUtil.get(this).getAsString(DownloadUtil.DISOUNT_JSON);
                handlerJosn(json);
            } else {
                String json = FileCacheUtil.get(this).getAsString(DownloadUtil.DISOUNT_HEADOFFICE_JSON);
                handlerJosn(json);
            }
        } else {//缓存中没有就从服务端再次读取
            if (isDisountArea) {
                getDiscountDataFromService("1");
            } else {
                getDiscountDataFromService("0");
            }
        }
    }


    /**
     * 处理Json数据
     *
     * @param json
     */
    private void handlerJosn(String json) {
        try {
            //解析Json
            List<DiscountInfo> discountInfos = ServiceUtil.getInstance().parserDiscountData(json);
            //图片顺序算法
            List<DiscountInfo> listImageSequence = AlgorithmUtil.getInstance().getSimpleImageSequence(discountInfos);
            //图片数据
            images = QuickUtils.getDiscountImage(listImageSequence);
            //图片控件处理
            setBanner(listImageSequence);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initClick() {
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
    }


    private void setBanner(final List<DiscountInfo> listImageSequence) {
        //设置VpImage的参数
        vpImage.setTransitionEffect(BGABanner.TransitionEffect.Default);
        vpImage.setPageChangeDuration(3000);
        //将Image放入VpImage中
        List<View> views = QuickUtils.getViews(mContext, images.size());
        vpImage.setViews(views);
        //处理图片的异步显示和点击事件
        for (int i = 0; i < views.size(); i++) {
            //通过给ImageView外套了一个RL解决在ViewPager中图片显示不全的BUG
            View rootView = views.get(i);
            ImageView view = (ImageView) rootView.findViewById(R.id.ivDisountImage);

            QuickUtils.log("---IMAGE---views=" + views.size() + "images=" + images.size());

            if (views.size() == 1) {
                Picasso.with(this).load(QuickUtils.spliceUrl(images.get(0))).placeholder(R.mipmap.discount_background).into(view);
                vpImage.getViewPager().setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            } else if (views.size() == 2 || views.size() == 3) {
                int index = (i > (images.size() - 1)) ? i - images.size() : i;
                QuickUtils.log("---IMAGE---index=" + index);
                Picasso.with(this).load(QuickUtils.spliceUrl(images.get((i > (images.size() - 1)) ? i - images.size() : i))).placeholder(R.mipmap.discount_background).into(view);
            } else {
                Picasso.with(this).load(QuickUtils.spliceUrl(images.get(i))).placeholder(R.mipmap.discount_background).into(view);
            }


            //Picasso.with(this).load(images.get((i > (images.size()-1)) ? i -images.size()  : i)).placeholder(R.mipmap.discount_background).into(view);


            // 点击事件
            final int finalPosition = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DiscountInfo discountInfo = listImageSequence.get(finalPosition);
                    Intent intent = new Intent(mContext, DiscountDetailActivity.class);
                    intent.putExtra(DiscountDetailActivity.INTENT_NAME, discountInfo);
                    startActivity(intent);
                }
            });
        }


        //Left和Right按钮点击设置当前的页码
        vpImage.setBAGInterfaceListener(new BGABanner.BAGInterface() {
            @Override
            public void setNum() {
                vpImage.setNum(tvDiscountNum);
            }
        });

        //VP滑动设置当前页码:在Point点变化的地方添加了相应代码，外部无需处理
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivLeft:
                vpImage.setDec(vpImage);
                break;

            case R.id.ivRight:
                vpImage.setAdd(vpImage);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(this, "E_Discount");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onEvent(this, "E_Discount");
    }

    @Override
    protected void onBack() {
        //清空List集合
        AlgorithmUtil.getInstance().clearImageSequence();
        startActivity(new Intent(this, VideoActivity.class));
        finish();
        ((CleverM) getApplication()).getpenService().setActivityFlag("VideoActivity");
    }

    private void getDiscountDataFromService(String type) {
        ServiceUtil.getInstance().getDiscountData(QuickUtils.getOrgIdFromSp(), type, new ServiceUtil.JsonInterface() {
            @Override
            public void onSucced(String json) {
                handlerJosn(json);
            }


            @Override
            public void onFail(String error) {

            }
        });
    }


}
