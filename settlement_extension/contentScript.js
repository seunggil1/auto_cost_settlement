function extractDetailsAndPost() {
    const units = document.querySelectorAll('div.section_ttl.setsection_ttl9, div[name^="cardUseDetail"]');

    for (let i = 0; i < units.length; i += 2) {
        const sectionTtl = units[i];
        const cardUseDetail = units[i + 1];
        const dateSpan = sectionTtl.querySelector('h3.tit_dsc.tit_dsc3 span').innerText;
        const amountDiv = cardUseDetail.querySelector('div[name="enteredAmount"]').innerText;

        // HTTP POST 요청 로직 구현
        fetch('/testapi', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ date: dateSpan, amount: amountDiv }),
        })
            .then(response => response.blob())
            .then(blob => {
                // Blob을 File 객체로 변환
                const file = new File([blob], "details.pdf", { type: "application/pdf" });

                // fileUpload 함수를 호출하는 로직으로 변경
                // 이 예시에서는 'cardAttachFileDiv1'이라는 ID를 가진 div에 파일을 첨부한다고 가정합니다.
                const cardAttachFileDiv = document.getElementById("cardAttachFileDiv1");
                fileUpload(file, 'cardAttachFileDiv1'); // 가정: fileUpload 함수가 적절히 정의되어 있음
            })
            .catch(error => console.error('Error:', error));
    }
}

// 페이지 로드 완료 후 함수 실행
window.addEventListener('load', extractDetailsAndPost);
