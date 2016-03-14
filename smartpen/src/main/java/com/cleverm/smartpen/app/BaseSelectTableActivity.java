package com.cleverm.smartpen.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.database.DatabaseHelper;
import com.cleverm.smartpen.database.TableColumns;
import com.cleverm.smartpen.database.TableTypeColumns;
import com.cleverm.smartpen.fragment.SelectTableFragment;
import com.cleverm.smartpen.modle.TableType;
import com.cleverm.smartpen.modle.impl.TableImpl;
import com.cleverm.smartpen.modle.impl.TableTypeImpl;
import com.cleverm.smartpen.pushtable.bean.TableInfo;
import com.cleverm.smartpen.pushtable.bean.TableResult;
import com.cleverm.smartpen.pushtable.bean.TableTypeInfo;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.RememberUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Jimmy on 2015/9/16.
 */
public abstract class BaseSelectTableActivity extends BaseActivity implements View.OnClickListener,
    SelectTableFragment.OnTableAdapterListener {
    public static final String ORGID ="OrgID";
    public static final String CLIENTID ="clientId";
    @SuppressWarnings("unused")
    private static final String TAG = BaseSelectTableActivity.class.getSimpleName();
    public static final String SELECTEDTABLEID="SelectedTableId";
    protected TablePagerAdapter mTablePagerAdapter;
    protected long mSelectedTableId;
    private TabWidget mTableTabHost;
    private ViewPager mTableViewPager;
    private List<TableType> mTableTypes;
    public static final int GOBack = 200;
    private EditText mInputOrgId;
    private Button mOrgIdConfirm;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOBack: {
                    Log.v(TAG, "come hand====");
                    BaseSelectTableActivity.this.finish();
                    startActivity(new Intent(BaseSelectTableActivity.this, VideoActivity.class));
                    ((CleverM) getApplication()).getpenService().setActivityFlag("VideoActivity");
                    break;
                }
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_table);
        initData();
        mHandler.sendEmptyMessageDelayed(GOBack, Constant.DELAY_BACK);
    }

    private void initData() {
        //*******************************************
        mInputOrgId= (EditText) findViewById(R.id.et_orgid);
        mOrgIdConfirm= (Button) findViewById(R.id.bt_orgid);
        mOrgIdConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ClientId = mInputOrgId.getText().toString();
                if (ClientId == null) {
                    Toast.makeText(BaseSelectTableActivity.this, "商户ID不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                setClientId(ClientId);
                RequestData();
            }
        });
        long orgid=getClientId();
        if(orgid==Constant.DESK_ID_DEF_DEFAULT){
            mInputOrgId.setHint("请输入商户ID");
        }else {
            mInputOrgId.setText(orgid+"",null);
            RequestData();
        }

    }


    private void RequestData(){
        String url="http://120.25.159.173/push/105/app/smartpen/weather/shop.txt";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.v(TAG, "onResponse=onError");
                    }

                    @Override
                    public void onResponse(String s) {
                        Log.v(TAG, "onResponse=" + s);
                        if (s != null) {
                            parserGson(s);
                            initTableData();
                            initView();
                        }
                    }
                });
    }
    /**
     * 初始化桌子信息
     */
    private void initTableData(){
        /**
         * data of the TableTypes
         */
        mTableTypes = DatabaseHelper.getsInstance(this).obtainAllTableTypes();//得到数据库中的桌子类型
        mSelectedTableId = OrderManager.getInstance(this).getTableId();
        mTablePagerAdapter = new TablePagerAdapter(getFragmentManager());
        //select the Table
        long defaultDeskId=RememberUtil.getLong(SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);
        if(defaultDeskId==Constant.DESK_ID_DEF_DEFAULT){
            return;
        }
        //设置上次选中的桌号
        OrderManager.getInstance(this).setTableId(defaultDeskId);
    }

    private void initView() {
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mTableTabHost = (TabWidget) findViewById(R.id.table_category_tab_host);
        mTableViewPager = (ViewPager) findViewById(R.id.table_panel_container);
        mTableViewPager.setAdapter(mTablePagerAdapter);
        mTableViewPager.setOnPageChangeListener(mTablePagerAdapter);
        initTabHost();
    }

    private void initTabHost() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View tabItem;
        for (int i = 0; i < mTableTypes.size(); ++i) {
            tabItem = inflater.inflate(R.layout.item_table_tab, null);
            ((TextView) tabItem.findViewById(R.id.table_type)).setText(mTableTypes.get(i).getName());
            tabItem.setTag(i);
            mTableTabHost.addView(tabItem);
            tabItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTableViewPager.setCurrentItem((Integer) v.getTag());
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.sendEmptyMessageDelayed(GOBack, Constant.DELAY_BACK);
                }
            });
        }
    }

    class TablePagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private List<Fragment> mTablePagers;

        public TablePagerAdapter(FragmentManager fm) {
            super(fm);
            if (mTableTypes == null || mTableTypes.isEmpty()){
                return;
            }
            mTablePagers = new ArrayList<Fragment>();
            for (TableType tableType : mTableTypes) {
                mTablePagers.add(SelectTableFragment.newInstance(tableType.getId()));
            }
        }

        public void updateTablesDisplayStatus(long tableId) {
            for (Fragment fragment : mTablePagers) {
                ((SelectTableFragment) fragment).updateTablesDisplayStatus(tableId);
            }
        }

        @Override
        public Fragment getItem(int i) {
            if (i < 0 || i >= getCount()) {
                return null;
            }
            return mTablePagers.get(i);
        }

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            mTableTabHost.setCurrentTab(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }

        @Override
        public int getCount() {
            return mTablePagers == null ? 0 : mTablePagers.size();
        }
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


    private void setClientId(String ClientId){
        String data="{\"clientID\":"+ClientId+"}";
        Log.v(TAG,"data="+data);
        String path1= Environment.getExternalStorageDirectory().getPath()+"/SystemPen";
        Log.v(TAG,"path1="+path1);
        String path2= Environment.getExternalStorageDirectory().getPath()+"/SystemPen/smartpen.txt";
        File file1=new File(path1);
        File file2=new File(path2);
        if(!file1.exists()){
            Log.v(TAG,"path1=NO");
          file1.mkdirs();
        }
        Log.v(TAG,"path1=YES");
        if(!file2.exists()){
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStream os=new FileOutputStream(file2);
            os.write(data.getBytes());
            Toast.makeText(this,"商户ID设置成功！",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void parserGson(String data){
        Gson gson=new Gson();
        Log.v(TAG,"parserGson()="+data);
        TableResult tableResult=gson.fromJson(data,TableResult.class);
        String OrgID=tableResult.getOrgID();
        Log.v(TAG,"OrgID="+OrgID+" ClientID="+tableResult.getClientID());
        RememberUtil.putString(ORGID,OrgID);
        RememberUtil.putString(CLIENTID,tableResult.getClientID());
        List<TableInfo> ListTableInfo=tableResult.getTableList();
        List<TableTypeInfo> ListTableTypeInfo=tableResult.getTableTypeList();
        insertTableData(ListTableInfo, ListTableTypeInfo);
    }

    /**
     * Terry Test
     */
    private void insertTableData(List<TableInfo> ListTableInfo,List<TableTypeInfo> ListTableTypeInfo){
        DatabaseHelper databaseHelper=DatabaseHelper.getsInstance(this);
        databaseHelper.deleteAll(TableColumns.TABLE_NAME );
        databaseHelper.deleteAll(TableTypeColumns.TABLE_NAME );
        Log.v(TAG,"insertTableData()");
        for(int i=0;i<ListTableTypeInfo.size();i++){
            String typeId=ListTableTypeInfo.get(i).getTypeId();
            long id=Long.parseLong(typeId);
            String typeName=ListTableTypeInfo.get(i).getTypeName();
            String minimum=ListTableTypeInfo.get(i).getMinimum();
            int min=Integer.parseInt(minimum);
            String capacity=ListTableTypeInfo.get(i).getCapacity();
            int max=Integer.parseInt(capacity);
            String description=ListTableTypeInfo.get(i).getDescription();
            databaseHelper.insertTableType(new TableTypeImpl(id,typeName,min,max,description));
        }

        for(int i=0;i<ListTableInfo.size();i++){
            Log.v(TAG,"ListTableInfo.size()="+ListTableInfo.size());
            long typeid    =Long.parseLong(ListTableInfo.get(i).getTypeId());
            long tableId=Long.parseLong(ListTableInfo.get(i).getTableId());
            String name=ListTableInfo.get(i).getTableName();
            databaseHelper.insertTable(new TableImpl(tableId, typeid, name));
        }
    }

}