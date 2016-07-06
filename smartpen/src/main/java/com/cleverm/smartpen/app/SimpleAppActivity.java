package com.cleverm.smartpen.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bleframe.library.bundle.OnLeScanBundle;
import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.SmartPenApplication;
import com.cleverm.smartpen.bean.evnet.OnChangeAnimNoticeEvent;
import com.cleverm.smartpen.net.InfoSendSMSVo;
import com.cleverm.smartpen.net.RequestNet;
import com.cleverm.smartpen.ui.FullScreenVideoView;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.NetWorkUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.StatisticsUtil;
import com.cleverm.smartpen.util.ThreadManager;

import java.lang.ref.WeakReference;

import static com.cleverm.smartpen.app.SimpleAppActivity.MessageCode.FOOD;
import static com.cleverm.smartpen.app.SimpleAppActivity.MessageCode.OTHER;
import static com.cleverm.smartpen.app.SimpleAppActivity.MessageCode.PAY;
import static com.cleverm.smartpen.app.SimpleAppActivity.MessageCode.WATER;

/**
 * Created by xiong,An android project Engineer,on 29/6/2016.
 * Data:29/6/2016  下午 03:22
 * Base on clever-m.com(JAVA Service)
 * Describe: 无笔版本Demo类
 * Version:1.0
 * Open source
 */
