package com.xq.xhttp.http.execption;

public class XHttpException extends RuntimeException {
    private String code;
    private String message;

    public XHttpException(String message) {
        this.code = "E0000";
        this.message = message;
    }

    public XHttpException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public XHttpException(ExceptionEnum exceptionEnum) {
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }

    public XHttpException(ExceptionEnum exceptionEnum, String message) {
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage().concat(message);
    }
}
