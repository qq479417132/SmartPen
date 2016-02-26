package com.cleverm.smartpen.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.ui.MyWebView;
import com.cleverm.smartpen.util.Constant;

/**
 * Created by 95 on 2016/2/24.
 */
public class GameActivity extends BaseActivity implements MyWebView.WebViewTouchEvent {

    public static final String TAG=GameActivity.class.getSimpleName();
    private MyWebView mWebView;
    //关闭按钮
    private ImageView mClose;
    private ProgressDialog mProgressDialog;
    private String mURL=Constant.URL;
    public static final String GAME_URL="game_url";
    public static final int ENTER=0;
    public static final int EXIT=1;
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ENTER:{
                    break;
                }
                case EXIT:{
                    startActivity(new Intent(GameActivity.this, VideoActivity.class));
                    GameActivity.this.finish();
                    ((CleverM) getApplication()).getpenService().setActivityFlag("VideoActivity");
                    break;
                }
                default:{
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        initIntent();
        initView();

    }

    private void initIntent() {
        Intent in=getIntent();
        if(in==null){
           return;
        }
        mURL=in.getStringExtra(GAME_URL);
    }

    private void initView() {
        mProgressDialog=ProgressDialog.show(this,getString(R.string.waiting), getString(R.string.isloading), true);
        mWebView= (MyWebView) findViewById(R.id.web);
        mClose= (ImageView) findViewById(R.id.game_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessage(EXIT);
            }
        });
        mWebView.setWebViewTouchEventListener(this);
        mWebView.loadUrl(mURL);
        mHandler.sendEmptyMessageDelayed(EXIT, Constant.DELAY_BACK);
        WebSettings sets=mWebView.getSettings();
        sets.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v(TAG, "shouldOverrideUrlLoading");
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressDialog.dismiss();
            }


        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mProgressDialog.dismiss();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void isWebViewTouch(boolean isTouch) {
        if(isTouch){
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessageDelayed(EXIT, Constant.DELAY_BACK);
        }
    }
}
