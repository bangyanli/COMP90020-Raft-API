package com.handshake.raft.logdb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.handshake.raft.common.utils.Json;
import com.handshake.raft.dto.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class Logdb {

    private static final Logger logger = LoggerFactory.getLogger(Logdb.class);

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void saveToLocal(ArrayList<LogEntry> logEntries){
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            Json.getInstance().writeValue(new File("log.json"),logEntries);
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        finally {
            writeLock.unlock();
        }
    }

    public ArrayList<LogEntry> readFromLocal(){
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        ArrayList<LogEntry> logEntries = new ArrayList<>();
        try {
            logEntries = Json.getInstance().readValue(new File("log.json"),
                    new TypeReference<ArrayList<LogEntry>>() {
            });
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        finally {
            readLock.unlock();
        }
        return logEntries;
    }
}
