package com.cleverm.smartpen.util;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.cleverm.smartpen.app.SelectTableActivity;
import com.cleverm.smartpen.app.VideoActivity;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.pushtable.UpdateTableHandler;
import com.cleverm.smartpen.statistic.dao.DaoMaster;
import com.cleverm.smartpen.statistic.dao.DaoSession;
import com.cleverm.smartpen.statistic.dao.StatsDao;
import com.cleverm.smartpen.statistic.model.Stats;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiong,An android project Engineer,on 2016/3/3.
 * Data:2016-03-03  14:18
 * Base on clever-m.com(JAVA Service)
 * Describe: 统计帮助类
 * sqlite数据库位于：/data/data/项目包/databases中
 * Version:1.0
 * Open source
 */
public class StatisticsUtil {


    //存储File文件的路径
    private static final String STATISTICS_FILE= Environment.getExternalStorageDirectory().getAbsolutePath() + "/muyestatistics";

    //统计Event对应表
    public static final int   CALL_ADD_FOOD =1;//点餐加菜
    public static final int   CALL_ADD_WATER =2;//添加茶水
    public static final int   CALL_ADD_TISSUE =3;//湿巾纸巾
    public static final int   CALL_PAY =4;//呼叫结账
    public static final int   CALL_OTHER_SERVIC =5;//其他服务
    //1现金结账/2银行家结账/3支付宝结账/4微信结账
    public static final int   CALL_PAY_CASH=1;
    public static final int   CALL_PAY_CARD=2;
    public static final int   CALL_PAY_ALIPAY=3;
    public static final int   CALL_PAY_WEIXIN=4;


    public static final int   SERVICE_DEMO =6;//DEMO引导
    public static final int   SERVICE_EVALUATE=7;//服务评价
    public static final int   SERVICE_BUY_MYSELF =8;//自助买单
    public static final int   SERVICE_DISCOUNT =9;//优惠专区
    public static final int   SERVICE_DAIJIA =10;//代驾服务
    public static final int   SERVICE_LOCAL_DISCOUNT =11;//本店推介

    public static final int   APP_WEATHER =12;//今日天气
    public static final int   APP_NEWS =13;//今日头条
    public static final int   APP_AROUNDPLAY =14;//周边玩乐
    public static final int   APP_ONLINEBUY =15;//在线商场
    public static final int   APP_AROUNDDISCOUNT =16;//周边优惠
    public static final int   APP_VIDEO =17;//视频娱乐
    public static final int   APP_MAGAZINE =18;//电子杂志
    public static final int   H5_GAME =19;//手游试玩
    public static final int   SETTING =20;//设置

    public static final int second_discount_activity=21;//优惠专区活动轮播
    public static final int SECOND_LOACL_DISCOUNT_ACTIVITY =22;//本店推介活动轮播

    public static final int OTHER_OPEN_TIME=23;//开机时间
    public static final int OTHER_CLOSE_TIME=24;//关机时间
    public static final int OTHER_COMMENT_SUBMIT=25;//评价提交
    public static final int OTHER_GIVE_MONEY=26;//打赏

    //点点笔数据库名
    private static final String DB_NAME="ddb_db";



    private static StatisticsUtil INSTANCE = new StatisticsUtil();

    public static StatisticsUtil getInstance() {
        return INSTANCE;
    }

    private  StatisticsUtil(){

    }

    public class DaoReturnValue{

        private SQLiteDatabase db;

        private StatsDao statsDao;

        public DaoReturnValue(){

        }

        public DaoReturnValue(SQLiteDatabase db,StatsDao statsDao){
            this.db=db;
            this.statsDao=statsDao;
        }

        public void setDb(SQLiteDatabase db) {
            this.db = db;
        }

        public StatsDao getStatsDao() {
            return statsDao;
        }

        public SQLiteDatabase getDb() {
            return db;
        }

        public void setStatsDao(StatsDao statsDao) {
            this.statsDao = statsDao;
        }
    }

