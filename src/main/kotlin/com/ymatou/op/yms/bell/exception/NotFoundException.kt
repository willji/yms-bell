package com.ymatou.op.yms.bell.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.ServletException

/**
 * Created by xuemingli on 16/9/9.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(override val message: String?): ServletException(message) {
}