package com.xq.xhttp.http.execption;

public enum ExceptionEnum {
    REMOTE_EXCEPTION("E0010", "remote http service fail."),
    ;

    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
