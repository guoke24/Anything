package com.guohao.anything.Hook;

import java.lang.reflect.Field;

public class FieldUtil {

    /**
     * 获得一个动态的具体的字段
     *
     * @param clazz 类型
     * @param target 类实例
     * @param name 字段名
     * @return
     * @throws Exception
     */
    public static Object getField(Class clazz, Object target, String name) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * 获得一个静态的具体的字段
     *
     * @param clazz 类型
     * @param name 字段名
     * @return
     * @throws Exception
     */
    public static Field getField(Class clazz, String name) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /**
     * 替换一个动态的具体的字段值为 value
     *
     * @param clazz 类型
     * @param target 类实例
     * @param name 字段名
     * @param value 替换值
     * @throws Exception
     */
    public static void setField(Class clazz, Object target, String name, Object value) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
