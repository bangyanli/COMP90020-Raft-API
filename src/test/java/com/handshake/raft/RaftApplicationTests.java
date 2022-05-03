package com.handshake.raft;

import com.handshake.raft.raftServer.proto.Command;
import com.handshake.raft.raftServer.proto.Impl.CreateBookCommand;
import com.handshake.raft.raftServer.proto.LogEntry;
import com.handshake.raft.raftServer.proto.Impl.UploadChapterCommand;
import com.handshake.raft.raftServer.log.Impl.Logdb;
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
import java.util.concurrent.CopyOnWriteArrayList;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
class RaftApplicationTests {

    @Autowired
    Logdb logdb;

    @Test
    void testLogDatabase() {
        CopyOnWriteArrayList<LogEntry> logEntries = new CopyOnWriteArrayList<>();
        logEntries.add(new LogEntry(1,1,new CreateBookCommand("zzz","aaa","1","1")));
        logEntries.add(new LogEntry(2,1,new CreateBookCommand("aaa","aaa","1","1")));
        logEntries.add(new LogEntry(3,1,new CreateBookCommand("bbb","aaa","1","1")));
        logEntries.add(new LogEntry(3,1,new UploadChapterCommand("zzz","aaa",createMultipartFile())));
        logdb.saveToLocal(logEntries);
        CopyOnWriteArrayList<LogEntry> logEntries1 = logdb.readFromLocal();
        Command command = logEntries.get(logEntries.size() - 1).getCommand();
        System.out.println(command.toString());
        command.execute();
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
