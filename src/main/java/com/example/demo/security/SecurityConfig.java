package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  //Spring will read class when the application start and apply security setup inside it.
@EnableWebSecurity
@EnableMethodSecurity  //tells spring security that u want to use seurity directly on methods like inside controller or service
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Bean //create and manage obj

    //Securityfilter define seq of security filter applied to every http req
    //HttpSecurity use in costumize spring security what is allowed and what is not allowed
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception{
        httpSecurity
                //cross site req forgery disabled it becz we use jwt instesad of session
                .csrf(csrf -> csrf.disable())
                .formLogin(form->form.disable())//disable s[pring default login
                .httpBasic(basic->basic.disable())//disable basic auth

                //define which end pt need authent
                .authorizeHttpRequests(auth -> auth

                        //alloewd every one to access
                        .requestMatchers("/v3/api-docs/**",
                                "/api/**",
                                "/api/getProduct",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/register",
                                "/api/login",
                                "/api/verifyWithOtp",
                                "/api/getImage",
                                "/api/deleteUser/{id}").permitAll()
                        .requestMatchers("/auth/**").permitAll()//login register api

                        //any other end pt req valid jwt token
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

       // Tells spring security not to create sessions becz Jwt is used
                .sessionManagement(sess ->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();// build and applies security chain
    }
}
