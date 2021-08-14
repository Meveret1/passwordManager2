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
class FingerAuthen(
    private val actvity: Activity
) {
    private lateinit var keyStore: KeyStore //密钥库
    private lateinit var biometricPrompt: BiometricPrompt //指纹模块
    private var cipher: Cipher? = null //cipher备用
    val cancellationSignal = CancellationSignal()  //取消指纹识别方法
    lateinit var perf: SharedPreferences  //保存初始化向量IV
    private lateinit var authenticationCallback: BiometricPrompt.AuthenticationCallback
    init {
        keyStore = KeyStore.getInstance("AndroidKeyStore")  //取得android密钥库
        keyStore.load(null)
    }

    /**
     * 创建加密的FingerAuthen
     */
    fun creatEncode(): FingerAuthen? {

        if (check()) {
            createKey()
            initEncode()
            initFinger()
            return this
        } else
            return null
    }

    /**
     * 创建解密的FingerAuthen
     */
    fun createDecode(): FingerAuthen? {
        if (check()) {
            if (!initDecode()) return null
            initFinger()
            return this
        } else
            return null
    }

    /**
     * 显示系统自带的指纹页面
     */
    fun show() {
        biometricPrompt.authenticate(
            BiometricPrompt.CryptoObject(cipher!!),//要加密需要传cipher
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
     * 创建key，仅在加密模式下才能创建
     */
    private fun createKey() {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val builder = KeyGenParameterSpec.Builder(
            Constant.DEFAULT_KEY_NAME,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setUserAuthenticationRequired(false)////每次使用这个密钥，需要指纹验证,为了内部使用
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }

    /**
     * 初始化加密cipher
     */
    private fun initEncode() {
        val key = keyStore.getKey(Constant.DEFAULT_KEY_NAME, null) as SecretKey
        cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
        cipher!!.init(Cipher.ENCRYPT_MODE, key)//初始化加密的cipher。

    }

    /**
     * 解密时初始化cipher
     * 解密需要获取iv，iv：初始化向量
     */

    fun initDecode(): Boolean {
        val key = keyStore.getKey(Constant.DEFAULT_KEY_NAME, null) as SecretKey
        cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
        perf = actvity.getSharedPreferences("data", Context.MODE_PRIVATE)
        val iv = perf.getString("iv", "")//获取iv值。这里解密之前肯定是加密过一次的
        return if (iv!!.isNotEmpty()) {
            cipher!!.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(Base64.getDecoder().decode(iv)))
            true
        } else {
            false
        }
    }

    fun getCipher():Cipher?{
        return cipher
    }

    fun setAuthenticationCallback(authenticationCallback:BiometricPrompt.AuthenticationCallback){
        this.authenticationCallback=authenticationCallback
    }
    /**
     * 初始化biometricPrompt
     */

    private fun initFinger() {
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

}