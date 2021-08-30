package com.linwei.passwordmanager2.encryp
import android.util.Log
import com.linwei.passwordmanager2.Constant
import java.security.Key
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 *@Auther Everet
 *@Date 2021/7/4
 **/
class Aes(key: String) {

    private val cipher: Cipher by lazy {
        Cipher.getInstance("AES/ECB/PKCS5Padding")//"算法/模式/补码方式"
    }
    private val keySpec: Key by lazy {
        SecretKeySpec(key.toByteArray(), "AES")
    }

    /**
     * 加密操作
     * @param plainText 明文，需要加密的字符串
     */
    fun encryp(plainText: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypt = cipher.doFinal(plainText.toByteArray())
        Log.e("TAG", "encryp: ${String(encrypt)}")
        return Base64.getEncoder().encodeToString(encrypt) //这里先转换成Base64
    }

    /**
     * 解密操作
     * @param cipherText 密文，需要解密的字符串
     */
    fun decrypt(cipherText: String): String {
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        var encrypt:ByteArray
        try {
             encrypt = cipher.doFinal(Base64.getDecoder().decode(cipherText))//这里将base64的字符串转换成字节数组
        }catch (e:Exception){
            e.printStackTrace()
            return Constant.PASSWORD_ERRO
        }
        return String(encrypt)
    }

}
