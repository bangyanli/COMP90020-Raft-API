package com.handshake.raft.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *  LibraryConfig: configuration of the library
 * </p>
 *
 * @author Lingxiao
 */
@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "library")
public class LibraryConfig {

    private String address;

}
