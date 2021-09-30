package csd.roster.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// For reference on how to do security configuration with AWS Cognito
// https://www.tutorialsbuddy.com/secure-spring-boot-rest-apis-with-amazon-cognito-example

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
