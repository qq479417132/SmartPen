package com.cleverm.smartpen.statistic.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


import com.cleverm.smartpen.statistic.model.Stats;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "t_stats".
*/
public class StatsDao extends AbstractDao<Stats, Long> {

    public static final String TABLENAME = "t_stats";

    /**
     * Properties of entity Stats.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ActionId = new Property(1, long.class, "actionId", false, "ACTION_ID");
        public final static Property TimePoit = new Property(2, long.class, "timePoit", false, "TIME_POIT");
        public final static Property TimeStay = new Property(3, Long.class, "timeStay", false, "TIME_STAY");
        public final static Property ClientId = new Property(4, Long.class, "clientId", false, "CLIENT_ID");
        public final static Property OrgId = new Property(5, Long.class, "orgId", false, "ORG_ID");
        public final static Property TableId = new Property(6, Long.class, "tableId", false, "TABLE_ID");
        public final static Property Desc = new Property(7, String.class, "desc", false, "DESC");
        public final static Property Secondid = new Property(8, Long.class, "secondid", false, "SECONDID");
        public final static Property Querydata = new Property(9, String.class, "querydata", false, "QUERYDATA");
        public final static Property Flag = new Property(10, Boolean.class, "flag", false, "FLAG");
    };


    public StatsDao(DaoConfig config) {
        super(config);
    }
    
    public StatsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"t_stats\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"ACTION_ID\" INTEGER NOT NULL ," + // 1: actionId
                "\"TIME_POIT\" INTEGER NOT NULL ," + // 2: timePoit
                "\"TIME_STAY\" INTEGER," + // 3: timeStay
                "\"CLIENT_ID\" INTEGER," + // 4: clientId
                "\"ORG_ID\" INTEGER," + // 5: orgId
                "\"TABLE_ID\" INTEGER," + // 6: tableId
                "\"DESC\" TEXT," + // 7: desc
                "\"SECONDID\" INTEGER," + // 8: secondid
                "\"QUERYDATA\" TEXT," + // 9: querydata
                "\"FLAG\" INTEGER);"); // 10: flag
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"t_stats\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Stats entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getActionId());
        stmt.bindLong(3, entity.getTimePoit());
 
        Long timeStay = entity.getTimeStay();
        if (timeStay != null) {
            stmt.bindLong(4, timeStay);
        }
 
        Long clientId = entity.getClientId();
        if (clientId != null) {
            stmt.bindLong(5, clientId);
        }
 
        Long orgId = entity.getOrgId();
        if (orgId != null) {
            stmt.bindLong(6, orgId);
        }
 
        Long tableId = entity.getTableId();
        if (tableId != null) {
            stmt.bindLong(7, tableId);
        }
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(8, desc);
        }
 
        Long secondid = entity.getSecondid();
        if (secondid != null) {
            stmt.bindLong(9, secondid);
        }
 
        String querydata = entity.getQuerydata();
        if (querydata != null) {
            stmt.bindString(10, querydata);
        }
 
        Boolean flag = entity.getFlag();
        if (flag != null) {
            stmt.bindLong(11, flag ? 1L: 0L);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Stats readEntity(Cursor cursor, int offset) {
        Stats entity = new Stats( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // actionId
            cursor.getLong(offset + 2), // timePoit
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // timeStay
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // clientId
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // orgId
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // tableId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // desc
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // secondid
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // querydata
            cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0 // flag
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Stats entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setActionId(cursor.getLong(offset + 1));
        entity.setTimePoit(cursor.getLong(offset + 2));
        entity.setTimeStay(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setClientId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setOrgId(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setTableId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setDesc(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setSecondid(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setQuerydata(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setFlag(cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Stats entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Stats entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
