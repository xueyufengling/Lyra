package lyra.lang;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

import lyra.lang.base.HandleBase;
import lyra.lang.base.ReflectionBase;
import lyra.object.ObjectManipulator;

public class InternalUnsafe {
	private static Class<?> internalUnsafeClass;
	static Object internalUnsafe;

	private static Method objectFieldOffset$Field;// 没有检查的jdk.internal.misc.Unsafe.objectFieldOffset()
	private static Method objectFieldOffset$Class$String;
	private static Method staticFieldBase;
	private static Method staticFieldOffset;

	private static Method defineClass;
	private static Method allocateInstance;

	private static Method putReference;
	private static Method getReference;

	private static Method putLong;
	private static Method getLong;

	private static Method putBoolean;
	private static Method getBoolean;

	private static Method putInt;
	private static Method getInt;

	private static Method putDouble;
	private static Method getDouble;

	private static Method putFloat;
	private static Method getFloat;

	public static final long INVALID_FIELD_OFFSET = -1;

	private static final long UNREACHABLE_lONG = -1;
	private static final Object UNREACHABLE_REFERENCE = null;
	private static final boolean UNREACHABLE_BOOLEAN = false;
	private static final int UNREACHABLE_INT = -1;
	private static final double UNREACHABLE_DOUBLE = -1;
	private static final float UNREACHABLE_FLOAT = -1;

