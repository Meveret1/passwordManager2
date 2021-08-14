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

    val TAG = "helloactivity"
    var skipCount = 5
    var isencrip = true
    var miwen = ""
    var plainPassword = ""
    var Md5Password = ""
    lateinit var perf: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.psactivity_hello)
        perf = getSharedPreferences("data", Context.MODE_PRIVATE)
        initView()
        val password = "123456"

        switch1.isChecked = perf.getBoolean("switch", false)
        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (editPlain.text.toString().isNotEmpty())
                    perf.edit {
                        putBoolean("switch", isChecked)
                    }
                else {

                }

            } else {
                Log.e(TAG, "onCreate: 关")
                perf.edit {
                    putBoolean("switch", isChecked)
                    putString("iv", "")//清空加密信息
                    putString("se", "")
                }
            }
        }

    }

    fun initView() {
        val fingerAuthen = FingerAuthen(this)
        fingerAuthen.setAuthenticationCallback(authenticationCallback)
        encodebtn.setOnClickListener {
            isencrip = true
            plainPassword = editPlain.text.toString()

            if (plainPassword.isNotEmpty()) {
                showText("原串 $plainPassword")
                Md5.MD5(plainPassword)?.let {
                    Md5Password = it
                    showText("加密原串：$it")
                    fingerAuthen.creatEncode()?.show()
                }
            }
        }
        decodebtn.setOnClickListener {
            fingerAuthen.createDecode()?.show()
        }

        innerbtn.setOnClickListener {
            fingerAuthen.initDecode()
            fingerAuthen.getCipher()?.let {
                val encodestr = perf.getString("se", "")
                if (!encodestr.isNullOrEmpty()) {
//                    val cipher = result!!.cryptoObject.cipher
                    val decode = it.doFinal(Base64.getDecoder().decode(encodestr))
                    showText("内部解密成功：${String(decode)}")
                }
            }
        }


    }

    fun showText(str: String) {
        Constant.logStr += "$str\n"
        Logtext.text = Constant.logStr

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
            Log.e(TAG, "onAuthenticationSucceeded: 识别成功")
            if (isencrip) {
                val cipher = result!!.cryptoObject.cipher
                val encode = cipher.doFinal(Md5Password.toByteArray())

                val se = Base64.getEncoder().encodeToString(encode)
                val siv = Base64.getEncoder().encodeToString(cipher.iv)
                showText("oldIV:" + perf.getString("iv", ""))
                showText("new IV: $siv")
                perf.edit {
                    putString("iv", siv)
                    putString("se", se)
                    showText("onAuthenticationSucceeded: 写入成功")
                }
            } else {
                val encodestr = perf.getString("se", "")
                if (!encodestr.isNullOrEmpty()) {
                    val cipher = result!!.cryptoObject.cipher
                    val decode = cipher.doFinal(Base64.getDecoder().decode(encodestr))
                    showText("解密成功：${String(decode)}")
                }

            }


        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Log.d("TAG", "验证失败")

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


}