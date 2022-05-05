package com.handshake.raft.service.Impl;
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

@Service
@Getter
@Setter
@Component
public class WebSocketServer extends TextWebSocketHandler {
    private static ConcurrentHashMap<String,WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static Map<String, Integer> lengthMap = new ConcurrentHashMap<>();

    @Value("${logging.file}")
    private String logFilePath;

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

    public void sendLog(){
        new Thread(() -> {
            logger.info("LoggingWebSocketServer 任务开始");
            boolean first = true;
            while (!sessions.isEmpty()) {
                for (WebSocketSession session:sessions.values()){
                    BufferedReader reader = null;
                    try {
                        //TODO 日志文件路径，获取最新的
                        File file = new File("");

                        //字符流
                        reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                        Object[] lines = reader.lines().toArray();

                        //只取从上次之后产生的日志
                        Object[] copyOfRange = Arrays.copyOfRange(lines, lengthMap.get(session.getId()), lines.length);

                        //对日志进行着色，更加美观  PS：注意，这里要根据日志生成规则来操作
                        for (int i = 0; i < copyOfRange.length; i++) {
                            String line = (String) copyOfRange[i];
                            //先转义
                            line = line.replaceAll("&", "&amp;")
                                    .replaceAll("<", "&lt;")
                                    .replaceAll(">", "&gt;")
                                    .replaceAll("\"", "&quot;");

                            //处理等级
                            line = line.replace("DEBUG", "<span style='color: blue;'>DEBUG</span>");
                            line = line.replace("INFO", "<span style='color: green;'>INFO</span>");
                            line = line.replace("WARN", "<span style='color: orange;'>WARN</span>");
                            line = line.replace("ERROR", "<span style='color: red;'>ERROR</span>");

                            //处理类名
                            String[] split = line.split("]");
                            if (split.length >= 2) {
                                String[] split1 = split[1].split("-");
                                if (split1.length >= 2) {
                                    line = split[0] + "]" + "<span style='color: #298a8a;'>" + split1[0] + "</span>" + "-" + split1[1];
                                }
                            }

                            copyOfRange[i] = line;
                        }

                        //存储最新一行开始
                        lengthMap.put(session.getId(), lines.length);

                        //第一次如果太大，截取最新的200行就够了，避免传输的数据太大
                        if(first && copyOfRange.length > 200){
                            copyOfRange = Arrays.copyOfRange(copyOfRange, copyOfRange.length - 200, copyOfRange.length);
                            first = false;
                        }

                        //String result = StringUtils.join(copyOfRange, "<br/>");

                        //发送
                        //send(session, result);

                        //休眠一秒
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        //捕获但不处理
                        e.printStackTrace();
                    } finally {
                        try {
                            reader.close();
                        } catch (IOException ignored) {
                        }
                    }
                }

            }
            logger.info("LoggingWebSocketServer 任务结束");
        }).start();
    }

    private void send(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
