package com.savog.doopooding.core.exposed

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class Cipher {
    companion object {
        lateinit var secretKey: String
        private val encoder = Base64.getEncoder()
        private val decoder = Base64.getDecoder()

        private fun cipher(opmode: Int, secretKey: String): javax.crypto.Cipher {
            if (secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")
            val cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
            val ivParameterSpec = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8))
            cipher.init(opmode, secretKeySpec, ivParameterSpec)
            return cipher
        }
        val encrypt: (String) -> String = {
            val encrypted = cipher(
                Cipher.ENCRYPT_MODE,
                secretKey
            ).doFinal(it.toByteArray(Charsets.UTF_8))
            String(encoder.encode(encrypted))
        }
        val decrypt: (String) -> String = {
            // FIXME: 기존 테이블 값들 암호화 후에 예외처리 해제 필요
            try {
                val byteStr = decoder.decode(it.toByteArray(Charsets.UTF_8))
                String(
                    cipher(
                        Cipher.DECRYPT_MODE,
                        secretKey
                    ).doFinal(byteStr))
            } catch (e: IllegalBlockSizeException) {
                it
            } catch (e: java.lang.RuntimeException) {
                it
            }
        }
    }

    @Value("\${database.encryptionKey}")
    fun setKey(key: String) {
        secretKey = key
    }
}