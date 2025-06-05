package lyra.lang.base;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import sun.reflect.ReflectionFactory;

/**
 * 没有依赖任何lyra.lang.base外部类，仅使用标准API的反射类
 */
public class ReflectionBase {
	public static final ReflectionFactory reflectionFactory;

	/**
	 * 64位JVM的offset从12开始为数据段，此处为java.lang.reflect.AccessibleObject的boolean override成员，将该成员覆写为true可以无视权限调用Method、Field、Constructor
	 */
	private static final VarHandle java_lang_reflect_AccessibleObject_override;

	static {
		reflectionFactory = ReflectionFactory.getReflectionFactory();
		// 最优先获取java.lang.reflect.AccessibleObject的override以获取访问权限
		java_lang_reflect_AccessibleObject_override = HandleBase.internalFindVarHandle(AccessibleObject.class, "override", boolean.class);
	}

	/**
	 * 使用反序列化时调用目标构造函数构造新实例，ReflectionFactory具有调用所有构造函数的权限，因此可以构建任何类的实例。
	 * 
	 * @param target
	 * @param targetConstructor
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T delegateConstructInstance(Class<T> target, Constructor<?> targetConstructor, Object... args) {
		try {
			Constructor<?> newConstructor = (Constructor<?>) reflectionFactory.newConstructorForSerialization(target, targetConstructor);
			return (T) newConstructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用反序列化创建一个新对象，该对象为执行任何构造函数，仅分配了内存。
	 * 
	 * @param target
	 * @return
	 */
	public static final <T> T delegateAllocateInstance(Class<T> target) {
		try {
			return delegateConstructInstance(target, Object.class.getConstructor());
		} catch (IllegalArgumentException | SecurityException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 无视权限设置是否可访问
	 * 
	 * @param <AO>
	 * @param accessibleObj
	 * @param accessible
	 * @return
	 */
	public static <AO extends AccessibleObject> AO setAccessible(AO accessibleObj, boolean accessible) {
		java_lang_reflect_AccessibleObject_override.set(accessibleObj, accessible);
		return accessibleObj;
	}

	public static <AO extends AccessibleObject> AO setAccessible(AO accessibleObj) {
		return setAccessible(accessibleObj, true);
	}
}
