package com.handshake.basic.common.exceptions;


import com.handshake.basic.common.constants.ExceptionMessageConstants;
import com.handshake.basic.common.response.ResponseResult;

public class ChapterFailUploadException extends BaseException {

    public ChapterFailUploadException() {
        super(ResponseResult.fail(ExceptionMessageConstants.FAIL_TO_UPLOAD_CHAPTER_EXCEPTION));
    }

}
