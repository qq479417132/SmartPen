package com.cleverm.smartpen.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.util.Constant;

/**
 * Created by 95 on 2016/2/3.
 */
public class FutureActivity extends BaseActivity {
    public static final String TAG = FutureActivity.class.getSimpleName();
    public static final int GOBack = 200;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOBack: {
                    Log.v(TAG, "come hand====");
                    FutureActivity.this.finish();
                    startActivity(new Intent(FutureActivity.this, VideoActivity.class));
                    ((CleverM) getApplication()).getpenService().setActivityFlag("VideoActivity");
                    break;
                }
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.future_activity);
        findViewById(R.id.future_pic).setAnimation(AnimationUtils.loadAnimation(this, R.anim.future));
        findViewById(R.id.future_close).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mHandler.sendEmptyMessage(GOBack);
                    }
                }
        );
        mHandler.sendEmptyMessageDelayed(GOBack, Constant.DELAY_BACK);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "OthersFragment onPause()=====");
        mHandler.removeCallbacksAndMessages(null);
    }
}