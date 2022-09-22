package me.iseunghan.todolist.controller.user;

import me.iseunghan.todolist.common.AuthUtils;
import me.iseunghan.todolist.common.LoginUser;
import me.iseunghan.todolist.common.ApiResponse;
import me.iseunghan.todolist.model.dto.*;
import me.iseunghan.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
public class UserAccountApiController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public ResponseEntity getAccount(@PageableDefault Pageable pageable) {
        RetrieveAccountResponse<PublicAccountDto> accounts = accountService.findAll_USER(pageable);

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{username}")
    public ResponseEntity getMyAccount(@PathVariable String username, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        AccountDto dto = accountService.findMyAccount(username);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/accounts")
    public ResponseEntity createAccount(@RequestBody
                                        @Valid CreateAccountRequest request) {
        CreateAccountResponse response = accountService.addAccount(request);

        URI uri = linkTo(methodOn(UserAccountApiController.class).createAccount(request)).withSelfRel().toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/accounts/{username}")
    public ResponseEntity updateAccount(@PathVariable String username, @RequestBody UpdateAccountRequest accountRequest, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        Long id = accountService.updateAccount(username, accountRequest);

        return ResponseEntity.ok(Map.of("id", id));
    }

    @DeleteMapping("/accounts/{username}")
    public ResponseEntity deleteAccount(@PathVariable String username, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        Long id = accountService.deleteAccount(username);

        return ResponseEntity.ok((Map.of("id", id)));
    }
}
