package lyra.klass.special;

/**
 * 用于lambda表达式内更改表达式外的局部变量值
 * 
 * @param <T>
 */
public class TypeWrapper<T> {
	public T value;

	public TypeWrapper(T value) {
		this.value = value;
	}

	public static <T> TypeWrapper<T> wrap(T value) {
		return new TypeWrapper<T>(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TypeWrapper wrap() {
		return new TypeWrapper(null);
	}
}
