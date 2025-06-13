package lyra.klass;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import lyra.lang.Reflection;

public class GenericTypes {
	public static enum EntryType {
		INTERFACE, CLASS, RAW_TYPE, UPPER_BOUNDS, LOWER_BOUNDS
	}

	/**
	 * 单个类或上界类数组、下界类数组
	 */
	public static class Entry {
		public final EntryType type;
		private Class<?>[] result;

		Entry(EntryType type, Class<?>... result) {
			this.type = type;
			this.result = result;
		}

		public Class<?> singleType() {
			if (type == EntryType.INTERFACE | type == EntryType.CLASS || type == EntryType.RAW_TYPE)
				return result[0];
			else
				return null;
		}

		public Class<?> type() {
			return result[0];
		}

		public Class<?>[] upperBounds() {
			if (type == EntryType.UPPER_BOUNDS)
				return result;
			else
				return null;
		}

		public Class<?>[] lowerBounds() {
			if (type == EntryType.LOWER_BOUNDS)
				return result;
			else
				return null;
		}

		/**
		 * 判断是否result中存在任意一个Class<?>严格地是cls类
		 * 
		 * @param cls
		 * @return
		 */
		public boolean equalsAny(Class<?> cls) {
			for (Class<?> c : result)
				if (cls == c)
					return true;
			return false;
		}

		/**
		 * 判断是否result中存在任意一个Class<?>是cls或其子类
		 * 
		 * @param cls
		 * @return
		 */
		public boolean isAny(Class<?> cls) {
			for (Class<?> c : result)
				if (Reflection.is(c, cls))
					return true;
			return false;
		}
	}

	/**
	 * 字段的泛型参数是否是某些类型
	 * 
	 * @param f
	 * @param types
	 * @return
	 */
	public static boolean is(Field f, int[] indices, Class<?>... types) {
		Entry[] classes = classes(f, indices);
		if (classes.length != types.length)
			return false;
		for (int idx = 0; idx < types.length; ++idx) {
			if (!classes[idx].isAny(types[idx]))
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
		Entry[] classes = classes(f, indices);
		for (int idx = 0; idx < types.length; ++idx) {
			if (!classes[idx].isAny(types[idx]))
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
	public static Entry[] classes(Field f, int... indices) {
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
		Entry[] entries = new Entry[actualTypeArguments.length];
		for (int idx = 0; idx < actualTypeArguments.length; ++idx) {
			currentType = actualTypeArguments[idx];
			if (currentType instanceof Class cls) {
				entries[idx] = new Entry(EntryType.CLASS, cls);
				continue;
			}
			// 如果参数还是泛型类，就直接getRawType()
			else if (currentType instanceof ParameterizedType parameterizedType) {
				Type rawType = parameterizedType.getRawType();
				if (rawType instanceof Class cls) {
					entries[idx] = new Entry(EntryType.RAW_TYPE, cls);
					continue;
				}
			} else if (currentType instanceof WildcardType wildcardType) {
				Type[] upper_bounds = wildcardType.getUpperBounds();
				Type[] lower_bounds = wildcardType.getLowerBounds();
				if (upper_bounds.length != 0) {
					Class<?>[] upper_bounds_clsarr = new Class[upper_bounds.length];
					for (int i = 0; i < upper_bounds_clsarr.length; ++i) {
						upper_bounds_clsarr[idx] = resolveTypeClass(upper_bounds[i]);
					}
					entries[idx] = new Entry(EntryType.UPPER_BOUNDS, upper_bounds_clsarr);
				} else if (lower_bounds.length != 0) {
					Class<?>[] lower_bounds_clsarr = new Class[lower_bounds.length];
					for (int i = 0; i < lower_bounds_clsarr.length; ++i) {
						lower_bounds_clsarr[idx] = resolveTypeClass(lower_bounds[i]);
					}
					entries[idx] = new Entry(EntryType.LOWER_BOUNDS, lower_bounds_clsarr);
				}
				continue;
			} else
				entries[idx] = null;
		}
		return entries;
	}

	/**
	 * 如果currentType是Class<?>则直接返回class，如果是带泛型参数的class，则返回rawType
	 * 
	 * @param currentType
	 * @return
	 */
	public static Class<?> resolveTypeClass(Type currentType) {
		if (currentType instanceof Class cls) {
			return cls;
		}
		// 如果参数还是泛型类，就直接getRawType()
		else if (currentType instanceof ParameterizedType parameterizedType) {
			Type rawType = parameterizedType.getRawType();
			if (rawType instanceof Class cls) {
				return cls;
			}
		}
		return null;
	}

	@Deprecated
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
