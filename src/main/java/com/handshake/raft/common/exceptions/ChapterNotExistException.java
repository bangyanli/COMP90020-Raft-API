package com.handshake.raft.common.exceptions;

import com.handshake.raft.common.constants.ExceptionMessageConstants;
import com.handshake.raft.common.response.ResponseResult;

public class ChapterNotExistException extends BaseException{

    public ChapterNotExistException() {
        super(ResponseResult.fail(ExceptionMessageConstants.CHAPTER_NOT_EXIST_EXCEPTION));
    }

}
