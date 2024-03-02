package com.seunggil.auto_cost_settlement.service.settlement

import org.springframework.stereotype.Service
import javax.mail.Flags.Flag
import javax.mail.Folder
import javax.mail.Session
import javax.mail.Store
import javax.mail.internet.InternetAddress

@Service
class EmailService {

    fun readEmails(host: String, user: String, password: String): MutableList<String> {
        val contents = mutableListOf<String>()

        try {
            // 1. 메일 서버 설정
            val properties = System.getProperties()
            properties.setProperty("mail.imap.host", host)
            properties.setProperty("mail.imap.port", "993")
            properties.setProperty("mail.imap.starttls.enable", "true")

            // 2. 세션 생성
            val emailSession = Session.getDefaultInstance(properties)

            // 3. IMAP 스토어 생성 및 연결
            val store: Store = emailSession.getStore("imaps")
            store.connect(host, user, password)

            // 4. 폴더 열기 (INBOX)
            val emailFolder: Folder = store.getFolder("INBOX")
            emailFolder.open(Folder.READ_WRITE)

            val totalMessages = emailFolder.messageCount

            // 최신 메시지부터 100개만 가져오기
            val start = if (totalMessages - 100 > 0) totalMessages - 100 else 1
            val end = totalMessages
            val messages = emailFolder.getMessages(start, end)
            val unreadMessages = messages.filter { !it.flags.contains(Flag.SEEN) }

            val latestUnreadMessages = unreadMessages.sortedByDescending { it.sentDate }.take(100)
            val woowahanMessages = latestUnreadMessages.filter {
                (it.from[0] as InternetAddress).address == "noreply@woowahan.com"
            }


            // 5. 메시지 읽기
            for (message in woowahanMessages) {
                println("Email Number: ${message.messageNumber}")
                println("Subject: ${message.subject}")
                try {
                    message.setFlag(Flag.SEEN, true)
                    contents.add(message.content.toString())

                } catch (e: Exception) {

                }
            }

            // 6. 폴더 및 스토어 닫기
            emailFolder.close(true)
            store.close()


        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

        }
        return contents
    }
}
