package com.cleverm.smartpen.pushtable;

import android.content.Context;
import android.util.Log;

import com.cleverm.smartpen.util.Constant;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

public class HotfixHandler extends NoticeHandler<HotfixSetVo, Void> {
    public static final String TAG = HotfixVo.class.getSimpleName();
    
    public static final String TARGET = "/sdcard/aaa.zip";
    private Context mContext;

    public HotfixHandler(Context context) {
        super("CONTENT_GENERIC");
        mContext = context;
        Log.v(TAG,"HotfixHandler Response<Void> onSuccess");
    }

    @Override
    public Response<Void> onSuccess(Headers headers, HotfixSetVo in) {
        //���µ�Ӱ��λ��
        Log.v(TAG,"Response<Void> onSuccess");
        if(in == null){
            return null;
        }
        Set<HotfixVo> set = in.getHotfixSet();
        Iterator<HotfixVo> iterator = set.iterator();
        HotfixCallback callback = new HotfixCallback();
        HttpUtils utils = new HttpUtils();
        while(iterator.hasNext()){
            HotfixVo hotfixVo = iterator.next();
            String url = hotfixVo.getUrl();
            Log.d("miaomiao", url);
            int index = 0;
            String name = null;
            if(url.contains("foods")){
                index = url.indexOf("foods");
                name = Constant.RESOURCEPATH+"/"+url.substring(index);
            }else if(url.contains("voice")){
                index = url.indexOf("voice");
                name = Constant.RESOURCEPATH+"/"+url.substring(index);
            }else if(url.contains("basepath")){
                index = url.indexOf("basepath")+8;
                name = Constant.RESOURCEPATH+"/"+url.substring(index);
            }else if(url.contains("shipin")){
                index = url.indexOf("shipin");
                name = "/mnt/sdcard/"+"/"+url.substring(index);
            }else if(url.contains("sayhello")){
                index = url.indexOf("sayhello");
                name = Constant.RESOURCEPATH+"/"+url.substring(index);
            }
            if(index != 0){
                utils.download(url, name, callback);
            }
        }
        return null;
    }

    @Override
    public void onError(int code, String message) {
        // TODO Auto-generated method stub
        
    }
    
    class HotfixCallback extends RequestCallBack<File> {

        @Override
        public void onFailure(HttpException arg0, String arg1) {
            Log.d(TAG, arg0.getMessage()+"==="+arg1);
        }

        @Override
        public void onSuccess(ResponseInfo<File> arg0) {
            
        }
        
    }

}
