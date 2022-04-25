package com.handshake.raft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class RaftApplication {

    public static void main(String[] args) {
        init();
        SpringApplication.run(RaftApplication.class, args);
    }

    /**
     * create the library if it does not exist
     */
    public static void init(){
        File file = new File("library");
        if(!file.exists()){
            if(!file.mkdir()){
                System.out.println("Fail to make dictionary!");
                System.exit(-1);
            }
        }
    }

}
