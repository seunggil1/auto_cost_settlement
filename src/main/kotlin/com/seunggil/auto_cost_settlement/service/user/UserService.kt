package com.seunggil.auto_cost_settlement.service.user

import com.seunggil.auto_cost_settlement.database.entity.Keystore
import com.seunggil.auto_cost_settlement.database.entity.UserAccount
import com.seunggil.auto_cost_settlement.database.repository.KeystoreRepository
import com.seunggil.auto_cost_settlement.database.repository.SettlementHistoryRepository
import com.seunggil.auto_cost_settlement.database.repository.UserAccountRepository
import com.seunggil.auto_cost_settlement.service.security.EncryptService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class UserService(
    private val userAccountRepository: UserAccountRepository,
    private val keystoreRepository: KeystoreRepository,
    private val encryptService: EncryptService,
    private val settlementHistoryRepository: SettlementHistoryRepository
) {

    @Transactional
    fun getUser(id: String, password: String, host: String = "imap.naver.com"): UserAccount? {
        val user = userAccountRepository.findById(id)

        if (user == null) {
            val encryptKey = encryptService.generateAESKey()
            val encryptPassword = encryptService.encryptPassword(password, encryptKey)

            val newUser = UserAccount(id = id, host = host, encryptedPassword = encryptPassword)
            userAccountRepository.save(newUser)

            val keystore = Keystore(passKey = encryptKey, userAccount = newUser)
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

    @Transactional
    fun deleteUser(userAccount: UserAccount): Boolean {
        if (userAccount.userIndex != null) {
            settlementHistoryRepository.deleteAllByUserAccount(userAccount)
            keystoreRepository.deleteByUserAccount(userAccount)
            userAccountRepository.deleteByUserIndex(userAccount.userIndex)
            return true
        } else {
            return false
        }

    }

}