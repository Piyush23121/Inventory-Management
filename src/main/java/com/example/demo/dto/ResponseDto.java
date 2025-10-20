package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor@NoArgsConstructor
public class ResponseDto {
    private String status;  //success or error
    private String message;  //Description
    private Long timestamp;

    public ResponseDto(String status,String message) {
        this.status = status;
        this.message = message;

        this.timestamp = System.currentTimeMillis();

    }
}
