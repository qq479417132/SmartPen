package com.cleverm.smartpen.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.net.InfoSendSMSVo;
import com.cleverm.smartpen.net.RequestNet;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.NetWorkUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.StatisticsUtil;
import com.umeng.analytics.MobclickAgent;


public class PayActivity extends BaseActivity {

    public static final String TAG = PayActivity.class.getSimpleName();
    public static final String SELECTEDTABLEID="SelectedTableId";
    private Object object=new Object();
    public static final int GOBack = 200;
    public static final int TIME = 60000;
    public static final int NotificateWaiterSHOW= 201;
    public static final int NotificateWaiterGone = 202;
    public static final int NotificateWaiterTIME = 3000;
    private Button mCashPay;
    private Button mUnionCardPay;
    private ImageView mClose;
    private LinearLayout mNotificateWaiter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOBack: {
                    Log.v(TAG, "come hand====");
                    startActivity(new Intent(PayActivity.this, VideoActivity.class));
                    PayActivity.this.finish();
                    ((CleverM) getApplication()).getpenService().setActivityFlag("VideoActivity");
                    break;
                }
                case NotificateWaiterSHOW:{
                    mNotificateWaiter.setVisibility(View.VISIBLE);
                    break;
                }
                case NotificateWaiterGone:{
                    mNotificateWaiter.setVisibility(View.GONE);
                    break;
                }
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_activity);
        initView();
    }

    @Override
    protected int onGetEventId() {
        return StatisticsUtil.CALL_PAY;
    }

    @Override
    protected String onGetDesc() {
        return StatisticsUtil.CALL_PAY_DESC;
    }

    private void initView() {
        mNotificateWaiter= (LinearLayout) findViewById(R.id.notificate_waiter);
        mCashPay= (Button) findViewById(R.id.cash_pay);
        mCashPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //统计代码
                StatisticsUtil.getInstance().insertWithSecondEvent(StatisticsUtil.CALL_PAY,StatisticsUtil.CALL_PAY_DESC+"--"+StatisticsUtil.CALL_PAY_CASH_DESC,StatisticsUtil.CALL_PAY_CASH);
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(GOBack, TIME);
                NotificateWaiter(Constant.CASH_PAY);
            }
        });
        mUnionCardPay= (Button) findViewById(R.id.union_pay_card_pay);
        mUnionCardPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //统计代码
                StatisticsUtil.getInstance().insertWithSecondEvent(StatisticsUtil.CALL_PAY,StatisticsUtil.CALL_PAY_DESC+"--"+StatisticsUtil.CALL_PAY_CARD_DESC,StatisticsUtil.CALL_PAY_CARD);
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(GOBack, TIME);
                NotificateWaiter(Constant.UNION_CARD_PAY);
            }
        });
        mClose= (ImageView) findViewById(R.id.pay_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessage(GOBack);
            }
        });
    }

    private void NotificateWaiter(int payCode) {
        if(!NetWorkUtil.hasNetwork()){
            QuickUtils.toast("网络异常，请直接找服务员～");
            return;
        }
        long deskId = RememberUtil.getLong(SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);;
        if(deskId==Constant.DESK_ID_DEF_DEFAULT){
            return;
        }
        final InfoSendSMSVo infoSendSMSVo = new InfoSendSMSVo();
        infoSendSMSVo.setTemplateID(payCode);
        infoSendSMSVo.setTableID(deskId);
        synchronized (object){
            new Thread(){
                @Override
                public void run() {
                    sendMes(infoSendSMSVo);
                    Log.v(TAG,"NotificateWaiter");
                }
            }.start();
        }
    }

    private void sendMes(InfoSendSMSVo infoSendSMSVo){
        InfoSendSMSVo getSMSVo = RequestNet.getData(infoSendSMSVo);
        if (getSMSVo != null && getSMSVo.getSuccess()) {
            mHandler.sendEmptyMessage(NotificateWaiterSHOW);
            mHandler.sendEmptyMessageDelayed(NotificateWaiterGone,NotificateWaiterTIME);
            Log.v(TAG, "sendMes===isSuccess");
        } else {
            mHandler.sendEmptyMessage(NotificateWaiterGone);
            Log.v(TAG, "sendMes===isfalse");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(GOBack, TIME);

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }


}
