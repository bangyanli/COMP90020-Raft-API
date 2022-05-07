package com.handshake.basic.common.exceptions;


import com.handshake.basic.common.constants.ExceptionMessageConstants;
import com.handshake.basic.common.response.ResponseResult;

public class BookAlreadyExistException extends BaseException{

    public BookAlreadyExistException() {
        super(ResponseResult.fail(ExceptionMessageConstants.BOOK_ALREADY_EXIST_EXCEPTION));
    }
}
