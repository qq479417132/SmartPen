package com.cleverm.smartpen.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.net.InfoSendSMSVo;
import com.cleverm.smartpen.net.RequestNet;
import com.cleverm.smartpen.pushtable.MessageType;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.NetWorkUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.StatisticsUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class PayActivity extends BaseActivity {

    public static final String TAG = PayActivity.class.getSimpleName();
    public static final String SELECTEDTABLEID="SelectedTableId";
    private Object object=new Object();
    public static final int GOBack = 200;
    public static final int TIME = 60000;
    public static final int NotificateWaiterSHOW= 201;
    public static final int NotificateWaiterGone = 202;
    public static final int NOTIFICATEWAITERGONE_IMMEDIATELY = 203;
    public static final int NotificateWaiterTIME = 2000;
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
                case NOTIFICATEWAITERGONE_IMMEDIATELY:{
                    mNotificateWaiter.setVisibility(View.GONE);
                    this.sendEmptyMessage(GOBack);
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
            mHandler.sendEmptyMessageDelayed(NOTIFICATEWAITERGONE_IMMEDIATELY,NotificateWaiterTIME);
            Log.v(TAG, "sendMes===isSuccess");
        } else {
            mHandler.sendEmptyMessage(NotificateWaiterGone);
            Log.v(TAG, "sendMes===isfalse");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(GOBack, TIME);

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }


    /**
     * 主动去请求店铺数据
     */
    public void testSendSMS() {
        InfoSendSMSVo infoSendSMSVo = new InfoSendSMSVo();
        infoSendSMSVo.setTemplateID(5);
        infoSendSMSVo.setClientID(102L);
        infoSendSMSVo.setOrgID(102L);
        infoSendSMSVo.setTableID(145);


        com.cleverm.smartpen.pushtable.Message message = com.cleverm.smartpen.pushtable.Message.create().messageType(MessageType
                .NOTIFICATION).header("Notice-Type", "SEND_SMSINTO").json(infoSendSMSVo).build();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(Constant.DDP_URL+"/cleverm/sockjs/execCommand");
            Log.v(TAG,"http://120.25.159.173:8080/cleverm/sockjs/execCommand==");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.connect();
            String input = JSON.toJSONString(message);
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes("utf-8"));
            os.flush();
            os.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf8"));
            String line = reader.readLine();
            reader.close();
            Log.v(TAG, "testSendSMS()= " + line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

        }
    }

}
