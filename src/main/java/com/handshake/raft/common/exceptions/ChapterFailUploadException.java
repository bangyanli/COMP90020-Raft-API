package com.handshake.raft.common.exceptions;

import com.handshake.raft.common.constants.ExceptionMessageConstants;
import com.handshake.raft.common.response.ResponseResult;

public class ChapterFailUploadException extends BaseException{

    public ChapterFailUploadException() {
        super(ResponseResult.fail(ExceptionMessageConstants.FAIL_TO_UPLOAD_CHAPTER_EXCEPTION));
    }

}
