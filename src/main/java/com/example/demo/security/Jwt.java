package com.example.demo.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class Jwt {
    //creating secret key used to sign and verify jwt token
    //encryption needs byte format
    private  final Key key= Keys.hmacShaKeyFor("MySuperSecurityKeyforJwtAuthentication12345".getBytes());//random long string act as a secret
    //Token validity 6hr
    private final long EXPIRE_TIME=1000L*60*60*6;
    //Generate jwt token using email and role
    public String generateToken(String email, String role){
        return Jwts.builder()    //Starts building JWt
                .setSubject(email)    //Subject which is main identity of the user
                .claim("role", role)  //Extra info called claim store user like admi dealer customer
                .setIssuedAt(new Date())  //Date when token created
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRE_TIME))  // Date and time token will expire (6hrs)
                .signWith(key, SignatureAlgorithm.HS256)    //Use the secret key and create signature with Hs256 and add to jwt
                .compact();  //finish everything and return final token as long encoded string

    }
    // method read token and gets user email
    public String extractEmail(String token){
       return Jwts.parserBuilder()  //Create obj which read jwt
                .setSigningKey(key) //teell the parser which secrect key to use  to check token signature
                .build()  //finalizer parser so ready to use
                .parseClaimsJws(token)  //read token,check signature,make sure its valid
                .getBody() //access part where email role are stored
                .getSubject();// return email that was stored in subject
    }
    public String extractRole(String token){
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");  //fetch value stored in claims
    }
    //Validate Token
    public boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }
}