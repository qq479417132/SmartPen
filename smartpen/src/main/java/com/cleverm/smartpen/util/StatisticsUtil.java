package com.cleverm.smartpen.util;

import android.os.Environment;

/**
 * Created by xiong,An android project Engineer,on 2016/3/3.
 * Data:2016-03-03  14:18
 * Base on clever-m.com(JAVA Service)
 * Describe: 统计帮助类
 * Version:1.0
 * Open source
 */
public class StatisticsUtil {


    //存储File文件的路径
    private static final String STATISTICS_FILE= Environment.getExternalStorageDirectory().getAbsolutePath() + "/muyestatistics";

    public static final int   CALL_ADD_FOOD =1;
    public static final int   CALL_ADD_WATER =2;
    public static final int   CALL_ADD_TISSUE =3;
    public static final int   CALL_PAY =4;
    public static final int   CALL_OTHER_SERVIC =5;

    public static final int   SERVICE_DEMO =6;
    public static final int   SERVICE_EVALUATE=7;
    public static final int   SERVICE_BUY_MYSELF =8;
    public static final int   SERVICE_DISCOUNT =9;
    public static final int   SERVICE_DAIJIA =10;
    public static final int   SERVICE_LOCAL_DISCOUNT =11;

    public static final int   APP_WEATHER =12;
    public static final int   APP_NEWS =13;
    public static final int   APP_AROUNDPLAY =14;
    public static final int   APP_ONLINEBUY =15;
    public static final int   APP_AROUNDDISCOUNT =16;
    public static final int   APP_VIDEO =17;
    public static final int   APP_MAGAZINE =18;
    public static final int   H5_GAME =19;
    public static final int   SETTING =20;


    private StatisticsUtil INSTANCE = new StatisticsUtil();

    public StatisticsUtil getInstance() {
        return INSTANCE;
    }

    private StatisticsUtil(){

    }

    /**
     * 记录点击次数
     * @param event
     */
    public void rememberCount(int event){

    }

    /**
     * 记录点击时间
     * @param event
     */
    public void rememberTime(int event){

    }

    /**
     * 记录待多久
     * @param event
     */
    public void rememberStayLong(int event){

    }


    /**
     * 拿取当前的时间戳
     * @return
     */
    private Long getCurrentTimeMillis(){
        return System.currentTimeMillis();
    }






}
