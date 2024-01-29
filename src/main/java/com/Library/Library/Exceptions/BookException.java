package com.Library.Library.Exceptions;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

public class BookException extends Exception{

    public BookException(String msg){
        super(msg);
    }
    public BookException(ERROR_CODE errorCode){
        super(new Gson().toJson(errorCode.toString()));
    }

    public enum ERROR_CODE{
        Book_NOT_FOUND("Book not found",1000);

        @Getter
        @Setter
        private String msg;

        @Getter
        @Setter
        private int code;

        ERROR_CODE(String msg,int code){
            this.msg = msg;
            this.code = code;
        }
    }
}
