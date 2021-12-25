package io.akikr.betterreads.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.ServletContext;

/**
 * @apiNote : The SpringSecurityAdapter configuration class to configure it to login vai OAuth2Login
 * @author ankit
 * @since 1.0
 */
@Configuration
public class AppSecurityAdapter extends WebSecurityConfigurerAdapter {

    /**
     * @apiNote : To get the spring-context path
     */
    @Autowired
    private ServletContext servletContext;

    /**
     * @apiNote : To configure the spring-security authorization to login vai OAuth2Login
     * @param http : {@link HttpSecurity}
     * @throws Exception : some http-security exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String contextPath = servletContext.getContextPath() + "/";
        // @formatter:off
        http
                .authorizeRequests(a -> a
//                        .antMatchers(contextPath, contextPath + "/error").permitAll()
//                        .anyRequest().authenticated()
                        // To allow all urls without authentication
                        .anyRequest().permitAll()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .logout(l -> l
                        .logoutSuccessUrl(contextPath + "/logout").permitAll()
                )
                .oauth2Login();
        // @formatter:on
    }
}
