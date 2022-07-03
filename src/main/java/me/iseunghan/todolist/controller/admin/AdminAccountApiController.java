package me.iseunghan.todolist.controller.admin;

import me.iseunghan.todolist.model.dto.AdminAccountDto;
import me.iseunghan.todolist.model.dto.RetrieveAccountResponse;
import me.iseunghan.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminAccountApiController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public ResponseEntity getAccounts(@PageableDefault Pageable pageable) {
        RetrieveAccountResponse<AdminAccountDto> accountList = accountService.findAll_ADMIN(pageable);

        return ResponseEntity.ok(accountList);
    }

    @GetMapping("/accounts/{username}")
    public ResponseEntity getAccount(@PathVariable String username) {
        AdminAccountDto accountDto = accountService.findAccount_ADMIN(username);

        return ResponseEntity.ok(accountDto);
    }

    @DeleteMapping("/accounts/{username}")
    public ResponseEntity deleteAccount(@PathVariable String username) {
        Long id = accountService.deleteAccount(username);

        return ResponseEntity.ok(Map.of("id", id));
    }
}
