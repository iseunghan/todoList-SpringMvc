/**
 * < ajax 주요 속성 >
 *
 * id값을 매핑할땐, $('#id값') 이런식으로 한다.
 * $('#btn').click(function(){
 *     $.ajax({
 *        url: 'url',           // 전송할 url
 *        dataType: 'json',     // 서버가 리턴하는 데이터의 타입
 *        type: 'POST',         // 서버로 전송할 메소드 타입
 *        contentType: 'application/json; charset=utf-8', // 서버로 전송할 데이터의 타입
 *        data: {               // 서버로 보낼 데이터 (현재는 json형식)
 *            title: $('#input-title').val()
 *        },
 *
 *        success: function(result){
 *            성공 시 타게되는 success 함수
 *        },
 *        fail: function(result){
 *            실패 시 타게되는 fail 함수
 *        }
 *     });
 * }
 */


$(function () {
    var $todos = $('.ajax-todo-lists');

    var todoTemplate = "" +
        "<li li-data-id={{id}}>" +
        "{{title}}" +
        "<span class=\"delete\" data-id={{id}}>✘</span>" +
        "</li>" +
        "";

    var todoTemplate2 = "" +
        "<li class=\"checked\" li-data-id={{id}}>" +
        "{{title}}" +
        "<span class=\"delete\" data-id={{id}}>✘</span>" +
        "</li>" +
        "";

    // Mustache 템플릿 엔진을 사용해서 html 코드 생성을 한다.
    function addTodo(todo) {
        if (todo.status == 'NEVER') {
            $todos.append(Mustache.render(todoTemplate, todo));
        } else {
            $todos.append(Mustache.render(todoTemplate2, todo));
        }
    }

    /**
     * by.승한 - 홈 화면에 할일목록을 뿌려주기 위해 GET 방식으로 ajax 통신을 합니다.
     *
     *  처음 홈 화면에 접속했을 때, ajax통신으로 서버에서 받은 응답 본문에 있는 json형태의
     *  할일 리스트를 for문으로 돌면서 Mustache 템플릿 엔진에게 넘겨줍니다.
     *
     */
    $.ajax({
        type: 'GET', // default 값이 GET
        url: 'http://localhost:8080/todoLists',

        success: function (result) {	// 꼭 result로 설정하는건 아니고, 내가 맘대로 정해줘도 된다!!
            var todoLists = result._embedded.todoResourceList;

            for (var i = 0; i < todoLists.length; i++) {
                addTodo(todoLists[i]);  // 함수로 넘기면 알아서 템플릿이 처리해줌.
            }
        }
    });

    /**
     *  by.승한 - 할일을 추가하기 위해 버튼을 클릭하면, POST 방식으로 ajax 통신을 합니다.
     *
     *  입력받은 할일을 json 형태로 변환하여 서버로 보냅니다.
     *  서버에서는 application/json 형태로 응답이 오게 됩니다.
     *  응답 본문에 실린 todoItem 객체를 Mustache 템플릿에게 넘겨줍니다.
     *
     */
    $('#add-btn').click(function () {

        if (validateForm()) {
            $.ajax({
                url: 'http://localhost:8080/todoLists',
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify({
                    title: $('#input-title').val()
                }),

                success: function (result) {
                    console.log('\"' + result.title + '\" 이(가) 추가 되었습니다.');
                    addTodo(result);
                },
                fail: function (result) {
                    alert('통신 실패');
                }
            });
        }
    });
});


/**
 * by.승한 - 할일을 추가할 때, 빈칸을 입력 했을 경우 경고창 발생.
 *
 * return 빈칸이 아닐 때, true를 리턴
 */
function validateForm() {
    var title =  $('#input-title').val();
    if (title == "" || title == null || title == " ") {
        alert("ℹ️ 빈칸을 입력할 수 없습니다.😅 다시 입력해 주세요.");
        return false;
    }
    return true;
}
