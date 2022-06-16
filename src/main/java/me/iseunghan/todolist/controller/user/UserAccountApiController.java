package me.iseunghan.todolist.controller.user;

import me.iseunghan.todolist.model.Account;
import me.iseunghan.todolist.model.dto.AccountDto;
import me.iseunghan.todolist.model.dto.CreateAccountRequest;
import me.iseunghan.todolist.model.dto.PublicAccountDto;
import me.iseunghan.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
public class UserAccountApiController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public ResponseEntity getAccount(@PageableDefault Pageable pageable) {
        Page<PublicAccountDto> accounts = accountService.findAll_USER(pageable);

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{username}")
    public ResponseEntity getMyAccount(@PathVariable String username) {
        Account account = accountService.findMyAccount(username);

        return ResponseEntity.ok(account);
    }

    @PostMapping("/accounts")
    public ResponseEntity createAccount(@RequestBody
                                        @Valid CreateAccountRequest request) {
        Account account = accountService.addAccount(request);

        URI uri = linkTo(methodOn(UserAccountApiController.class).createAccount(request)).withSelfRel().toUri();

        return ResponseEntity.created(uri).body(account);
    }

    @PatchMapping("/accounts/{username}")
    public ResponseEntity updateAccount(@PathVariable String username, @RequestBody AccountDto accountDto) {
        Long id = accountService.updateAccount(username, accountDto);

        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/accounts/{username}")
    public ResponseEntity deleteAccount(@PathVariable String username) {
        accountService.deleteAccount(username);

        return ResponseEntity.ok().build();
    }
}
