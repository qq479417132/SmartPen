package com.cleverm.smartpen.util;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.VideoView;


import com.cleverm.smartpen.bean.VideoInfo;

import java.io.File;
import java.util.List;

/**
 * Created by x on 2016/2/15.
 * 业务逻辑:将所有视频的URL地址的文件下载，然后存储到本地
 * 从本地中取出文件,判断每个文件的时常大小，根据时常和排序规则来处理视频
 */
public class VideoUtil {


    public int videoIndex = 0;
    public VideoView mVideoView;

    public VideoUtil(VideoView mVideoView) {
        this.mVideoView = mVideoView;
    }

    /**
     * 开启播放在线视频
     * @param info
     */
    public void prepareOnlineVideo(List<VideoInfo> info) {
        final String[] videoUrls = getOnlineVideoURI(info);
        if (videoUrls == null) {
            return;
        }
        mVideoView.setVideoPath(videoUrls[videoIndex]);
        mVideoView.start();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoIndex++;
                if (videoIndex >= videoUrls.length) {
                    videoIndex = 0;
                }
                mVideoView.setVideoPath(videoUrls[videoIndex]);
                mVideoView.start();
            }
        });
    }

    /**
     * 开启播放本地视频
     * @param mPath
     */
    public void prepareLocalVideo(final String mPath,int currentPosition) {
        //初始化videoIndex=0;
        videoIndex=0;

        final String[] videoUrls = getVideoURIs(mPath);
        if (videoUrls == null) {
            return;
        }

        mVideoView.setVideoPath(videoUrls[videoIndex]);


        if(RememberUtil.getInt(Constant.MEMORY_PLAY_VIDEO_URI_KEY,0)!=0){
            mVideoView.setVideoPath(videoUrls[RememberUtil.getInt(Constant.MEMORY_PLAY_VIDEO_URI_KEY, 0)]);
            videoIndex=RememberUtil.getInt(Constant.MEMORY_PLAY_VIDEO_URI_KEY,0);
            QuickUtils.log("算法中的videoUrl=" + RememberUtil.getInt(Constant.MEMORY_PLAY_VIDEO_URI_KEY, 0));

        }


        if(currentPosition!=0){
            mVideoView.seekTo(currentPosition);
        }

        if(RememberUtil.getInt(Constant.MEMORY_PLAY_KEY, 0)!=0){
            QuickUtils.log("算法中的videoValue=" + RememberUtil.getInt(Constant.MEMORY_PLAY_KEY, 0));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //seekTo是异步操作
                    mVideoView.seekTo(RememberUtil.getInt(Constant.MEMORY_PLAY_KEY, 0));
                }
            }, 200);

        }


        mVideoView.start();

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                //重置一次VideoView.因为VideoView会每次都运行,所以无需重置
                //String[] videoUrls=getVideoURIs(mPath);

                videoIndex++;
                if (videoIndex >= videoUrls.length) {
                    videoIndex = 0;
                }
                mVideoView.setVideoPath(videoUrls[videoIndex]);

                RememberUtil.putInt(Constant.MEMORY_PLAY_VIDEO_URI_KEY, videoIndex);

                mVideoView.start();
            }
        });
    }

    /**
     * 获取本地视频数组
     * @param path
     * @return
     */
    private String[] getVideoURIs(String path) {

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        File files[] = file.listFiles();
        if (files.length == 0) {
            return null;
        }
        String[] videoUrls = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            videoUrls[i] = files[i].getAbsolutePath();
        }
        return videoUrls;
    }

    /**
     * 获取在线视频数组
     * @param info
     * @return
     */
    private String[] getOnlineVideoURI(List<VideoInfo> info) {
        if(info==null){
            return null;
        }
        String[] videoUrls = new String[info.size()];
        if (info != null) {
            if (info.size() > 0) {
                for (int i =0 ;i<info.size();i++) {
                    videoUrls[i] = info.get(i).getVideoPath();
                }
            }
        }
        return videoUrls;
    }






    //-------------------------------------------------------------------------------

    public static int getVideoSeconde(int dutaion) {

        int second = dutaion / 1000;

        return second;
    }

    public static void playOnlineVideo(VideoView videoView, String httpUrl) {
        videoView.setVideoPath(httpUrl);
        videoView.start();
        //videoView.setOnPreparedListener(viewPreListener);
    }


    MediaPlayer.OnPreparedListener viewPreListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //Toast.makeText(VideoActivity.this, VideoUtil.getVideoSeconde(mp.getDuration()) + "", Toast.LENGTH_LONG).show();
        }
    };

}
