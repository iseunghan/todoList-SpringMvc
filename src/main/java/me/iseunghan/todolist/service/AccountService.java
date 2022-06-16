package me.iseunghan.todolist.service;

import lombok.RequiredArgsConstructor;
import me.iseunghan.todolist.exception.AccountDuplicateException;
import me.iseunghan.todolist.exception.AccountNotFoundException;
import me.iseunghan.todolist.model.Account;
import me.iseunghan.todolist.model.AccountAdapter;
import me.iseunghan.todolist.model.AccountRole;
import me.iseunghan.todolist.model.dto.AccountDto;
import me.iseunghan.todolist.model.dto.AdminAccountDto;
import me.iseunghan.todolist.model.dto.CreateAccountRequest;
import me.iseunghan.todolist.model.dto.PublicAccountDto;
import me.iseunghan.todolist.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));
        return new AccountAdapter(account);
    }

    @Transactional
    public Account addAccount(CreateAccountRequest request) {
        // check duplicate account
        isDuplicateAccount(request.getUsername());

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .roles(Set.of(AccountRole.USER))
                .build();

        return accountRepository.save(account);
    }

    /* ADMIN_ROLE: 관리자가 모든 회원 정보 조회 */
    public Page<AdminAccountDto> findAll_ADMIN(Pageable pageable) {
        Page<Account> accountPage = accountRepository.findAll_ADMIN(pageable);

        List<AdminAccountDto> adminAccountDtoList = accountPage.getContent().stream()
                .map(a -> AdminAccountDto.builder()
                        .username(a.getUsername())
                        .email(a.getEmail())
                        .nickname(a.getNickname())
                        .role(a.getRolesToString())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<AdminAccountDto>(adminAccountDtoList, pageable, accountPage.getTotalElements());
    }

    public Page<PublicAccountDto> findAll_USER(Pageable pageable) {
        Page<Account> account = accountRepository.findAll_USER(pageable);

        List<PublicAccountDto> publicAccountDtoList = account.getContent().stream()
                .map(a -> PublicAccountDto.builder()
                            .username(a.getUsername())
                            .email(a.getEmail())
                            .nickname(a.getNickname())
                            .build())
                .collect(Collectors.toList());

        return new PageImpl<PublicAccountDto>(publicAccountDtoList, pageable, account.getTotalElements());
    }

    public Account findMyAccount(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));
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

        return new PublicAccountDto(account.getUsername(),
                                    account.getEmail(),
                                    account.getNickname(),
                                    account.getTodoList().size());
    }

    @Transactional
    public Long updateAccount(String username, AccountDto accountDto) {
        Account account = findMyAccount(username);

        if (accountDto.getUsername() != null) {
            isDuplicateAccount(accountDto.getUsername());
            account.setUsername(accountDto.getUsername());
        }
        if (accountDto.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        }
        if (accountDto.getEmail() != null) {
            account.setEmail(accountDto.getEmail());
        }
        if (accountDto.getNickname() != null) {
            account.setNickname(accountDto.getNickname());
        }

        return account.getId(); // dirty checking!
    }

    public void deleteAccount(String username) {
        Account account = findMyAccount(username);
        accountRepository.delete(account);
    }

    public void isDuplicateAccount(String username) {
        Optional<Account> account = accountRepository.findByUsername(username);

        if (account.isPresent()) throw new AccountDuplicateException(username);
    }
}
