package com.handshake.basic.common.exceptions;


import com.handshake.basic.common.constants.ExceptionMessageConstants;
import com.handshake.basic.common.response.ResponseResult;

public class BookFailCreateException extends BaseException{

    public BookFailCreateException() {
        super(ResponseResult.fail(ExceptionMessageConstants.FAIL_TO_CREATE_BOOK_EXCEPTION));
    }
}
