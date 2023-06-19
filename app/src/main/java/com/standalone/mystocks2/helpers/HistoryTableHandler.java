package com.standalone.mystocks2.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.standalone.droid.dbase.SqliteTableHandler;
import com.standalone.mystocks2.models.Stock;


public class HistoryTableHandler extends SqliteTableHandler<Stock> {
    static final String TBL_NAME = "tbl_history";
    static final String COL_ID = "id";
    static final String COL_SYMBOL = "symbol";
    static final String COL_PRICE = "price";
    static final String COL_SHARES = "shares";
    static final String COL_REF = "reference";
    static final String COL_ORDER = "order_type";
    static final String COL_DATE = "date";

    public HistoryTableHandler(SQLiteDatabase db) {
        super(db, new SqliteTableHandler.MetaTable(TBL_NAME,
                new String[]{
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT",
                        COL_SYMBOL + " TEXT",
                        COL_PRICE + " INTEGER",
                        COL_SHARES + " INTEGER",
                        COL_REF + " INTEGER",
                        COL_ORDER + " TEXT",
                        COL_DATE + " TEXT"
                }));
    }

    @SuppressLint("Range")
    @Override
    public Stock cursorToData(Cursor curs) {
        Stock s = new Stock();
        s.setId(curs.getInt(curs.getColumnIndex(COL_ID)));
        s.setSymbol(curs.getString(curs.getColumnIndex(COL_SYMBOL)));
        s.setPrice(curs.getDouble(curs.getColumnIndex(COL_PRICE)));
        s.setShares(curs.getInt(curs.getColumnIndex(COL_SHARES)));
        s.setReference(curs.getDouble(curs.getColumnIndex(COL_REF)));
        s.setOrder(Stock.OrderType.valueOf(curs.getString(curs.getColumnIndex(COL_ORDER))));
        s.setDate(curs.getString(curs.getColumnIndex(COL_DATE)));
        return s;
    }

    @Override
    public ContentValues convertToContentValues(Stock s) {
//        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        ContentValues cv = new ContentValues();
        cv.put(COL_SYMBOL, s.getSymbol());
        cv.put(COL_PRICE, s.getPrice());
        cv.put(COL_SHARES, s.getShares());
        cv.put(COL_REF, s.getReference());
        cv.put(COL_ORDER, s.getOrder().toString());
        cv.put(COL_DATE, s.getDate());
        return cv;
    }

    public void insert(Stock s) {
        db.insert(TBL_NAME, null, convertToContentValues(s));
    }

    @Override
    public void update(Stock stock) {

    }

    @Override
    public void remove(int id) {

    }
}
