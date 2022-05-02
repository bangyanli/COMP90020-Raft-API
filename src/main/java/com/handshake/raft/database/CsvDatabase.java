package com.handshake.raft.database;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvDatabase {

    public static void writeDataToCsv(String chapter,String bookName,String bookContent){
        // specified by filepath
        String dbDir = "./database-raft/" + System.getProperty("serverPort") + "/" + bookName + "/" + chapter;
        File file = new File(dbDir);

        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);

            String[] header = { "bookName", "chapter", "content" };
            writer.writeNext(header);
            
            writer.writeNext(new String [] {bookName,chapter,bookContent});
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
