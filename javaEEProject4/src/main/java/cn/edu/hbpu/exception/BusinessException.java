package cn.edu.hbpu.exception;

/**
 * 业务异常：由全局异常处理器捕获后以 Result.fail(msg) 返回给前端。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
