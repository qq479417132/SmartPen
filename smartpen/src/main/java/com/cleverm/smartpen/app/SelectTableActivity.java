package com.cleverm.smartpen.app;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.util.RememberUtil;


/**
 * Created by Jimmy on 2015/9/21.
 */
public class SelectTableActivity extends BaseSelectTableActivity {

    @SuppressWarnings("unused")
    private static final String TAG = SelectTableActivity.class.getSimpleName();
    public static final String SELECTEDTABLEID="SelectedTableId";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel: {
                onBackPressed();
                break;
            }
            case R.id.btn_confirm: {
                if (mSelectedTableId == -1) {
                    Toast.makeText(this, "no_table_selected", Toast.LENGTH_LONG).show();
                } else {
                    OrderManager.getInstance(this).setTableId(mSelectedTableId);
                    onBackPressed();
                    Log.v(TAG, "mSelectedTableId=" + mSelectedTableId);
                    RememberUtil.putLong(SELECTEDTABLEID,mSelectedTableId);
                }
                break;
            }
            default:
                break;
        }
        Intent intent = new Intent(this, VideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onTableSelected(long tableId) {
        mSelectedTableId = tableId;
        mTablePagerAdapter.updateTablesDisplayStatus(tableId);
    }
}
