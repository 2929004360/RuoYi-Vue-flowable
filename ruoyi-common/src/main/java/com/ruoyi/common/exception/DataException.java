package com.ruoyi.common.exception;

/**
 * 数据异常
 *
 * @author fengcheng
 */
public class DataException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public DataException(Throwable e) {
        super(e.getMessage(), e);
    }

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
