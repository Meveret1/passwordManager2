package com.linwei.passwordmanager2.encryp

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.linwei.passwordmanager2.Constant
import java.lang.Exception
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CipherHelper {
    private val TAG: String = "CipherHelper"
    private var keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore") // 密钥库
    private lateinit var cipher: Cipher   // cipher备用
    val key: SecretKey? by lazy {
        var key1:SecretKey?=null
        try {
            key1 = keyStore.getKey(Constant.DEFAULT_KEY_NAME, null) as SecretKey
        }catch (e:Exception){
            e.printStackTrace()
            createKey()
            key1 = keyStore.getKey(Constant.DEFAULT_KEY_NAME, null) as SecretKey
        }
        key1
    }

    init {
        keyStore.load(null)
    }

    /**
     * 创建key
     * 1.应用首次启动时创建
     */
    private fun createKey() {
        Log.e(TAG, "createKey: 被调用")
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")



        val builder = KeyGenParameterSpec.Builder(
            Constant.DEFAULT_KEY_NAME,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setUserAuthenticationRequired(false)// 每次使用这个密钥，需要指纹验证,为了内部使用
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }

    /**
     * 获取加密cipher，仅在加密时才会调用
     * @return Cipher
     */
    fun getCipher(): Cipher {
        cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
        cipher.init(Cipher.ENCRYPT_MODE, key)// 初始化加密的cipher。
        return cipher
    }

    /**
     * 获取解密cipher，最常用
     * @param iv IvParameterSpec
     * @return Cipher
     */

    fun getCipher(iv: IvParameterSpec): Cipher {
        cipher = Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
        cipher.init(Cipher.DECRYPT_MODE, key, iv)// 初始化解密的cipher。
        return cipher
    }


}