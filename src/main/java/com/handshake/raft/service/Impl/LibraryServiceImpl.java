package com.handshake.raft.service.Impl;

import com.handshake.raft.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;


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

    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceImpl.class);

    /**
     * get the catalog of library
     * @return the catalog of library
     */
    @Override
    public String[] getLibraryCatalog() {
        File file = new File("library");
        logger.info("Get Library Catalog");
        return file.list();

    }

}
