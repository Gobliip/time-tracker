package com.gobliip.chronos.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

@SpringBootApplication
@EnableOAuth2Resource
@EnableEurekaClient
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableAspectJAutoProxy
public class TimeTrackerApplication {

    @Bean
    public Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
            ObjectMapper objectMapper) {
        objectMapper.registerModule(new Hibernate4Module());
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    public static void main(String[] args) {
        SpringApplication.run(TimeTrackerApplication.class, args);
    }
}
