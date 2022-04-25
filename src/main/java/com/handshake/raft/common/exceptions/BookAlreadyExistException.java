package com.handshake.raft.common.exceptions;

import com.handshake.raft.common.constants.ExceptionMessageConstants;
import com.handshake.raft.common.response.ResponseResult;

public class BookAlreadyExistException extends BaseException{

    public BookAlreadyExistException() {
        super(ResponseResult.fail(ExceptionMessageConstants.BOOK_ALREADY_EXIST_EXCEPTION));
    }
}
