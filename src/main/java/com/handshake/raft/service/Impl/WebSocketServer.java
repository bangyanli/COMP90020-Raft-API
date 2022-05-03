package com.handshake.raft.service.Impl;
import com.handshake.raft.raftServer.Node;
import com.handshake.raft.raftServer.proto.LogEntry;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/raft/{portId}")
@Service
@Getter
@Setter
public class WebSocketServer {
    private Session session;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    @Autowired
    private Node node;
    private String port = node.getNodeConfig().getSelf();

    @OnOpen
    public void onOpen(Session session, @PathParam("portId") String portId){
        session = this.session;
        System.out.println("websocketet connected, session id: " + session.getId());
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
            try {
                session.getBasicRemote().sendText("Hello World");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
