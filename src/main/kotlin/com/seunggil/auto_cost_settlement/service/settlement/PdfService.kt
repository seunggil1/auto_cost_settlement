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

        val font = "src/main/resources/fonts/NanumSquareRoundL.ttf"

        //ConverterProperties : htmlconverter의 property를 지정하는 메소드인듯
        val properties: ConverterProperties = ConverterProperties()
        val fontProvider: FontProvider = DefaultFontProvider(false, false, false)
        val fontProgram = FontProgramFactory.createFont(font)
        fontProvider.addFont(fontProgram)
        properties.setFontProvider(fontProvider)

        val elements: List<IElement> = HtmlConverter.convertToElements(htmlContent, properties)
        val pdf: PdfDocument = PdfDocument(PdfWriter(outputFilePath))
        val document: Document = Document(pdf, PageSize.A4)

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
        val link = htmlService.findHyperlinks(emailContent)
        var content = htmlService.fetchContentFromUrl(link[0])
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
