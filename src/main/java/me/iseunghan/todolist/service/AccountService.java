package me.iseunghan.todolist.service;

import lombok.RequiredArgsConstructor;
import me.iseunghan.todolist.exception.AccountDuplicateException;
import me.iseunghan.todolist.exception.AccountNotFoundException;
import me.iseunghan.todolist.model.Account;
import me.iseunghan.todolist.model.AccountAdapter;
import me.iseunghan.todolist.model.AccountRole;
import me.iseunghan.todolist.model.dto.AccountDto;
import me.iseunghan.todolist.model.dto.PublicAccountDto;
import me.iseunghan.todolist.repository.AccountRepository;
import org.springframework.data.domain.Page;
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
    public Account addAccount(AccountDto accountDto) {
        // check duplicate account
        isDuplicateAccount(accountDto.getUsername());

        Account account = Account.builder()
                .username(accountDto.getUsername())
                .password(passwordEncoder.encode(accountDto.getPassword()))
                .nickname(accountDto.getNickname())
                .email(accountDto.getEmail())
                .roles(Set.of(AccountRole.USER))
                .build();

        return accountRepository.save(account);
    }

    /* ADMIN_ROLE: 관리자가 모든 회원 정보 조회 */
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    /* ADMIN_ROLE: 관리자가 특정 회원 정보 조회 */
    public Account findAccount_ADMIN(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));
    }

    /* USER_ROLE: 모든 사용자가 보는 다른 회원의 공개되도 안전한 정보 */
    public PublicAccountDto findAccount_USER(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(username));

        return new PublicAccountDto(account.getUsername(),
                                    account.getEmail(),
                                    account.getNickname());
    }

    @Transactional
    public Account updateAccount(String username, AccountDto accountDto) {
        Account account = findAccount_ADMIN(username);

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

        return account;
    }

    public void deleteAccount(String username) {
        Account account = findAccount_ADMIN(username);
        accountRepository.delete(account);
    }

    public void isDuplicateAccount(String username) {
        Optional<Account> account = accountRepository.findByUsername(username);

        if (account.isPresent()) throw new AccountDuplicateException(username);
    }
}
