package me.iseunghan.todolist.controller.user;

import me.iseunghan.todolist.common.ApiResponse;
import me.iseunghan.todolist.common.AuthUtils;
import me.iseunghan.todolist.common.LoginUser;
import me.iseunghan.todolist.model.dto.*;
import me.iseunghan.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserAccountApiController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public ApiResponse<RetrieveAccountResponse<PublicAccountDto>> getAccount(@PageableDefault Pageable pageable) {
        RetrieveAccountResponse<PublicAccountDto> accounts = accountService.findAll_USER(pageable);

        return ApiResponse.<RetrieveAccountResponse<PublicAccountDto>>of()
                .success(true)
                .error(null)
                .content(accounts)
                .build();
    }

    @GetMapping("/accounts/{username}")
    public ApiResponse<RetrieveMyAccountResponse> getMyAccount(@PathVariable String username, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        RetrieveMyAccountResponse response = accountService.findMyAccount(username);

        return ApiResponse.<RetrieveMyAccountResponse>of()
                .success(true)
                .error(null)
                .content(response)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts")
    public ApiResponse<CreateAccountResponse> createAccount(@RequestBody @Valid CreateAccountRequest request) {
        CreateAccountResponse response = accountService.addAccount(request);

        return ApiResponse.<CreateAccountResponse>of()
                .success(true)
                .error(null)
                .content(response)
                .build();
    }

    @PatchMapping("/accounts/{username}")
    public ApiResponse<Long> updateAccount(@PathVariable String username, @RequestBody UpdateAccountRequest accountRequest, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        Long id = accountService.updateAccount(username, accountRequest);

        return ApiResponse.<Long>of()
                .success(true)
                .error(null)
                .content(id)
                .build();
    }

    @DeleteMapping("/accounts/{username}")
    public ApiResponse<Void> deleteAccount(@PathVariable String username, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        accountService.deleteAccount(username);

        return ApiResponse.<Void>of()
                .success(true)
                .error(null)
                .content(null)
                .build();
    }
}
