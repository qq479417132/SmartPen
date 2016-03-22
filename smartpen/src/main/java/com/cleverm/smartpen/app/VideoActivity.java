package com.cleverm.smartpen.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.database.DatabaseHelper;
import com.cleverm.smartpen.modle.TableType;
import com.cleverm.smartpen.net.InfoSendSMSVo;
import com.cleverm.smartpen.net.RequestNet;
import com.cleverm.smartpen.service.DownloadPicassoService;
import com.cleverm.smartpen.service.DownloaderDifferenceService;
import com.cleverm.smartpen.service.ScreenLockListenService;
import com.cleverm.smartpen.service.penService;
import com.cleverm.smartpen.ui.FullScreenVideoView;
import com.cleverm.smartpen.ui.MyText;
import com.cleverm.smartpen.util.AlgorithmUtil;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.DownloadUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.StatisticsUtil;
import com.cleverm.smartpen.util.VideoTimeTask;
import com.cleverm.smartpen.util.VideoUtil;
import com.cleverm.smartpen.util.excle.CreateExcel;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;


/**
 * Created by xiong,An android project Engineer,on 2016/2/15.
 * Data:2016-02-16  11:25
 * Base on clever-m.com(JAVA Service)
 * Describe: 广告视频播放,该界面将作为基Activity存在,所有的其他点点笔的操作Activity都会覆盖于其上,该Activity将一直存在于栈内.
 * Version:1.0
 * Open source
 */
public class VideoActivity extends BaseActivity implements penService.MessageListener {

    public static final String TAG = VideoActivity.class.getSimpleName();
    FullScreenVideoView vvAdvertisement;
    private RelativeLayout mrlNotice;
    private ImageView mivNoticeImage;
    private TextView mrlNoticeText;
    private RelativeLayout mrlNoticeDesk;
    private MyText mtv;
    private UpdateTableHandlerSuccess mUpdateTableHandlerSuccess;
    public static final String SUCCESSACTION="UpdateTableHandlerSuccess";
    public final static int updateSpeed=3;
    public static float width;
    public static final int ANIMATION_TIME = 500;
    public static final int STOP_ANIMATION = 1;
    public static final int HANDLER_DATA = 2;
    public static final int GET_PENSERVICE = 3;
    public static final int GET_STUB = 4;
    public static final int DELAY_TIME = 3000;
    public static final int FOOD_ADD = Constant.FOOD_ADD;
    public static final int WATER_ADD = Constant.WATER_ADD;
    public static final int TISSUE_ADD = Constant.TISSUE_ADD;
    public static final int PAY_MONRY = Constant.PAY_MONRY;
    public static final int OTHER_SERVICE = Constant.OTHER_SERVICE;
    public static final int CLEAN = Constant.CLEAN;

