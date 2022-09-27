package me.iseunghan.todolist.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.todolist.common.LoginUserArgumentResolver;
import me.iseunghan.todolist.jwt.*;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver(jwtTokenUtil()));
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring()
                // enable h2-console
                .requestMatchers(PathRequest.toH2Console())

                // ignoring [/js/**, /css/**, /images/**..]
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                    .accessDeniedHandler(new JwtAccessDeniedHandler())

                .and()
                    .formLogin().disable()
                    .httpBasic().disable()

                // logout
                    .logout()
                    .logoutUrl("/logout")
                    .addLogoutHandler(new CustomLogoutHandler())
                    .logoutSuccessUrl("/")
                    .clearAuthentication(true)
                    .deleteCookies("Authorization")
                .and()

                .authorizeRequests()
                    .antMatchers("/", "/error")
                        .permitAll()
                    .antMatchers("/login", "/logout", "/signup")
                        .permitAll()
                    .antMatchers(HttpMethod.POST,"/user/accounts")
                        .anonymous()
                    .antMatchers("/user/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")
                    .antMatchers("/manager/**")
                        .hasAnyRole("MANAGER, ADMIN")
                    .antMatchers("/admin/**")
                        .hasAnyRole("ADMIN")
                    .anyRequest().authenticated()

                // JWT Filter
                .and()
                    .addFilterBefore(new JwtAuthorizationFilter(jwtTokenUtil(), authenticationManagerBean()), BasicAuthenticationFilter.class)
        ;

    }
}
