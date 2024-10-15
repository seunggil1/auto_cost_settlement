package com.seunggil.auto_cost_settlement.service.settlement

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Service
class BaeminService {
    fun extractMoney(htmlContent: String): Long {
        val doc = Jsoup.parse(htmlContent)
        val totalRows = doc.select("td[data-id='__react-email-column']").filter { it -> it.text().contains("합계") }

        var totalSum = 0L
        totalRows.forEach { row ->
            val totalAmountStringBuilder = StringBuilder()

            val amountTd = row.nextElementSibling()
            if (amountTd != null) {
                val totalLabel = amountTd.select("p").first() // "합계"라는 텍스트를 포함하는 첫 번째 <p> 태그를 찾습니다.

                totalLabel?.text()?.replace("원", "")?.replace(",", "")?.trim().takeIf {
                    it2 -> it2?.isNotEmpty() == true
                }?.let {
                    number -> totalAmountStringBuilder.append(number)
                } // 해당 <td> 태그에서 텍스트를 가져와 "원"을 제거하고 공백을 제거합니다.

                totalSum += totalAmountStringBuilder.toString().toIntOrNull() ?: 0
            }

        }

        return totalSum
    }

    fun extractDate(htmlContent: String): ZonedDateTime? {
        val doc = Jsoup.parse(htmlContent)
        val dateTags = doc.select("p:contains(결제일자)")

        if (dateTags.isNotEmpty()) {
            val dateTimeString = dateTags.first()?.nextElementSibling()?.text()
            dateTimeString?.let { dateTime ->
                // 날짜 및 시간을 LocalDateTime 형태로 파싱합니다.
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                return LocalDateTime.parse(dateTime, formatter)
                    .atZone(ZoneId.of("Asia/Seoul")) // UTC+9는 Asia/Seoul 타임존으로 대체합니다.
            }
        }
        return null
    }
}
