package lyra.object;

public final class Placeholders {

	@SuppressWarnings("unchecked")
	public static final <T> T Undefined(long castTypeKlassWord) {
		return (T) ObjectManipulator.cast(new Object(), castTypeKlassWord);
	}

	/**
	 * 用于作为Object类型的static final变量初始值，防止变量字面值或null值被内联
	 * 
	 * @param <T>
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T Undefined(T destTypeObj) {
		return (T) ObjectManipulator.cast(new Object(), destTypeObj);
	}

	@SuppressWarnings("unchecked")
	public static final <T> T Undefined(Object obj, Class<?> castType) {
		return (T) ObjectManipulator.cast(new Object(), castType);
	}

	@SuppressWarnings("unchecked")
	public static final <T> T Undefined(Object obj, String castType) {
		return (T) ObjectManipulator.cast(new Object(), castType);
	}

	/**
	 * 防止Object类型的static final变量初始null字面值被内联<br>
	 * 当跨类修改目标类字段，且static final Object被初始化为null字面值时：如果不在修改之前在本类使用这个变量，那么这个值的修改就不会成功（会被内联）。
	 * 
	 * @param var
	 * @return
	 */
	public static final void NotInlined(Object var) {

	}

	/**
	 * 任何枚举类型的占位符
	 */
	public static enum AnyEnumeration {
		Null;

		/**
		 * 将该占位符转换为实际的枚举类型值
		 * 
		 * @param <T>
		 * @param targetClass
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final <T extends Enum<T>> T as(Class<T> targetClass) {
			return (T) ObjectManipulator.cast(this, targetClass);
		}

		/**
		 * 将一个枚举类型值包装为占位符
		 * 
		 * @param <T>
		 * @param enumeration
		 * @return
		 */
		public static final <T extends Enum<T>> AnyEnumeration pack(T enumeration) {
			return (AnyEnumeration) ObjectManipulator.cast(enumeration, AnyEnumeration.class);
		}
	}
}
