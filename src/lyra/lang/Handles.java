package lyra.lang;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import lyra.lang.internal.HandleBase;
import lyra.lang.internal.MemberName;
import lyra.lang.internal.ReflectionBase;

/**
 * 句柄操作相关，包括调用native方法<br>
 * 不依赖<br>
 * 
 * @implNote 该类未引用任何本库的类，需要最先初始化
 */
public class Handles extends HandleBase {
	public static final Lookup IMPL_LOOKUP;

	static {
		Lookup trusted_lookup = null;
		try {
			Field IMPL_LOOKUP_Field = Lookup.class.getDeclaredField("IMPL_LOOKUP");// 获取拥有所有权限的受信任的Lookup的唯一方法
			trusted_lookup = (Lookup) (ReflectionBase.setAccessible(IMPL_LOOKUP_Field, true).get(null));
		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
		trusted_lookup = allocateTrustedLookup();
		IMPL_LOOKUP = trusted_lookup;
	}

	/**
	 * 查找任意字节码行为的方法句柄(包括native)，从search_chain_start_subclazz开始查找，如果该类不存在方法则一直向上查找方法，直到在指定的超类search_chain_end_superclazz中也找不到方法时终止并抛出错误
	 * 句柄等价于Unsafe查找到的offset与base的组合，明确指定了一个内存中的方法地址
	 * 
	 * @param search_chain_start_subclazz 查找链起始类，也是要查找的对象，必须是search_chain_end_superclazz的子类
	 * @param search_chain_end_superclazz 查找链终止类
	 * @param type                        方法类型，包含返回值和参数类型
	 * @return 查找到的方法句柄
	 */
	public static MethodHandle findSpecialMethodHandle(Class<?> search_chain_start_subclazz, Class<?> search_chain_end_superclazz, String method_name, MethodType type) {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(search_chain_start_subclazz, IMPL_LOOKUP); // 获取该类所有的字节码行为的Lookup，即无视访问权限查找
			return lookup.findSpecial(search_chain_end_superclazz, method_name, type, search_chain_start_subclazz);
		} catch (IllegalAccessException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static MethodHandle findSpecialMethodHandle(Class<?> search_clazz, String method_name, MethodType type) {
		return findSpecialMethodHandle(search_clazz, search_clazz, method_name, type);
	}

	public static MethodHandle findSpecialMethodHandle(Class<?> search_chain_start_subclazz, Class<?> search_chain_end_superclazz, String method_name, Class<?> return_type, Class<?>... arg_types) {
		return findSpecialMethodHandle(search_chain_start_subclazz, search_chain_end_superclazz, method_name, MethodType.methodType(return_type, arg_types));
	}

	public static MethodHandle findSpecialMethodHandle(Class<?> search_clazz, String method_name, Class<?> return_type, Class<?>... arg_types) {
		return findSpecialMethodHandle(search_clazz, search_clazz, method_name, return_type, arg_types);
	}

	public static MethodHandle findVirtualMethodHandle(Class<?> clazz, String method_name, MethodType type) {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(clazz, IMPL_LOOKUP); // 获取该类所有的字节码行为的Lookup，即无视访问权限查找
			return lookup.findVirtual(clazz, method_name, type);
		} catch (IllegalAccessException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static MethodHandle findVirtualMethodHandle(Class<?> clazz, String method_name, Class<?> return_type, Class<?>... arg_types) {
		return findVirtualMethodHandle(clazz, method_name, MethodType.methodType(return_type, arg_types));
	}

	/**
	 * 查找静态函数的方法句柄
	 * 
	 * @param clazz
	 * @param method_name
	 * @param type
	 * @return
	 */
	public static MethodHandle findStaticMethodHandle(Class<?> clazz, String method_name, MethodType type) {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(clazz, IMPL_LOOKUP); // 获取该类所有的字节码行为的Lookup，即无视访问权限查找
			return lookup.findStatic(clazz, method_name, type);
		} catch (IllegalAccessException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static MethodHandle findStaticMethodHandle(Class<?> clazz, String method_name, Class<?> return_type, Class<?>... arg_types) {
		return findStaticMethodHandle(clazz, method_name, MethodType.methodType(return_type, arg_types));
	}

	public static VarHandle findVarHandle(Class<?> clazz, String field_name, Class<?> type) {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(clazz, IMPL_LOOKUP); // 获取该类所有的字节码行为的Lookup，即无视访问权限查找
			return lookup.findVarHandle(clazz, field_name, type);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static VarHandle findStaticVarHandle(Class<?> clazz, String field_name, Class<?> type) {
		MethodHandles.Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(clazz, IMPL_LOOKUP); // 获取该类所有的字节码行为的Lookup，即无视访问权限查找
			return lookup.findStaticVarHandle(clazz, field_name, type);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用反射获取目标信息并查找对应的方法句柄，仅支持查找本类的方法和构造函数。
	 * 
	 * @param clazz
	 * @param method_name
	 * @param return_type
	 * @param arg_types
	 * @return
	 */
	public static MethodHandle findMethodHandle(Class<?> clazz, String method_name, Class<?>... arg_types) {
		MethodHandle m = null;
		if (method_name.equals(MemberName.CONSTRUCTOR_NAME))
			m = HandleBase.findConstructor(clazz, arg_types);
		else {
			Method rm = Reflection.getDeclaredMethod(clazz, method_name, arg_types);
			if (Modifier.isStatic(rm.getModifiers()))
				m = Handles.findStaticMethodHandle(clazz, method_name, rm.getReturnType(), arg_types);
			else
				m = Handles.findVirtualMethodHandle(clazz, method_name, rm.getReturnType(), arg_types);
		}
		return m;
	}
}
