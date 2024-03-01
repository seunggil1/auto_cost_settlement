package com.seunggil.auto_cost_settlement.service.security

import org.jasypt.util.text.AES256TextEncryptor
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


@Service
class EncryptService(
    private val keyGen: KeyGenerator = KeyGenerator.getInstance("AES")
) {
    init {
        // AES 키 생성기 초기화
        keyGen.init(256) // 128, 192, 256 중 선택 가능
    }

    fun generateAESKey(): String {
        // 키 생성
        val secretKey: SecretKey = keyGen.generateKey()

        // 바이트 배열을 Base64 인코딩하여 문자열로 변환
        return Base64.getEncoder().encodeToString(secretKey.encoded)
    }

    fun encryptPassword(password: String, encryptionKey: String): String {
        val textEncryptor = AES256TextEncryptor()
        textEncryptor.setPassword(encryptionKey)
        return textEncryptor.encrypt(password)
    }

    fun decryptPassword(encryptedPassword: String, encryptionKey: String): String {
        val textEncryptor = AES256TextEncryptor()
        textEncryptor.setPassword(encryptionKey)
        return textEncryptor.decrypt(encryptedPassword)
    }
}