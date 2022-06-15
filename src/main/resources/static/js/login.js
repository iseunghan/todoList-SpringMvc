// $(function () {
//     $('#btn-login').click(function () {
//         login(this);
//     });
// });

function formValidate() {
    var $id = $('#input-login-username');
    var $pw = $('#input-login-password');
    console.log($id.val() + ', ' + $pw.val());

    if ($id.val() === '') {
        $id.removeClass('is-valid');
        $id.addClass('is-invalid');
    } else {
        $id.removeClass('is-invalid');
        $id.addClass('is-valid');
    }

    if ($pw.val() === '') {
        $pw.removeClass('is-valid');
        $pw.addClass('is-invalid');
    } else {
        $pw.removeClass('is-invalid');
        $pw.addClass('is-valid');
    }

    if ($id.val() === '' || $pw.val() === '') {
        return false;
    }
    return true;
}

function login(e) {
    // console.log(e);
    if ((e.type === 'click' || e.key === 'Enter') && formValidate()) {
        let $username = $('#input-login-username').val();
        let $password = $('#input-login-password').val();

        $.ajax({
            type: 'POST',
            url: '/login',
            contentType: 'application/json',
            data: JSON.stringify({
                username: $username,
                password: $password
            }),

            success: function (data, textStatus, xhr) {
                console.log(data, " / ", textStatus, " / " , xhr);
                if (xhr.status === 200) {
//                    alert('Login Success!');
                    location.href = "/";
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                console.log("error:", errorThrown);
                if (xhr.status === 401 || xhr.status === 404) {
                    alert('Check your Username or Password');
                } else {
                    alert('Sorry, please contact us!');
                }
            }
        });
    }
}