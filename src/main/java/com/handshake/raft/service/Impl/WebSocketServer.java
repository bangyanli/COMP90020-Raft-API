package com.handshake.raft.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/raft/{userId}")
@Service
public class WebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private static AtomicInteger onlineNum = new AtomicInteger();

    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId){
        if(sessionMap.containsKey(userId)){
            sessionMap.remove(userId);
            sessionMap.put(userId,session);
        }
        else {
            sessionMap.put(userId,session);
            addOnlineCount();
        }
        logger.info("User: " + userId + " connect to web socket.");

    }

    @OnClose
    public void OnClose(@PathParam("userId") String userId){
        if(sessionMap.containsKey(userId)){
            sessionMap.remove(userId);
            subOnlineCount();
        }
        logger.info("User: " + userId + " disconnect from web socket.");

    }

    @OnError
    public void onError(Throwable throwable){
        logger.warn(throwable.getMessage());
    }

    /**
     * send message to all online user
     * @param message message to send
     */
    public static void broadcastMessage(String message){
        for(Session session: sessionMap.values()){
            try{
                session.getBasicRemote().sendText(message);
            }
            catch (IOException e){
                logger.warn(e.getMessage());
            }
        }
    }

    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }




}
