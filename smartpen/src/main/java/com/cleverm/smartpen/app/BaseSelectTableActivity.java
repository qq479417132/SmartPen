package com.cleverm.smartpen.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.database.DatabaseHelper;
import com.cleverm.smartpen.fragment.SelectTableFragment;
import com.cleverm.smartpen.modle.TableType;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.RememberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 2015/9/16.
 */
public abstract class BaseSelectTableActivity extends BaseActivity implements View.OnClickListener,
    SelectTableFragment.OnTableAdapterListener {

    @SuppressWarnings("unused")
    private static final String TAG = BaseSelectTableActivity.class.getSimpleName();
    public static final String SELECTEDTABLEID="SelectedTableId";
    protected TablePagerAdapter mTablePagerAdapter;
    protected long mSelectedTableId;
    private TabWidget mTableTabHost;
    private ViewPager mTableViewPager;
    private List<TableType> mTableTypes;
    public static final int GOBack = 200;
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
        initView();
        mHandler.sendEmptyMessageDelayed(GOBack, Constant.DELAY_BACK);
    }

    private void initData() {
        /**
         * data of the TableTypes
         */
        mTableTypes = DatabaseHelper.getsInstance(this).obtainAllTableTypes();;
        mSelectedTableId = OrderManager.getInstance(this).getTableId();
        mTablePagerAdapter = new TablePagerAdapter(getFragmentManager());
        //select the Table
        long defaultDeskId=RememberUtil.getLong(SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);
        if(defaultDeskId==Constant.DESK_ID_DEF_DEFAULT){
            return;
        }
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
}