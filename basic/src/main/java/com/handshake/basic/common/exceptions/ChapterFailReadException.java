package com.handshake.basic.common.exceptions;


import com.handshake.basic.common.constants.ExceptionMessageConstants;
import com.handshake.basic.common.response.ResponseResult;

public class ChapterFailReadException extends BaseException{

    public ChapterFailReadException() {
        super(ResponseResult.fail(ExceptionMessageConstants.FAIL_TO_READ_CHAPTER_EXCEPTION));
    }

}
