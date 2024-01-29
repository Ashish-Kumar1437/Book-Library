package com.Library.Library.Exceptions;

import com.google.gson.Gson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserException extends Exception{

    ErrorCode error;
    public UserException(ErrorCode errorCode){
        super(new Gson().toJson(errorCode.toString()));
        this.error = errorCode;
    }
    public UserException(String message){
        super(message);
    }

    public enum ErrorCode{
        USER_NOT_FOUND("User not present in system",100);

        @Getter
        @Setter
        private String message;

        @Getter
        @Setter
        private int code;

        ErrorCode(){};

        ErrorCode(String message,int code){
            this.message = message;
            this.code = code;
        }

    }
}
