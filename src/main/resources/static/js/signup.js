$(function () {

        $("#signup_form").submit(function(e) {

            e.preventDefault();

            let jsonData = JSON.stringify({
                username: $('#inputUsername').val(),
                email: $('#inputEmail').val(),
                nickname: $('#inputNickname').val(),
                password: $('#inputPassword').val(),
            });

            $.ajax({
                url: "/user/accounts",
                method: 'POST',
                data: jsonData,
                contentType: 'application/json',
                success: function (result) {
                    alert('회원가입을 환영합니다.');
                    location.href = "/";
                },
                error: function (error) {
                    if(error.status === 400) {
                        alert('중복된 username 입니다. 다시 시도해주세요.');
                    } else if (error.status === 500) {
                        alert('필수값을 확인해주세요');
                    } else {
                        alert('Server Error');
                    }
                }
            });
        });
});