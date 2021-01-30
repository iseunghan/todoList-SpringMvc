/**
 * by.승한 - form에서 빈칸을 입력 했을 경우 경고창 발생.
 */
function validateForm() {
    var x = document.forms["todoForm"]["title"].value;
    if (x == "" || x == null) {
        alert("ℹ️ 빈칸을 입력할 수 없습니다.😅 다시 입력해 주세요 ");
        return false;
    }
}