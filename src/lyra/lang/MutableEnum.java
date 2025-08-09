package lyra.lang;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import lyra.klass.special.CRTP;
import lyra.lang.internal.HandleBase;

/**
 * 修改枚举、创建新的枚举值实例
 * 
 * @param <T>
 */
public interface MutableEnum<T extends Enum<T>> extends CRTP<T> {

	public default T of(Class<?>[] arg_types, Object... args) {
		return of("$tmp", -1, arg_types, args);
	}

	public default T of(String name, int ordinal, Class<?>[] arg_types, Object... args) {
		return of(this.getDerivedClass(), name, ordinal, arg_types, args);
	}

	/**
	 * 新建一个枚举值实例，该枚举值为自由对象，没有被添加到枚举类的values()中。
	 * 
	 * @param <T>
	 * @param targetEnum
	 * @param arg_types
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T of(Class<T> targetEnum, String name, int ordinal, Class<?>[] arg_types, Object... args) {
		MethodHandle constructor = HandleBase.findConstructor(targetEnum, enumConstructorArgTypes(arg_types));
		try {
			return (T) constructor.invokeWithArguments(Arrays.cat(name, ordinal, args));
		} catch (Throwable ex) {
			System.err.println("Create enum instance failed.");
			ex.printStackTrace();
		}
		return null;
	}

	public static <T extends Enum<T>> T of(Class<T> targetEnum, Class<?>[] arg_types, Object... args) {
		return of(targetEnum, "$tmp", -1, arg_types, args);
	}

	/**
	 * 通过定义的枚举的构造函数类型获取实际的构造函数类型。<br>
	 * 这是因为编译器会在枚举类的构造函数声明的构造函数参数最前方自动添加两个额外参数，如果不加入这两个额外参数，就找不到枚举的构造函数。<br>
	 * 自动添加的两个参数是String name：枚举值的字符串名称、int ordinal枚举值的序号，从0开始计数。
	 * 
	 * @param ctor_arg_types
	 * @return
	 */
	public static Class<?>[] enumConstructorArgTypes(Class<?>... ctor_arg_types) {
		return Arrays.cat(String.class, int.class, ctor_arg_types);
	}

	/**
	 * 为目标枚举设置values()
	 * 
	 * @param <T>
	 * @param targetEnum
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> void setValues(Class<T> targetEnum, T... values) {
		VarHandle __ENUM$VALUES = HandleBase.internalFindStaticVarHandle(targetEnum, "ENUM$VALUES", targetEnum.arrayType());
		__ENUM$VALUES.set(values);
	}
}
