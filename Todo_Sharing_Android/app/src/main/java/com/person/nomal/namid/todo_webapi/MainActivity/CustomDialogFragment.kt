package com.person.nomal.namid.todo_webapi.MainActivity

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import com.person.nomal.namid.todo_webapi.R

class CustomDialogFragment(): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_dialog, null)

        val sp = activity.getSharedPreferences("sp", Context.MODE_PRIVATE)
        dialogView.findViewById<EditText>(R.id.ip_address).setText(sp.getString("ip", ""))
        dialogView.findViewById<EditText>(R.id.port_number).setText(sp.getString("port", ""))

        builder.setView(dialogView)
            .setPositiveButton("保存", { dialog, which ->
                sp.edit().putString("ip", dialogView.findViewById<EditText>(R.id.ip_address).text.toString()).apply()
                sp.edit().putString("port", dialogView.findViewById<EditText>(R.id.port_number).text.toString()).apply()
            })
            .setNegativeButton("キャンセル", { dialog, which ->
                dialog.cancel()
            })

        return builder.create()
    }
}