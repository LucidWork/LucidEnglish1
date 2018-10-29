package com.thesejongproject.smart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedInputStream;
import java.io.IOException;

public class SmartOpenHelper extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase myDataBase;
    private String DB_SQL;

    public SmartOpenHelper(Context context, String dbname, int dbversion, String dbSqlName) throws IOException {
        super(context, dbname, null, dbversion);
        this.context = context;
        this.DB_SQL = dbSqlName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            BufferedInputStream inStream = new BufferedInputStream(context.getAssets().open(DB_SQL));
            String sql = "";
            int character = -2;
            do {
                character = inStream.read();
                if ((character != -1) && (character != -2))
                    sql += (char) character;
                else
                    break;
            } while (true);
            System.out.println("onCreate DB SQL = " + sql.split("\n"));
            String[] arrSQL = sql.split("\n");

            for (int i = 0; i < arrSQL.length; i++) {
                db.execSQL(arrSQL[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            BufferedInputStream inStream = new BufferedInputStream(context.getAssets().open(DB_SQL));
            String sql = "";
            int character = -2;
            do {
                character = inStream.read();
                if ((character != -1) && (character != -2))
                    sql += (char) character;
                else
                    break;
            } while (true);

            System.out.println("onUpgrade DB SQL = " + sql.split("\n"));
            String[] arrSQL = sql.split("\n");
            for (int i = 0; i < arrSQL.length; i++) {
                db.execSQL(arrSQL[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SQLiteDatabase getOpenDatabase() {
        return myDataBase;
    }

    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }
}
