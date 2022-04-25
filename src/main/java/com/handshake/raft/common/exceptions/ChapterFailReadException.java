package com.handshake.raft.common.exceptions;

import com.handshake.raft.common.constants.ExceptionMessageConstants;
import com.handshake.raft.common.response.ResponseResult;

public class ChapterFailReadException extends BaseException{

    public ChapterFailReadException() {
        super(ResponseResult.fail(ExceptionMessageConstants.FAIL_TO_READ_CHAPTER_EXCEPTION));
    }

}
