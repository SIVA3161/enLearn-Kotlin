package com.example.enlearn.utils

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Created by Siva G Gurusamy on 10/Apr/2022
 * email : siva@paxel.co
 */

object DialogUtils{
    fun showDialog(dialogFragment: DialogFragment, isCancelable:Boolean = false, manager: FragmentManager){
        try {
            dialogFragment.isCancelable = isCancelable
            dialogFragment.show(manager, dialogFragment.tag)
        } catch (e: Exception) { e.printStackTrace() }
    }
}