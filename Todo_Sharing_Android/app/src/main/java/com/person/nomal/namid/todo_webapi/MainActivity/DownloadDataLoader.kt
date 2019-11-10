package com.person.nomal.namid.todo_webapi.MainActivity

import android.app.AlertDialog
import android.content.AsyncTaskLoader
import android.content.Context
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStream

class DownloadDataLoader(context: Context): AsyncTaskLoader<JSONObject>(context) {

    //----- 別スレッドで行いたい処理 -----//
    override fun loadInBackground(): JSONObject? {
        val sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE)
        val ip = sp.getString("ip", "")
        val port = sp.getString("port", "")
        val inputStream:InputStream? = JSONGet(ip, port)
        if (inputStream == null) {
            // データ取得失敗時にはnull返却
            return null
        }

        val jsonObject:JSONObject = JSONObject(inputStream.bufferedReader().readLine())
        return jsonObject
    }


    //----- コールバックに処理結果を渡すメソッド -----//
    override fun deliverResult(data: JSONObject?) {
        if (this.isReset()) {
            return  // リセットされた場合は結果を返さない
        }

        super.deliverResult(data)
    }


    //----- 非同期処理の前にコール -----//
    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()     // loadInBackground実行
    }


    //----- ローダー停止時にコール -----//
    override fun onStopLoading() {
        cancelLoad()
    }

    //----- ローダー破棄時にコール -----//
    override fun onReset() {
        onStopLoading()
    }

}