package com.example.enlearn

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.enlearn.utils.DialogCallback
import com.example.enlearn.utils.DialogUtils
import com.example.enlearn.utils.PromptDialog


/**
 * Created by Siva G Gurusamy on 03/Apr/2022
 * email : siva@paxel.co
 */
fun Activity.navigateTo(to: Class<*>, animatedUp: Boolean = false) {
    startActivity(Intent(this, to))
    if (animatedUp) overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_up)
    else overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
}

fun Activity.navigateTo(intent: Intent, animatedUp: Boolean = false) {
    startActivity(intent)
    if (animatedUp) overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_up)
    else overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
}

fun Activity.backTo(intent: Intent, animatedUp: Boolean = false) {
    startActivity(intent)
    if (animatedUp) overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_up)
    else overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
}

fun AppCompatActivity.showPromptDialog(
    title: String,
    message: CharSequence,
    okbutton: String,
    cancelbutton: String,
    oklistener: DialogCallback.AlertCallback? = null,
    cancellistener: DialogCallback.AlertCallback? = null
) {
    val dialog = PromptDialog.newInstance(title, message, okbutton, cancelbutton)
    oklistener?.let {
        dialog.okListener = oklistener
    }
    cancellistener?.let {
        dialog.cancelListener = cancellistener
    }
    DialogUtils.showDialog(dialog, false, supportFragmentManager)
}



