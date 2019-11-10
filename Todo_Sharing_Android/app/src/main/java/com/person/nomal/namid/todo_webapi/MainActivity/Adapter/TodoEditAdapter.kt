package com.person.nomal.namid.todo_webapi.MainActivity.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.person.nomal.namid.todo_webapi.MainActivity.*
import com.person.nomal.namid.todo_webapi.R



class TodoEditAdapter(context: Context, dbManager: DataBaseManager): TodoAdapter(context, dbManager) {

    init {
        is_edit = true
    }


    // セルViewのプロパティ郡
    private data class ViewHolder(val cellView: View) {
        val checkBox:CheckBox = cellView.findViewById(R.id.todo_edit_checkbox)
        val editButton: Button = cellView.findViewById(R.id.todo_edit_edit)
        val deleteButton: Button = cellView.findViewById(R.id.todo_edit_delete)
    }


    //--- CellView作成メソッド ---//
    private fun createCellView(parent: ViewGroup?): View {
        val cell_view: View = inflater.inflate(R.layout.todo_cell_edit, parent, false)
        cell_view.tag = ViewHolder(cell_view)
        return cell_view
    }


    //--- 各セルのViewを返すメソッド ---//
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val cell_view: View = convertView ?: createCellView(parent)
        val view_holder: ViewHolder = cell_view.tag as ViewHolder
        view_holder.editButton.setOnClickListener {
            /*
            編集ボタンの処理をここに記述
             */
            val dialog = AlertDialog.Builder(context)
            val edit_text: EditText = EditText(context)
            dialog.setView(edit_text)
            dialog.setTitle("Todoを編集")
            dialog.setPositiveButton("更新", { dialog, which ->
                val sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE)
                val todoData = TodoData(view_holder.checkBox.tag as Int, edit_text.text.toString(), view_holder.checkBox.isChecked)
                // ローカルDB更新処理
                dbManager.updateDataBase(todoData)
                // サーバDB更新処理(非同期)
                Thread { updateRequest(ip=sp.getString("ip", ""), port=sp.getString("port", ""), todoData=todoData) }.start()
                (context as MainActivity).reloadAdapter(MainActivity.ADAPTEROPTION.EDIT)
            })
            dialog.setNegativeButton("キャンセル", {dialog, which ->
                dialog.cancel()
            })
            dialog.show()
        }
        view_holder.deleteButton.setOnClickListener {
            /*
            削除ボタンの処理をここに記述
             */
            val sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE)
            val todoData = TodoData(view_holder.checkBox.tag as Int)
            // ローカルDB更新処理
            dbManager.deleteDataBase(id=view_holder.checkBox.tag as Int)   // DBからidにヒットするレコードを削除
            // サーバDB更新処理(非同期)
            Thread { deleteRequest(sp.getString("ip", ""), port = sp.getString("port", ""), todoData = todoData)}.start()
            (context as MainActivity).reloadAdapter()   // Adapterを更新
        }
        view_holder.checkBox.setOnClickListener {
            /*
            チェックボックの処理をここに記述
             */
            (parent as ListView).performItemClick(it, position, position.toLong())  // setOnItemClickListener呼び出し
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
}