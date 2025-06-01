package lyra.lang;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 反射工具，大部分功能可以直接使用Manipulator调用
 */
public abstract class Reflection {
	private static MethodHandle Class_getDeclaredFields0;// Class.getDeclaredFields0无视反射访问权限获取字段
	private static MethodHandle Class_getDeclaredMethods0;
	private static MethodHandle Class_getDeclaredConstructors0;
	private static MethodHandle Class_searchFields;
	private static MethodHandle Class_searchMethods;
	private static MethodHandle Class_getConstructor0;
	private static MethodHandle Class_forName0;

	static {
		try {
			Class_getDeclaredFields0 = Handles.findSpecialMethodHandle(Class.class, Class.class, "getDeclaredFields0", Field[].class, boolean.class);
			Class_getDeclaredMethods0 = Handles.findSpecialMethodHandle(Class.class, Class.class, "getDeclaredMethods0", Method[].class, boolean.class);
			Class_getDeclaredConstructors0 = Handles.findSpecialMethodHandle(Class.class, Class.class, "getDeclaredConstructors0", Constructor[].class, boolean.class);
			Class_searchFields = Handles.findStaticMethodHandle(Class.class, "searchFields", Field.class, Field[].class, String.class);
			Class_searchMethods = Handles.findStaticMethodHandle(Class.class, "searchMethods", Method.class, Method[].class, String.class, Class[].class);
			Class_getConstructor0 = Handles.findSpecialMethodHandle(Class.class, Class.class, "getConstructor0", Constructor.class, Class[].class, int.class);
			Class_forName0 = Handles.findStaticMethodHandle(Class.class, "forName0", Class.class, String.class, boolean.class, ClassLoader.class, Class.class);
		} catch (SecurityException | IllegalArgumentException ex) {
			ex.printStackTrace();
		}
	}

	public static Field setAccessible(Class<?> cls, String field_name, boolean accessible) {
		Field f = Reflection.getField(cls, field_name);
		InternalUnsafe.setAccessible(f, accessible);
		return f;
	}

