package com.handshake.raft.service;

import java.util.List;

/**
 * <p>
 *  service
 *  for get book names in library
 * </p>
 *
 * @author Lingxiao
 * @since 2022-04-25
 */
public interface LibraryService {

    public String[] getLibraryCatalog();
}
