package com.handshake.basic.dao;

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
                    @JsonProperty("author")String author,
                    @JsonProperty("category")String category,
                    @JsonProperty("description")String description) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.description = description;
    }

    private String name;
    private String author;
    private String category;
    private String description;

}
