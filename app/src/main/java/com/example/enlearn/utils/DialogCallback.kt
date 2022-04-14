package com.example.enlearn.utils

import android.app.Dialog

/**
 * Created by Siva G Gurusamy on 10/Apr/2022
 * email : siva@paxel.co
 */
interface DialogCallback {
    fun onDismiss()
    fun onOk()
    fun onCancel()

    interface AlertCallback{
        fun onButtonClicked(dialog: Dialog)
    }
}