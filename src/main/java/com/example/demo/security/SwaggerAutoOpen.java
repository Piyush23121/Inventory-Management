package com.example.demo.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

@Component //spring detects and manage class as bean
public class SwaggerAutoOpen  implements CommandLineRunner {

    @Override
    //string args is variable no of string argument "3dots are called varargs means parameter can take zero or mare string values"
    public void run(String... args) throws Exception{
        System.setProperty("java.awt.headless", "false");
        String swaggerUrl="http://localhost:8080/swagger-ui/index.html";
    if(Desktop.isDesktopSupported()){
            Desktop.getDesktop().browse(new URI(swaggerUrl));
        }
    }
}
