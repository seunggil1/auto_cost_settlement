package com.seunggil.auto_cost_settlement.service


import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.nio.file.Paths


@Service
class EmailScheduler(
    private val emailService: EmailService,
    private val htmlService: HtmlService,
    private val pdfService: PdfService

) {

    @Scheduled(fixedRate = 5000)
    fun onEmailReceived() {
        val host = "imap.naver.com"
        val user = "{naverid}"
        val password = "{naverpassword}"

        val results = emailService.readEmails(host, user, password)
        for (result in results) {
            val link = htmlService.findHyperlinks(result)
            var content = htmlService.fetchContentFromUrl(link[0])
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