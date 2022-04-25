package com.handshake.raft.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  book information
 * </p>
 *
 * @author Lingxiao
 * @since 2021-09-01
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BookInfo {

    @JsonCreator
    public BookInfo(@JsonProperty("name")String name,
                    @JsonProperty("author")String author) {
        this.name = name;
        this.author = author;
    }

    private String name;
    private String author;

}
