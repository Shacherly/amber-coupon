package com.trading.backend.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import java.time.Duration;


@Configuration @RefreshScope
public class AceUpAuthConfigurer {

    @Value("${remote-call.aceup.key}")
    private String aceUpKey;

    @Value("${remote-call.aceup.secret}")
    private String aceUpSecret;

    @Value("${remote-call.aceup.token-url}")
    private String tokenUrl;

    public static final Integer TIMEOUT = 50;//超时时间

    @Bean
    protected OAuth2ProtectedResourceDetails resource() {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setAccessTokenUri(tokenUrl);
        resource.setClientId(aceUpKey);
        resource.setClientSecret(aceUpSecret);
        return resource;
    }

    @Bean
    public OAuth2RestOperations oAuth2RestOperations() {
        AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(resource(), new DefaultOAuth2ClientContext(accessTokenRequest));
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(TIMEOUT).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(TIMEOUT).toMillis());
        requestFactory.setConnectionRequestTimeout((int) Duration.ofSeconds(TIMEOUT).toMillis());
        oAuth2RestTemplate.setRequestFactory(requestFactory);
        return oAuth2RestTemplate;
    }
}
