package com.cleverm.smartpen.util;

import android.util.Log;

import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.bean.VideoInfo;
import com.cleverm.smartpen.ui.FullScreenVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by xiong,An android project Engineer,on 2016/2/24.
 * Data:2016-02-24  14:45
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class VideoAlgorithmUtil {

    private static VideoAlgorithmUtil INSTANCE =new VideoAlgorithmUtil();

    public static VideoAlgorithmUtil getInstance() {
        return INSTANCE;
    }

    private VideoAlgorithmUtil(){

    }

    public interface videoInterface{
        void onSucess(String json);
        void onFail(String error);
    }



    /**
     * 第一次从服务端直接读取并存储到本地
     * @param vvAdvertisement
     */
    public  void getVideoFirst(final FullScreenVideoView vvAdvertisement) {

        videoAPI(new videoInterface() {
            @Override
            public void onSucess(String json) {
                try {
                    List<VideoInfo> info = JsonUtil.parser(json, VideoInfo.class);

                    //第一个视频为在线直接播放，其他所有视频为下载后再播放
                    for (int i = 0; i < info.size(); i++) {
                        downloadVideoFirst(info.get(i).getVideoPath(), info.get(i).getVideoId() + "");
                    }

                    VideoUtil videoUtil = new VideoUtil(vvAdvertisement);
                    videoUtil.prepareOnlineVideo(info);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {

            }
        });

    }


    /**
     * 第一次下载所有视频的方法
     * @param path
     * @param num
     */
    private  void downloadVideoFirst(String path, String num) {

        //存储的地址为storage/emulated/0/muye/木爷我们的视频.mp4
        OkHttpUtils//
                .get()//
                .url(path)//
                .build()//
                .execute(new FileCallBack(AlgorithmUtil.VIDEO_FILE, num + ".mp4")//
                {
                    @Override
                    public void inProgress(float progress) {
                        //Log.i("FILE", "onResponse :" + progress);
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Log.i("FILE", "onResponse :" + e.getMessage());
                    }


                    @Override
                    public void onResponse(File file) {
                        Log.i("FILE", "onResponse :" + file.getAbsolutePath());
                    }
                });
    }


    private void videoAPI(final videoInterface videoInterface){
        String url = "http://120.25.159.173:8280/api/api/v10/video/list";
        OkHttpUtils
                .get()
                .url(url)
                .addParams("orgId", QuickUtils.getOrgIdFromSp())
                .addParams("type", "1")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        videoInterface.onFail(e.getMessage());
                    }

                    @Override
                    public void onResponse(String s) {
                        videoInterface.onSucess(s.toString());
                    }
                });
    }


    private HashMap<String,String> serviceFielsMap = new HashMap<String, String>();


    /**
     * 轮询检查VideoId是否存在
     */
    public void loopFileName(final FullScreenVideoView videoView){

        videoAPI(new videoInterface() {
            @Override
            public void onSucess(String json) {
                try {
                    //服务器上的videoId
                    List<VideoInfo> info = JsonUtil.parser(json, VideoInfo.class);
                    //本地的videoId
                    File[] files = QuickUtils.getVideoFolderFiles();


                    QuickUtils.log("Video--------"+files[0].getName());


                    for(int i = 0 ; i < info.size() ; i++){
                        serviceFielsMap.put(info.get(i).getVideoId()+"",info.get(i).getVideoPath());
                    }

                    for (int i = 0; i < files.length; i++) {
                        //拿本地的FileName
                        String fileName = files[i].getName();

                        //过滤单词101.mp4  为101
                        String num_fileName = fileName.substring(0, fileName.length() - 4);

                        QuickUtils.log("Video----num_fileName----"+num_fileName);


                        //1.如果服务端有这个key,表示这个视频本地有,我们不做任何处理
                        if(serviceFielsMap.containsKey(num_fileName)){

                        }

                        //2.如果服务端没有这个key.但是本地是有这个key的,表示本地的这个视频就是要被删除的
                        if(!serviceFielsMap.containsKey(num_fileName)){
                            QuickUtils.log("Video----deleteFile----");
                            QuickUtils.deleteFile(AlgorithmUtil.VIDEO_FILE + num_fileName + ".mp4");
                        }

                        //3.如果服务端有这个key,同时本地没有服务端有的key，我们就要去下载
                        for (Map.Entry<String, String> entry : serviceFielsMap.entrySet()) {
                            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                            //如果map中没有这个key才会走下面
                            if(!serviceFielsMap.containsKey(num_fileName)){

                                //如果fileName中没有该key,就直接下载
                                if(!entry.getKey().equals(num_fileName)){

                                    //Video----downloadVideoFirst----entry.getKey()=100/num_fileName101
                                    QuickUtils.log("Video----downloadVideoFirst----entry.getKey()="+entry.getKey()+"/num_fileName"+num_fileName);

                                    QuickUtils.log("Video----downloadVideoFirst----");

                                    String serviceUrl = entry.getValue();
                                    downloadVideoFirst(serviceUrl,entry.getKey());
                                }

                            }



                        }


                    }

                    //因为无法判断多个视频在下载时多久结束,所以我只读这个目录中的,而且是循环的读
                    VideoUtil videoUtil = new VideoUtil(videoView);
                    videoUtil.prepareLocalVideo(AlgorithmUtil.VIDEO_FILE, 0);


                } catch (JSONException e) {
                    e.printStackTrace();
                    //VideoUtil videoUtil = new VideoUtil(videoView);
                    //videoUtil.prepareLocalVideo(AlgorithmUtil.VIDEO_FILE, 0);
                }


            }

            @Override
            public void onFail(String error) {

            }
        });
    }


}
