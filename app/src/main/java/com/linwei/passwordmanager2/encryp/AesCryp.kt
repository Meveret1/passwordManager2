package com.linwei.passwordmanager2.encryp

import android.annotation.SuppressLint
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 *@Auther Everet
 *@Date 2021/7/4
 **/
class AesCryp {

    /**
     * 加密操作
     * @param plainText 明文，需要加密的字符串
     * @param key 密码
     */
    @SuppressLint("NewApi", "GetInstance")
    fun encryp(plainText: String, key: String): String {
        val cipher = Cipher.getInstance("AES")
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypt = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(encrypt) //这里先转换成Base64
    }

    /**
     * 解密操作
     * @param cipherText 密文，需要解密的字符串
     * @param key 密码
     */
    @SuppressLint("NewApi", "GetInstance")
    fun decrypt(cipherText: String, key: String): String {
        val cipher = Cipher.getInstance("AES")
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val encrypt = cipher.doFinal(Base64.getDecoder().decode(cipherText))//这里将base64的字符串转换成字节数组
        return String(encrypt)
    }

}
