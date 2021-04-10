package com.xq.xhttp.http.execption;

public enum ExceptionEnum {
    NOT_FIND("E0010", "123"),
    ;

    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
