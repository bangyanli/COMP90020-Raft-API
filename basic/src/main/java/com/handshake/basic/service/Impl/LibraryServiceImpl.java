package com.handshake.basic.service.Impl;

import com.handshake.basic.config.LibraryConfig;
import com.handshake.basic.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    LibraryConfig libraryConfig;

    /**
     * get the catalog of library
     * @return the catalog of library
     */
    @Override
    public String[] getLibraryCatalog() {
        File file = new File(libraryConfig.getAddress());
        logger.debug("Get Library Catalog");
        return file.list();

    }

}
