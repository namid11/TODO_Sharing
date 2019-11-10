package com.person.nomal.namid.todo_webapi.MainActivity

import android.app.LoaderManager
import android.content.Context
import android.content.Loader
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.person.nomal.namid.todo_webapi.MainActivity.Adapter.TodoAdapter
import com.person.nomal.namid.todo_webapi.MainActivity.Adapter.TodoEditAdapter
import com.person.nomal.namid.todo_webapi.R
import org.json.JSONObject

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<JSONObject> {

    /*
    プロパティ
     */
    lateinit var todo_listView: ListView
    lateinit var progressBar: ProgressBar
    val dataBaseManager: DataBaseManager = DataBaseManager(this)

    /*
    クラス内列挙型
     */
    enum class ADAPTEROPTION {
        DEFAULT, EDIT,
    }


    /*
    ローダー作成時にコール
     */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<JSONObject> {
        return DownloadDataLoader(context = this)
    }

    /*
    ローダ完了時にコール
     */
    override fun onLoadFinished(loader: Loader<JSONObject>?, data: JSONObject?) {
        if (data == null || loader == null) {
            // データ取得失敗時、エラーダイアログ表示
            AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setMessage("データ取得に失敗しました")
                .setPositiveButton("OK",null)
                .show()
        } else {
            dataBaseManager.reflectAPIDataBase(data)    // データがあればデータベースに反映
            if (loader.id == 1) {
                reloadAdapter()                             // アダプターを更新
            }
        }

        progressBar.visibility = android.widget.ProgressBar.INVISIBLE
    }

    /*
    ローダ破棄時にコール
     */
    override fun onLoaderReset(loader: Loader<JSONObject>?) {
    }


    /*
    Activityが作成された時に呼ばれるメソッド
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // リストビューにアダプターをセット
        todo_listView = findViewById<ListView>(R.id.todo_listview)
        todo_listView.adapter = TodoAdapter(this, dataBaseManager)
        todo_listView.setOnScrollListener(ListScrollListener(this))

        // プログレスバー取得
        progressBar = findViewById<ProgressBar>(R.id.progressbar)

        /*
        ListViewItemクリック処理
         */
        todo_listView.setOnItemClickListener { parent, view, position, id ->
            val sp = getSharedPreferences("sp", Context.MODE_PRIVATE)
            val checkBox = view as CheckBox
            val todoData:TodoData = TodoData(id=checkBox.tag as Int, content=checkBox.text.toString(), flag=checkBox.isChecked)
            // ローカルDBを更新
            dataBaseManager.updateDataBase(todoData)
            // サーバDB更新処理(非同期)
            Thread { updateRequest(ip=sp.getString("ip", ""), port=sp.getString("port", ""), todoData=todoData) }.start()
        }

        // DB更新
        loaderManager.initLoader(1, Bundle(), this)
    }


    /*
    メニューレイアウトを設定するメソッド
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }



    /*
    メニューアイテムが押された時に呼ばれるメソッド
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when(item.itemId) {
                // ＋ボタン //
                R.id.add_button -> {
                    val editText = EditText(this)
                    val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                    dialog.setTitle("Todoを入力してください")
                    dialog.setView(editText)
                    dialog.setPositiveButton("OK", { dialog, which ->
                        dataBaseManager.insertDataBase(editText.text.toString())
                        reloadAdapter()
                    })
                    dialog.setNegativeButton("Cansel", {dialog, which ->
                        dialog.cancel()
                    })
                    dialog.show()
                    Toast.makeText(this, "ADD", Toast.LENGTH_SHORT).show()
                }

                // 編集ボタン //
                R.id.edit_button -> {
                    if ((todo_listView.adapter as TodoAdapter).is_edit) {
                        // 編集 -> 通常
                        reloadAdapter(ADAPTEROPTION.DEFAULT)
                    } else {
                        // 通常 -> 編集
                        reloadAdapter(ADAPTEROPTION.EDIT)
                    }
                }

                // 更新ボタン //
                R.id.update_button -> {
                    startSyncDB()
                }

                // 設定ボタン //
                R.id.setting_button -> {
                    val customDialogFragment = CustomDialogFragment()
                    customDialogFragment.show(fragmentManager, "show")
                }
            }
        }
        return true
    }


    /*
    ListViewを更新するメソッド
     */
    fun reloadAdapter(option: ADAPTEROPTION = ADAPTEROPTION.DEFAULT) {
        when (option) {
            ADAPTEROPTION.DEFAULT -> {
                todo_listView.adapter =
                        TodoAdapter(this, dataBaseManager)
            }
            ADAPTEROPTION.EDIT -> {
                todo_listView.adapter =
                        TodoEditAdapter(this, dataBaseManager)
            }
        }
    }


    /*
    更新(同期)ボタンメソッド
     */
    fun startSyncDB() {
        loaderManager.restartLoader(1,Bundle(),this)
        progressBar.visibility = android.widget.ProgressBar.VISIBLE
    }

    class ListScrollListener(val context: Context): AbsListView.OnScrollListener {
        override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            //Log.d("OnScroll", "")
        }

        override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            if (view == null) {return}

            // スクロールがオーバーした時、更新処理
            if (view.firstVisiblePosition == 0 && scrollState == 1) {
                (context as MainActivity).startSyncDB()
            }
        }
    }

}

