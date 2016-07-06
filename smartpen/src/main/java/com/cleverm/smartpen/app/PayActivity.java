package com.cleverm.smartpen.app;

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
import com.cleverm.smartpen.application.SmartPenApplication;
import com.cleverm.smartpen.bean.TemplateIDState;
import com.cleverm.smartpen.net.InfoSendSMSVo;
import com.cleverm.smartpen.net.RequestNet;
import com.cleverm.smartpen.pushtable.MessageType;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.IntentUtil;
import com.cleverm.smartpen.util.NetWorkUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.StatisticsUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class PayActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = PayActivity.class.getSimpleName();
    public static final String SELECTEDTABLEID = "SelectedTableId";
    public static final int GOBack = 200;
    public static final int TIME = 60000;
    private Button mCashPay;
    private Button mUnionCardPay;
    private ImageView mClose;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOBack: {
                    IntentUtil.goBackToVideoActivity(PayActivity.this);
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
        mCashPay = (Button) findViewById(R.id.cash_pay);
        mUnionCardPay = (Button) findViewById(R.id.union_pay_card_pay);
        mClose = (ImageView) findViewById(R.id.pay_close);
        mCashPay.setOnClickListener(this);
        mUnionCardPay.setOnClickListener(this);
        mClose.setOnClickListener(this);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cash_pay:
                IntentUtil.goPayServcie(PayActivity.this,Constant.CASH_PAY);
                break;
            case R.id.union_pay_card_pay:
                IntentUtil.goPayServcie(PayActivity.this,Constant.UNION_CARD_PAY);
                break;

            case R.id.pay_close:
                mHandler.sendEmptyMessage(GOBack);
                break;

        }
    }
}
