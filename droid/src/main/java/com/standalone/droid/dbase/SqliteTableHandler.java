package com.standalone.droid.dbase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class SqliteTableHandler<T> {
    protected MetaTable table;
    protected final SQLiteDatabase db;

    public SqliteTableHandler(SQLiteDatabase db, MetaTable metaTable) {
        this.table = metaTable;
        this.db = db;
        db.execSQL(table.getCreateTableStmt());
    }

    public abstract T cursorToData(Cursor cursor);

    public abstract ContentValues convertToContentValues(T t);

    public int getCount() {
        return (int) DatabaseUtils.queryNumEntries(db, table.getName());
    }

    public List<T> fetchAll() {
        List<T> res = new ArrayList<>();
        Cursor curs = null;
        db.beginTransaction();
        try {
            curs = db.query(table.getName(), null, null, null, null, null, null);
            if (curs != null) {
                if (curs.moveToFirst()) {
                    do {
                        res.add(cursorToData(curs));
                    } while (curs.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert curs != null;
            curs.close();
        }
        return res;
    }

    public abstract void insert(T t);

    public abstract void update(T t);

    public abstract void remove(int id);

    public static class MetaTable {
        private final String tableName;
        private final String[] colDefinitions;

        public MetaTable(String tableName, String[] colDefinitions) {
            this.tableName = tableName;
            this.colDefinitions = colDefinitions;
        }

        public String getCreateTableStmt() {
            return "CREATE TABLE IF NOT EXISTS " + tableName + "(" + String.join(", ", colDefinitions) + ");";
        }

        public String getDropTableStmt() {
            return "DROP TABLE IF EXISTS " + tableName;
        }


        public String getName() {
            return tableName;
        }
    }
}
