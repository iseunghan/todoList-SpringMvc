package me.iseunghan.todolist.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class AccountAdapter extends User {

    private Account account;

    public AccountAdapter(Account account) {
        super(account.getUsername(), account.getPassword(), getAuthority(account.getRoles()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> getAuthority(Set<AccountRole> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
    }
}
