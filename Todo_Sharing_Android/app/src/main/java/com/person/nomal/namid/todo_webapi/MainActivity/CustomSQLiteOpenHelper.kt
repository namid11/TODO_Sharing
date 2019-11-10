package com.person.nomal.namid.todo_webapi.MainActivity

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class CustomSQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version:Int)
    :SQLiteOpenHelper(context, name, factory, version) {

    // SQL定型文
    private val CREATE_TABLE = "create table todo_table(id integer primary key, todo text, check_flag integer)"
    private val DROP_TABLE = "drop table todo_table"


    //--- データベースが初めて作成された時に呼ばれるメソッド ---//
    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(this.toString(), "データベースが作成されました")
        if(db != null) {
            db.execSQL(CREATE_TABLE)
        }
    }


    //--- データベースをアップグレードする際に呼ばれるメソッド(今回はリセットするだけ) ---//
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(this.toString(), "データベースを削除しました")
        if(db != null) {
            db.execSQL(DROP_TABLE)
            onCreate(db)
        }
    }


    override fun toString(): String {
        return "CustomSQLiteOpenHelperクラス"
    }
}