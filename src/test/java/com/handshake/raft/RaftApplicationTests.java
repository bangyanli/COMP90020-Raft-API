package com.handshake.raft;

import com.handshake.raft.dto.Command;
import com.handshake.raft.dto.CreateBookCommand;
import com.handshake.raft.dto.LogEntry;
import com.handshake.raft.dto.UploadChapterCommand;
import com.handshake.raft.logdb.Logdb;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RaftApplicationTests {

    @Autowired
    Logdb logdb;

    @Test
    void contextLoads() {
        ArrayList<LogEntry> logEntries = new ArrayList<>();
        logEntries.add(new LogEntry(1L,1L,new CreateBookCommand("zzz","aaa")));
        logEntries.add(new LogEntry(2L,1L,new CreateBookCommand("aaa","aaa")));
        logEntries.add(new LogEntry(3L,1L,new CreateBookCommand("bbb","aaa")));
        logEntries.add(new LogEntry(3L,1L,new UploadChapterCommand("zzz","aaa",createMultipartFile())));
        logdb.saveToLocal(logEntries);
        ArrayList<LogEntry> logEntries1 = logdb.readFromLocal();
        Command command = logEntries.get(logEntries.size() - 1).getCommand();
        System.out.println(command.toString());
        command.excute();
        //System.out.println(logEntries1);
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
