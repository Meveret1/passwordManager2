package com.linwei.passwordmanager2.encryp

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.linwei.passwordmanager2.Constant
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


/**
 *@Auther Everet
 *@Date 2021/7/4
 **/
class FingerAuthen(private val actvity: Activity) {

    private lateinit var biometricPrompt: BiometricPrompt //指纹模块
    val cancellationSignal = CancellationSignal()  //取消指纹识别方法
    lateinit var perf: SharedPreferences  //获取初始化向量IV
    private lateinit var authenticationCallback: BiometricPrompt.AuthenticationCallback
    private var cipherHelper = CipherHelper()

    init {
        // 初始化biometricPrompt
        biometricPrompt = BiometricPrompt.Builder(actvity)
            .setTitle("指纹验证")
            .setNegativeButton(
                "使用密码验证",
                actvity.mainExecutor,
                { dialog, which ->
                    Toast.makeText(actvity, "取消验证", Toast.LENGTH_LONG).show()
                })
            .build()
    }

    /**
     * 开启指纹加密
     */
    fun fingerPrint_encryption() {
        if (check()) {
            show(cipherHelper.getCipher())
        }

    }

    /**
     * 指纹解密
     */
    fun fingerPrint_Decryption() {
        if (check()) {
            getCipher()?.let {
                show(it)
            }
        }
    }

    /**
     * 显示系统自带的指纹页面
     */
    private fun show(cipher: Cipher) {
        // 显示
        biometricPrompt.authenticate(
            BiometricPrompt.CryptoObject(cipher),//要加密需要传cipher
            cancellationSignal,
            actvity.mainExecutor,
            authenticationCallback
        )
    }

    /**
     * 检查是否支持指纹
     * @return Boolean
     */

    private fun check(): Boolean {
        if (Build.VERSION.SDK_INT < 23) {//小于android6.0是没有指纹支持的
            Constant.ToastUtil(actvity, "系统不支持指纹", Toast.LENGTH_LONG)
            return false
        } else {
            val manager = FingerprintManagerCompat.from(actvity)
            val keyguardManager = actvity.getSystemService(KeyguardManager::class.java)
            if (!manager.isHardwareDetected) {
                Constant.ToastUtil(actvity, "不支持指纹功能", Toast.LENGTH_LONG)
                return false
            } else if (!keyguardManager.isKeyguardSecure) {
                Constant.ToastUtil(actvity, "屏幕未设置锁屏 请先设置锁屏并添加一个指纹", Toast.LENGTH_LONG)
                return false
            } else if (!manager.hasEnrolledFingerprints()) {
                Constant.ToastUtil(actvity, "至少在系统中添加一个指纹", Toast.LENGTH_LONG)
                return false
            } else {
                return true
            }
        }
    }

    /**
     *
     * @return Cipher?
     */
    fun getCipher(): Cipher {
        perf = actvity.getSharedPreferences("data", Context.MODE_PRIVATE)
        val iv = perf.getString("iv", "")//获取iv值。这里解密之前肯定是加密过一次的

        return if (iv!!.isNotEmpty()) {
            cipherHelper.getCipher(IvParameterSpec(Base64.getDecoder().decode(iv)))
        } else {
            cipherHelper.getCipher()
        }

    }

    fun setAuthenticationCallback(authenticationCallback: BiometricPrompt.AuthenticationCallback) {
        this.authenticationCallback = authenticationCallback
    }
}