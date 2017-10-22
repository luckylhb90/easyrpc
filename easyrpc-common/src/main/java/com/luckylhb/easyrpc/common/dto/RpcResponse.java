package com.luckylhb.easyrpc.common.dto;

import lombok.Data;

/**
 * @Project : easyrpc
 * @Description : rpc应答实体类
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@Data
public class RpcResponse {
    private String traceId;
    private Throwable error;
    private Object result;

    public RpcResponse() {
    }

    public boolean isError() {return error != null;}
}
