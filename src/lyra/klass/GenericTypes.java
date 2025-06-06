package lyra.klass;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericTypes {
	/**
	 * 字段的泛型参数是否是某些类型
	 * 
	 * @param f
	 * @param types
	 * @return
	 */
	public static boolean is(Field f, int[] indices, Class<?>... types) {
		Class<?>[] classes = classes(f, indices);
		if (classes.length != types.length)
			return false;
		for (int idx = 0; idx < types.length; ++idx) {
			if (!classes[idx].getTypeName().equals(types[idx].getTypeName()))
				return false;
		}
		return true;
	}

	/**
	 * 返回第一层的泛型参数是否匹配给定类列表
	 * 
	 * @param f
	 * @param types
	 * @return
	 */
	public static boolean is(Field f, Class<?>... types) {
		return is(f, new int[] {}, types);
	}

	/**
	 * 匹配前N个泛型参数是否和types一致
	 * 
	 * @param f
	 * @param types
	 * @return
	 */
	public static boolean startWith(Field f, int[] indices, Class<?>... types) {
		Class<?>[] classes = classes(f, indices);
		for (int idx = 0; idx < types.length; ++idx) {
			if (!classes[idx].getTypeName().equals(types[idx].getTypeName()))
				return false;
		}
		return true;
	}

	public static boolean startWith(Field f, Class<?>... types) {
		return startWith(f, new int[] {}, types);
	}

	/**
	 * 获取指定字段的指定嵌套深度的泛型参数的Class<?>
	 * 
	 * @param f
	 * @return
	 */
	public static Class<?>[] classes(Field f, int... indices) {
		Type currentType = f.getGenericType();
		Type[] actualTypeArguments = null;
		for (int nest_depth = 0; nest_depth < indices.length; ++nest_depth) {
			// 没有泛型参数则直接返回
			if (currentType instanceof ParameterizedType currentParameterizedType) {
				int nest_idx = indices[nest_depth];
				actualTypeArguments = currentParameterizedType.getActualTypeArguments();
				// 索引超出该深度的泛型参数个数
				if (nest_idx < 0 || nest_idx >= actualTypeArguments.length) {
					return null;
				}
				// 除非是最后一层，否则继续向下查找
				currentType = actualTypeArguments[nest_idx];
			} else
				return null;
		}
		// 获取最终深度的特定索引的全部泛型参数
		actualTypeArguments = ((ParameterizedType) currentType).getActualTypeArguments();
		Class<?>[] classes = new Class[actualTypeArguments.length];
		for (int idx = 0; idx < actualTypeArguments.length; ++idx) {
			if (actualTypeArguments[idx] instanceof Class cls) {
				classes[idx] = cls;
				continue;
			}
			// 如果参数还是泛型类，就直接getRawType()
			else if (actualTypeArguments[idx] instanceof ParameterizedType parameterizedType) {
				Type rawType = parameterizedType.getRawType();
				if (rawType instanceof Class cls) {
					classes[idx] = cls;
					continue;
				}
			} else
				classes[idx] = Object.class;
		}
		return classes;
	}

	public static Class<?>[] classes(Class<?> target) {
		Type directSuperClassGenericType = target.getGenericSuperclass();
		if (directSuperClassGenericType instanceof ParameterizedType superParameterizedType) {
			Type[] actualTypeArguments = superParameterizedType.getActualTypeArguments();
			Class<?>[] classes = new Class[actualTypeArguments.length];
			for (int idx = 0; idx < actualTypeArguments.length; ++idx) {
				if (actualTypeArguments[idx] instanceof Class cls) {
					classes[idx] = cls;
					continue;
				}
				// 如果参数还是泛型类，就直接getRawType()
				else if (actualTypeArguments[idx] instanceof ParameterizedType parameterizedType) {
					Type rawType = parameterizedType.getRawType();
					if (rawType instanceof Class cls) {
						classes[idx] = cls;
						continue;
					}
				} else
					classes[idx] = Object.class;
			}
			return classes;
		} else if (directSuperClassGenericType instanceof Class directSuperClassGenericClass)
			return new Class<?>[] { directSuperClassGenericClass };
		return new Class<?>[] {};
	}
}
