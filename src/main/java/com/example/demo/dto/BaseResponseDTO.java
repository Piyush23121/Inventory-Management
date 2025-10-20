package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class BaseResponseDTO<T> {
    private String status;  //success or error
    private String message;  //Description
    private  T data;
    private Long timestamp;

    public BaseResponseDTO(String status,String message,T data) {
        this.status=status;
        this.message=message;
       this.data=data;
        this.timestamp=System.currentTimeMillis();


    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
     }

    public Long getTimestamp() {
        return timestamp;
    }
}
