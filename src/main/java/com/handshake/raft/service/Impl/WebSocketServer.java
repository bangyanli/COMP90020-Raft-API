package com.handshake.raft.service.Impl;
import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Setter
@Component
public class WebSocketServer extends TextWebSocketHandler {
    private static ConcurrentHashMap<String,WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static Map<String, Integer> lengthMap = new ConcurrentHashMap<>();
    private static Map<String, Boolean> firstMap = new ConcurrentHashMap<>();

    @Value("${logging.file.name}")
    private String logFileName;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        System.out.println(session.getId());
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        sessions.put(session.getId(),session);
        lengthMap.put(session.getId(), 1);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        try {
            super.afterConnectionClosed(session, closeStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sessions.remove(session.getId());
    }

    public void sendMessage(String msg){
        if(sessions==null) {
            //logger.warn("session is null");
            return;
        }
        try {
            for(WebSocketSession session:sessions.values()){
                session.sendMessage(new TextMessage(msg));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //adopted from https://www.cnblogs.com/huanzi-qch/p/11041300.html
    public void startSendLog(){
        logger.info("logging sending task start!");
        RaftThreadPool raftThreadPool = SpringContextUtil.getBean(RaftThreadPool.class);
        raftThreadPool.getScheduledExecutorService().scheduleWithFixedDelay(() -> {
            if (!sessions.isEmpty()) {
                //read log
                BufferedReader reader = null;
                String[] lines;
                try{
                    File file = new File(logFileName);
                    //IO steam
                    reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                    lines = reader.lines().toArray(String[]::new);
                }catch (Exception e) {
                    logger.info(e.getMessage(),e);
                    logger.info("Fail to get log message!");
                    return;
                } finally {
                    try {
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
                for (WebSocketSession session:sessions.values()){
                    try {
                        //get log from last
                        String[] copyOfRange = Arrays.copyOfRange(lines, lengthMap.getOrDefault(session.getId(),0), lines.length);
                        //prepossess the log
                        for (int i = 0; i < copyOfRange.length; i++) {
                            String line = copyOfRange[i];
                            //only get log information
                            String[] split = line.split(": ");
                            if(split.length >= 2){
                                line = split[1];
                            }
                            copyOfRange[i] = line;
                        }

                        //store the last line
                        lengthMap.put(session.getId(), lines.length);

                        //max range for the first time
                        if(firstMap.getOrDefault(session.getId(),true) && copyOfRange.length > 10){
                            copyOfRange = Arrays.copyOfRange(copyOfRange, copyOfRange.length - 10, copyOfRange.length);
                            firstMap.put(session.getId(),false);
                        }

                        //send log message
                        if(copyOfRange.length > 0){
                            send(session, String.join("\n",copyOfRange));
                        }

                    } catch (Exception e) {
                        logger.info(e.getMessage(),e);
                        logger.info("Fail to send log message!");
                    }
                }
            }
        },1000,1000, TimeUnit.MILLISECONDS);
    }

    private void send(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
