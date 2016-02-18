package com.cleverm.smartpen.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cleverm.smartpen.R;

/**
 * Created by 95 on 2016/2/3.
 */
public class FutureActivity extends BaseActivity {
    public static final String TAG = FutureActivity.class.getSimpleName();
    private ImageView mClose;
    public static final int GOBack = 200;
    public static final int TIME = 60000;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOBack: {
                    Log.v(TAG, "come hand====");
                    startActivity(new Intent(FutureActivity.this, MainActivity.class));
                    FutureActivity.this.finish();
                    break;
                }
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.future_activity);
        findViewById(R.id.future_pic).setAnimation(AnimationUtils.loadAnimation(this,R.anim.future));
        findViewById(R.id.future_close).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mHandler.sendEmptyMessage(GOBack);
                    }
                }
        );
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "OthersFragment onPause()=====");
        mHandler.removeCallbacksAndMessages(null);
    }
}