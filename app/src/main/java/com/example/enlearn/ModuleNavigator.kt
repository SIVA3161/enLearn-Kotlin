package com.example.enlearn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable

/**
 * Created by Siva G Gurusamy on 03/Apr/2022
 * email : siva@paxel.co
 */
class ModuleNavigator {


    fun navigate(activity: Activity, string: String, map: Map<String,Any>? = null, bundle: Bundle? = null) {
        try {
            val intent = Intent(activity, Class.forName(string))
            bundle?.let {
                intent.putExtras(it)
            }
            map?.let {
                it.forEach {
                    when(it.value) {
                        is Parcelable -> intent.putExtra(it.key, it.value as Parcelable)
                        is String -> intent.putExtra(it.key, it.value as String)
                        is Int -> intent.putExtra(it.key, it.value as Int)
                        is Boolean -> intent.putExtra(it.key, it.value as Boolean)
                    }

                }
            }
            activity.navigateTo(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun back(activity: Activity, string: String, map: Map<String,Any>? = null, bundle: Bundle? = null) {
        try {
            val intent = Intent(activity, Class.forName(string))
            bundle?.let {
                intent.putExtras(it)
            }
            map?.let {
                it.forEach {
                    when(it.value) {
                        is Parcelable -> intent.putExtra(it.key, it.value as Parcelable)
                        is String -> intent.putExtra(it.key, it.value as String)
                        is Int -> intent.putExtra(it.key, it.value as Int)
                        is Boolean -> intent.putExtra(it.key, it.value as Boolean)
                    }

                }
            }
            activity.backTo(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun navigateDefault(activity: Activity, string: String) {
        try {
            val intent = Intent(activity, Class.forName(string))
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val SPLASH_PAGE = "com.exampl.enlearn.SplashActivity"
        const val LOGIN_PAGE = "com.exampl.enlearn.LoginActivity"
    }
}