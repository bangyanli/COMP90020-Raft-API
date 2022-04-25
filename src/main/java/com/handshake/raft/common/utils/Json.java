package com.handshake.raft.common.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * configured json
 * @author lingxiao
 */
public class Json {

    private static volatile ObjectMapper mapper = null;


    public static ObjectMapper getInstance(){
        if(mapper == null){
            synchronized (Json.class){
                if(mapper == null) {
                    //keep socket IO stream open after IO
                    JsonFactory jsonFactory = new JsonFactory();
//                    jsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
//                    jsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
                    mapper = new ObjectMapper(jsonFactory);
                }
            }
        }
        return mapper;
    }



}
