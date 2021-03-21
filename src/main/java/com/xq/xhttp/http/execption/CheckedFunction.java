package com.xq.xhttp.http.execption;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws Exception;

}