	/**
	 * 获取对象定义的字段原对象，无视反射过滤和访问权限，直接调用JVM内部的native方法获取全部字段。<br>
	 * 注意：本方法没有拷贝对象，因此对返回字段的任何修改都将反应在反射系统获取的所有的复制对象中
	 * 
	 * @param clazz 要获取的类
	 * @return 字段列表
	 */
	public static Field[] getDeclaredFields(Class<?> clazz, boolean publicOnly) {
		try {
			return (Field[]) Class_getDeclaredFields0.invokeExact(clazz, publicOnly);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Field[] getDeclaredFields(Class<?> clazz) {
		return getDeclaredFields(clazz, false);
	}

	public static Field getDeclaredField(Class<?> clazz, String field_name) {
		try {
			return (Field) Class_searchFields.invokeExact(getDeclaredFields(clazz), field_name);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取对象定义的方法原对象，无视反射过滤和访问权限，直接调用JVM内部的native方法获取全部方法
	 * 
	 * @param clazz 要获取的类
	 * @return 字段列表
	 */
	public static Method[] getDeclaredMethods(Class<?> clazz, boolean publicOnly) {
		try {
			return (Method[]) Class_getDeclaredMethods0.invokeExact(clazz, false);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Method[] getDeclaredMethods(Class<?> clazz) {
		return getDeclaredMethods(clazz, false);
	}

	public static Method getDeclaredMethod(Class<?> clazz, String method_name, Class<?>... arg_types) {
		try {
			return (Method) Class_searchMethods.invokeExact(getDeclaredMethods(clazz), method_name, arg_types);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static <T> Constructor<T>[] getDeclaredConstructors(Class<?> clazz, boolean publicOnly) {
		try {
			return (Constructor<T>[]) Class_getDeclaredConstructors0.invokeExact(clazz, publicOnly);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取构造函数
	 * 
	 * @param <T>
	 * @param clazz
	 * @param which    java.lang.reflect.Member接口中的访问类型，Member.DECLARED为全部定义的构造函数，Member.PUBLIC为public的构造函数
	 * @param argTypes 构造函数的参数类型
	 * @return
	 */
	public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, int which, Class<?>... argTypes) {
		try {
			return (Constructor<T>) Class_getConstructor0.invokeExact(clazz, argTypes, which);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... argTypes) {
		return getDeclaredConstructor(clazz, Member.DECLARED, argTypes);
	}

	/**
	 * 查找类
	 * 
	 * @param name
	 * @param initialize
	 * @param loader
	 * @param caller
	 * @return
	 */
	public static Class<?> forName(String name, boolean initialize, ClassLoader loader, Class<?> caller) {
		try {
			return (Class<?>) Class_forName0.invokeExact(name, initialize, loader, caller);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Class<?> forName(String name, boolean initialize) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		return forName(name, initialize, caller.getClassLoader(), caller);
	}

	public static Class<?> forName(String name) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		return forName(name, true, caller.getClassLoader(), caller);
	}

	public static Class<?> forNameSys(String name, boolean initialize) {
		return forName(name, initialize, null, Class.class);
	}

	public static Class<?> forNameSys(String name) {
		return forNameSys(name, true);
	}

	public static String getClassNameWithoutPackage(String full_name) {
		return full_name.substring(full_name.lastIndexOf('.') + 1);
	}

	public static String getClassNameWithoutPackage(Object obj) {
		return getClassNameWithoutPackage(obj.getClass().getName());
	}

	public static String getPackageName(String full_name) {
		return full_name.substring(0, full_name.lastIndexOf('.'));
	}

	/**
	 * 查询类成员，如果该类没有则递归查找父类
	 */
	public static Field getField(Object obj, String name) {
		Class<?> cls;
		if (obj instanceof Class<?> c)
			cls = c;
		else
			cls = obj.getClass();
		Field result = getDeclaredField(cls, name);
		if (result == null) {
			Class<?> supercls = cls.getSuperclass();
			if (supercls == null) {
				System.err.println("Cannot find field " + name);
				return null;
			} else
				return getField(supercls, name);
		}
		return result;
	}

	public static Object getValue(Object obj, Field field) {
		if (obj == null || field == null)
			return null;
		try {
			InternalUnsafe.setAccessible(field, true);
			return field.get(obj);
		} catch (IllegalAccessException ex) {
			System.err.println("Reflection throws IllegalAccessException reading field " + field);
			ex.printStackTrace();
		}
		return null;
	}

	public static Object getValue(Object obj, String field) {
		return getValue(obj, getField(obj, field));
	}

	public static boolean setValue(Object obj, Field field, Object value) {
		if (obj == null || field == null)
			return false;
		try {
			InternalUnsafe.setAccessible(field, true);
			field.set(obj, value);
		} catch (IllegalAccessException ex) {
			System.err.println("Reflection throws IllegalAccessException writing field " + field + " with value " + value + " in object " + obj.toString());
			ex.printStackTrace();
			;
			return false;
		}
		return true;
	}

	public static boolean setValue(Object obj, String field, Object value) {
		return setValue(obj, getField(obj, field), value);
	}

	public static String methodDescription(String name, Class<?>... arg_types) {
		String method_description = name + '(';
		if (arg_types != null)
			for (int a = 0; a < arg_types.length; ++a) {
				method_description += arg_types[a].getName();
				if (a != arg_types.length - 1)
					method_description += ", ";
			}
		method_description += ')';
		return method_description;
	}

	// 只搜寻该类自己的方法
	public static Method getMethodSelf(Class<?> clazz, String name, Class<?>... arg_types) {
		return getDeclaredMethod(clazz, name, arg_types == null ? (new Class<?>[] {}) : arg_types);
	}

	// 只搜寻该类及其父类、实现接口的方法
	public static Method getMethodDirectInherited(Class<?> clazz, String name, Class<?>... arg_types) {
		Method result = getDeclaredMethod(clazz, name, arg_types == null ? (new Class<?>[] {}) : arg_types);
		if (result == null) {
			Class<?> supercls = clazz.getSuperclass();
			Class<?>[] interfaces = clazz.getInterfaces();
			if (supercls == null && interfaces.length == 0) {
				System.err.println("Cannot find method " + name + " in neither super class nor implemented interfaces");
				return null;
			} else {
				Method method = getMethodSelf(supercls, name, arg_types);
				if (method != null)// 如果父类有方法则优先返回父类的方法
					return method;
				else {// 从接口中搜寻方法
					for (Class<?> i : interfaces)
						method = getMethodSelf(i, name, arg_types);
				}
				return method;
			}
		}
		return result;
	}

	public static Method getMethod(Object obj, String name, Class<?>... arg_types) {
		Class<?> cls;
		if (obj instanceof Class<?> c)
			cls = c;
		else
			cls = obj.getClass();
		Method method = null;
		ArrayList<ArrayList<Class<?>>> chain = resolveInheritImplamentChain(cls);
		FOUND: for (int depth = 0; depth < chain.size(); ++depth) {
			ArrayList<Class<?>> equal_depth_classes = chain.get(depth);
			for (int i = 0; i < equal_depth_classes.size(); ++i)
				if ((method = getMethodSelf(equal_depth_classes.get(i), name, arg_types)) != null)
					break FOUND;
		}
		if (method == null) {
			System.err.println("Method " + methodDescription(name, arg_types) + " not found in class " + cls.getName() + " or its parents");
		}
		return method;
	}

	public static void resolveInheritChain(Class<?> clazz, ArrayList<Class<?>> chain) {
		chain.add(clazz);
		Class<?> supercls = clazz.getSuperclass();
		if (supercls != null)
			resolveInheritChain(supercls, chain);
	}

	public static Class<?>[] resolveInheritChain(Class<?> clazz) {
		ArrayList<Class<?>> chain = new ArrayList<>();
		resolveInheritChain(clazz, chain);
		return chain.toArray(new Class<?>[chain.size()]);
	}

	public static ArrayList<ArrayList<Class<?>>> resolveInheritImplamentChain(Class<?> self, int current_depth, ArrayList<ArrayList<Class<?>>> chain) {
		ArrayList<Class<?>> current_depth_classes = null;
		while (current_depth_classes == null)
			try {
				current_depth_classes = chain.get(current_depth);
			} catch (IndexOutOfBoundsException ex) {
				chain.add(new ArrayList<>());
			}
		current_depth_classes.add(self);
		Class<?> supercls = self.getSuperclass();
		if (supercls != null)
			resolveInheritImplamentChain(supercls, current_depth + 1, chain);
		Class<?>[] interfaces = self.getInterfaces();
		for (Class<?> i : interfaces)
			resolveInheritImplamentChain(i, current_depth + 1, chain);
		return chain;
	}

	public static ArrayList<ArrayList<Class<?>>> resolveInheritImplamentChain(Class<?> clazz) {
		ArrayList<ArrayList<Class<?>>> chain = new ArrayList<>();
		return resolveInheritImplamentChain(clazz, 0, chain);
	}

	/**
	 * 推断每个参数的类型，每个参数的类型均是一个数组，为该类型的继承链
	 * 
	 * @param args 要推断的参数列表
	 * @return
	 */
	public static Class<?>[][] resolveArgTypesChain(Object... args) {
		Class<?>[][] arg_types = new Class<?>[args.length][];
		for (int idx = 0; idx < args.length; ++idx)
			arg_types[idx] = resolveInheritChain(args[idx].getClass());
		return arg_types;
	}

	/**
	 * 推断每个参数的类型，每个参数的类型均是传入参数本类型，不包括其父类继承链
	 * 
	 * @param args 要推断的参数列表
	 * @return
	 */
	public static Class<?>[] resolveArgTypes(Object... args) {
		Class<?>[] arg_types = new Class<?>[args.length];
		for (int idx = 0; idx < args.length; ++idx)
			arg_types[idx] = args[idx].getClass();
		return arg_types;
	}

	public static Object invoke(Object obj, String method_name, Class<?>[] arg_types, Object... args) {
		Method method = getMethod(obj, method_name, arg_types);
		try {
			InternalUnsafe.setAccessible(method, true);
			return method.invoke(obj, args);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			System.err.println("Reflection throws exception invoking method " + method_name + " with arguments " + args + " in object " + obj.toString());
			ex.printStackTrace();
			return null;
		}
	}

	public static Constructor<?> getConstructor(Object obj, Class<?>... arg_types) {
		Class<?> cls;
		if (obj instanceof Class<?> c)
			cls = c;
		else
			cls = obj.getClass();
		Constructor<?> result = getDeclaredConstructor(cls, arg_types == null ? (new Class<?>[] {}) : arg_types);
		if (result == null) {
			Class<?> supercls = cls.getSuperclass();
			return supercls == null ? null : getConstructor(supercls, arg_types);
		}
		return result;
	}

	/**
	 * 利用反射调用构造函数
	 * 
	 * @param obj  目标类型的对象实例或Class<T>
	 * @param args
	 * @return
	 */
	public static Object construct(Object obj, Class<?>[] arg_types, Object... args) {
		Constructor<?> constructor = getConstructor(obj, arg_types);
		try {
			InternalUnsafe.setAccessible(constructor, true);
			return constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			System.err.println("Reflection throws exception contructing " + obj.toString() + " with arguments " + args);
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断某个类是否具有指定超类，支持向上递归查找超类
	 * 
	 * @param clazz       要判断是否有超类的类
	 * @param super_class 超类
	 * @return clazz具有超类super_class则返回true，否则返回false
	 */
	public static boolean hasSuperClass(Class<?> clazz, Class<?> super_class) {
		Class<?> supercls = clazz.getSuperclass();
		if (supercls == super_class)
			return true;
		return supercls == null ? false : hasSuperClass(supercls, super_class);
	}

	/**
	 * f所声明的类型是否是type或者其子类
	 * 
	 * @param f
	 * @param type
	 * @return
	 */
	public static boolean is(Field f, Class<?> type) {
		return type.isAssignableFrom(f.getType());
	}
}
