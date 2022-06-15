
$(function () {
    const $todos = $('.list-group');
    const $userName = $('#user').text();
    const $baseURL = "/user/accounts/" + $userName + "/todolist";

     console.log('username: ' + $userName + '의 할일 리스트입니다.');

    const todoTemplate = "" +
        "<li class=\"list-group-item\" li-data-id={{id}}>" +
        "{{title}}" +
        "<span class=\"delete\" data-id={{id}}>✘</span>" +
        "</li>" +
        "";

    const todoTemplate2 = "" +
        "<li class=\"list-group-item checked\" li-data-id={{id}}>" +
        "{{title}}" +
        "<span class=\"delete\" data-id={{id}}>✘</span>" +
        "</li>" +
        "";

    // Mustache 템플릿 엔진을 사용해서 html 코드 생성을 한다.
    function addTodo(todo) {
        if (todo.status === 'NEVER') {
            $todos.append(Mustache.render(todoTemplate, todo));
        } else {
            $todos.append(Mustache.render(todoTemplate2, todo));
        }
    }

    /* Pagination Code */
    const $pagination = $('.pagination');
    const pageTemplate =
        "<li class=\"{{first_btn_class}}\"><button class=\"page-link\" id='first-btn' href=\"{{first_link}}\"><<</button></li>\n" +
        "<li class=\"{{prev_btn_class}}\"><button class=\"page-link\" id='prev-btn' href=\"{{prev_link}}\"><</button></li>\n" +
        "<li class=\"page-item active\"><button class=\"page-link here\" href=\"#\">{{number}}</button></li>\n" +
        "<li class=\"{{next_btn_class}}\"><button class=\"page-link\" id='next-page' href=\"{{next_link}}\">></button></li>" +
        "<li class=\"{{last_btn_class}}\"><button class=\"page-link\" id='last-page' href=\"{{last_link}}\">>></button></li>";

    function addPagination(pageObject) {
        console.log("[page] : ", pageObject);

        // make link
        pageObject.first_link = $baseURL;
        pageObject.prev_link = $baseURL + '?page=' + (pageObject.number + 1);
        pageObject.next_link = $baseURL + '?page=' + (pageObject.totalPages - 1);

        if(pageObject.number > 0) {
            pageObject.prev_link = $baseURL + '?page=' + (pageObject.number - 1);
        } else {
            pageObject.prev_btn_class = 'page-item disabled';
        }

        if(pageObject.number < pageObject.totalPages - 1) {
            pageObject.next_link = $baseURL + '?page=' + (pageObject.number + 1);
        } else {
            pageObject.next_btn_class = 'page-item disabled';
        }

        pageObject.first_btn_class = 'page-item';
        pageObject.last_btn_class = 'page-item';

        // disable btn
        if(pageObject.first) {
            pageObject.first_btn_class = "page-item disabled";
        }
        if(pageObject.last) {
            pageObject.last_btn_class = "page-item disabled";
        }

        $pagination.append(Mustache.render(pageTemplate, pageObject));
    }

    // init
    $.ajax({
        type: 'GET', // default 값이 GET
        url: $baseURL,

        //서버의 응답데이터가 클라이언트에게 도착하면 자동으로 실행되는함수(콜백)
        success: function (result) {	// 꼭 result로 설정하는건 아니고, 내가 맘대로 정해줘도 된다!!
            console.log("success");
            var todoList = result.content;
            console.log(result);

            for (var i = 0; i < todoList.length; i++) {
                addTodo(todoList[i]);  // 함수로 넘기면 알아서 템플릿이 처리해줌.
            }
            // let linkObject = result._links;
            // let pageObject = result.page;
            let pageObject = {
                first: result.first,
                last: result.last,
                number: result.number,
                totalPages: result.totalPages
            }
            addPagination(pageObject);
        }, error: function (a, b, c) {
//            console.log(a, b, c);
        }
    });

    $('#add-btn').click(function () {

        if (validateForm()) {
            $.ajax({
                url: $baseURL,
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify({
                    title: $('#input-title').val()
                }),

                //서버의 응답데이터가 클라이언트에게 도착하면 자동으로 실행되는함수(콜백)
                success: function (result) {
                    console.log('\"' + result.title + '\" 이(가) 추가 되었습니다.');
                    addTodo(result);
                    $('#input-title').val(''); // input 공백으로 초기
                },
                fail: function (result) {
                    alert('통신 실패');
                }
            });
        }
    });

    // page link 이동
    $(document).on('click', '.page-link', function (e) {
        e.preventDefault();
        console.log("-> " + $(this).attr('href'));
        const $url = $(this).attr('href');
        if($url === '' || $url === '#' || $url === undefined) return;

        $.ajax({
            url: $url,
            type: 'GET',
            contentType: 'application/json',

            success: function (result) {
                // area remove
                const $_list_group = document.getElementById('list-group');
                const $_pagination = document.getElementById('pagination');

                while($_list_group.hasChildNodes()) {
                    $_list_group.removeChild($_list_group.firstChild);
                }
                while($_pagination.hasChildNodes()) {
                    $_pagination.removeChild($_pagination.firstChild);
                }

//                console.log("success");
                var todoList = result.content;
                console.log(result);

                for (var i = 0; i < todoList.length; i++) {
                    addTodo(todoList[i]);  // 함수로 넘기면 알아서 템플릿이 처리해줌.
                }
                // let linkObject = result._links;
                // let pageObject = result.page;
                let pageObject = {
                    first: result.first,
                    last: result.last,
                    number: result.number,
                    totalPages: result.totalPages
                }
                addPagination(pageObject);
            },
            fail: function (result) {
                alert('통신 실패');
            }
        });
    });

    let keydown = false;    // 중복 발생 방지하기 위한 변수
    const $input = document.getElementById('input-title');
    $input.addEventListener('keydown', function (e) {
        if (keydown) return;
        else {
            // key가 Enter이고, 빈칸 입력이 아닐 때 실행!
            if (e.key == 'Enter' && validateForm()) {
                keydown = true;     // 엔터키가 눌렸고, 빈칸 체크도 완료했을 때, true로 값을 줘서 중복 실행 방지!
                $.ajax({
                    url: $baseURL,
                    type: 'POST',
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        title: $('#' + $input.id).val()
                    }),

                    success: function (result) {
                        console.log(result);
                        addTodo(result);
                        $('#input-title').val(''); // input 공백으로 초기
                        keydown = false;
                    }
                });
            }
        }
    });

    /**
     *  by.승한 - 해당 할일을 클릭하게 되면, 현재 상태의 반대 상태로 변경합니다.
     *
     *  ul.li태그안에는 각 할일의 id값이 들어있는 data-id가 있습니다.
     *  클릭을 하게 되면 해당 li태그안에 있는 data-id값을 url에 붙여서 PATCH 방식으로 ajax가 통신을 하게됩니다.
     *
     *  삭제 버튼과 이벤트가 겹치게 되어, 중복 이벤트 실행을 방지하기 위해서 이벤트 발생요소가 수정하려는 것이 맞는지 체크를 하게 됩니다.
     *
     *  수정이 정상적으로 이루어져서, 서버에서 200 응답이 오게 되면 해당 태그에 checked 클래스를 부여해줍니다.
     *  (checked 태그는 해당 할일이 완료되었다는 시각적으로 표시를 하게 끔 디자인 되었습니다.)
     */

    $todos.delegate('li', 'click', function (e) {
        var $li = $(this).closest('li');

        /* 삭제버튼 이벤트 발생요소가 현재 요소인지 체크 */
        if(e.target === $(this).get(0)) {

            $.ajax({
                url: $baseURL + '/' + $(this).attr('li-data-id'), // 여기서 아까 저장한 변수를 이용해 url 생성
                type: 'PATCH',

                success: function (result) {
                    var stat = result.status === 'NEVER' ? 'NEVER' : 'DONE';
                    console.log(result.title + '의 할일상태가 ' + stat + '으로 변경되었습니다.');
                    $li.toggleClass('checked');
//                    $li.toggleClass('active');
                },
                error: function (result) {
                    // alert('통신 실패');
                },
            });
        }
    });

    /**
     *  by.승한 - 삭제 버튼을 클릭하게 되면, span 태그안에 해당 할일의 id값이 들어있는 data-id가 있다.
     *
     *  클릭을 하게 되면 해당 li태그안에 있는 data-id값을 url에 붙여서 PATCH 방식으로 ajax가 통신을 하게됩니다.
     *  서버로 부터 200 응답을 받고, 해당 li태그를 .remove를 이용해 삭제해줍니다.
     */
    $todos.delegate('.delete', 'click', function () {
        console.log('id : ' + $(this).attr('data-id') + ' 가 삭제됩니다.');
        var $li = $(this).closest('li');

        $.ajax({
            url: $baseURL + '/' + $(this).attr('data-id'), // 여기서 아까 저장한 변수를 이용해 url 생성
            type: 'DELETE',

            success: function (result) {
                // alert('통신 성공');
                $li.fadeOut(300, function (){
                    $li.remove();
                });
            },
            error: function (result) {
                // alert('통신 실패');
            },
        });
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

function removeChild(e) {
    console.log(e.hasChildNodes());
}
