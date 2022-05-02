package com.handshake.raft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class RaftApplication {

    private static final Logger logger = LoggerFactory.getLogger(RaftApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RaftApplication.class, args);
    }

}
