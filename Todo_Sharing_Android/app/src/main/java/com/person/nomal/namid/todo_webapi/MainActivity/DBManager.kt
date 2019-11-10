package com.person.nomal.namid.todo_webapi.MainActivity

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.json.JSONObject


/**
 * データベースに対する操作を管理するクラス
 */
class DataBaseManager(val context: Context, val db_name: String = "App.db") {

    val helper = CustomSQLiteOpenHelper(context, db_name, null, 1)


    /*
    DBにレコードを追加するメソッド
     */
    fun insertDataBase(todo_message: String): Unit {
        val db = helper.writableDatabase
        // 追加するレコードを作成 //
        val values = ContentValues()
        values.put("todo", todo_message)
        values.put("check_flag", 0)
        // レコードインソート
        db.insert("todo_table", null, values)

        Thread{
            val ip = context.getSharedPreferences("sp", Context.MODE_PRIVATE).getString("ip", "")
            val port = context.getSharedPreferences("sp", Context.MODE_PRIVATE).getString("port", "")
            if(insertRequest(ip = ip, port = port, todoData = TodoData(id = 0, content = todo_message, flag = false))) {
                Log.d("insertDB", "送信成功")
            } else {
                Log.d("insertDB", "送信失敗")
            }
        }.start()
    }


    /*
    DBからレコードの値を取得するメソッド
     */
    fun getContentDataBase(position: Int): TodoData? {
        val db = helper.readableDatabase

        // クエリ文でデータを取得
        val cursor: Cursor = db.query(
            "todo_table",
            arrayOf<String>("id", "todo", "check_flag"),
            null, null, null, null, null)

        // カーソルをpositionの位置に移動
        if (cursor.moveToPosition(position)) {
            //Log.d("getContentDataBase()", "カーソルが指定した位置に移動しました")
        } else {
            Log.d("getContentDataBase()", "カーソルがありません")
            return null
        }

        // todoの名前部分とcheck_flagをTodoDataクラスにセットして返却s
        val todoData = TodoData(
            id = cursor.getInt(0),
            content = cursor.getString(1),
            flag = cursor.getInt(2) != 0
        )
        cursor.close()
        return todoData
    }


    /*
    DBのレコードを更新するメソッド
     */
    fun updateDataBase(todoData: TodoData) {
        val db: SQLiteDatabase = helper.writableDatabase
        var COMMAND = "update todo_table set" + " "
        if (todoData.content != "") {
            COMMAND += "todo=\"%s\" ".format(todoData.content)
        }
        if (todoData.flag) {
            COMMAND += ",check_flag=%d ".format(1)
        } else {
            COMMAND += ",check_flag=%d ".format(0)
        }
        COMMAND += "where id=%d;".format(todoData.id)
        db.execSQL(COMMAND)
    }


    /*
    DBの指定されたレコードを削除するメソッド
     */
    fun deleteDataBase(id:Int) {
        val db: SQLiteDatabase = helper.writableDatabase
        db.execSQL("delete from todo_table where id=" + id + ";")
    }


    /*
    サーバ上のDBを反映させるメソッド
     */
    fun reflectAPIDataBase(jsonObject:JSONObject) {
        val db:SQLiteDatabase = helper.writableDatabase
        helper.onUpgrade(db, 0, 0)      // DBを初期化

        val data = jsonObject.getJSONArray("data")
        for (i in 0 until data.length()) {
            val todo = data.getJSONObject(i)
            val contentValues = ContentValues()
            contentValues.put("id", todo.getInt("id"))
            contentValues.put("todo", todo.getString("content"))
            contentValues.put("check_flag", todo.getBoolean("check_flag"))
            db.insert("todo_table", null, contentValues)
        }
    }

    /*
    レコードの数を返すメソッド
     */
    fun getDBCount(): Int {
        val db: SQLiteDatabase = helper.readableDatabase

        // クエリ文でデータを取得
        val cursor: Cursor = db.query(
            "todo_table",
            arrayOf<String>("todo", "check_flag"),
            null, null, null, null, null)

        // カーソルをpositionの位置に移動
        if (cursor.moveToFirst()) {
            Log.d("getContentDataBase()", "カーソルが先頭に位置に移動しました")
        } else {
            Log.d("getContentDataBase()", "カーソルがありません")
            return 0
        }

        cursor.close()
        return cursor.count
    }
}


/*
DBのレコード部分を模した構造体
 */
data class TodoData(val id: Int, val content: String = "", val flag: Boolean = false)


/*
SharePreference
 */
fun getSP(context: Context, key: String) : String? {
    val pref = context.getSharedPreferences("sp", Context.MODE_PRIVATE)
    return pref.getString(key, null)
}

