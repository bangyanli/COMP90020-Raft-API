package com.handshake.raft.common.exceptions;

import com.handshake.raft.common.constants.ExceptionMessageConstants;
import com.handshake.raft.common.response.ResponseResult;

public class BookInfoFailReadException extends BaseException{

    public BookInfoFailReadException() {
        super(ResponseResult.fail(ExceptionMessageConstants.FAIL_TO_READ_BOOK_INFO_EXCEPTION));
    }

}
