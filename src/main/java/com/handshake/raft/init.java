package com.handshake.raft;

import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.dto.Command;
import com.handshake.raft.dto.CreateBookCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class init implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(init.class);

    @Autowired
    NodeConfig nodeConfig;


    @Override
    public void afterPropertiesSet() throws Exception {
        initLibrary();
        logger.info(nodeConfig.toString());
    }

    /**
     * create the library if it does not exist
     */
    public static void initLibrary(){
        File file = new File("library");
        if(!file.exists()){
            if(!file.mkdir()){
                System.out.println("Fail to make dictionary!");
                System.exit(-1);
            }
        }
        logger.info("Successfully connect to library!");
    }
}
