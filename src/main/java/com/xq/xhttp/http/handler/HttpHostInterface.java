package com.xq.xhttp.http.handler;


public interface HttpHostInterface {
    String getHost();

    public static class DefaultHttpHostImpl implements HttpHostInterface {
        @Override
        public String getHost() {
            return null;
        }
    }
}




