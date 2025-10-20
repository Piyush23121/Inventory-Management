package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component //mark this class as spring manage component (spring will auto create and mange instance of class)
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private Jwt jwt;// inject jwt to validte and extract info from jwt tokem

    @Override
    //this method runs for every incoing req
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException{

        //Get authorize header from incoming http req
        String header = request.getHeader("Authorization");

        //check if header existand start with bearer (standerd jwt format
        if(header !=null && header.startsWith("Bearer ")){
            //Extract actual token string(skip first seven char "bearer "
            String token = header.substring(7);

            //Validtae token using jwt
            if(jwt.isTokenValid(token)){
                String email= jwt.extractEmail(token);
                String role= jwt.extractRole(token);

                //Creat user detail obj(spring secu internal model)
                //username=email
                //password= blank(not needed for jwt)
                //authorities =role from token
                UserDetails userDetails= User.withUsername(email)
                        .password("")
                        .authorities(role)
                        .build();

                //Create authentication obj to store in security context
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null,userDetails.getAuthorities());

                //attach extra details abt the req like IP adress
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //this tells spring security that user is now authenticated
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        //continue filter chain move to next controller or component
        filterChain.doFilter(request, response);
    }
}
