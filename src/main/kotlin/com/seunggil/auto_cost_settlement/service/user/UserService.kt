package com.seunggil.auto_cost_settlement.service.user

import com.seunggil.auto_cost_settlement.database.entity.Keystore
import com.seunggil.auto_cost_settlement.database.entity.User
import com.seunggil.auto_cost_settlement.database.repository.KeystoreRepository
import com.seunggil.auto_cost_settlement.database.repository.UserRepository
import com.seunggil.auto_cost_settlement.service.security.EncryptService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class UserService(
    private val userRepository: UserRepository,
    private val keystoreRepository: KeystoreRepository,
    private val encryptService: EncryptService
) {

    @Transactional
    fun getUser(id: String, password: String, host: String = "imap.naver.com"): User? {
        val user = userRepository.findById(id)

        if (user == null) {
            val encryptKey = encryptService.generateAESKey()
            val encryptPassword = encryptService.encryptPassword(password, encryptKey)

            val newUser = User(id = id, host = host, encryptedPassword = encryptPassword)
            userRepository.save(newUser)

            val keystore = Keystore(passKey = encryptKey, user = newUser)
            keystoreRepository.save(keystore)

            return newUser
        } else {
            val keystore = keystoreRepository.findByUserIndex(user.userIndex ?: -1)

            keystore?.let {
                val decryptedPassword = encryptService.decryptPassword(user.encryptedPassword, keystore.passKey)

                if (password == decryptedPassword)
                    return user
            }

            return null
        }
    }

}