    /**
     * 初始化获取dao对象
     * @param application
     */
    public DaoReturnValue onInit(Application application){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(application, DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        StatsDao statsDao = daoSession.getStatsDao();

        return new DaoReturnValue(db,statsDao);
    }

    /**
     * 获取bean对象
     * @param actionId
     * @param timePoit
     * @param timeStay
     * @param clientId
     * @param orgId
     * @param tableId
     * @param desc
     * @param secondid
     * @param querydata
     * @return
     */
    private Stats getStasObject( int actionId, Long timePoit, Long timeStay, Long clientId, Long orgId, Long tableId, String desc, Long secondid, String querydata){
        Stats stats = new Stats(null,  actionId,  timePoit,  timeStay,  clientId,  orgId,  tableId,  desc,  secondid,  querydata);
        return stats;
    }

    /**
     * 执行Event的插入
     * insert语法
     * @param eventId
     * @return Long 该自增长的id值,他将会完成timeStay的赋值
     */
    public Long insert(int eventId,String desc){

        //插入数据库
        return CleverM.getStatsDao().insert(StatisticsUtil.getInstance().getStasObject(
                eventId,
                getCurrentTimeMillis(),
                0L,
                getClientId(),
                getOrgId(),
                getDeskId(),
                desc,null,getEventHappenTime()
                ));

    }

    /**
     * Event 4,21,22 三个事件是含有子事件的
     * @param eventId
     * @param desc
     * @param secondEvent
     * @return
     */
    public Long insertWithSecondEvent(int eventId,String desc,Long secondEvent){

        //插入数据库
        return CleverM.getStatsDao().insert(StatisticsUtil.getInstance().getStasObject(
                eventId,
                getCurrentTimeMillis(),
                0L,
                getClientId(),
                getOrgId(),
                getDeskId(),desc,secondEvent,
                getEventHappenTime()));
    }

    /**
     * 根据自增长Id值,统计该界面停留时长
     * 计算 oDestory()-onCreate()方法之间的时长
     * update语法,直接使用sql语法
     * update TableName set timeStay = xxxx where id = xxxx;
     * update t_stats set de.greenrobot.dao.Property@41d1a1a8 = 8744 where de.greenrobot.dao.Property@41d19fb0 = 11;
     * @param rawId
     * @param timeStay 毫秒值
     */
    public void  update_timestay(Long rawId,Long timeStay){

        String SQL="update " +StatsDao.TABLENAME+
                " set "+StatsDao.Properties.TimeStay.columnName+
                " =?"+" where " +StatsDao.Properties.Id.columnName+
                " =?";

        //Or Use this SQL
        String SQL2="update t_stats set "+" TIME_STAY ="+ timeStay +" where _id = "+rawId;

        CleverM.getDb().execSQL(SQL,new Object[] {timeStay, rawId});
    }


    /**
     * 记录点击次数
     * @param event
     */
    public void rememberCount(int event){


    }



    /**
     * 拿取当前的时间戳
     * @return
     */
    private Long getCurrentTimeMillis(){
        return System.currentTimeMillis();
    }


    public String getEventHappenTime(){
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String queryTime = dateFormat.format(date);
        return queryTime;
    }


    private static final String DEFALUT_CLIENTID="";
    private static final String DEFALUT_ORGID="";

    /**
     * 获取推送的商户号
     * @return
     */
    private Long getClientId(){
        String clientId = RememberUtil.getString(UpdateTableHandler.CLIENTID, DEFALUT_CLIENTID);
        if(clientId.equals(DEFALUT_CLIENTID)){
            return 8888L;
        }
        return Long.getLong(clientId);
    }

    /**
     * 获取推送的餐厅号
     * @return
     */
    private Long getOrgId(){
        String orgId=RememberUtil.getString(UpdateTableHandler.ORGID, DEFALUT_ORGID);
        if(orgId.equals(DEFALUT_ORGID)){
            return 8888L;
        }
        return Long.getLong(orgId);
    }

    /**
     * 获取推送的餐桌号
     * @return
     */
    private Long getDeskId(){
        long deskId = RememberUtil.getLong(SelectTableActivity.SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);
        return deskId;
    }






}
