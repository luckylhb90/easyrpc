package com.luckylhb.easyrpc.common.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.collect.Maps;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
public class SerializationUtil {

    private static Map<Class<?>, Schema> cachedSchema = Maps.newConcurrentMap();
    private static Objenesis objenesis = new ObjenesisStd(true);

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if(schema == null) {
            schema = RuntimeSchema.getSchema(clazz);
            if(schema != null) {
                cachedSchema.put(clazz, schema);
            }
        }
        return schema;
    }

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] serializer(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserializer (byte[] data, Class<T> clazz) {
        T obj = objenesis.newInstance(clazz);
        Schema<T> schema = getSchema(clazz);
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }


}
