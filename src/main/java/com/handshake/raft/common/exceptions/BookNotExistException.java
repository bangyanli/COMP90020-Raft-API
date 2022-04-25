package com.handshake.raft.common.exceptions;

import com.handshake.raft.common.constants.ExceptionMessageConstants;
import com.handshake.raft.common.response.ResponseResult;

public class BookNotExistException extends BaseException{

    public BookNotExistException() {
        super(ResponseResult.fail(ExceptionMessageConstants.BOOK_NOT_EXIST_EXCEPTION));
    }

}
