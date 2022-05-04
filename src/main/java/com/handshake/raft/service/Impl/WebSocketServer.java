package com.handshake.raft.service.Impl;
import com.handshake.raft.raftServer.Node;
import com.handshake.raft.raftServer.proto.LogEntry;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

@Service
@Getter
@Setter
@Component
public class WebSocketServer extends TextWebSocketHandler {
    private HashMap<String,WebSocketSession> sessions = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        System.out.println(session.getId());
        session.sendMessage(new TextMessage("Hello world!"));
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        this.sessions.put(session.toString(),session);
        System.out.println(session.getId());
        try {
            session.sendMessage(new TextMessage("Hello world!"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        try {
            super.afterConnectionClosed(session, closeStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sessions.remove(session.toString());
    }

    public void sendMessage(String msg){
        if(sessions==null)
            return;
        try {
            for(WebSocketSession session:sessions.values()){
                if(session!=null) {
                    session.sendMessage(new TextMessage(msg));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
        if(!portId.equals(port) && session!=null) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //TODO:implement logs
            //LogEntry logEntry = node.getLog().getLast();
            //session.getBasicRemote().sendObject(logEntry);

        }
        */



}
