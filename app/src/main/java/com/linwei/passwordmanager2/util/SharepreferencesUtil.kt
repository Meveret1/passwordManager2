package com.linwei.passwordmanager2.util

import android.app.Activity
import android.content.SharedPreferences

class SharepreferencesUtil(val activity: Activity) {



    fun getSharePreference(name:String,model:Int):SharedPreferences{
        return activity.getSharedPreferences(name,model)
    }



}