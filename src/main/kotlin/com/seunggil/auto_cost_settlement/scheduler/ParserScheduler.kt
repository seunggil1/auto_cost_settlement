package com.seunggil.auto_cost_settlement.scheduler


import com.seunggil.auto_cost_settlement.database.repository.KeystoreRepository
import com.seunggil.auto_cost_settlement.database.repository.UserAccountRepository
import com.seunggil.auto_cost_settlement.service.security.EncryptService
import com.seunggil.auto_cost_settlement.service.settlement.EmailService
import com.seunggil.auto_cost_settlement.service.settlement.PdfService
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class ParserScheduler(
    private val emailService: EmailService,
    private val pdfService: PdfService,
    private val userAccountRepository: UserAccountRepository,
    private val keystoreRepository: KeystoreRepository,
    private val encryptService: EncryptService,
) {

    @Scheduled(fixedRate = 1800000)
    @Transactional
    fun scheduler() {
        val userList = userAccountRepository.findAll()

        userList.forEach { user ->
            val decryptKey = keystoreRepository.findByUserAccount(user)?.passKey

            if (decryptKey.isNullOrEmpty())
                return@forEach

            val password = encryptService.decryptPassword(user.encryptedPassword, decryptKey)
            val results = emailService.readEmails(user.host, user.id, password)

            for (result in results) {
                pdfService.savePdfFromEmail(user, result)
            }
        }

    }
}