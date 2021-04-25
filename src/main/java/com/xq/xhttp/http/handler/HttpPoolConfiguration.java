package com.xq.xhttp.http.handler;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class HttpPoolConfiguration {

    public static RestTemplate restTemplate() {
        return new RestTemplate(HttpPoolConfiguration.clientHttpRequestFactory());
    }

    public static ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpClientConnectionManager connectionManager = null;
        try {
            connectionManager = poolingHttpClientConnectionManager();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .disableAutomaticRetries()
                .build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    public static HttpClientConnectionManager poolingHttpClientConnectionManager() throws Exception {
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connManager.setMaxTotal(128);
        connManager.setDefaultMaxPerRoute(16);
        return connManager;

    }

    @Bean
    public ClientHttpRequestFactory getClientHttpRequestFactory() {
        return HttpPoolConfiguration.clientHttpRequestFactory();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return HttpPoolConfiguration.restTemplate();
    }
}
