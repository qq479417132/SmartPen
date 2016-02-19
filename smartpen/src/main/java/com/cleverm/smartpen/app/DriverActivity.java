package com.cleverm.smartpen.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.cleverm.smartpen.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 95 on 2016/2/3.
 */
public class DriverActivity extends BaseActivity {
    public static final String TAG = DriverActivity.class.getSimpleName();
    private ImageView mClose;
    public static final int GOBack = 200;
    public static final int TIME = 60000;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOBack: {
                    Log.v(TAG, "come hand====");
                    startActivity(new Intent(DriverActivity.this, VideoActivity.class));
                    DriverActivity.this.finish();
                    break;
                }
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.driver_activity);
        init();
    }


    private void init() {
        mClose= (ImageView) findViewById(R.id.e_driver_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessage(GOBack);
            }
        });
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(GOBack, TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(this, "E_JIA");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "OthersFragment onPause()=====");
        mHandler.removeCallbacksAndMessages(null);
        MobclickAgent.onEvent(this, "E_JIA");
    }


}