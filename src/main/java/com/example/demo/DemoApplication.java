package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class DemoApplication {

    public static void main(String[] args) {

//        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    A mkA() {
        return new A("bbb");
    }

//    @Bean
//    TemplateEngine templateEngine(){
//        TemplateEngine templateEngine = new SpringTemplateEngine();
//        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//        templateResolver.setPrefix("templates/");
//        templateResolver.setSuffix(".html");
//        templateEngine.addTemplateResolver(templateResolver);
//        return templateEngine;
//    }
}

class A {

    String f;

    public A(String f) {
        this.f = f;
    }

    String foo(){
        return f;
    }
}