public class SimpleAppActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    Dispatch mDispatch;
    private static final String TAG = "SimpleAppActivity";

    FullScreenVideoView mVideoFsvv;
    ImageView mCallFood;
    ImageView mCallWater;
    ImageView mCallPay;
    ImageView mCallOther;

    RelativeLayout mNoticeRl;
    ImageView mNoticeImageIv;
    TextView mNoticeTextRl;

    public enum MessageCode {
        FOOD, WATER, PAY, OTHER
    }


    /**
     * 分发器
     */
    public class Dispatch {

        private DispatchHandler mDispatchHandler;

        private static final int MESSAGE_ANIM_START = 1;

        private static final int MESSAGE_ANIM_STOP = 2;

        private static final int DELAY_TIME = 3000;

        public Dispatch() {
            mDispatchHandler = new DispatchHandler(Looper.getMainLooper(), this);
        }


        public void dispatchAnimStart(OnChangeAnimNoticeEvent info) {
            mDispatchHandler.postAnimStart(info);
        }

        public void dispatchAnimStop() {
            mDispatchHandler.postAnimStop();
        }

        public class DispatchHandler extends Handler {

            private WeakReference<Dispatch> mDispatchRef;

            public DispatchHandler(Looper looper, Dispatch dispatch) {
                super(looper);
                this.mDispatchRef = new WeakReference<Dispatch>(dispatch);
            }

            @Override
            public void handleMessage(Message msg) {
                Dispatch dispatch = mDispatchRef.get();
                switch (msg.what) {
                    case MESSAGE_ANIM_START:
                        dispatch.startAnim((OnChangeAnimNoticeEvent) msg.obj);
                        break;
                    case MESSAGE_ANIM_STOP:
                        dispatch.stopAnim();
                        break;

                }
            }

            void postAnimStart(OnChangeAnimNoticeEvent info) {
                removeCallbacksAndMessages(null);
                obtainMessage(MESSAGE_ANIM_START, info).sendToTarget();
            }

            void postAnimStop() {
                mDispatchHandler.sendEmptyMessageDelayed(MESSAGE_ANIM_STOP, DELAY_TIME);
            }

        }


        private void startAnim(OnChangeAnimNoticeEvent event) {
            doAnim(new OnChangeAnimNoticeEvent(event.getImageResoure(), event.getText(), OnChangeAnimNoticeEvent.Status.SHOW));
        }

        private void stopAnim() {
            doAnim(new OnChangeAnimNoticeEvent(0, null, OnChangeAnimNoticeEvent.Status.HIDE));
        }


        private void doStartAnimation(final MessageCode code) {
            String text = null;
            int imageResource = -1;
            switch (code) {
                case FOOD: {
                    imageResource = R.mipmap.icon_simple_food;
                    text = SmartPenApplication.getApplication().getString(R.string.add_greens);
                    break;
                }
                case WATER: {
                    imageResource = R.mipmap.icon_simple_water;
                    text = SmartPenApplication.getApplication().getString(R.string.add_water);
                    break;
                }
                case PAY: {
                    imageResource = R.mipmap.icon_simple_pay;
                    text = SmartPenApplication.getApplication().getString(R.string.pay);
                    break;
                }

                case OTHER: {
                    imageResource = R.mipmap.icon_simple_other;
                    text = SmartPenApplication.getApplication().getString(R.string.other);
                    break;
                }

            }

            mDispatch.dispatchAnimStart(new OnChangeAnimNoticeEvent(imageResource, text, OnChangeAnimNoticeEvent.Status.SHOW));

            mDispatch.dispatchAnimStop();
        }

        private void doAnim(OnChangeAnimNoticeEvent event) {
            onChangeAnimNoticeEvent(event);
        }


    }

    public void onChangeAnimNoticeEvent(OnChangeAnimNoticeEvent event) {
        if (event.getType() == OnChangeAnimNoticeEvent.Status.HIDE) {
            mNoticeRl.setVisibility(View.GONE);
        } else if (event.getType() == OnChangeAnimNoticeEvent.Status.SHOW) {
            mNoticeRl.setVisibility(View.VISIBLE);
            mNoticeTextRl.setText(event.getText());
            mNoticeImageIv.setImageResource(event.getImageResoure());
        }
    }


    public static class DoSmsSendPart {

        private static class DoSmsPartHolder {
            private static final DoSmsSendPart INSTANCE = new DoSmsSendPart();
        }

        private DoSmsSendPart() {
        }

        public static final DoSmsSendPart getInstance() {
            return DoSmsPartHolder.INSTANCE;
        }


        public void sendSms(MessageCode penCode, boolean alreadySend, Dispatch dispatch) {
            int smsCode = statIteration(penCode);
            long deskId = RememberUtil.getLong(SelectTableActivity.SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);
            if (!NetWorkUtil.hasNetwork()) {
                QuickUtils.toast("网络异常，请直接找服务员～");
                return;
            }
            if (deskId == Constant.DESK_ID_DEF_DEFAULT) {
                QuickUtils.toast("桌号未设置，请直接找服务员～");
                return;
            }

            InfoSendSMSVo infoSendSMSVo = new InfoSendSMSVo();
            infoSendSMSVo.setTemplateID(smsCode);
            infoSendSMSVo.setTableID(deskId);
            Log.v(TAG, "SendSMS：" + "id=" + penCode + " deskId=" + deskId + " alreadySend=" + alreadySend);

            sendMessageToService(infoSendSMSVo, penCode, false, dispatch);

        }

        private void sendMessageToService(final InfoSendSMSVo infoSendSMSVo, final MessageCode code, final boolean alreadySend, final Dispatch dispatch) {
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (alreadySend == false) {
                        InfoSendSMSVo getSMSVo = RequestNet.getData(infoSendSMSVo);
                        if (getSMSVo != null && getSMSVo.getSuccess()) {
                            QuickUtils.log(TAG + "SendSMS-success" + getSMSVo.getSuccess());
                            dispatch.doStartAnimation(code);
                        } else {
                        }
                    } else {
                        dispatch.doStartAnimation(code);
                    }
                }
            });
        }


        private int statIteration(MessageCode penCode) {
            int smsCode = 0;
            switch (penCode) {
                case FOOD:
                    smsCode = 1;
                    doStatistic(StatisticsUtil.CALL_ADD_FOOD, StatisticsUtil.CALL_ADD_FOOD_DESC);
                    break;

                case WATER:
                    smsCode = 2;
                    doStatistic(StatisticsUtil.CALL_ADD_WATER, StatisticsUtil.CALL_ADD_FOOD_DESC);
                    break;

                case PAY:
                    smsCode = 4;
                    doStatistic(StatisticsUtil.CALL_PAY, StatisticsUtil.CALL_PAY_DESC);
                    break;

                case OTHER:
                    smsCode = 5;
                    doStatistic(StatisticsUtil.CALL_OTHER_SERVIC, StatisticsUtil.CALL_OTHER_SERVIC_DESC);
                    break;
            }
            return smsCode;
        }

        private void doStatistic(final int eventId, final String eventDesc) {
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    StatisticsUtil.getInstance().insert(eventId, eventDesc);
                }
            });
        }

        private void doStatistic(final int eventId, final String eventDesc, final Long secondEventId) {
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    StatisticsUtil.getInstance().insertWithSecondEvent(eventId, eventDesc, secondEventId);
                }
            });
        }


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }


    private void initView() {
        mContext = this;
        setContentView(R.layout.activity_simple_app);
        mVideoFsvv = (FullScreenVideoView) findViewById(R.id.fsvv_video);
        mCallFood = (ImageView) findViewById(R.id.iv_call_food);
        mCallWater = (ImageView) findViewById(R.id.iv_call_water);
        mCallPay = (ImageView) findViewById(R.id.iv_call_pay);
        mCallOther = (ImageView) findViewById(R.id.iv_call_other);

        mNoticeRl = (RelativeLayout) findViewById(R.id.rlNotice);
        mNoticeImageIv = (ImageView) findViewById(R.id.ivNoticeImage);
        mNoticeTextRl = (TextView) findViewById(R.id.rlNoticeText);
    }

    private void initListener() {
        mCallFood.setOnClickListener(this);
        mCallWater.setOnClickListener(this);
        mCallPay.setOnClickListener(this);
        mCallOther.setOnClickListener(this);
    }


    private void initData() {
        mDispatch = new Dispatch();
    }


    @Override
    protected int onGetEventId() {
        return StatisticsUtil.BACK_VIDEO;
    }

    @Override
    protected String onGetDesc() {
        return StatisticsUtil.BACK_VIDEO_DESC;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_call_food:
                handlerMessage(FOOD);
                break;

            case R.id.iv_call_water:
                handlerMessage(WATER);
                break;

            case R.id.iv_call_pay:
                handlerMessage(PAY);
                break;

            case R.id.iv_call_other:
                handlerMessage(OTHER);
                break;
        }
    }


    /**
     * 在30秒内多次点击的不会做发送，但会显示图标
     */
    private void handlerMessage(MessageCode code) {
        DoSmsSendPart.getInstance().sendSms(code, false, mDispatch);
    }


}
