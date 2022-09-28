$(function() {
    const $userName = $('#user').text();
    const $baseURL = "/admin/accounts";
    const $accountArea = $('.account-result');

    const accountTemplate = '<div class="account-box d-md-flex align-items-center justify-content-between mb-30">' +
                                                     '<div class="job-left my-4 d-md-flex align-items-center flex-wrap">' +
                                                         '<div class="account-content">' +
                                                             '<h5 class="text-center text-md-left">{{username}}</h5>' +
                                                             '<ul class="d-md-flex flex-wrap text-capitalize ff-open-sans">' +
                                                                 '<li class="mr-md-4">' +
                                                                     '<i class="bi bi-envelope mr-2"></i>{{email}}' +
                                                                 '</li>' +
                                                                 '<li class="mr-md-4">' +
                                                                     '<i class="bi bi-emoji-smile mr-2"></i> {{nickname}}' +
                                                                 '</li>' +
                                                                 '<li class="mr-md-4">' +
                                                                     '<i class="bi bi-command mr-2"></i> {{role}}' +
                                                                 '</li>' +
                                                                 '<li class="mr-md-4">' +
                                                                     '<i class="bi bi-card-checklist mr-2"></i> todoList: {{todoSize}}' +
                                                                 '</li>' +
                                                             '</ul>' +
                                                         '</div>' +
                                                     '</div>' +
//                                                     '<div class="job-right my-4 flex-shrink-0">' +
//                                                         '<a href="#" class="btn d-block w-100 d-sm-inline-block btn-light">Detail</a>' +
//                                                     '</div>' +
                                                 '</div>';

    function addAccountList(accountList) {
        for(var i=0; i<accountList.length; i++) {
            $accountArea.append(Mustache.render(accountTemplate, accountList[i]));
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

        // make link
        pageObject.first_link = $baseURL;
        pageObject.prev_link = $baseURL + '?page=' + (pageObject.number - 1);
        pageObject.next_link = $baseURL + '?page=' + (pageObject.number + 1);
        pageObject.last_link = $baseURL + '?page=' + (pageObject.totalPages - 1);

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

    $.ajax({
        url: $baseURL,
        method: 'GET',

        success: function(result){
//            console.log(result);
            $('#list-size').html(result.totalElements);
            addAccountList(result.content);

            // pageable
            var pageObject = {
                first: result.first,
                last: result.last,
                number: result.number,
                totalPages: result.totalPages,
                totalSize: result.totalElements
            }

            addPagination(pageObject);
        },
        error: function(error) {
            console.log(error);
        }
    });

    $("#search-form").submit(function(e) {

        e.preventDefault();

        // form check
        let $username = $('#username').val();
        if($username === '' || $username === undefined || $username === null) {
            return ;
        }

        // remove account Area
        $accountArea.html('');

        $.ajax({
            url: "/admin/accounts/" + $username,
            method: 'GET',
            success: function (result) {
                addAccountList([result]);
            },
            error: function (error) {
                // error
                if(error.status === 404) {
                    $accountArea.html('해당 username은 존재하지 않습니다.');
                }
            }
        });
    });

    function removeItems() {
        // area remove
//        const $accountList = document.getElementById('account-result');
        const $pagination = document.getElementById('pagination');

        while($accountArea.hasChildNodes()) {
            $accountArea.removeChild(accountArea.firstChild);
        }
        while($pagination.hasChildNodes()) {
            $pagination.removeChild($pagination.firstChild);
        }
    }

});