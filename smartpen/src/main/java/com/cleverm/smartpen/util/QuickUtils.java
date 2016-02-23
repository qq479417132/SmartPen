package com.cleverm.smartpen.util;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.bean.DiscountInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiong,An android project Engineer,on 2016/2/19.
 * Data:2016-02-19  11:43
 * Base on clever-m.com(JAVA Service)
 * Describe: 快速帮助辅助类
 * Version:1.0
 * Open source
 */
public class QuickUtils {


    private static final String image1 = "http://img3.imgtn.bdimg.com/it/u=1167659344,71834717&fm=21&gp=0.jpg";
    private static final String image2 = "http://www.winwin-hotel.com/uploadfile/2012/0324/20120324112535452.jpg";
    private static final String image3 = "http://pic27.nipic.com/20130321/12176395_170138349154_2.jpg";
    private static final String image4 = "http://hansong.zshl.com/datashow/picb/2009/p2009271112833343.jpg";


    /**
     * 隐藏 navigation 和 status
     * Hide both the navigation bar and the status bar.
     * SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
     * a general rule, you should design your app to hide the status bar whenever you
     * hide the navigation bar.
     *
     * @param activity
     */
    public static void hideNavigation(Activity activity) {
        View decorView = activity.getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static void hideTitel(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 隐藏运行Android 4.0以上系统的平板的屏幕下方的状态栏
     * onCreate调用
     */
    public static void hideHighApiBottomStatusBar() {
        try {
            String ProcID = "79";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                ProcID = "42"; // ICS
            // 需要root 权限
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "service call activity " + ProcID + " s16 com.android.systemui"}); // WAS
            proc.waitFor();
        } catch (Exception ex) {

        }
    }

    /**
     * 恢复运行Android 4.0以上系统的平板的屏幕下方的状态栏
     * onDestory调用
     */
    public static void showHighApiBottomStatusBar() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"am", "startservice", "-n", "com.android.systemui/.SystemUIService"});
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static ArrayList<String> getTestImage() {
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

    public static ArrayList<String> getDiscountImage(List<DiscountInfo> lists) {
        ArrayList<String> images = new ArrayList<String>();
        for (int i = 0; i < lists.size(); i++) {
            images.add(lists.get(i).getPictruePath());
        }
        return images;
    }


    public static void toast(String message) {
        Toast.makeText(CleverM.getApplication(), message, Toast.LENGTH_LONG).show();
    }

    public static void log(String message) {
        Log.i("MAIN-ACTIVITY", message);
    }

    public static View getPageView(Activity activity, @DrawableRes int resid) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageResource(resid);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    public static List<View> getViews(Activity activity, int count) {
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < count; i++) {
            views.add(activity.getLayoutInflater().inflate(R.layout.image_discount, null));
        }
        return views;
    }

    /**
     * 根据startTime和endTime来确定是否同一天
     */
    public static boolean isSameDay(Long startTime,Long endTime){
        String startDay = getDayFromData(timeStamp2Date(startTime.toString(), null));
        String startMonth = getMonthFromData(timeStamp2Date(startTime.toString(), null));

        String endDay = getDayFromData(timeStamp2Date(endTime.toString(), null));
        String endMonth = getMonthFromData(timeStamp2Date(endTime.toString(), null));

        //只有日期和月份都相同的我们才定义为一整天
        if( startDay == endDay && startMonth == endMonth){
            return true;
        }else{
            return false;
        }

    }

    /**
     * 时间戳转具体年月日
     * @param seconds
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }

    public static String getDayFromData(String date){
        String what_day = date.substring(8, 10);
        return what_day;
    }

    public static String getMonthFromData(String date){
        String what_month = date.substring(5, 7);
        return what_month;
    }

}
