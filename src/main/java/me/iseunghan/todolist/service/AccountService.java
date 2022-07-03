package me.iseunghan.todolist.service;

import lombok.RequiredArgsConstructor;
import me.iseunghan.todolist.exception.AccountDuplicateException;
import me.iseunghan.todolist.exception.AccountNotFoundException;
import me.iseunghan.todolist.model.Account;
import me.iseunghan.todolist.model.AccountAdapter;
import me.iseunghan.todolist.model.AccountRole;
import me.iseunghan.todolist.model.dto.*;
import me.iseunghan.todolist.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));
        return new AccountAdapter(account);
    }

    @Transactional
    public CreateAccountResponse addAccount(CreateAccountRequest request) {
        // check duplicate account
        isDuplicateAccount(request.getUsername());

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .roles(Set.of(AccountRole.USER))
                .build();

        Account save = accountRepository.save(account);
        return modelMapper.map(save, CreateAccountResponse.class);
    }

    /* ADMIN_ROLE: 관리자가 모든 회원 정보 조회 */
    public RetrieveAccountResponse<AdminAccountDto> findAll_ADMIN(Pageable pageable) {
        Page<Account> accountPage = accountRepository.findAll_ADMIN(pageable);

        return RetrieveAccountResponse.<AdminAccountDto>builder()
                .accountList(accountPage.getContent().stream()
                        .map(a -> AdminAccountDto.builder()
                                .username(a.getUsername())
                                .email(a.getEmail())
                                .nickname(a.getNickname())
                                .role(a.getRolesToString())
                                .todoSize(a.getTodoList().size())
                                .build())
                        .collect(Collectors.toList()))
                .pageable(PageDto.builder()
                        .number(accountPage.getNumber())
                        .totalPages(accountPage.getTotalPages())
                        .totalElements(accountPage.getTotalElements())
                        .first(accountPage.isFirst())
                        .last(accountPage.isLast())
                        .build())
                .build();
    }

    public RetrieveAccountResponse<PublicAccountDto> findAll_USER(Pageable pageable) {
        Page<Account> accountPage = accountRepository.findAll_USER(pageable);

        return RetrieveAccountResponse.<PublicAccountDto>builder()
                .accountList(accountPage.getContent().stream()
                        .map(a -> PublicAccountDto.builder()
                                .username(a.getUsername())
                                .email(a.getEmail())
                                .nickname(a.getNickname())
                                .build())
                        .collect(Collectors.toList()))
                .pageable(PageDto.builder()
                        .number(accountPage.getNumber())
                        .totalPages(accountPage.getTotalPages())
                        .totalElements(accountPage.getTotalElements())
                        .first(accountPage.isFirst())
                        .last(accountPage.isLast())
                        .build())
                .build();
    }

    public AccountDto findMyAccount(String username) {
        Account account = findByUsername(username);

        return AccountDto.builder()
                .id(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .password(account.getPassword())
                .nickname(account.getNickname())
                .roles(account.getRolesToString())
                .todoList(null)
                .build();
    }

    /* ADMIN_ROLE: 관리자가 특정 회원 정보 조회 */
    public AdminAccountDto findAccount_ADMIN(String username) {
        Account account = accountRepository.findByUsernameWithTodoList(username)
                .orElseThrow(() -> new AccountNotFoundException(username));

        return AdminAccountDto.builder()
                .username(account.getUsername())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .role(account.getRolesToString())
                .todoSize(account.getTodoList().size())
                .build();
    }

    /* USER_ROLE: 모든 사용자가 보는 다른 회원의 공개되도 안전한 정보 */
    public PublicAccountDto findAccount_USER(String username) {
        Account account = accountRepository.findByUsernameWithTodoList(username)
                .orElseThrow(() -> new AccountNotFoundException(username));

        return PublicAccountDto.builder()
                .username(account.getUsername())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .todoSize(account.getTodoList().size())
                .build();
    }

    @Transactional
    public Long updateAccount(String username, UpdateAccountRequest accountRequest) {
        Account account = findByUsername(username);

        if (accountRequest.getUsername() != null) {
            isDuplicateAccount(accountRequest.getUsername());
            account.setUsername(accountRequest.getUsername());
        }
        if (accountRequest.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
        }
        if (accountRequest.getEmail() != null) {
            account.setEmail(accountRequest.getEmail());
        }
        if (accountRequest.getNickname() != null) {
            account.setNickname(accountRequest.getNickname());
        }

        return account.getId(); // dirty checking!
    }

    public Long deleteAccount(String username) {
        Account account = findByUsername(username);

        accountRepository.delete(account);

        return account.getId();
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));
    }

    public void isDuplicateAccount(String username) {
        accountRepository.findByUsername(username)
                .ifPresent(a -> {
                    throw new AccountDuplicateException(username);
                });
    }
}
