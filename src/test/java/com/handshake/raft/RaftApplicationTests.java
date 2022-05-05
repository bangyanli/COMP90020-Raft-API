package com.handshake.raft;

import com.handshake.raft.raftServer.log.Impl.LogDatabaseImpl;
import com.handshake.raft.raftServer.log.LogSystem;
import com.handshake.raft.raftServer.proto.LogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
class RaftApplicationTests {

    @Autowired
    LogDatabaseImpl logdb;

    @Autowired
    LogSystem logSystem;

    @Test
    void testLogDatabase() {
//        HashMap<Integer,LogEntry> logEntries = new HashMap<>();
//        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//        logEntries.put(1,new LogEntry(1,1,new CreateBookCommand("zzz","aaa","1","1")));
//        logEntries.put(2,new LogEntry(2,1,new CreateBookCommand("aaa","aaa","1","1")));
//        logEntries.put(3,new LogEntry(3,1,new CreateBookCommand("bbb","aaa","1","1")));
//        logEntries.put(4,new LogEntry(4,1,new UploadChapterCommand("zzz","aaa",createMultipartFile())));
//        LogInfo logInfo = new LogInfo(4, 1, logEntries);
        System.out.println(logSystem.getCommitIndex());
        LogEntry last = logSystem.getLast();
        last.getCommand().execute();

    }

    private MultipartFile createMultipartFile(){
        Path path = Paths.get("C:\\Users\\44743\\Documents\\Distributed Algorithm\\raft\\library\\HelloWorld\\hello");
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        return new MockMultipartFile(name,
                originalFileName, contentType, content);
    }

}
