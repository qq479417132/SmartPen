package com.cleverm.smartpen.util;

import android.os.Environment;
import android.util.Log;

import com.cleverm.smartpen.bean.VideoInfo;
import com.cleverm.smartpen.ui.FullScreenVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.io.File;
import java.util.List;


/**
 * Created by x on 2016/2/15.
 */
public class DownloadUtil {


    public static void preVideoFile(final FullScreenVideoView vvAdvertisement){
        String url = "http://120.25.159.173:8280/api/api/v10/video/list";
        OkHttpUtils
                .get()
                .url(url)
                .addParams("orgId", "100")
                .addParams("type", "1")
                .build()
                .execute(new StringCallback() {



                    @Override
                    public void onError(okhttp3.Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            List<VideoInfo> info = JsonUtil.parser(response, VideoInfo.class);


                            //第一个视频为在线直接播放，其他所有视频为下载后再播放

                            for(int i =0;i<info.size();i++){
                                DownloadUtil.downloadFile(info.get(i).getVideoPath(), (i + 1) + "");
                            }



                            VideoUtil videoUtil = new VideoUtil(vvAdvertisement);
                            videoUtil.prepareOnlineVideo(info);


                            //vvAdvertisement.setVideoPath(info.get(0).getVideoPath());
                            //vvAdvertisement.start();


                            //vvAdvertisement.setVideoPath(info.get(0).getVideoPath());
                            // vvAdvertisement.start();
                            //vvAdvertisement.setOnPreparedListener(viewPreListener);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }



                });
    }


    public static void downloadFile(String path, String num) {

        //存储的地址为storage/emulated/0/muye/木爷我们的视频.mp4
        OkHttpUtils//
                .get()//
                .url(path)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath() + "/muye", num + ".mp4")//
                {
                    @Override
                    public void inProgress(float progress) {
                        Log.e("FILE", "onResponse :" + progress);
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Log.e("FILE", "onResponse :" + e.getMessage());
                    }


                    @Override
                    public void onResponse(File file) {
                        Log.e("FILE", "onResponse :" + file.getAbsolutePath());
                    }
                });
    }


}
