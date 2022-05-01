package com.handshake.raft.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@ConfigurationProperties(prefix = "raft")
public class NodeConfig {

    private String[] servers;
    private String self;
}
