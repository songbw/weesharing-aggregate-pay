package com.weesharing.pay.exception;

/**
 * 业务异常
 * 
 * @author zp
 */
public class ServiceException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    protected final String message;

    public ServiceException(String message)
    {
        this.message = message;
    }

    public ServiceException(String message, Throwable e)
    {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