    public static final String VIDEO_ACTIVITY_KEY = "video_activity_key";
    public static final String VIDEO_ACTIVITY_ISSEND = "video_activity_isSend";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.ORDER_DISHES1:
                case Constant.ADD_WATER1:
                case Constant.PAY1:
                case Constant.TISSUE1:
                case Constant.OTHER1:
                case Constant.ORDER_DISHES2:
                case Constant.ADD_WATER2:
                case Constant.PAY2:
                case Constant.TISSUE2:
                case Constant.OTHER2:
                case Constant.ORDER_DISHES3:
                case Constant.ADD_WATER3:
                case Constant.PAY3:
                case Constant.TISSUE3:
                case Constant.OTHER3:
                case Constant.ORDER_DISHES4:
                case Constant.ADD_WATER4:
                case Constant.PAY4:
                case Constant.TISSUE4:
                case Constant.OTHER4:
                case Constant.ORDER_DISHES5:
                case Constant.ADD_WATER5:
                case Constant.PAY5:
                case Constant.TISSUE5:
                case Constant.OTHER5:
                case Constant.CLEAN_DESK:{
                    Log.v(TAG, "AnimationStart(msg.what)=" + msg.what);
                    //xiong change on 20160322
                    //AnimationStart(msg.what);
                    AnimationStartNoAnim(msg.what);
                    break;
                }
                case STOP_ANIMATION: {
                    mrlNotice.setVisibility(View.GONE);
                    break;
                }
                case HANDLER_DATA: {
                    handlerCode(msg.arg1, (Boolean) msg.obj);
                    break;
                }
                case GET_PENSERVICE: {
                    initPenServiceListener();
                    break;
                }
                case GET_STUB: {
                    initStubListener();
                    break;
                }
                default: {
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuickUtils.log("ActivityClay---onCreate()=" + videoValue);
        setContentView(R.layout.activity_video);
        QuickUtils.showHighApiBottomStatusBar();
        initView();
        initBroadcastReceiver();

        //只有重启后的第一次才去取数据
        if(RememberUtil.getBoolean(Constant.BROADCAST_RESATRT_EVENT,true)){
            initData();
            initCacheJson();
            initStats();
        }else{
            if(QuickUtils.isHasVideoFolder()&&QuickUtils.isVideoFolderHaveFiel2()){
                VideoUtil videoUtil = new VideoUtil(vvAdvertisement);
                videoUtil.prepareLocalVideo(AlgorithmUtil.VIDEO_FILE_PLAY, 0);
            }else{
                initData();
            }
        }

        RememberUtil.putBoolean(Constant.BROADCAST_RESATRT_EVENT,false);

        initIntent();
        mHandler.sendEmptyMessage(GET_PENSERVICE);
        initVersion();
        initTimeTask();

    }

    /**
     * 60秒后再次检查
     */
    private void initTimeTask() {
        Timer timer = new Timer();
        timer.schedule(new VideoTimeTask(vvAdvertisement,this), 3600000);
    }

    private void initBroadcastReceiver() {
        mUpdateTableHandlerSuccess=new UpdateTableHandlerSuccess();
        registerReceiver(mUpdateTableHandlerSuccess,new IntentFilter(SUCCESSACTION));
    }

    @Override
    protected int onGetEventId() {
        return StatisticsUtil.BACK_VIDEO;
    }

    @Override
    protected String onGetDesc() {
        return StatisticsUtil.BACK_VIDEO_DESC;
    }

