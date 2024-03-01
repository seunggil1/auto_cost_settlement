package com.seunggil.auto_cost_settlement.service.settlement

import org.springframework.stereotype.Service
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class BaeminService {
    fun extractMoney(htmlContent : String) : Int {
        val doc = Jsoup.parse(htmlContent)
        val totalRows = doc.select("tr").filter { it -> it.text().contains("합계") }
        var totalSum = 0

        totalRows.forEach { row ->
            val totalAmountStringBuilder = StringBuilder()
            row.select("td").forEach { td ->
                td.text().trim().takeIf { it.isNotEmpty() }?.let { number ->
                    totalAmountStringBuilder.append(number)
                }
            }
            totalSum += totalAmountStringBuilder.toString().toIntOrNull() ?: 0
        }

        return totalSum
    }

    fun extractDate(htmlContent: String) : LocalDateTime? {
        val doc = Jsoup.parse(htmlContent)
        val rows = doc.select("table.tb_type1 tr")

        for (row in rows) {
            // "결제일시"가 포함된 행을 찾습니다.
            if (row.text().contains("결제일시")) {
                val nextRow = row.nextElementSibling() // "결제일시"가 포함된 행의 다음 행
                nextRow?.let {
                    val dateTimeString = it.select("td").first()?.text() // 결제일시 값을 가져옵니다.
                    dateTimeString?.let { dateTime ->
                        // 날짜 및 시간을 LocalDateTime 형태로 파싱합니다.
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        return LocalDateTime.parse(dateTime, formatter)
                    }
                }
                break
            }
        }
        return null
    }
}
