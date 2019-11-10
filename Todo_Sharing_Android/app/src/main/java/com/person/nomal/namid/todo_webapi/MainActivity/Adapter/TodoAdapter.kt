package com.person.nomal.namid.todo_webapi.MainActivity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ListView
import com.person.nomal.namid.todo_webapi.MainActivity.DataBaseManager
import com.person.nomal.namid.todo_webapi.MainActivity.TodoData
import com.person.nomal.namid.todo_webapi.R


/*
TODOリスト用のアダプタークラス
 */
open class TodoAdapter(val context: Context, val dbManager: DataBaseManager): BaseAdapter() {

    /*
    プロパティ郡
     */
    public var is_edit = false      // 現在のアダプターが編集用であるかのフラグ
    protected val inflater = LayoutInflater.from(context)

    private data class ViewHolder(val cellView: View) {
        val checkBox = cellView.findViewById<CheckBox>(R.id.todo_checkbox)
    }

    private fun createCellView(parent: ViewGroup?): View {
        val cell_view:View = inflater.inflate(R.layout.todo_cell, parent, false)
        cell_view.tag = ViewHolder(cell_view)
        return cell_view
    }

    //--- 各Cellに対応するViewを返すメソッド ---//
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // セルViewを作成、または再利用
        val cell_view = convertView ?: createCellView(parent)
        // チェックボックのクリックイベント処理
        (cell_view.tag as ViewHolder).checkBox.setOnClickListener {
            /*
            チェックボックの処理をここに記述
             */
            val todoData: TodoData? = getItem(position)
            if(todoData != null) {
                (parent as ListView).performItemClick(it, position, position.toLong())  // setOnItemClickListener呼び出し
            }

        }
        // セルに必要なデータを取得
        val todoData = getItem(position)
        // セルView内のUIにデータをセット
        val checkBox = (cell_view.tag as ViewHolder).checkBox
        if (todoData != null) {
            checkBox.tag = todoData.id
            checkBox.text = todoData.content
            checkBox.isChecked = todoData.flag
        }
        // ビューを返却
        return cell_view
    }


    //--- 各Cellに使うデータを返すメソッド ---//
    override fun getItem(position: Int): TodoData? {
        // positionに相当するレコードのデータを取得
        val todoData: TodoData? = dbManager.getContentDataBase(position)
        // スマートキャストして返却
        if(todoData != null) {
            return todoData
        } else {
            return null
        }
    }


    //--- 各セルのIDを返すメソッド ---//
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    //--- ListViewのセルの数を返すメソッド ---//
    override fun getCount(): Int {
        return dbManager.getDBCount()
    }

}