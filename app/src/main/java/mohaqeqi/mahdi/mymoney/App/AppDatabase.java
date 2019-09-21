package mohaqeqi.mahdi.mymoney.App;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {

    public static String db_name = "data";
    String table_transactions = "transactions";
//    String table_balances = "balances";

    public static String col_id = "id";
    public static String col_title = "title";
    public static String col_amount = "amount";
    public static String col_day = "day";
    public static String col_month = "month";
    public static String col_year = "year";
    public static String col_description = "description";
    public static String col_type = "type";
//    public static String col_balance = "balance";

    public AppDatabase(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + table_transactions + "(" +
                col_id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                col_title + " TEXT," +
                col_amount + " LONG," +
                col_day + " INTEGER," +
                col_month + " INTEGER," +
                col_year + " INTEGER," +
                col_description + " TEXT," +
                col_type + " TEXT);");

        /*db.execSQL("CREATE TABLE IF NOT EXISTS " + table_balances + "(" +
                col_id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                col_balance + " TEXT,"+
                col_day + " INTEGER," +
                col_month + " INTEGER," +
                col_year + " INTEGER);");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_transactions);
//        db.execSQL("DROP TABLE IF EXISTS " + table_balances);
        onCreate(db);
    }

    public long addTransaction(String title, long amount, int day, int month, int year, String description, String type) {

        ContentValues values = new ContentValues();
        values.put(col_title, title);
        values.put(col_amount, amount);
        values.put(col_day, day);
        values.put(col_month, month);
        values.put(col_year, year);
        values.put(col_description, description);
        values.put(col_type, type);

        return this.getWritableDatabase().insert(table_transactions, null, values);
    }

    public int getAllTransactionsCount() {

        return this.getReadableDatabase().rawQuery("SELECT * FROM " + table_transactions, null).getCount();
    }

    public Cursor getNewestTransactions(int month, int year) {

        return this.getReadableDatabase().rawQuery("SELECT * FROM " + table_transactions + " WHERE " + col_month + "=" + month + " and " + col_year + "=" + year + " ORDER BY " + col_day + " DESC", null);
    }

    public Cursor getOldestTransactions(int month, int year) {

        return this.getReadableDatabase().rawQuery("SELECT * FROM " + table_transactions + " WHERE " + col_month + "=" + month + " and " + col_year + "=" + year + " ORDER BY " + col_day + " ASC", null);
    }

    public Cursor getHighestAmountTransactions(int month, int year) {

        return this.getReadableDatabase().rawQuery("SELECT * FROM " + table_transactions + " WHERE " + col_month + "=" + month + " and " + col_year + "=" + year + " ORDER BY " + col_amount + " DESC", null);
    }

    public Cursor getLowestAmountTransactions(int month, int year) {

        return this.getReadableDatabase().rawQuery("SELECT * FROM " + table_transactions + " WHERE " + col_month + "=" + month + " and " + col_year + "=" + year + " ORDER BY " + col_amount + " ASC", null);
    }

    public long deleteAll() {

        return this.getWritableDatabase().delete(table_transactions, null, null);
    }

    public long deleteBy(int month, int year) {

        return this.getWritableDatabase().delete(table_transactions, col_month + "=" + month + " AND " + col_year + "=" + year, null);
    }

    public long editTransaction(int id, String title, long amount, int day, int month, int year, String description, String type) {

        ContentValues values = new ContentValues();
        values.put(col_title, title);
        values.put(col_amount, amount);
        values.put(col_day, day);
        values.put(col_month, month);
        values.put(col_year, year);
        values.put(col_description, description);
        values.put(col_type, type);

        return this.getWritableDatabase().update(table_transactions, values, col_id + "=" + id, null);
    }
}
