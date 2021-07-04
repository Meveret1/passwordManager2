package com.linwei.passwordmanager2.encryp

import android.app.Activity
import android.app.KeyguardManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.linwei.passwordmanager2.Constant
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


/**
 *@Auther Everet
 *@Date 2021/7/4
 **/
class FingerAuthen(
    private val actvity: Activity,
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
) {
    private lateinit var keyStore: KeyStore
    private lateinit var biometricPrompt: BiometricPrompt
    private  var cipher:Cipher?=null
    val cancellationSignal = CancellationSignal()

    fun create(): FingerAuthen? {
        if (check()) {
            initKey()
            initCipher()
            initFinger()
            return this
        } else
            return null
    }

    fun show() {
        biometricPrompt.authenticate(
            BiometricPrompt.CryptoObject(cipher!!),
            cancellationSignal,
            actvity.mainExecutor,
            authenticationCallback
        )
    }

    private fun check(): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
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

    private fun initKey() {
        keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val builder = KeyGenParameterSpec.Builder(
            Constant.DEFAULT_KEY_NAME,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setUserAuthenticationRequired(true)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }

    fun initCipher() {
        val key = keyStore.getKey(Constant.DEFAULT_KEY_NAME, null) as SecretKey

        cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
        cipher!!.init(Cipher.ENCRYPT_MODE, key)//加密


    }
    fun initdecode():FingerAuthen{
        val key = keyStore.getKey(Constant.DEFAULT_KEY_NAME, null) as SecretKey
        cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
        cipher!!.init(Cipher.DECRYPT_MODE, key,IvParameterSpec(cipher!!.iv) )//解密

        return this
    }

    private fun initFinger() {
        biometricPrompt = BiometricPrompt.Builder(actvity)
            .setTitle("指纹验证")
            .setNegativeButton("使用密码验证", actvity.mainExecutor,
                { dialog, which ->
                    Toast.makeText(actvity, "取消验证", Toast.LENGTH_LONG).show()
                })
            .build()


    }

}