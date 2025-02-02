package net.earthcomputer.clientcommands.util;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static Stream<Field> getAllFields(Class<?> clazz) {
        Stream.Builder<Field> builder = Stream.builder();
        Class<?> targetClass = clazz;
        while (targetClass.getSuperclass() != null) {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                builder.add(field);
            }
            targetClass = targetClass.getSuperclass();
        }
        return builder.build();
    }
}
