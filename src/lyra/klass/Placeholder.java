package lyra.klass;

public final class Placeholder {

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
}
