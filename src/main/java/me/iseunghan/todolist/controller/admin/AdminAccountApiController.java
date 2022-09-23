package me.iseunghan.todolist.controller.admin;

import me.iseunghan.todolist.common.ApiResponse;
import me.iseunghan.todolist.model.dto.AdminAccountDto;
import me.iseunghan.todolist.model.dto.RetrieveAccountResponse;
import me.iseunghan.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminAccountApiController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public ApiResponse<RetrieveAccountResponse<AdminAccountDto>> getAccounts(@PageableDefault Pageable pageable) {
        RetrieveAccountResponse<AdminAccountDto> accountList = accountService.findAll_ADMIN(pageable);

        return ApiResponse.<RetrieveAccountResponse<AdminAccountDto>>of()
                .success(true)
                .error(null)
                .content(accountList)
                .build();
    }

    @GetMapping("/accounts/{username}")
    public ApiResponse<AdminAccountDto> getAccount(@PathVariable String username) {
        AdminAccountDto accountDto = accountService.findAccount_ADMIN(username);

        return ApiResponse.<AdminAccountDto>of()
                .success(true)
                .error(null)
                .content(accountDto)
                .build();
    }

    @DeleteMapping("/accounts/{username}")
    public ApiResponse<Void> deleteAccount(@PathVariable String username) {
        accountService.deleteAccount(username);

        return ApiResponse.<Void>of()
                .success(true)
                .error(null)
                .content(null)
                .build();
    }
}
