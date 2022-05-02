package com.handshake.raft;

import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.service.Impl.BookServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

import javax.annotation.PostConstruct;
import java.io.File;

@EnableOpenApi
@SpringBootApplication
public class RaftApplication {

    private static final Logger logger = LoggerFactory.getLogger(RaftApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RaftApplication.class, args);
    }

}
