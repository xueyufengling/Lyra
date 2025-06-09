package lyra.lang.base;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Modifier;
import java.lang.invoke.VarHandle;

/**
 * 没有依赖任何lyra.lang.base外部类，仅使用标准API的Handle类
 */
public class HandleBase {
	public static final int PUBLIC = Modifier.PUBLIC;
	public static final int PRIVATE = Modifier.PRIVATE;
	public static final int PROTECTED = Modifier.PROTECTED;
	public static final int PACKAGE = Modifier.STATIC;
	public static final int MODULE = PACKAGE << 1;
	public static final int UNCONDITIONAL = PACKAGE << 2;
	public static final int ORIGINAL = PACKAGE << 3;

	public static final int ALL_MODES = (PUBLIC | PRIVATE | PROTECTED | PACKAGE | MODULE | UNCONDITIONAL | ORIGINAL);
	public static final int FULL_POWER_MODES = (ALL_MODES & ~UNCONDITIONAL);

	public static final int TRUSTED = -1;

	static Lookup TRUSTED_LOOKUP;

	static {
		ReflectionBase.init();
	}

	/**
	 * Handle调用时try-catch外返回值，这些值永远不会被返回，但出于语法需要必须有一个返回值。
	 */
	public static final byte UNREACHABLE_BYTE = -1;
	public static final char UNREACHABLE_CHAR = 0;
	public static final short UNREACHABLE_SHORT = -1;
	public static final long UNREACHABLE_lONG = -1;
	public static final Object UNREACHABLE_REFERENCE = null;
	public static final boolean UNREACHABLE_BOOLEAN = false;
	public static final int UNREACHABLE_INT = -1;
	public static final double UNREACHABLE_DOUBLE = -1;
	public static final float UNREACHABLE_FLOAT = -1;

	/**
	 * 用于查找任何字段
	 * 
	 * @param clazz
	 * @param field_name
	 * @param type
	 * @return
	 */
	public static VarHandle internalFindVarHandle(Class<?> clazz, String field_name, Class<?> type) {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(clazz, TRUSTED_LOOKUP); // 获取该类所有的字节码行为的Lookup，即无视访问权限查找
			return lookup.findVarHandle(clazz, field_name, type);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
		return (VarHandle) UNREACHABLE_REFERENCE;
	}

	public static VarHandle internalFindStaticVarHandle(Class<?> clazz, String field_name, Class<?> type) {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(clazz, TRUSTED_LOOKUP); // 获取该类所有的字节码行为的Lookup，即无视访问权限查找
			return lookup.findStaticVarHandle(clazz, field_name, type);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
		return (VarHandle) UNREACHABLE_REFERENCE;
	}

	/**
	 * 查找构造函数
	 * 
	 * @param clazz
	 * @param field_name
	 * @param type
	 * @return
	 */
	public static MethodHandle internalFindConstructor(Class<?> clazz, Class<?>... arg_types) {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(clazz, TRUSTED_LOOKUP); // 获取该类所有的字节码行为的Lookup，即无视访问权限查找
			return lookup.findConstructor(clazz, MethodType.methodType(void.class, arg_types));
		} catch (IllegalAccessException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		return (MethodHandle) UNREACHABLE_REFERENCE;
	}

	/**
	 * 调用method方法，自动打包对象和参数
	 * 
	 * @param method
	 * @param obj
	 * @param args
	 * @return
	 */
	public static Object invoke(MethodHandle method, Object obj, Object... args) {
		Object[] wrapped_args = new Object[args.length + 1];
		wrapped_args[0] = obj;
		System.arraycopy(args, 0, wrapped_args, 1, args.length);
		try {
			return method.invokeWithArguments(wrapped_args);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_REFERENCE;
	}

	/**
	 * 使用ReflectionFactory的反序列化调用Lookup的构造函数新构建一个Lookup对象。<br>
	 * 
	 * @param lookupClass
	 * @param prevLookupClass
	 * @param allowedModes
	 * @return
	 */
	public static final Lookup allocateLookup(Class<?> lookupClass, Class<?> prevLookupClass, int allowedModes) {
		Lookup lookup = null;
		try {
			lookup = (Lookup) ReflectionBase.delegateConstructInstance(Lookup.class, Lookup.class.getDeclaredConstructor(Class.class, Class.class, int.class), lookupClass, prevLookupClass, allowedModes);
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
			ex.printStackTrace();
		}
		return lookup;
	}

	/**
	 * 构造一个TRUSTED的Lookup
	 * 
	 * @return
	 */
	public static final Lookup allocateTrustedLookup() {
		return allocateLookup(Object.class, null, HandleBase.TRUSTED);
	}

	/**
	 * 使用ReflectionFactory的反序列化调用Lookup的构造函数新构建一个Lookup对象。<br>
	 * 
	 * @return
	 */
	public static final Lookup allocateLookup(Class<?> lookupClass) {
		Lookup lookup = null;
		try {
			lookup = (Lookup) ReflectionBase.delegateConstructInstance(Lookup.class, Lookup.class.getDeclaredConstructor(Class.class), lookupClass);
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
			ex.printStackTrace();
		}
		return lookup;
	}

	public static final Lookup allocateLookup() {
		return allocateLookup(Object.class);
	}
}
