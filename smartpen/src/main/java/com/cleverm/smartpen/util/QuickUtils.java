package com.cleverm.smartpen.util;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.app.SelectTableActivity;
import com.cleverm.smartpen.app.VideoActivity;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.bean.DiscountInfo;
import com.cleverm.smartpen.net.InfoSendSMSVo;
import com.cleverm.smartpen.net.RequestNet;
import com.cleverm.smartpen.pushtable.UpdateTableHandler;

import java.io.File;
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
    public static boolean isSameDay(Long startTime, Long endTime) {
        String startDay = getDayFromData(timeStamp2Date(startTime.toString(), null));
        String startMonth = getMonthFromData(timeStamp2Date(startTime.toString(), null));

        String endDay = getDayFromData(timeStamp2Date(endTime.toString(), null));
        String endMonth = getMonthFromData(timeStamp2Date(endTime.toString(), null));

        //只有日期和月份都相同的我们才定义为一整天
        if (startDay == endDay && startMonth == endMonth) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 时间戳转具体年月日
     *
     * @param seconds
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    public static String timeStamp2DateNoSec(String days, String format) {
        if (days == null || days.isEmpty() || days.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(days)));
    }

    public static String getDayFromData(String date) {
        String what_day = date.substring(8, 10);
        return what_day;
    }

    public static String getMonthFromData(String date) {
        String what_month = date.substring(5, 7);
        return what_month;
    }

    /**
     * 从SP中去拿餐厅号
     *
     * @return
     */
    public static String getOrgIdFromSp() {
        return RememberUtil.getString(UpdateTableHandler.ORGID, "100");
    }

    /**
     * 判断目录路径,比如storage/emulated/0/muye是否存在
     *
     * @return
     */
    public static boolean isHasVideoFolder() {
        File file = new File(AlgorithmUtil.VIDEO_FILE);
        //如果目录不存在public
        if (!file.exists() && !file.isDirectory()) {
            //先创建目录，然后返回false
            file.mkdir();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断目录下是否有文件
     *
     * @return
     */
    public static boolean isVideoFolderHaveFile() {
        File file = new File(AlgorithmUtil.VIDEO_FILE);
        File[] files = file.listFiles();

        if (files != null) {
            QuickUtils.log("files.length=" + files.length);
        }

        if ((files == null) || (files.length == 0)) {
            return false;
        } else {
            for (int i = 0; i < files.length; i++) {
                QuickUtils.log("files.name=" + files[i]);
                if (files[i].isDirectory()) {
                    //递归删除目录
                    deleteDir(files[i]);
                }
            }
            //在经过一轮删除目录中的目录后，如果该目录中还有length,那么就代表一定有文件
            if (files.length > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isVideoFolderHaveFiel2() {
        File file = new File(AlgorithmUtil.VIDEO_FILE);
        File[] files = file.listFiles();

        if (files != null) {
            QuickUtils.log("files=" + files.toString());
        }


        if ((files == null) || (files.length == 0)) {
            return false;
        } else {
            return true;
        }
    }

    public static File[] getVideoFolderFiles() {
        File file = new File(AlgorithmUtil.VIDEO_FILE);
        File[] files = file.listFiles();
        return files;
    }


    /**
     * 递归删除目录
     *
     * @param dir
     * @return
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileExists(String path) {
        boolean flag = false;
        File file = new File(path);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 过滤单词101.mp4  为101
     *
     * @param fileName
     * @return
     */
    public static String subVideoEnd(String fileName) {
        return fileName.substring(0, fileName.length() - 4);
    }

    /**
     * 拼接URL前缀
     *
     * @param url
     * @return
     */
    public static String spliceUrl(String url) {
        return "http://www.myee.online/push/" + url;
    }


    /**
     * 发送短信
     */
    public static void sendSMSToService(int eventId){
        long deskId = RememberUtil.getLong(SelectTableActivity.SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);
        if (deskId == Constant.DESK_ID_DEF_DEFAULT) {
            return;
        }
        final InfoSendSMSVo infoSendSMSVo = new InfoSendSMSVo();
        infoSendSMSVo.setTemplateID(eventId);
        infoSendSMSVo.setTableID(deskId);

        new Thread() {
            @Override
            public void run() {
                RequestNet.getData(infoSendSMSVo);
            }
        }.start();

    }
}
