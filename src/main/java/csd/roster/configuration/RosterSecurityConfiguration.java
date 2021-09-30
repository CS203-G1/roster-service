package csd.roster.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// For reference on how to do security configuration with AWS Cognito
// https://www.tutorialsbuddy.com/secure-spring-boot-rest-apis-with-amazon-cognito-example
// For spring security: https://www.baeldung.com/spring-security-oauth-cognito
// For spring security with OAuth: https://auth0.com/blog/spring-boot-authorization-tutorial-secure-an-api-java/
// For kotlin spring with OAuth: https://kevcodez.de/posts/2020-03-26-secure-spring-boot-app-with-oauth2-aws-cognito/ 
// A github repo with cognito auth: https://github.com/ihuaylupo/cognitoAuthentication/tree/master/src/main/java/com/huaylupo/cognito/security/config

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RosterSecurityConfiguration extends WebSecurityConfigurerAdapter {

    // Currently not doing any antMatchers yet since this is just a general configuration to enable jwt
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http .authorizeRequests(expressionInterceptUrlRegistry -> expressionInterceptUrlRegistry
                        .antMatchers()
                        .permitAll()
                        .anyRequest()
                        .authenticated())
            .oauth2ResourceServer()
                .jwt();
    }
}
