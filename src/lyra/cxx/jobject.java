package lyra.cxx;

import java.lang.invoke.MethodHandle;

import lyra.internal.oops.markWord;
import lyra.lang.Callable;
import lyra.lang.InternalUnsafe;
import lyra.lang.internal.HandleBase;

public abstract class jobject {
	/**
	 * 在给定指针处调用对象的构造函数，不会设置对象头，仅初始化字段。父类的构造函数也会被调用。
	 * 
	 * @param ptr
	 * @param target_type
	 * @param arg_types
	 * @return
	 */
	public static final pointer placement_new(pointer ptr, Class<?> target_type, Class<?>[] arg_types, Object... args) {
		Object target = ptr.cast(target_type).dereference();
		MethodHandle constructor = Callable.invokeVirtualConstructor(target_type, arg_types);
		try {
			HandleBase.invoke(constructor, target, args);
		} catch (Throwable ex) {
			System.err.println("Placement new for " + target_type + " failed");
			ex.printStackTrace();
		}
		return ptr;
	}

	public static final pointer placement_new(pointer ptr, Class<?>[] arg_types, Object... args) {
		Class<?> target_type = ptr.ptr_jtype;
		MethodHandle constructor = Callable.invokeVirtualConstructor(target_type, arg_types);
		try {
			HandleBase.invoke(constructor, ptr.dereference(), args);
		} catch (Throwable ex) {
			System.err.println("Placement new for " + target_type + " failed");
			ex.printStackTrace();
		}
		return ptr;
	}

	/**
	 * 在已实例化的对象上再次调用构造函数，不会设置对象头，仅初始化字段。父类的构造函数也会被调用。
	 * 
	 * @param jobject
	 * @param arg_types
	 * @param args
	 * @return
	 */
	public static final Object placement_new(Object jobject, Class<?>[] arg_types, Object... args) {
		Class<?> target_type = jobject.getClass();
		MethodHandle constructor = Callable.invokeVirtualConstructor(target_type, arg_types);
		try {
			HandleBase.invoke(constructor, jobject, args);
		} catch (Throwable ex) {
			System.err.println("Placement new for " + target_type + " failed");
			ex.printStackTrace();
		}
		return jobject;
	}

	public static final Object copy(Object jobject) {
		Class<?> clazz = jobject.getClass();
		Object o = InternalUnsafe.allocateInstance(clazz);
		InternalUnsafe.copyMemory0(jobject, markWord.HEADER_BYTE_LENGTH, o, markWord.HEADER_BYTE_LENGTH, jtype.sizeof_object(clazz) - markWord.HEADER_BYTE_LENGTH);// 只拷贝字段，不覆盖对象头
		return o;
	}
}
