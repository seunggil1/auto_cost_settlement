package com.seunggil.auto_cost_settlement.Scheduler


import com.seunggil.auto_cost_settlement.service.BaeminService
import com.seunggil.auto_cost_settlement.service.EmailService
import com.seunggil.auto_cost_settlement.service.HtmlService
import com.seunggil.auto_cost_settlement.service.PdfService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class EmailScheduler(
    private val emailService: EmailService,
    private val htmlService: HtmlService,
    private val pdfService: PdfService,
    private val baeminService: BaeminService

) {

    @Scheduled(fixedRate = 5000)
    fun onEmailReceived() {
        val host = "imap.naver.com"
        val user = "{naverId}"
        val password = "{naverPw}"

        val results = emailService.readEmails(host, user, password)
        for (result in results) {
            val link = htmlService.findHyperlinks(result)
            var content = htmlService.fetchContentFromUrl(link[0])
            baeminService.extractMoney(content)
            baeminService.extractDate(content)

            content = htmlService.convertCssLink(content)

            content = htmlService.removeScriptTag(content)
            content = htmlService.removeLinkTag(content)

            val fileName = "test"
            val path = "html/$fileName.pdf"
            val pdfResult = pdfService.renderHtmlToPdf(
                content, path
            )

            print(1)
        }

        // 추가 로직 구현
    }
}