	static {
		VarHandle theInternalUnsafe;
		try {
			internalUnsafeClass = Class.forName("jdk.internal.misc.Unsafe");
			theInternalUnsafe = HandleBase.internalFindStaticVarHandle(internalUnsafeClass, "theUnsafe", internalUnsafeClass);
			internalUnsafe = theInternalUnsafe.get();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		if (internalUnsafe == null)
			System.err.println("Get jdk.internal.misc.Unsafe instance failed! Lyra library will be broken.");
		try {
			objectFieldOffset$Field = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("objectFieldOffset", Field.class), true);
			objectFieldOffset$Class$String = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("objectFieldOffset", Class.class, String.class), true);
			staticFieldBase = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("staticFieldBase", Field.class), true);
			staticFieldOffset = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("staticFieldOffset", Field.class), true);
			defineClass = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class), true);
			allocateInstance = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("allocateInstance", Class.class), true);
			putReference = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("putReference", Object.class, long.class, Object.class), true);
			getReference = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("getReference", Object.class, long.class), true);
			putLong = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("putLong", Object.class, long.class, long.class), true);
			getLong = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("getLong", Object.class, long.class), true);
			putBoolean = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("putBoolean", Object.class, long.class, boolean.class), true);
			getBoolean = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("getBoolean", Object.class, long.class), true);
			putInt = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("putInt", Object.class, long.class, int.class), true);
			getInt = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("getInt", Object.class, long.class), true);
			putDouble = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("putDouble", Object.class, long.class, double.class), true);
			getDouble = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("getDouble", Object.class, long.class), true);
			putFloat = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("putFloat", Object.class, long.class, float.class), true);
			getFloat = ReflectionBase.setAccessible(internalUnsafeClass.getDeclaredMethod("getFloat", Object.class, long.class), true);
		} catch (NoSuchMethodException | SecurityException ex) {
			ex.printStackTrace();
		}

	}

	public static final class Invoker {
		/**
		 * 调用internalUnsafe的方法
		 * 
		 * @param method_name 方法名称
		 * @param arg_types   参数类型
		 * @param args        实参
		 * @return
		 */
		public static final Object invoke(String method_name, Class<?>[] arg_types, Object... args) {
			try {
				return ObjectManipulator.invoke(InternalUnsafe.internalUnsafe, method_name, arg_types, args);
			} catch (SecurityException ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * 没有任何安全检查的Unsafe.objectFieldOffset方法，可以获取record的成员offset
	 * 
	 * @param field
	 * @return
	 */
	public static long objectFieldOffset(Field field) {
		try {
			return (long) objectFieldOffset$Field.invoke(internalUnsafe, field);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return INVALID_FIELD_OFFSET;
	}

	public static long objectFieldOffset(Class<?> cls, String field_name) {
		try {
			return (long) objectFieldOffset$Class$String.invoke(internalUnsafe, cls, field_name);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return INVALID_FIELD_OFFSET;
	}

	public static Object staticFieldBase(Field field) {
		try {
			return staticFieldBase.invoke(internalUnsafe, field);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static long staticFieldOffset(Field field) {
		try {
			return (long) staticFieldOffset.invoke(internalUnsafe, field);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return INVALID_FIELD_OFFSET;
	}

	/**
	 * 不调用构造函数创建一个对象
	 * 
	 * @param cls 对象类
	 * @return 分配的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T allocateInstance(Class<T> cls) {
		try {
			return (T) allocateInstance.invoke(internalUnsafe, cls);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 存引用字段
	 * 
	 * @param o
	 * @param offset
	 * @param x
	 */
	public static void putReference(Object o, long offset, Object x) {
		try {
			putReference.invoke(internalUnsafe, o, offset, x);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	public static Object getReference(Object o, long offset) {
		try {
			return getReference.invoke(internalUnsafe, o, offset);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_REFERENCE;
	}

	public static void putLong(Object o, long offset, long x) {
		try {
			putLong.invoke(internalUnsafe, o, offset, x);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	public static long getLong(Object o, long offset) {
		try {
			return (long) getLong.invoke(internalUnsafe, o, offset);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_lONG;
	}

	public static void putBoolean(Object o, long offset, boolean x) {
		try {
			putBoolean.invoke(internalUnsafe, o, offset, x);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	public static boolean getBoolean(Object o, long offset) {
		try {
			return (boolean) getBoolean.invoke(internalUnsafe, o, offset);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_BOOLEAN;
	}

	public static void putInt(Object o, long offset, int x) {
		try {
			putInt.invoke(internalUnsafe, o, offset, x);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	public static int getInt(Object o, long offset) {
		try {
			return (int) getInt.invoke(internalUnsafe, o, offset);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_INT;
	}

	public static void putDouble(Object o, long offset, double x) {
		try {
			putDouble.invoke(internalUnsafe, o, offset, x);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	public static double getDouble(Object o, long offset) {
		try {
			return (double) getDouble.invoke(internalUnsafe, o, offset);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_DOUBLE;
	}

	public static void putFloat(Object o, long offset, float x) {
		try {
			putFloat.invoke(internalUnsafe, o, offset, x);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	public static float getFloat(Object o, long offset) {
		try {
			return (float) getFloat.invoke(internalUnsafe, o, offset);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_FLOAT;
	}

	/**
	 * 无视访问权限和修饰符修改Object值，如果是静态成员忽略obj参数.此方法对于HiddenClass和record同样有效
	 * 
	 * @param obj   要修改值的对象
	 * @param field 要修改的Field
	 * @param value 要修改的值
	 * @return
	 */
	public static void putObject(Object obj, Field field, Object value) {
		if (Modifier.isStatic(field.getModifiers()))
			putReference(staticFieldBase(field), staticFieldOffset(field), value);
		else
			putReference(obj, objectFieldOffset(field), value);
	}

	public static void putObject(Object obj, String field, Object value) {
		Field f = Reflection.getField(obj, field);
		if (Modifier.isStatic(f.getModifiers()))
			putReference(staticFieldBase(f), staticFieldOffset(f), value);
		else
			putReference(obj, objectFieldOffset(obj.getClass(), field), value);
	}

	public static Object getObject(Object obj, Field field) {
		if (Modifier.isStatic(field.getModifiers()))
			return getReference(staticFieldBase(field), staticFieldOffset(field));
		else
			return getReference(obj, objectFieldOffset(field));
	}

	public static Object getObject(Object obj, String field) {
		Field f = Reflection.getField(obj, field);
		if (Modifier.isStatic(f.getModifiers()))
			return getReference(staticFieldBase(f), staticFieldOffset(f));
		else
			return getReference(obj, objectFieldOffset(obj.getClass(), field));
	}

	public static void putLong(Object obj, Field field, long value) {
		if (Modifier.isStatic(field.getModifiers()))
			putLong(staticFieldBase(field), staticFieldOffset(field), value);
		else
			putLong(obj, objectFieldOffset(field), value);
	}

	public static void putLong(Object obj, String field, long value) {
		putLong(obj, Reflection.getField(obj, field), value);
	}

	public static long getLong(Object obj, Field field) {
		if (Modifier.isStatic(field.getModifiers()))
			return getLong(staticFieldBase(field), staticFieldOffset(field));
		else
			return getLong(obj, objectFieldOffset(field));
	}

	public static long getLong(Object obj, String field) {
		Field f = Reflection.getField(obj, field);
		if (Modifier.isStatic(f.getModifiers()))
			return getLong(staticFieldBase(f), staticFieldOffset(f));
		else
			return getLong(obj, objectFieldOffset(obj.getClass(), field));
	}

	public static void putBoolean(Object obj, Field field, boolean value) {
		if (Modifier.isStatic(field.getModifiers()))
			putBoolean(staticFieldBase(field), staticFieldOffset(field), value);
		else
			putBoolean(obj, objectFieldOffset(field), value);
	}

	public static void putBoolean(Object obj, String field, boolean value) {
		putBoolean(obj, Reflection.getField(obj, field), value);
	}

	public static boolean getBoolean(Object obj, Field field) {
		if (Modifier.isStatic(field.getModifiers()))
			return getBoolean(staticFieldBase(field), staticFieldOffset(field));
		else
			return getBoolean(obj, objectFieldOffset(field));
	}

	public static boolean getBoolean(Object obj, String field) {
		Field f = Reflection.getField(obj, field);
		if (Modifier.isStatic(f.getModifiers()))
			return getBoolean(staticFieldBase(f), staticFieldOffset(f));
		else
			return getBoolean(obj, objectFieldOffset(obj.getClass(), field));
	}

	public static void putInt(Object obj, Field field, int value) {
		if (Modifier.isStatic(field.getModifiers()))
			putInt(staticFieldBase(field), staticFieldOffset(field), value);
		else
			putInt(obj, objectFieldOffset(field), value);
	}

	public static void putInt(Object obj, String field, int value) {
		putInt(obj, Reflection.getField(obj, field), value);
	}

	public static int getInt(Object obj, Field field) {
		if (Modifier.isStatic(field.getModifiers()))
			return getInt(staticFieldBase(field), staticFieldOffset(field));
		else
			return getInt(obj, objectFieldOffset(field));
	}

	public static int getInt(Object obj, String field) {
		Field f = Reflection.getField(obj, field);
		if (Modifier.isStatic(f.getModifiers()))
			return getInt(staticFieldBase(f), staticFieldOffset(f));
		else
			return getInt(obj, objectFieldOffset(obj.getClass(), field));
	}

	public static void putDouble(Object obj, Field field, double value) {
		if (Modifier.isStatic(field.getModifiers()))
			putDouble(staticFieldBase(field), staticFieldOffset(field), value);
		else
			putDouble(obj, objectFieldOffset(field), value);
	}

	public static void putDouble(Object obj, String field, double value) {
		putDouble(obj, Reflection.getField(obj, field), value);
	}

	public static double getDouble(Object obj, Field field) {
		if (Modifier.isStatic(field.getModifiers()))
			return getDouble(staticFieldBase(field), staticFieldOffset(field));
		else
			return getDouble(obj, objectFieldOffset(field));
	}

	public static double getDouble(Object obj, String field) {
		Field f = Reflection.getField(obj, field);
		if (Modifier.isStatic(f.getModifiers()))
			return getDouble(staticFieldBase(f), staticFieldOffset(f));
		else
			return getDouble(obj, objectFieldOffset(obj.getClass(), field));
	}

	public static void putFloat(Object obj, Field field, float value) {
		if (Modifier.isStatic(field.getModifiers()))
			putFloat(staticFieldBase(field), staticFieldOffset(field), value);
		else
			putFloat(obj, objectFieldOffset(field), value);
	}

	public static void putFloat(Object obj, String field, float value) {
		putFloat(obj, Reflection.getField(obj, field), value);
	}

	public static float getFloat(Object obj, Field field) {
		if (Modifier.isStatic(field.getModifiers()))
			return getFloat(staticFieldBase(field), staticFieldOffset(field));
		else
			return getFloat(obj, objectFieldOffset(field));
	}

	public static float getFloat(Object obj, String field) {
		Field f = Reflection.getField(obj, field);
		if (Modifier.isStatic(f.getModifiers()))
			return getFloat(staticFieldBase(f), staticFieldOffset(f));
		else
			return getFloat(obj, objectFieldOffset(obj.getClass(), field));
	}

	/**
	 * 直接令loader加载指定class<br>
	 * 绕过类加载器： 直接向JVM注册类，不经过ClassLoader体系.<br>
	 * 无依赖解析：不自动加载依赖类，如果依赖类不存在则直接抛出java.lang.NoClassDefFoundError<br>
	 * 无安全检查： 跳过字节码验证、包可见性检查等<br>
	 * 内存驻留： 定义的类不会被 GC 回收<br>
	 * 
	 * @param name
	 * @param b
	 * @param off
	 * @param len
	 * @param loader
	 * @param protectionDomain
	 * @return
	 */
	public static Class<?> defineClass(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain protectionDomain) {
		try {
			return (Class<?>) defineClass.invoke(internalUnsafe, name, b, off, len, loader, protectionDomain);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
