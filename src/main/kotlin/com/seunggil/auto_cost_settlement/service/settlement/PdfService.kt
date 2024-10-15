package com.seunggil.auto_cost_settlement.service.settlement


import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider
import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.kernel.*
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.IBlockElement
import com.itextpdf.layout.element.IElement
import com.itextpdf.layout.font.FontProvider
import com.seunggil.auto_cost_settlement.database.entity.SettlementHistory
import com.seunggil.auto_cost_settlement.database.repository.SettlementHistoryRepository
import org.springframework.stereotype.Service
import java.io.*
import com.seunggil.auto_cost_settlement.database.entity.UserAccount
import jakarta.transaction.Transactional
import java.nio.file.Files
import java.nio.file.Paths


@Service
class PdfService(
    private val htmlService: HtmlService,
    private val baeminService: BaeminService,
    private val settlementHistoryRepository: SettlementHistoryRepository

) {
    // reference : https://mchch.tistory.com/116
    @Throws(IOException::class)
    fun renderHtmlToPdf(htmlContent: String, outputFilePath: String): Boolean {
        File(outputFilePath).parentFile.mkdirs()

        val fontStream = this::class.java.getResourceAsStream("/fonts/NanumSquareRoundL.ttf")
            ?: throw IOException("폰트 파일을 찾을 수 없습니다.")
        val fontProgram = FontProgramFactory.createFont(fontStream.readBytes())

        //ConverterProperties : htmlconverter의 property를 지정하는 메소드인듯
        val properties = ConverterProperties()
        val fontProvider: FontProvider = DefaultFontProvider(false, false, false).apply {
            addFont(fontProgram)
        }

        fontProvider.addFont(fontProgram)
        properties.setFontProvider(fontProvider)

        val elements: List<IElement> = HtmlConverter.convertToElements(htmlContent, properties)
        val pdf = PdfDocument(PdfWriter(outputFilePath))
        val document = Document(pdf, PageSize.A4)

        //setMargins 매개변수순서 : 상, 우, 하, 좌

        document.setMargins(-30f, 0f, 0f, 50f)
        for (element in elements) {
            document.add(element as IBlockElement)
        }
        document.close()
        return true

    }

    @Transactional
    fun savePdfFromEmail(userAccount: UserAccount, emailContent: String) {
//        2024.10.15
//        이메일 형식 변경으로 인해 사용하지 않음
//        AS IS : 메일에 링크를 제공하는 방식
//        TO BE : 메일 본문이 영수증
        
//        val link = htmlService.findHyperlinks(emailContent)
//        var content = htmlService.fetchContentFromUrl(link[0])

        var content = emailContent
        val cost = baeminService.extractMoney(content)
        val date = baeminService.extractDate(content)

        content = htmlService.convertCssLink(content)

        content = htmlService.removeScriptTag(content)
        content = htmlService.removeLinkTag(content)

        val fileName = userAccount.id
        val path = "html/$fileName.pdf"

        if (renderHtmlToPdf(content, path) && date != null) {
            val pdfData = Files.readAllBytes(Paths.get(path))
            val history = SettlementHistory(userAccount = userAccount, cost = cost, date = date, pdf = pdfData)

            settlementHistoryRepository.save(history)
        }

    }


}
