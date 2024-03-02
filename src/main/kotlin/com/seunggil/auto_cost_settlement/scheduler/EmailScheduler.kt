package com.seunggil.auto_cost_settlement.scheduler


import com.seunggil.auto_cost_settlement.database.repository.KeystoreRepository
import com.seunggil.auto_cost_settlement.database.repository.UserRepository
import com.seunggil.auto_cost_settlement.service.security.EncryptService
import com.seunggil.auto_cost_settlement.service.settlement.BaeminService
import com.seunggil.auto_cost_settlement.service.settlement.EmailService
import com.seunggil.auto_cost_settlement.service.settlement.HtmlService
import com.seunggil.auto_cost_settlement.service.settlement.PdfService
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class EmailScheduler(
    private val emailService: EmailService,
    private val htmlService: HtmlService,
    private val pdfService: PdfService,
    private val baeminService: BaeminService,
    private val userRepository: UserRepository,
    private val keystoreRepository: KeystoreRepository,
    private val encryptService: EncryptService
) {

    @Scheduled(fixedRate = 1800000)
    @Transactional
    fun onEmailReceived() {
        val userList = userRepository.findAll()

        userList.forEach { user ->
            val decryptKey = keystoreRepository.findByUser(user)?.passKey

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