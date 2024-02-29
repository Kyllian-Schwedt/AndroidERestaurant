package fr.isen.schwedt.androiderestaurant.data

import android.annotation.SuppressLint
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    private const val ALGORITHM = "AES"
    private const val KEY = "1Hbfh667adfDEJ78"

    fun encrypt(data: String): String {
        val secretKey = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    fun decrypt(data: String): String {
        val secretKey = SecretKeySpec(KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val decryptedData = cipher.doFinal(decodedData)
        return String(decryptedData)
    }
}