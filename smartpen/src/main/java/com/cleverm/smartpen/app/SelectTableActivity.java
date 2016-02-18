package com.cleverm.smartpen.app;

import android.view.View;
import android.widget.Toast;

import com.cleverm.smartpen.R;


/**
 * Created by Jimmy on 2015/9/21.
 */
public class SelectTableActivity extends BaseSelectTableActivity {

    @SuppressWarnings("unused")
    private static final String TAG = SelectTableActivity.class.getSimpleName();

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
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onTableSelected(long tableId) {
        mSelectedTableId = tableId;
        mTablePagerAdapter.updateTablesDisplayStatus(tableId);
    }
}
