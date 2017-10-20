package com.luckylhb.easyrpc.common.exception.base;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
public class RpcException extends RuntimeException {
    public RpcException(String message, Object... args) {
        super(String.format(message, args));
    }

    public RpcException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }

    public RpcException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
