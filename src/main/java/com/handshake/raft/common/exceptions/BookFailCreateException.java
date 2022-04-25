package com.handshake.raft.common.exceptions;

import com.handshake.raft.common.constants.ExceptionMessageConstants;
import com.handshake.raft.common.response.ResponseResult;

public class BookFailCreateException extends BaseException{

    public BookFailCreateException() {
        super(ResponseResult.fail(ExceptionMessageConstants.FAIL_TO_CREATE_BOOK_EXCEPTION));
    }
}
