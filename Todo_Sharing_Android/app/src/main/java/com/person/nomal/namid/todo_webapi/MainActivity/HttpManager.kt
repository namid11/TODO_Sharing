package com.person.nomal.namid.todo_webapi.MainActivity

import android.app.AlertDialog
import android.util.Log
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

// TODOリストのJSONデータを取得するためのメソッド
fun JSONGet(ip:String, port: String): InputStream? {
    /*
    ip: サーバのipアドレス
     */
    val url:URL = URL("http://" + ip + ":" + port + "/main/json/datas/")

    try {
        val connection = url.openConnection() as HttpURLConnection

        connection.apply {
            requestMethod = "GET"       // メソッド
            connectTimeout = 3000       // 接続のタイムアウト
            readTimeout = 5000          // 読み込みのタイムアウト
            instanceFollowRedirects = true  // リダイレクトの許可
        }
         connection.connect()   // 接続

        // ステータスコードの確認
        if (connection.responseCode in 200..299) {
            return BufferedInputStream(connection.inputStream)
        }

    } catch (e:IOException) {
        Log.d("ERROR", e.message)
        return null
    }

    return null
}


/*
コネクション作成メソッド
 */
fun createHttpURLConnection(ip: String, port:String, location: String): HttpURLConnection {
    val connection = URL("http://" + ip + ":" + port + location).openConnection() as HttpURLConnection
    connection.apply {
        requestMethod = "POST"
        connectTimeout = 3000       // 接続のタイムアウト
        readTimeout = 5000          // 読み込みのタイムアウト
        instanceFollowRedirects = true  // リダイレクトの許可
        doOutput = true
    }
    return connection
}


/*
WebサーバへのTodo追加リクエスト
 */
fun insertRequest(ip:String, port: String, todoData: TodoData): Boolean {
    try {
        val connection:HttpURLConnection = createHttpURLConnection(ip, port, "/main/api/insert/")

        try {
            // bodyにデータを追加
            val outputStream = OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8)
            outputStream.write("content=" + todoData.content + "&")
            outputStream.flush()
        } catch (e:IOException) {
            Log.d("io error", e.message)
        }

        connection.connect()

        if (connection.responseCode in 200 .. 299) {
            val reader = BufferedInputStream(connection.inputStream).bufferedReader()
            Log.d("response body", reader.readLine())
            reader.close()
            return true
        } else {
            val reader = BufferedInputStream(connection.inputStream).bufferedReader()
            Log.d("response body", reader.readLine())
            reader.close()
        }

    } catch (e:SocketTimeoutException) {
        Log.d("timeout", e.message)
    } catch (e:IOException) {
        Log.d("io error", e.message)
    }

    return false
}


/*
WebサーバへのTodo更新リクエスト
 */
fun updateRequest(ip:String, port: String, todoData: TodoData): Boolean {
    try {
        val connection:HttpURLConnection = createHttpURLConnection(ip, port, "/main/api/update/")

        try {
            val outputStreamWriter = OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8)
            outputStreamWriter.write("id=" + todoData.id + "&")
            outputStreamWriter.write("content=" + todoData.content + "&")
            outputStreamWriter.write("check_flag=" + (if(todoData.flag) 1 else 0) + "&")
            outputStreamWriter.flush()
        } catch (e:Exception) {
            Log.e(e.toString(), e.message)
        }

        connection.connect()

        if (connection.responseCode in 200 .. 299) {
            val reader = BufferedInputStream(connection.inputStream).bufferedReader()
            Log.d("response body", reader.readLine())
            reader.close()
            return true
        } else {
            val reader = BufferedInputStream(connection.inputStream).bufferedReader()
            Log.d("response body", reader.readLine())
            reader.close()
        }
    } catch (e:Exception) {
        Log.e(e.toString(), e.message)
    }


    return false
}


/*
WebサーバへのTodo削除リクエスト
 */
fun deleteRequest(ip: String, port: String, todoData: TodoData): Boolean {
    try {
        val connection = createHttpURLConnection(ip, port, "/main/api/delete/")

        try {
            val outputStreamWriter = OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8)
            outputStreamWriter.write("id=" + todoData.id + "&")
            outputStreamWriter.flush()
        } catch (e: Exception) {
            Log.e(e.toString(), e.message)
        }

        connection.connect()

        if (connection.responseCode in 200 .. 299) {
            val reader: BufferedReader = BufferedInputStream(connection.inputStream).bufferedReader()
            Log.d("response body", reader.readLine())
            reader.close()
            return true
        } else {
            val reader = BufferedInputStream(connection.inputStream).bufferedReader()
            Log.d("response body", reader.readLine())
            reader.close()
        }
    } catch (e: IOException) {
        Log.e(e.toString(), e.message)
    }

    return false
}