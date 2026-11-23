package cn.edu.hbpu.advice;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.pojo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(ProjectExceptionAdvice.class);

    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException ex) {
        return Result.fail(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex) {
        log.error("服务器发生异常", ex);
        return Result.fail("服务器发生异常，请稍后再进行操作！");
    }
}
