package csd.roster.configurations;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableAutoConfiguration
@ComponentScan
@PropertySource("application-test.properties")
@EnableTransactionManagement
public class IntegrationTestConfig {
}
