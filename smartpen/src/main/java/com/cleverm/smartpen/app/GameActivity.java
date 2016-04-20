package com.cleverm.smartpen.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.service.penService;
import com.cleverm.smartpen.ui.MyWebView;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.StatisticsUtil;

/**
 * Created by 95 on 2016/2/24.
 */
public class GameActivity extends BaseActivity implements MyWebView.WebViewTouchEvent {

    public static final String TAG=GameActivity.class.getSimpleName();
    private MyWebView mWebView;
    //关闭按钮
    private ImageView mClose;
    private ProgressDialog mProgressDialog;
    private String mURL;
    public static final String GAME_URL="game_url";
    public static final int ENTER=0;
    public static final int EXIT=1;
    private static final String INJECTION_TOKEN = "**injection**";
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
        initView();
    }

    @Override
    protected void initSonIntent() {
        super.initSonIntent();
        Intent in=getIntent();
        if(in==null){
            return;
        }
        mURL=in.getStringExtra(GAME_URL);
    }

    @Override
    protected int onGetEventId() {
        if(mURL.equals(Constant.NEARBY_DISCOUNT_URL)){
            return StatisticsUtil.APP_AROUNDDISCOUNT;
        }else{
            return StatisticsUtil.H5_GAME;
        }
    }

    @Override
    protected String onGetDesc() {
        if(mURL.equals(Constant.NEARBY_DISCOUNT_URL)){
            return StatisticsUtil.APP_AROUNDDISCOUNT_DESC;
        }else{
            return StatisticsUtil.H5_GAME_DESC;
        }
    }


    private void initView() {
        mProgressDialog=ProgressDialog.show(this,getString(R.string.waiting), getString(R.string.isloading), true);
        mProgressDialog.setCancelable(true);
        mWebView= (MyWebView) findViewById(R.id.web);
        mClose= (ImageView) findViewById(R.id.game_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessage(EXIT);
            }
        });
        mWebView.setWebViewTouchEventListener(this);
        Log.v(TAG, "mURL=" + mURL);
        mHandler.sendEmptyMessageDelayed(EXIT, Constant.DELAY_BACK);
        WebSettings sets=mWebView.getSettings();
        sets.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v(TAG, "url= " + url);
                view.loadUrl(url);
                return true;
//                if( url.startsWith("http:") || url.startsWith("https:") ) {
//                    return false;
//                }
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
//                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressDialog.dismiss();
            }


        });
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());  //在前面加入下载监听器
        mWebView.loadUrl(mURL);

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

    private boolean falg=true;
    class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                    String mimetype,long contentLength) {
            Log.v(TAG,"onDownloadStart url="+url);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        penService penService = ((CleverM) getApplication()).getpenService();
        if(mURL.equals(Constant.NEARBY_DISCOUNT_URL)){
            if(penService!=null){
                penService.setActivityFlag(penService.DISCOUNT);
            }
        }else{
            if(penService!=null){
                penService.setActivityFlag("GAME_ACTIVITY");
            }
        }



    }
}
