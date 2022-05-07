package com.handshake.basic.common.exceptions;


import com.handshake.basic.common.constants.ExceptionMessageConstants;
import com.handshake.basic.common.response.ResponseResult;

public class ChapterNotExistException extends BaseException{

    public ChapterNotExistException() {
        super(ResponseResult.fail(ExceptionMessageConstants.CHAPTER_NOT_EXIST_EXCEPTION));
    }

}
