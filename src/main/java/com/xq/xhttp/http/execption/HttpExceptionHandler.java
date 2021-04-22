package com.xq.xhttp.http.execption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class HttpExceptionHandler implements ResponseErrorHandler {
    private final static Logger logger = LoggerFactory.getLogger(HttpExceptionHandler.class);

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ResponseErrorHandler.super.handleError(url, method, response);
        logger.error("remote http fail. => {} {}", method, url);
        throw new XHttpException(ExceptionEnum.REMOTE_EXCEPTION);
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

    }
}
