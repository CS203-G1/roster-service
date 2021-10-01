package csd.roster.configuration;

import csd.roster.store.CognitoAccessTokenConverter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.*;

// For reference on how to do security configuration with AWS Cognito
// https://www.tutorialsbuddy.com/secure-spring-boot-rest-apis-with-amazon-cognito-example
// For spring security: https://www.baeldung.com/spring-security-oauth-cognito
// For spring security with OAuth: https://auth0.com/blog/spring-boot-authorization-tutorial-secure-an-api-java/
// For kotlin spring with OAuth: https://kevcodez.de/posts/2020-03-26-secure-spring-boot-app-with-oauth2-aws-cognito/
// A github repo with cognito auth: https://github.com/ihuaylupo/cognitoAuthentication/tree/master/src/main/java/com/huaylupo/cognito/security/config

// For more extension implementation of Cognito: https://github.com/szerhusenBC/jwt-spring-security-demo
// This has their own implementation of JWT and filters

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RosterSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final ResourceServerProperties resource;

    public RosterSecurityConfiguration(ResourceServerProperties resource) {
        this.resource = resource;
    }

    // Currently not doing any antMatchers yet since this is just a general configuration to enable jwt
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(auth ->
                        auth.anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        ));
    }

    private JwtAuthenticationConverter grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Converter<Jwt, Collection<GrantedAuthority>> converter = new Converter<Jwt, Collection<GrantedAuthority>>() {
                @Override
                public Collection<GrantedAuthority> convert(Jwt jwt) {
                    return (Collection<GrantedAuthority>) jwt.getClaims().getOrDefault("cognito:groups", new ArrayList<String>());
                }
            };

            return converter.convert(jwt);
        });

        return jwtAuthenticationConverter;
    }
}
