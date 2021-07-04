package com.linwei.passwordmanager2.ui.activty

import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import com.linwei.passwordmanager2.R
import com.linwei.passwordmanager2.base.BaseActivity
import com.linwei.passwordmanager2.encryp.FingerAuthen
import kotlinx.android.synthetic.main.psactivity_hello.*
import java.lang.ref.WeakReference


/**
 *@Auther Everet
 *@Date 2021/7/4
 **/
class HelloActivity : BaseActivity() {

    val TAG = "helloactivity"
    var skipCount = 5
    var isencrip=true
    var miwen=""
    var mingwen=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.psactivity_hello)
//        button.text = skipCount.toString()
        val fingerAuthen=FingerAuthen(this,authenticationCallback)
        button.text = "加密指纹"
        button.setOnClickListener {
            fingerAuthen.create()
        }
        button2.text="解密指纹"
        button2.setOnClickListener {
            isencrip=false
            fingerAuthen.initdecode().show()
        }
//        val myHandler = MyHandler(this)
//        myHandler.sendEmptyMessage(1)


    }

    override fun onResume() {
        super.onResume()





    }


    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            Log.d("TAG", "onAuthenticationError errorCode: $errorCode errString: $errString")
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)
            Log.d("TAG", "onAuthenticationHelp helpCode:" + helpCode + "helpString: " + helpString)
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            Log.e(TAG, "onAuthenticationSucceeded: 识别成功" )
            Thread{
                if (isencrip) {
                     val cipher = result!!.cryptoObject.cipher.doFinal("abvc".toByteArray())
                    miwen=String(cipher)
                    if (miwen != null)
                        Log.e(TAG, "onAuthenticationSucceeded: cipher=${miwen}")
                }else{
                     val cipher = result!!.cryptoObject.cipher.doFinal(miwen.toByteArray())
                    mingwen=String(cipher)
                    Log.e(TAG, "onAuthenticationSucceeded: cipher=${mingwen}")
                }
            }.start()



        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Log.d("TAG", "验证失败")

        }
    }


    private class MyHandler(helloActivity: HelloActivity) : Handler() {

        private var wakeReference: WeakReference<HelloActivity> = WeakReference(helloActivity)


        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val actvity = wakeReference.get()
            when (msg.what) {
                1 -> {
                    actvity?.run {
                        if (skipCount == 0) {
                            Log.e(TAG, "handleMessage: ")
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Log.e(TAG, "handleMessage: skip--")
                            skipCount--
                            button.text = skipCount.toString()
                            postDelayed(Runnable {
                                sendEmptyMessage(1)
                            }, 1000)
                        }

                    }

                }


            }

        }


    }


}