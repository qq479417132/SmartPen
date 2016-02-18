package com.cleverm.smartpen.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.cleverm.smartpen.R;

import com.cleverm.smartpen.service.ScreenLockListenService;
import com.cleverm.smartpen.service.penService;
import com.cleverm.smartpen.ui.FullScreenVideoView;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.DownloadUtil;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.VideoUtil;


/**
 * Created by xiong on 2016/2/15.
 * 已知待处理情况:第一次开启时无网络
 */
public class VideoActivity extends Activity implements penService.MessageListener {
    public static final String TAG=VideoActivity.class.getSimpleName();
    /**
     * 该参数由服务端给与
     */
    private boolean isNotChange = true;

    /**
     * 该参数由本地存储判断获取
     */
    private boolean isHaveVideo = true;

    private String VIDEO_MAIN_URI = Environment.getExternalStorageDirectory().getAbsolutePath() + "/muye";


    FullScreenVideoView vvAdvertisement;


    /**
     * ServiceConnection
     */
    private penService mpenService;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mpenService = ((penService.penServiceBind) service).getService();
            mpenService.setMessageListener(VideoActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ScreenLockListenService.ScreenLockListenServiceStub mStub;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStub = (ScreenLockListenService.ScreenLockListenServiceStub) service;
            mStub.setWindow(getWindow());
            mStub.setTaskId(getTaskId());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        bindService();
        initView();
        initData();


    }

    private void bindService() {
        bindService(new Intent(this, penService.class), mConn, BIND_AUTO_CREATE);
        bindService(new Intent(this, ScreenLockListenService.class), mConnection, BIND_AUTO_CREATE);
    }


    private void initView() {
        vvAdvertisement = (FullScreenVideoView) findViewById(R.id.vvAdvertisement);
    }


    private void initData() {


        //1.先判断服务器实现需要我们去更新


        //2.如果不需要更新,直接检查我们的视频目录是否存在视频


        //3.根据排序规则进行视频的依次播放

        DownloadUtil.preVideoFile(vvAdvertisement);


        /*if (isNotChange) {
            if (isHaveVideo) {
                //拿本地的Video：本地Video的顺序是文件名的顺序
                VideoUtil videoUtil = new VideoUtil(vvAdvertisement);
                videoUtil.prepareVideo(VIDEO_MAIN_URI, 0);
            } else {
                //本地没有视频的话,就重新去服务器取地址并下载存储
                DownloadUtil.preVideoFile(vvAdvertisement);
            }
        } else {
            //如果更新Video,直接去读取Video,并存储所有的Video
            DownloadUtil.preVideoFile(vvAdvertisement);
        }*/


    }


    /**
     *
     */

    /**
     * 横竖屏数据处理逻辑
     * 在onPause中记下位置
     * 在onResume中从该位置播放
     * 记录到SharePrefrence中
     */
    @Override
    protected void onPause() {
        super.onPause();
        RememberUtil.putInt("key", vvAdvertisement.getCurrentPosition());
    }

    int videoValue = 0;


    @Override
    protected void onResume() {
        super.onResume();
        videoValue = RememberUtil.getInt("key", 0);
        vvAdvertisement.seekTo(videoValue);
        vvAdvertisement.start();
    }


    /**
     * implements penService.MessageListener
     *
     * @param id
     */
    @Override
    public void receiveData(int id) {
        Log.v(TAG, "receiveData id=" + id);
        if (id == 0) {
            return;
        }
        mHandler.sendEmptyMessage(id);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handlerCode(msg.what);
        }
    };


    private void handlerCode(int id) {

        //QuickUtils.toast(this,"code="+id);

        switch (id) {
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
            case Constant.OTHER5:{

                break;
            }
            case Constant.TWO_DIMENSION_CODE1:

                //优惠推介
            case Constant.YOU_HUI1: {
                //gotoDiscountFragment(id);
                break;
            }


            case Constant.DEMO1:

                //商家特惠
                //case Constant.RECOMMEND:{
                //gotoDiscountFragment(id);
                //    break;
                //}


            case Constant.AMUSEMENTFRAGMENT1:
            case Constant.WEB1:
            case Constant.MO_JI1:
            case Constant.TOU_TIAO1:
            case Constant.BAI_DU1:
            case Constant.ONE_SHOP1:
            case Constant.DA_ZONG1:
            case Constant.E_JIA1:
            case Constant.ZHI_ZHU1:


            case Constant.EVALUATE1: {

                break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
        unbindService(mConnection);
    }
}
