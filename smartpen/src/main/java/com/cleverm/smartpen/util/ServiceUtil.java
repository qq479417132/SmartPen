package com.cleverm.smartpen.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.bean.DiscountInfo;
import com.lidroid.xutils.util.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * Created by xiong,An android project Engineer,on 2016/2/21.
 * Data:2016-02-21  12:04
 * Base on clever-m.com(JAVA Service)
 * Describe: 所有网络请求和Json解析
 * Version:1.0
 * Open source
 */
public class ServiceUtil {

    private static ServiceUtil INSTANCE = new ServiceUtil();

    public static ServiceUtil getInstance() {
        return INSTANCE;
    }


    public interface JsonInterface {
        void onSucced(String json);

        void onFail(String error);
    }


    /**
     * Url地址:http://120.25.159.173:8280/api/api/v10/roll/main/list?orgId=100&type=1
     * 1代表 优惠专区
     * 2代表 本店推介
     * @param orgId
     */
    public void getDiscountData(String orgId, String type, final JsonInterface jsonInterface) {
        String url = Constant.DDP_URL+"/api/api/v10/roll/main/list";
        Log.e("getDiscountData",url+"orgId="+orgId);
        OkHttpUtils
                .get()
                .url(url)
                .addParams("orgId", orgId)
                .addParams("type", type)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        jsonInterface.onFail(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        jsonInterface.onSucced(response.toString());
                    }
                });
    }


    /**
     * 解析优惠专区Json
     *
     * @param result
     * @return
     * @throws JSONException
     */
    public List<DiscountInfo> parserDiscountData(String result) throws JSONException {
        JSONObject json = new JSONObject(result);
        String data = json.getString("data");
        List<DiscountInfo> list = JSON.parseArray(data, DiscountInfo.class);
        return list;
    }


}
