package io.github.biezhi.redisdqueue.utils;

import io.github.biezhi.redisdqueue.core.Callback;
import lombok.experimental.UtilityClass;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * ClassUtil
 *
 * @author biezhi
 * @date 2019/11/25
 */
@UtilityClass
public class ClassUtil {

	public Class getGenericType(Callback callback) {
		Class<?> targetClass = callback.getClass();
		Type[]   interfaces  = targetClass.getGenericInterfaces();
		Class<?> superclass  = targetClass.getSuperclass();
		while ((Objects.isNull(interfaces) || 0 == interfaces.length) &&
				Objects.nonNull(superclass)) {
			interfaces = superclass.getGenericInterfaces();
			superclass = targetClass.getSuperclass();
		}
		if (Objects.nonNull(interfaces)) {
			for (Type type : interfaces) {
				if (type instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) type;
					if (Objects.equals(parameterizedType.getRawType(), Callback.class)) {
						Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
						if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
							return (Class) actualTypeArguments[0];
						} else {
							return Object.class;
						}
					}
				}
			}
			return Object.class;
		} else {
			return Object.class;
		}
	}

	/**
	 * 是否为包装类型
	 *
	 * @param clazz 类
	 * @return 是否为包装类型
	 */
	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return BasicType.wrapperPrimitiveMap.containsKey(clazz);
	}

	/**
	 * 是否为基本类型（包括包装类和原始类）
	 *
	 * @param clazz 类
	 * @return 是否为基本类型
	 */
	public static boolean isBasicType(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	public static Object convert(Class clazz, String value) {
		if (String.class == clazz) return value;
		if (Boolean.class == clazz) return Boolean.parseBoolean(value);
		if (Byte.class == clazz) return Byte.parseByte(value);
		if (Short.class == clazz) return Short.parseShort(value);
		if (Integer.class == clazz) return Integer.parseInt(value);
		if (Long.class == clazz) return Long.parseLong(value);
		if (Float.class == clazz) return Float.parseFloat(value);
		if (Double.class == clazz) return Double.parseDouble(value);
		return value;
	}

}
