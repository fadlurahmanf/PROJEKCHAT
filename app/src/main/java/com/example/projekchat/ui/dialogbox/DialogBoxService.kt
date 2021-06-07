package com.example.projekchat.ui.dialogbox

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.projekchat.R

class DialogBoxService(var activity: Activity) {
    lateinit var alertDialog: AlertDialog
    val view = View.inflate(activity, R.layout.dialog_box_status, null)
    var builder = AlertDialog.Builder(activity).setView(view).setCancelable(false)

    val layoutLoading:ConstraintLayout = view.findViewById(R.id.dialogBox_layoutLoading)
    val layoutStatus:ConstraintLayout = view.findViewById(R.id.dialogBox_layoutStatus)
    val imageStatus:ImageView = view.findViewById(R.id.dialog_image_status)
    val messageStatus:TextView = view.findViewById(R.id.dialog_msgStatus)

    fun startLoading(){
        layoutLoading.visibility = View.VISIBLE
        layoutStatus.visibility = View.INVISIBLE

        alertDialog = builder.create()
        alertDialog.show()
    }

    fun dismissDialog(){
        alertDialog.dismiss()
    }

    fun successDialog(message:String){
        alertDialog.setCancelable(true)
        layoutLoading.visibility = View.INVISIBLE
        layoutStatus.visibility = View.VISIBLE

        imageStatus.setImageResource(R.drawable.img_success)
        messageStatus.text = message.toString().toUpperCase()

        view.setOnClickListener {
            dismissDialog()
        }
    }

    fun failedDialog(message: String){
        alertDialog.setCancelable(true)
        layoutLoading.visibility = View.INVISIBLE
        layoutStatus.visibility = View.VISIBLE

        imageStatus.setImageResource(R.drawable.img_error)
        messageStatus.text = message.toString().toUpperCase()

        view.setOnClickListener {
            dismissDialog()
        }

    }
}