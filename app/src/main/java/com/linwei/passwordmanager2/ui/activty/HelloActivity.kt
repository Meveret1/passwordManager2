package com.linwei.passwordmanager2.ui.activty

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.edit
import com.linwei.passwordmanager2.Constant
import com.linwei.passwordmanager2.R
import com.linwei.passwordmanager2.base.BaseActivity
import com.linwei.passwordmanager2.encryp.Aes
import com.linwei.passwordmanager2.encryp.FingerAuthen
import com.linwei.passwordmanager2.encryp.Md5
import kotlinx.android.synthetic.main.psactivity_hello.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.*


/**
 *@Auther Everet
 *@Date 2021/7/4
 **/
class HelloActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    override fun initView() {
        super.initView()

    }

}


//    private class MyHandler(helloActivity: HelloActivity) : Handler() {
//
//        private var wakeReference: WeakReference<HelloActivity> = WeakReference(helloActivity)
//
//        override fun handleMessage(msg: Message) {
//            super.handleMessage(msg)
//            val actvity = wakeReference.get()
//            when (msg.what) {
//                1 -> {
//                    actvity?.run {
//                        if (skipCount == 0) {
//                            Log.e(TAG, "handleMessage: ")
//                            startActivity(Intent(this, MainActivity::class.java))
//                            finish()
//                        } else {
//                            Log.e(TAG, "handleMessage: skip--")
//                            skipCount--
//                            button.text = skipCount.toString()
//                            postDelayed(Runnable {
//                                sendEmptyMessage(1)
//                            }, 1000)
//                        }
//
//                    }
//
//                }
//
//
//            }
//
//        }
//
//
//    }
