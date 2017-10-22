package com.luckylhb.easyrpc.common.dto;

import lombok.Data;

/**
 * @Project : easyrpc
 * @Description : RPC 请求实体类
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@Data
public class RpcRequest {

    //相当于 requestId
    private String traceId;

    //权限校验用
    private String appName;

    private String token;

    private String userId;

    // 调用的类名
    private String className;

    //调用的方法名
    private String methodName;

    // 参数类型
    private Class<?>[] parameterTypes;

    // 参数值
    private Object[] parameters;


}
