package com.cleverm.smartpen.util;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cleverm.smartpen.application.CleverM;

import java.util.ArrayList;

/**
 * Created by xiong,An android project Engineer,on 2016/2/19.
 * Data:2016-02-19  11:43
 * Base on clever-m.com(JAVA Service)
 * Describe: 快速帮助辅助类
 * Version:1.0
 * Open source
 */
public class QuickUtils {


    private static final String image1="http://img3.imgtn.bdimg.com/it/u=1167659344,71834717&fm=21&gp=0.jpg";
    private static final String image2="http://www.winwin-hotel.com/uploadfile/2012/0324/20120324112535452.jpg";
    private static final String image3="http://pic27.nipic.com/20130321/12176395_170138349154_2.jpg";
    private static final String image4="http://hansong.zshl.com/datashow/picb/2009/p2009271112833343.jpg";


    /**
     * 隐藏 navigation 和 status
     * Hide both the navigation bar and the status bar.
     * SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
     * a general rule, you should design your app to hide the status bar whenever you
     * hide the navigation bar.
     * @param activity
     */
    public static void hideNavigation(Activity activity) {
        View decorView = activity.getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static ArrayList<String> getTestImage(){
        ArrayList<String> images = new ArrayList<String>();
        images.add(image1);
        images.add(image2);
        images.add(image3);
        images.add(image4);
        images.add(image2);
        images.add(image3);
        images.add(image4);
        images.add(image1);
        images.add(image3);
        images.add(image4);
        return images;
    }

    public static void toast(String message){
        Toast.makeText(CleverM.getApplication(), message, Toast.LENGTH_LONG).show();
    }

    public static void log(String message){
        Log.i("MAIN-ACTIVITY", message);
    }

    public static View getPageView(Activity activity,@DrawableRes int resid) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageResource(resid);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}
