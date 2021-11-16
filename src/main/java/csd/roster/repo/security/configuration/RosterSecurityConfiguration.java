package csd.roster.repo.security.configuration;

import csd.roster.repo.security.store.CognitoAccessTokenConverter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;

// For reference on how to do security configuration with AWS Cognito
// https://www.tutorialsbuddy.com/secure-spring-boot-rest-apis-with-amazon-cognito-example
// For spring security: https://www.baeldung.com/spring-security-oauth-cognito
// For spring security with OAuth: https://auth0.com/blog/spring-boot-authorization-tutorial-secure-an-api-java/
// For kotlin spring with OAuth: https://kevcodez.de/posts/2020-03-26-secure-spring-boot-app-with-oauth2-aws-cognito/
// A github repo with cognito auth: https://github.com/ihuaylupo/cognitoAuthentication/tree/master/src/main/java/com/huaylupo/cognito/security/config

// For more extension implementation of Cognito: https://github.com/szerhusenBC/jwt-spring-security-demo
// This has their own implementation of JWT and filters

// Actual implementation taken from: https://arjunsk.medium.com/resource-server-with-cognito-b7fbfbee0155

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
// Since we are authenticating and authorizing access to the resources at the HTTP layer
// We use ResourceServerConfigurerAdapter instead
// It inherits WebSecurityConfigurer Adapter
// More on the difference between ResourceServerConfigurerAdapter and WebSecurityConfigurer can be found on:
// https://jeffrey-chen.medium.com/source-code-tracing-resourceserverconfigureradapter-and-websecurityconfigureradapter-54fe6f2af573
public class RosterSecurityConfiguration extends ResourceServerConfigurerAdapter {
    private final ResourceServerProperties resource;

    public RosterSecurityConfiguration(ResourceServerProperties resource) {
        this.resource = resource;
    }

    // Currently not doing any antMatchers yet since this is just a general configuration to enable jwt
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors();

        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/actuator/health").permitAll();
            //     .and()
            // .authorizeRequests()
            //     .anyRequest().authenticated();

    }

    // Enabling Cognito Converter
    @Bean
    public TokenStore jwkTokenStore() {
        return new JwkTokenStore(
                Collections.singletonList(resource.getJwk().getKeySetUri()),
                new CognitoAccessTokenConverter(),
                null);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
