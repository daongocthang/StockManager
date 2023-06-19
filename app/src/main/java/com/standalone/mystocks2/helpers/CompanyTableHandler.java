package com.standalone.mystocks2.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.dbase.SqliteTableHandler;
import com.standalone.mystocks2.models.Company;


public class CompanyTableHandler extends SqliteTableHandler<Company> {
    static final String TBL_NAME = "tbl_company";
    static final String COL_ID = "id";
    static final String COL_SYMBOL = "symbol";
    static final String COL_NAME = "short_name";

    public CompanyTableHandler(SQLiteDatabase db) {
        super(db, new MetaTable(TBL_NAME, new String[]{
                COL_ID + " TEXT PRIMARY KEY",
                COL_SYMBOL + " TEXT",
                COL_NAME + " TEXT",
        }));
    }

    @SuppressLint("Range")
    @Override
    public Company cursorToData(Cursor curs) {
        Company d = new Company();
        d.setStockNo(curs.getString(curs.getColumnIndex(COL_ID)));
        d.setSymbol(curs.getString(curs.getColumnIndex(COL_SYMBOL)));
        d.setShortName(curs.getString(curs.getColumnIndex(COL_NAME)));

        return d;
    }

    @Override
    public ContentValues convertToContentValues(Company d) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, d.getStockNo());
        cv.put(COL_SYMBOL, d.getSymbol());
        cv.put(COL_NAME, d.getShortName());

        return cv;
    }

    public void insert(Company d) {
        db.insert(TBL_NAME, null, convertToContentValues(d));
    }

    @Override
    public void update(Company company) {

    }

    @Override
    public void remove(int id) {

    }
}