    private void initStats() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long start = System.currentTimeMillis();
                CreateExcel createExcel = new CreateExcel();
                String execlName = createExcel.init();
                createExcel.writeExcel(StatisticsUtil.getInstance().getDBForExcel(), execlName);
                final long end = System.currentTimeMillis();
                QuickUtils.log("由DB导出为excle的时间为" + (end - start));
                if(!RememberUtil.getBoolean(StatisticsUtil.getInstance().getEventHappenTime(),false)){
                    //上传文件到服务器
                    StatisticsUtil.getInstance().updateExcleToService(StatisticsUtil.UPLOAD_FILE_URL,StatisticsUtil.getInstance().getStatsFile());
                }

            }
        }).start();
    }


    private void initVersion() {
        ((CleverM) CleverM.getApplication()).UpdataApp(this);
    }

    private void initIntent() {
        Intent in = getIntent();
        if (in == null) {
            return;
        }
        int id = in.getIntExtra(VIDEO_ACTIVITY_KEY, 0);
        boolean isSend = in.getBooleanExtra(VIDEO_ACTIVITY_ISSEND, false);
        handlerCode(id, isSend);
    }

    private void initPenServiceListener() {
        penService penService = ((CleverM) getApplication()).getpenService();
        if (penService == null) {
            mHandler.sendEmptyMessageDelayed(GET_PENSERVICE, 500);
        } else {
            penService.setMessageListener(this);
        }

    }

    private void initStubListener() {
        ScreenLockListenService.ScreenLockListenServiceStub stub = ((CleverM) getApplication()).getStub();
        if (stub == null) {
            mHandler.sendEmptyMessageDelayed(GET_STUB, 500);
        } else {
            stub.setTaskId(getTaskId());
            stub.setWindow(getWindow());
        }
    }

    private void initView() {
        vvAdvertisement = (FullScreenVideoView) findViewById(R.id.vvAdvertisement);
        mrlNotice = (RelativeLayout) findViewById(R.id.rlNotice);
        mivNoticeImage = (ImageView) findViewById(R.id.ivNoticeImage);
        mrlNoticeText = (TextView) findViewById(R.id.rlNoticeText);
        /**
         *跑马灯效果，可点击控制启动与暂停
         */
        mtv= (MyText) findViewById(R.id.tv_setdesk);
        ViewTreeObserver vto = mtv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mtv.getHeight();
                width=mtv.getWidth();
                mtv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mtv.init(getWindowManager());
                mtv.startScroll();
                mtv.setTextColor(Color.WHITE);
            }
        });
        //数据库为空或者没有选择桌号
        List<TableType> mTableTypes = DatabaseHelper.getsInstance(this).obtainAllTableTypes();
        long deskId = RememberUtil.getLong(SelectTableActivity.SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);
        if(mTableTypes==null || mTableTypes.size()==0 || deskId == Constant.DESK_ID_DEF_DEFAULT){
            mtv.setVisibility(View.VISIBLE);
        }else {
            mtv.setVisibility(View.GONE);
        }
    }


    private void initData() {
        //1.先判断服务器实现需要我们去更新
        //2.如果不需要更新,直接检查我们的视频目录是否存在视频
        //3.根据排序规则进行视频的依次播放
        //AlgorithmUtil.getInstance().getSimpleVideo(vvAdvertisement);
        AlgorithmUtil.getInstance().startVideoPlayAlgorithm(vvAdvertisement, this);
    }

    /**
     * 在处理Video前将特惠专区的json数据保存到本地的文件中，然后每天都是读取的该次数据
     * 将只取一次的数据放入Cache中
     */
    private void initCacheJson() {
        DownloadUtil.cacheDiscountJson(QuickUtils.getOrgIdFromSp());
    }

    /**
     * 横竖屏数据处理逻辑
     * 在onPause中记下位置
     * 在onResume中从该位置播放
     * 记录到SharePrefrence中
     */
    @Override
    protected void onPause() {
        super.onPause();
        RememberUtil.putInt(Constant.MEMORY_PLAY_KEY, vvAdvertisement.getCurrentPosition());
        QuickUtils.log("ActivityClay---onPause()=");
    }

    int videoValue = 0;


    @Override
    protected void onResume() {
        super.onResume();
        videoValue = RememberUtil.getInt(Constant.MEMORY_PLAY_KEY, 0);
        /**
         * 因为VideoActivity会被不断的重启,算法太耗时导致必须延迟
         */
        QuickUtils.log("ActivityClay---onResume()");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vvAdvertisement.seekTo(videoValue);
                vvAdvertisement.start();
            }
        }, 250);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        QuickUtils.log("ActivityClay---onDestroy()");
        unregisterReceiver(mUpdateTableHandlerSuccess);
    }


    /**
     * implements penService.MessageListener
     *
     * @param id
     */
    @Override
    public void receiveData(int id, boolean isSend) {
        Log.v(TAG, "receiveData id=" + id);
        Message mes = mHandler.obtainMessage();
        mes.arg1 = id;
        mes.what = HANDLER_DATA;
        mes.obj = isSend;
        mes.sendToTarget();
    }

    private void handlerCode(int id, boolean isSend) {
        QuickUtils.log("code=" + id);
        int templateID = 0;
        switch (id) {
            case Constant.ORDER_DISHES1:
            case Constant.ORDER_DISHES2:
            case Constant.ORDER_DISHES3:
            case Constant.ORDER_DISHES4:
            case Constant.ORDER_DISHES5: {
                templateID = FOOD_ADD;
                //统计代码
                StatisticsUtil.getInstance().insert(StatisticsUtil.CALL_ADD_FOOD,StatisticsUtil.CALL_ADD_FOOD_DESC);
                break;
            }
            case Constant.ADD_WATER1:
            case Constant.ADD_WATER2:
            case Constant.ADD_WATER3:
            case Constant.ADD_WATER4:
            case Constant.ADD_WATER5: {
                templateID = WATER_ADD;
                //统计代码
                StatisticsUtil.getInstance().insert(StatisticsUtil.CALL_ADD_WATER,StatisticsUtil.CALL_ADD_WATER_DESC);
                break;
            }
            case Constant.TISSUE1:
            case Constant.TISSUE2:
            case Constant.TISSUE3:
            case Constant.TISSUE4:
            case Constant.TISSUE5: {
                templateID = TISSUE_ADD;
                //统计代码
                StatisticsUtil.getInstance().insert(StatisticsUtil.CALL_ADD_TISSUE,StatisticsUtil.CALL_ADD_TISSUE_DESC);
                break;
            }
            case Constant.PAY1:
            case Constant.PAY2:
            case Constant.PAY3:
            case Constant.PAY4:
            case Constant.PAY5: {
                templateID = PAY_MONRY;
                //呼叫结账统计代码到具体界面处理
                break;
            }
            case Constant.OTHER1:
            case Constant.OTHER2:
            case Constant.OTHER3:
            case Constant.OTHER4:
            case Constant.OTHER5: {
                templateID = OTHER_SERVICE;
                //统计代码
                StatisticsUtil.getInstance().insert(StatisticsUtil.CALL_OTHER_SERVIC,StatisticsUtil.CALL_OTHER_SERVIC_DESC);
                break;
            }
            case Constant.CLEAN_DESK: {
                templateID = CLEAN;
                //统计代码
                StatisticsUtil.getInstance().insert(StatisticsUtil.CLEAN_DESK,StatisticsUtil.CLEAN_DESK_DESC);
                break;
            }
        }
        long deskId = RememberUtil.getLong(SelectTableActivity.SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);
        if (deskId == Constant.DESK_ID_DEF_DEFAULT) {
            return;
        }
        InfoSendSMSVo infoSendSMSVo = new InfoSendSMSVo();
        infoSendSMSVo.setTemplateID(templateID);
        infoSendSMSVo.setTableID(deskId);
        Log.v(TAG, "id=" + id + " deskId=" + deskId + "isSend=" + isSend);
        sendMessageToService(infoSendSMSVo, id, isSend);
    }


    /**
     * SEND ems
     *
     * @param infoSendSMSVo
     * @param code
     */
    private void sendMessageToService(final InfoSendSMSVo infoSendSMSVo, final int code, final boolean isSend) {
        new Thread() {
            @Override
            public void run() {
                if (isSend == false) {
                    InfoSendSMSVo getSMSVo = RequestNet.getData(infoSendSMSVo);
                    Log.v(TAG, "sendMessageToService()===");
                    if (getSMSVo != null && getSMSVo.getSuccess()) {
                        Log.v(TAG, "sendMessageToService()===isSuccess");
                        mHandler.sendEmptyMessage(code);
                    } else {
                        Log.v(TAG, "sendMessageToService()===isfalse");
                    }
                } else {
                    mHandler.sendEmptyMessage(code);
                    Log.v(TAG, "Not sendMessageToService()===isfalse");
                }
            }
        }.start();
    }

    /**
     * Animation
     *
     * @param id
     */
    int i = 0;

    private void AnimationStart(final int id) {
        mrlNotice.clearAnimation();
        mHandler.removeMessages(STOP_ANIMATION);
        mrlNotice.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(false);
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(ANIMATION_TIME);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new BounceInterpolator());


        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(ANIMATION_TIME);
        scale.setFillAfter(true);


        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(ANIMATION_TIME);
        alpha.setFillAfter(true);

        set.addAnimation(rotate);
        set.addAnimation(scale);
        set.addAnimation(alpha);


        set.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String text = null;
                Log.v(TAG, "nimationStart onAnimationEnd" + id);
                switch (id) {
                    case Constant.ORDER_DISHES1:
                    case Constant.ORDER_DISHES2:
                    case Constant.ORDER_DISHES3:
                    case Constant.ORDER_DISHES4:
                    case Constant.ORDER_DISHES5: {
                        mivNoticeImage.setImageResource(R.mipmap.icon_dish);
                        text = getString(R.string.add_greens);
                        break;
                    }
                    case Constant.ADD_WATER1:
                    case Constant.ADD_WATER2:
                    case Constant.ADD_WATER3:
                    case Constant.ADD_WATER4:
                    case Constant.ADD_WATER5: {
                        mivNoticeImage.setImageResource(R.mipmap.icon_water);
                        text = getString(R.string.add_water);
                        break;
                    }
                    case Constant.TISSUE1:
                    case Constant.TISSUE2:
                    case Constant.TISSUE3:
                    case Constant.TISSUE4:
                    case Constant.TISSUE5: {
                        mivNoticeImage.setImageResource(R.mipmap.icon_tissue);
                        text = getString(R.string.tissue);
                        break;
                    }
                    case Constant.PAY1:
                    case Constant.PAY2:
                    case Constant.PAY3:
                    case Constant.PAY4:
                    case Constant.PAY5: {
                        mivNoticeImage.setImageResource(R.mipmap.icon_pay);
                        text = getString(R.string.pay);
                        break;
                    }
                    case Constant.OTHER1:
                    case Constant.OTHER2:
                    case Constant.OTHER3:
                    case Constant.OTHER4:
                    case Constant.OTHER5: {
                        mivNoticeImage.setImageResource(R.mipmap.icon_service);
                        text = getString(R.string.other);
                        break;
                    }
                    case Constant.CLEAN_DESK: {
                        mivNoticeImage.setImageResource(R.mipmap.icon_clean);
                        text = getString(R.string.clean);
                        break;
                    }
                }
                Log.v(TAG, "AnimationStart=" + id);
                mrlNoticeText.setText(text);
                mHandler.sendEmptyMessageDelayed(STOP_ANIMATION, DELAY_TIME);
            }
        });
        mrlNotice.startAnimation(set);
        Log.v(TAG, "AnimationStart mrlNotice.startAnimation(set)" + i);
    }

    private void AnimationStartNoAnim(final int id) {
        mrlNotice.setVisibility(View.VISIBLE);

        String text = null;
        Log.v(TAG, "nimationStart onAnimationEnd" + id);
        switch (id) {
            case Constant.ORDER_DISHES1:
            case Constant.ORDER_DISHES2:
            case Constant.ORDER_DISHES3:
            case Constant.ORDER_DISHES4:
            case Constant.ORDER_DISHES5: {
                mivNoticeImage.setImageResource(R.mipmap.icon_dish);
                text = getString(R.string.add_greens);
                break;
            }
            case Constant.ADD_WATER1:
            case Constant.ADD_WATER2:
            case Constant.ADD_WATER3:
            case Constant.ADD_WATER4:
            case Constant.ADD_WATER5: {
                mivNoticeImage.setImageResource(R.mipmap.icon_water);
                text = getString(R.string.add_water);
                break;
            }
            case Constant.TISSUE1:
            case Constant.TISSUE2:
            case Constant.TISSUE3:
            case Constant.TISSUE4:
            case Constant.TISSUE5: {
                mivNoticeImage.setImageResource(R.mipmap.icon_tissue);
                text = getString(R.string.tissue);
                break;
            }
            case Constant.PAY1:
            case Constant.PAY2:
            case Constant.PAY3:
            case Constant.PAY4:
            case Constant.PAY5: {
                mivNoticeImage.setImageResource(R.mipmap.icon_pay);
                text = getString(R.string.pay);
                break;
            }
            case Constant.OTHER1:
            case Constant.OTHER2:
            case Constant.OTHER3:
            case Constant.OTHER4:
            case Constant.OTHER5: {
                mivNoticeImage.setImageResource(R.mipmap.icon_service);
                text = getString(R.string.other);
                break;
            }
            case Constant.CLEAN_DESK: {
                mivNoticeImage.setImageResource(R.mipmap.icon_clean);
                text = getString(R.string.clean);
                break;
            }
        }
        Log.v(TAG, "AnimationStart=" + id);
        mrlNoticeText.setText(text);
        mHandler.sendEmptyMessageDelayed(STOP_ANIMATION, DELAY_TIME);

    }

    public class UpdateTableHandlerSuccess extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
               String action=intent.getAction();
               if(SUCCESSACTION.equals(action)){
                   mtv.setVisibility(View.GONE);
               }
        }
    }
}
