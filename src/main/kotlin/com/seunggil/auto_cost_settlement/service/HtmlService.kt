package com.seunggil.auto_cost_settlement.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient


@Service
class HtmlService(private val webClient: WebClient.Builder) {

    fun findHyperlinks(html: String): List<String> {
        val document: Document = Jsoup.parse(html)
        // "a.lnk-receipt"는 <a> 태그에 "lnk-receipt" 클래스가 있는 요소를 찾습니다.
        val links = document.select("a.lnk-receipt")

        // 절대 URL 추출
        return links.mapNotNull { it.attr("abs:href") }
    }

    fun fetchContentFromUrl(url: String): String {
        return Jsoup.connect(url).get().html()
    }

    fun convertHtmlToXhtml(htmlContent: String): String {
        val document = Jsoup.parse(htmlContent)
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
        return document.html()
    }

    fun convertCssLink(htmlContent: String): String {
        var converted = htmlContent
        cssMap.forEach { (k, v) ->
            converted = converted.replace(k, v)
        }
        return converted
    }

    fun removeScriptTag(htmlContent: String): String {
        return htmlContent.replace(scriptTagRegex, "")
    }

    fun removeLinkTag(htmlContent: String): String {
        return htmlContent.replace(linkTagRegex, "")
    }

}

val scriptTagRegex = "<script[^>]*>(.*?)</script>".toRegex()
val linkTagRegex = """<link\s+[^>]*?\s*/?>""".toRegex()
val cssMap = hashMapOf(
    "<link rel=\"stylesheet\" type=\"text/css\" href=\"//img.woowahan.com/css/m/order/cardreceipt.css\">"
            to """
        <style>
        body{margin:0;padding:0;font-family:'돋움',dotum,sans-serif;font-size:14px;color:#000;line-height:1.5;}
        img{border:0 none;}
        div, p, span, ul, li, h1, h2, form, ul, li, dl, dt, dd, fieldset, input, td, th{margin:0;padding:0;}
        ul li{list-style-type:none;}
        a{color:#444;text-decoration:none;}
        a:hover{text-decoration:underline;}

        #wrap{}
        #wrap .logo{margin:30px auto;text-align:center;width:157px;height:58px;text-indent:-9999px;background:url(../../../m/order/mailing_logo.gif) no-repeat;}
        #wrap .receipt{margin:0 auto;padding:30px;width:100%;border:1px solid #ccc;box-sizing: border-box;}
        #wrap .receipt h2{margin:0 0 20px;font-size:18px;text-align:center;}
        #wrap .receipt h3{margin:30px 0 1px;font-size:13px;}
        #wrap .receipt .tb_type1{width:100%;table-layout:fixed;border-collapse:collapse;word-break:break-all;border-top:3px solid #000;border-bottom:1px solid #e5e5e5;}
        #wrap .receipt .tb_type1 th{padding:8px 0 2px;font-size:11px;font-weight:normal;text-align:left;vertical-align:top;color:#a3a3a3;}
        #wrap .receipt .tb_type1 td{padding:0 0 6px;vertical-align:top;}
        #wrap .receipt .tb_type2{width:100%;table-layout:fixed;border-collapse:collapse;word-break:break-all;border-top:3px solid #ff4200;border-bottom:1px solid #e5e5e5;border-right:1px dashed #e5e5e5;}
        #wrap .receipt .tb_type2 th{font-size:11px;font-weight:normal;text-align:left;color:#000;}
        #wrap .receipt .tb_type2 td{height:50px;border-left:1px dashed #e5e5e5;text-align:center;}
        #wrap .receipt .line{border-bottom:1px solid #e5e5e5;}
        #wrap .receipt .t_point1{color:#ff4200 !important;}
        #wrap .receipt .t_point2{color:#a3a3a3;font-size:11px;padding-bottom:5px;}
        #wrap .notice {text-align:center;font-size:12px;padding-top:25px;}
        #wrap .btn_area, .btn_area{clear:both;padding:30px 0;text-align:center;}
        #wrap .btn_area a, .btn_area a{display:inline-block;width:212px;height:77px;text-indent:-9999px;background:url(../../../m/order/btn_print.gif) no-repeat;}
        .group1 {width:50%;float:left;}
        .group2 {width:50%;float:right;}

        @media print {
            .logo,.btn_area{display:none;}
            #wrap{padding-top:35px;}
            #wrap .receipt{width:460px;}
            #wrap .receipt h2{font-weight:bold;}
            #wrap .receipt .line{border-bottom:1px solid #cfcfcf;}
            #wrap .receipt .tb_type1{border-bottom:1px solid #cfcfcf;}
            #wrap .receipt .tb_type1 th{color:#878787;font-size:8pt;}
            #wrap .receipt .tb_type1 td{font-size:10pt;letter-spacing:1px;}
            #wrap .receipt .tb_type2{border-bottom:1px solid #cfcfcf;border-right:1px dashed #cfcfcf;}
            #wrap .receipt .tb_type2 td{border-left:1px dashed #cfcfcf;}
            #wrap .receipt .t_point1{font-weight:bold;}
            #wrap .receipt .t_point2{color:#cfcfcf;padding-bottom:6px;}
        }
    </style>
    """.trimIndent()
)