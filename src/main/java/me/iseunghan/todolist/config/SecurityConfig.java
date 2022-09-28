package me.iseunghan.todolist.config;

import me.iseunghan.todolist.common.LoginUserArgumentResolver;
import me.iseunghan.todolist.jwt.JwtAccessDeniedHandler;
import me.iseunghan.todolist.jwt.JwtAuthenticationEntryPoint;
import me.iseunghan.todolist.jwt.JwtAuthorizationFilter;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public void configure(WebSecurity web) {
        web.ignoring()
                .requestMatchers(
                        PathRequest.toH2Console(),
                        PathRequest.toStaticResources().atCommonLocations()
                )
                .antMatchers("/docs/**")
        ;
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
                    .permitAll()
                    .addLogoutHandler(new CustomLogoutHandler())
                    .logoutSuccessUrl("/")
                    .clearAuthentication(true)
                    .deleteCookies("Authorization")
                .and()

                .authorizeRequests()
                    .antMatchers("/", "/error", "/login", "/signup")
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
