package com.seunggil.auto_cost_settlement.service


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
import org.springframework.stereotype.Service
import java.io.*


@Service
class PdfService {
    // reference : https://mchch.tistory.com/116
    @Throws(IOException::class)
    fun renderHtmlToPdf(htmlContent: String, outputFilePath: String): Boolean {
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
}
