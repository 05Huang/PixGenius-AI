package cn.hx.pix.genius.core.advice;

import cn.hx.pix.genius.core.dto.common.Result;
import cn.hx.pix.genius.core.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class CommonExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity customException(CustomException customException){
        ResponseEntity<Result<Object>> responseEntity = ResponseEntity.status(HttpStatus.OK.value())
                .body(Result.error(customException.getMessage()));
        return responseEntity;
//        return Result.error(customException.getMessage());
    }

}
