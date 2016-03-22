package com.cleverm.smartpen.Version;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.util.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 95 on 2016/2/23.
 */
public class VersionManager {
    public static final String TAG = VersionManager.class.getSimpleName();
    private Context context;
    private int m_newVerCode;
    private String m_newVerName;
    private String m_description;
    private String m_appNameStr;

    private Handler m_mainHandler;
    private ProgressDialog m_progressDlg;
    public static final int SHOW=1;

    public static final String PACKAGE_NAME = "com.cleverm.smartpen";
    public static final String APP_NAME = Constant.APP_NAME;
    public static final String SERVER_IP = Constant.DDP_URL+"/push/105/app/smartpen/";
    public static final String SERVER_ADDRESS = SERVER_IP + "version.json";
    public static final String UPDATESOFTADDRESS = SERVER_IP + "smartpen.apk";

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW:{
                    doNewVersionUpdate();
                    break;
                }
            }
        }
    };
    public VersionManager(Context context) {
        this.context = context;
        initVariable();
    }

    private void initVariable() {
        m_mainHandler = new Handler();
        m_progressDlg = new ProgressDialog(context);
        m_progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        m_progressDlg.setIndeterminate(false);
        m_progressDlg.setProgressNumberFormat("%1d KB/%2d KB");
        m_progressDlg.setCancelable(false);
        m_appNameStr =APP_NAME;
    }

    /**
     *
     */
    public void uddateVersion(){
        new Thread(){
            @Override
            public void run() {
                post_to_server1();
            }
        }.start();
    }

    public  void post_to_server1() {
//        long ClientId=getClientId();
//        if(ClientId==Constant.DESK_ID_DEF_DEFAULT){
//            return;
//        }
        try {
            HttpClient Client=new DefaultHttpClient();
            HttpGet get=new HttpGet(SERVER_ADDRESS);
            HttpResponse Response=Client.execute(get);
            if(Response.getStatusLine().getStatusCode()==200){
                HttpEntity entity=Response.getEntity();
                String ResponseData= EntityUtils.toString(entity, "UTF-8");
                postCheckNewestVersionCommand2Server(ResponseData);
                Log.v(TAG, "version post_to_server1 success=" + ResponseData);
            }
            Log.v(TAG,"version post_to_server1 "+Response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG,"version post_to_server1 IOException");
        }
    }

    /**
     *
     * @return
     */
    private  void postCheckNewestVersionCommand2Server(String ResponseData)  {
        String result = ResponseData;
        if (result==null){
            Log.v(TAG,"version ResponseData=null");
            return;
        }
        JSONObject object = null;
        try {
            object = new JSONObject(result);
            Log.v(TAG,"version post_to_server1 success=="+object.toString());
            m_newVerName = object.getString("verName");
            m_newVerCode=object.getInt("verCode");
            m_description=object.getString("description");
            int vercode =getVerCode(context);
            Log.v(TAG,"version post_to_server1 m_newVerName="+m_newVerName+" m_newVerCode="+m_newVerCode+" vercode="+vercode);
            if(m_newVerCode>vercode){
                mHandler.sendEmptyMessage(SHOW);
                Log.v(TAG,"version postCheckNewestVersionCommand2Server mHandler.sendEmptyMessage(SHOW)");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     */
    private  void doNewVersionUpdate() {
        int verCode =getVerCode(context);
        String verName = getVerName(context);

        String str = context.getString(R.string.currentversion) + verName + context.getString(R.string.findnew) + m_newVerName+"\n"+ context.getString(R.string.description)+m_description+"\n\n"+context.getString(R.string.isupdate);
        Dialog dialog = new AlertDialog.Builder(context).setTitle(context.getString(R.string.update)).setMessage(str)
                .setPositiveButton(context.getString(R.string.change),//
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                m_progressDlg.setTitle(context.getString(R.string.isdownloading));
                                m_progressDlg.setMessage(context.getString(R.string.wait));
                                m_progressDlg.show();
                                downFile(UPDATESOFTADDRESS);  //��ʼ����
                            }
                        })
                .setNegativeButton(context.getString(R.string.nochange),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        }).create();
        dialog.show();
    }

    private  void downFile(final String url) {
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();

                    m_progressDlg.setMax((int) length/1024);

                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                m_appNameStr);
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                                m_progressDlg.setProgress(count/1024);
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     *
     */
    private  void down() {
        m_mainHandler.post(new Runnable() {
            public void run() {
                m_progressDlg.cancel();
                update();
            }
        });
    }

    /**
     *
     */
    void  update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), m_appNameStr)),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }



    /**
     *
     * @param <NameValuePair> vps POST�����Ĳ�ֵ��
     * @return StringBuilder builder ���ز鵽�Ľ��
     * @throws Exception
     */
    public  String resultdata =null;

    /**
     * @param context
     * @return
     */
    public  int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    PACKAGE_NAME, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());

        }
        System.out.println("getVerCode:"+verCode);
        return verCode;
    }

    /**
     * @param context
     * @return
     */
    public  String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    PACKAGE_NAME, 0).versionName;
            Log.v(TAG,"getVerName()="+verName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verName;
    }


    private long getClientId(){
        long ClientId=Constant.DESK_ID_DEF_DEFAULT;
        String path= Environment.getExternalStorageDirectory().getPath()+"/SystemPen/smartpen.txt";
        File file=new File(path);
        if(!file.exists()){
            return ClientId;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String data=br.readLine();
            Log.v(TAG, "data=" + data);
            JSONObject object=new JSONObject(data);
            ClientId=Long.parseLong(object.getString("clientID"));
            Log.v(TAG, "ClientId=" + ClientId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ClientId;
    }
}
