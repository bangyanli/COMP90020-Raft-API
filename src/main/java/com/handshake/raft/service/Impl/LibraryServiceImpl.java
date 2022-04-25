package com.handshake.raft.service.Impl;

import com.handshake.raft.service.LibraryService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *  service implementation
 *  for get book names in library
 * </p>
 *
 * @author Lingxiao
 * @since 2022-04-25
 */
@Service
public class LibraryServiceImpl implements LibraryService {

    /**
     * get the catalog of library
     * @return the catalog of library
     */
    @Override
    public String[] getLibraryCatalog() {
        File file = new File("library");
        return file.list();

    }

}
