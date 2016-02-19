package com.cleverm.smartpen.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.ui.banner.BGABanner;
import com.cleverm.smartpen.util.FileCacheUtil;
import com.cleverm.smartpen.util.DownloadUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiong,An android project Engineer,on 2016/2/19.
 * Data:2016-02-19  11:25
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class DiscountActivity extends Activity implements View.OnClickListener {

    private Activity mContext;

    private BGABanner vpImage;
    private ArrayList<String> images;
    private ImageView ivClose;
    private ImageView ivLeft;
    private ImageView ivRight;
    TextView tvDiscountNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        mContext=this;

        QuickUtils.toast(FileCacheUtil.get(this).getAsString(DownloadUtil.DISOUNT_JSON));

        initView();
        initDate();
        initClick();
    }


    private void initView() {
         vpImage = (BGABanner) findViewById(R.id.banner_main_default);
         ivClose = (ImageView) findViewById(R.id.ivClose);
         ivLeft = (ImageView) findViewById(R.id.ivLeft);
         ivRight = (ImageView) findViewById(R.id.ivRight);
         tvDiscountNum= (TextView) findViewById(R.id.tvDiscountNum);
    }

    private void initDate() {
        images = getDataFromImage();
        setBanner();
    }



    private void initClick() {
        ivClose.setOnClickListener(this);
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
    }


    public ArrayList<String> getDataFromImage() {
        return QuickUtils.getTestImage();
    }


    List<View> views;
    private void setBanner() {

        vpImage.setTransitionEffect(BGABanner.TransitionEffect.Rotate);
        vpImage.setPageChangeDuration(10000);



        //views = getViews(9);
        views = new ArrayList<View>();
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        views.add(getPageView(R.mipmap.test_discount));
        vpImage.setViews(views);




        //getImageFromService
        for (int i = 0; i < views.size(); i++) {
            ImageView view = (ImageView) views.get(i);
            Picasso.with(this).load(images.get(i)).into(view);
            // 点击事件
            final int finalPosition = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "点击" + (finalPosition + 1) + "ҳ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, DiscountActivity.class));
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

    private View getPageView(@DrawableRes int resid) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resid);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivClose:
                QuickUtils.toast("ivClose");
                finish();
                break;

            case R.id.ivLeft:
                vpImage.setDec(vpImage);
                break;

            case R.id.ivRight:
                vpImage.setAdd(vpImage);
                break;
        }
    }
}
