package lyra.lang.internal;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lyra.lang.Reflection;
import sun.reflect.ReflectionFactory;

/**
 * 没有依赖任何lyra.lang.base外部类，仅使用标准API的反射类
 */
public class ReflectionBase {
	public static final ReflectionFactory reflectionFactory;
	public static final Class<?> jdk_internal_reflect_Reflection;

	/**
	 * 反射的过滤字段表，位于该map的字段无法被反射获取
	 */
	private static VarHandle Reflection_fieldFilterMap;

	/**
	 * 反射的过滤方法表，位于该map的方法无法被反射获取
	 */
	private static VarHandle Reflection_methodFilterMap;

	/**
	 * 64位JVM的offset从12开始为数据段，此处为java.lang.reflect.AccessibleObject的boolean override成员，将该成员覆写为true可以无视权限调用Method、Field、Constructor
	 */
	private static VarHandle java_lang_reflect_AccessibleObject_override;

	static {
		reflectionFactory = ReflectionFactory.getReflectionFactory(); // 最优先获取java.lang.reflect.AccessibleObject的override以获取访问权限
		HandleBase.TRUSTED_LOOKUP = HandleBase.allocateTrustedLookup();
		java_lang_reflect_AccessibleObject_override = HandleBase.internalFindVarHandle(AccessibleObject.class, "override", boolean.class);
		Class<?> ReflectionClass = null;
		try {
			ReflectionClass = Class.forName("jdk.internal.reflect.Reflection");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		jdk_internal_reflect_Reflection = ReflectionClass;
		Reflection_fieldFilterMap = HandleBase.internalFindStaticVarHandle(jdk_internal_reflect_Reflection, "fieldFilterMap", Map.class);
		Reflection_methodFilterMap = HandleBase.internalFindStaticVarHandle(jdk_internal_reflect_Reflection, "methodFilterMap", Map.class);
	}

	/**
	 * 供HandleBase初始化本类
	 */
	static final void init() {

	}

	/**
	 * 初始化一个类
	 * 
	 * @param cls
	 * @throws ClassNotFoundException
	 */
	public static final void initialize_class(Class<?> cls) throws ClassNotFoundException {
		Class.forName(cls.getName(), true, cls.getClassLoader());
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
			return (T) newConstructor.newInstance(args);// 通过该方法拿到的构造函数默认可以直接调用，如果再手动setAccessible(true)则会报错无权限，需要开放模块。
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

	/**
	 * 在没有反射字段过滤器的环境下操作
	 * 
	 * @param op
	 */
	public static final void noReflectionFieldFilter(Runnable op) {
		Map<Class<?>, Set<String>> filterMap = getReflectionFieldFilter();
		removeReflectionFieldFilter();
		op.run();
		setReflectionFieldFilter(filterMap);
	}

	/**
	 * 不经过反射过滤获取字段
	 * 
	 * @param cls
	 * @param field_name
	 * @return
	 */
	public static final Field fieldNoReflectionFilter(Class<?> cls, String field_name) {
		Field f = null;
		Map<Class<?>, Set<String>> filterMap = getReflectionFieldFilter();
		removeReflectionFieldFilter();
		f = Reflection.getField(cls, field_name);
		setReflectionFieldFilter(filterMap);
		return f;
	}

	/**
	 * 获取反射过滤的字段
	 * 
	 * @return
	 */
	public static Map<Class<?>, Set<String>> getReflectionFieldFilter() {
		return (Map<Class<?>, Set<String>>) Reflection_fieldFilterMap.get();
	}

	/**
	 * 获取反射过滤的方法
	 * 
	 * @return
	 */
	public static Map<Class<?>, Set<String>> getReflectionMethodFilter() {
		return (Map<Class<?>, Set<String>>) Reflection_methodFilterMap.get();
	}

	/**
	 * 设置字段反射过滤，Java设置了一些非常核心的类无法通过反射获取即设置反射过滤，此操作将会替换原有的过滤限制。危险操作。
	 */
	public static void setReflectionFieldFilter(Map<Class<?>, Set<String>> filter_map) {
		Reflection_fieldFilterMap.set(filter_map);
	}

	/**
	 * 设置方法反射过滤，Java设置了一些非常核心的类无法通过反射获取即设置反射过滤，此操作将会替换原有的过滤限制。危险操作。
	 */
	public static void setReflectionMethodFilter(Map<Class<?>, Set<String>> filter_map) {
		Reflection_methodFilterMap.set(filter_map);
	}

	/**
	 * 移除反射过滤，使得全部字段均可通过反射获取，Java设置了一些非常核心的类无法通过反射获取即设置反射过滤，此操作将会移除该限制。危险操作。
	 */
	public static void removeReflectionFieldFilter() {
		setReflectionFieldFilter(new HashMap<Class<?>, Set<String>>());
	}

	/**
	 * 移除反射过滤，使得全部方法均可通过反射获取，Java设置了一些非常核心的类无法通过反射获取即设置反射过滤，此操作将会移除该限制。危险操作。
	 */
	public static void removeReflectionMethodFilter() {
		setReflectionMethodFilter(new HashMap<Class<?>, Set<String>>());
	}
}
