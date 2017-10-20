package com.luckylhb.easyrpc.common.exception;

import com.luckylhb.easyrpc.common.exception.base.RpcException;
import lombok.Getter;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
public class ServerException extends RpcException {

    @Getter
    private String traceId;

    public ServerException(Throwable cause, String traceId) {
        super(cause);
        this.traceId = traceId;
    }


}
