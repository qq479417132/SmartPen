package com.cleverm.smartpen.util;

import android.os.Environment;

import com.cleverm.smartpen.bean.DiscountInfo;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xiong,An android project Engineer,on 2016/2/21.
 * Data:2016-02-21  14:12
 * Base on clever-m.com(JAVA Service)
 * Describe: 视频和图片处理的算法
 * Version:1.0
 * Open source
 */
public class AlgorithmUtil {


    public static final String VIDEO_FILE= Environment.getExternalStorageDirectory().getAbsolutePath() + "/muyevideo";


    private static AlgorithmUtil INSTANCE =new AlgorithmUtil();

    AlgorithmUtil(){

    }

    public static AlgorithmUtil getInstance(){
        return INSTANCE;
    }

    //-------------------------------------------------------------
    // 图片顺序算法
    //-------------------------------------------------------------
    HashMap<Integer,String> bussinessMapImage= new HashMap<Integer,String>();
    ArrayList<String> bussinessImage=new ArrayList<String>();
    HashMap<Integer,String> muyeMapImage= new HashMap<Integer,String>();
    ArrayList<String> muyeImage=new ArrayList<String>();

    /**
     * @deprecated
     * 图片顺序算法：
     * 算法核心：
     * 1.本店活动和商圈活动两个内容间隔播放
     * 2.数量限制，本店活动最多10个，商圈活动最多5个，即轮播的最大数量为15个
     *      例：素材库上有4个本店活动,2个商圈活动，则播放顺序为：本店、商圈、本店、商圈、本店、本店
     * 3.第一个播放本店活动，本店活动播放的优先顺序在后台可手动配置，默认以上次顺序排列。轮播时间设定为3秒
     * @param list
     * @return 播放顺序符合算法的图片集合
     */
    public List<DiscountInfo> getImageSequence(List<DiscountInfo> list){


        if(list==null || list.size()<=0){
            return null;
        }


        //1.根据type分集合(0表示 是本店商户的，1表示图片是商圈的)
        for(int i =0;i<list.size();i++){

            int type = list.get(i).getType();

            if(0==type){//本店商家
                //bussinessList.add(list.get(i));
                bussinessMapImage.put(list.get(i).getOrderSeq(), list.get(i).getPictruePath());
            }else{//商圈
                muyeMapImage.put(list.get(i).getOrderSeq(),list.get(i).getPictruePath());
            }

        }


        //2.根据orderSeq进行集合内排序，orderSql的起始值为1
        if(bussinessMapImage.size()>0){
            for(int j =1;j<bussinessMapImage.size()+1;j++){
                String path = bussinessMapImage.get(j);
                bussinessImage.add(path);
            }
        }

        if(muyeMapImage.size()>0){
            for(int j =1 ; j <muyeMapImage.size()+1;j++){
                String path = muyeMapImage.get(j);
                muyeImage.add(path);
            }
        }


        //TODO 3.根据规则从不同的集合中取值出来:先拿商家的再拿商圈的.以此类推放入一个新集合



        return null;

    }



    private int index=0;
    List<DiscountInfo> bussinessList= new ArrayList<DiscountInfo>();
    List<DiscountInfo> muyeList = new ArrayList<DiscountInfo>();
    List<DiscountInfo> sequenceList = new ArrayList<DiscountInfo>();

    /**
     * 图片顺序算法：
     * @param list
     * @return 已经传插排序好的内容
     */
    public List<DiscountInfo> getSimpleImageSequence(List<DiscountInfo> list){

        if(list==null || list.size()<=0){
            return null;
        }

        //1.根据type分集合(0表示 是本店商户的，1表示图片是商圈的)
        for(int i =0;i<list.size();i++){
            int type = list.get(i).getType();
            if(0==type){//本店商家
                bussinessList.add(list.get(i));
            }else{//商圈
                muyeList.add(list.get(i));
            }
        }


        //2.根据json顺序进行集合内排序,该操作服务端已处理



        //3.根据规则从不同的集合中取值出来:先拿商家的再拿商圈的.以此类推放入一个新集合

        while(true){

            if(bussinessList.size()>index){
                sequenceList.add(bussinessList.get(index));
            }

            if(muyeList.size()>index){
                sequenceList.add(muyeList.get(index));
            }

            index++;

            if(bussinessList.size()<index&&bussinessList.size()<index){
                break;
            }

        }

        return sequenceList;

    }

    //-------------------------------------------------------------
    // 视频处理算法
    //-------------------------------------------------------------
    public void getSimpleVideo(String orgId){




    }



}
