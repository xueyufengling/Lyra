package lyra.lang;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

import lyra.lang.base.HandleBase;
import lyra.object.ObjectManipulator;

public final class InternalUnsafe {
	private static Class<?> internalUnsafeClass;
	static Object internalUnsafe;

	private static MethodHandle objectFieldOffset$Field;// 没有检查的jdk.internal.misc.Unsafe.objectFieldOffset()
	private static MethodHandle objectFieldOffset$Class$String;
	private static MethodHandle staticFieldBase;
	private static MethodHandle staticFieldOffset;

	private static MethodHandle getAddress;
	private static MethodHandle putAddress;
	private static MethodHandle addressSize;
	private static MethodHandle getUncompressedObject;
	private static MethodHandle allocateMemory;
	private static MethodHandle freeMemory;

	private static MethodHandle defineClass;
	private static MethodHandle allocateInstance;

	private static MethodHandle arrayBaseOffset;
	private static MethodHandle arrayIndexScale;

	private static MethodHandle putReference;
	private static MethodHandle getReference;

	private static MethodHandle putByte;
	private static MethodHandle getByte;

	private static MethodHandle putChar;
	private static MethodHandle getChar;

	private static MethodHandle putBoolean;
	private static MethodHandle getBoolean;

	private static MethodHandle putShort;
	private static MethodHandle getShort;

	private static MethodHandle putInt;
	private static MethodHandle getInt;

	private static MethodHandle putLong;
	private static MethodHandle getLong;

	private static MethodHandle putDouble;
	private static MethodHandle getDouble;

	private static MethodHandle putFloat;
	private static MethodHandle getFloat;

	public static final long INVALID_FIELD_OFFSET = -1;

	public static final byte UNREACHABLE_BYTE = -1;
	public static final char UNREACHABLE_CHAR = 0;
	public static final short UNREACHABLE_SHORT = -1;
	public static final long UNREACHABLE_lONG = -1;
	public static final Object UNREACHABLE_REFERENCE = null;
	public static final boolean UNREACHABLE_BOOLEAN = false;
	public static final int UNREACHABLE_INT = -1;
	public static final double UNREACHABLE_DOUBLE = -1;
	public static final float UNREACHABLE_FLOAT = -1;

	public static final int ADDRESS_SIZE;
	public static final int ARRAY_OBJECT_BASE_OFFSET;

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
		objectFieldOffset$Field = Handles.findSpecialMethodHandle(internalUnsafeClass, "objectFieldOffset", long.class, Field.class);
		objectFieldOffset$Class$String = Handles.findSpecialMethodHandle(internalUnsafeClass, "objectFieldOffset", long.class, Class.class, String.class);
		staticFieldBase = Handles.findSpecialMethodHandle(internalUnsafeClass, "staticFieldBase", Object.class, Field.class);
		staticFieldOffset = Handles.findSpecialMethodHandle(internalUnsafeClass, "staticFieldOffset", long.class, Field.class);

		getAddress = Handles.findSpecialMethodHandle(internalUnsafeClass, "getAddress", long.class, Object.class, long.class);
		putAddress = Handles.findSpecialMethodHandle(internalUnsafeClass, "putAddress", void.class, Object.class, long.class, long.class);
		addressSize = Handles.findSpecialMethodHandle(internalUnsafeClass, "addressSize", int.class);
		getUncompressedObject = Handles.findSpecialMethodHandle(internalUnsafeClass, "getUncompressedObject", Object.class, long.class);
		allocateMemory = Handles.findSpecialMethodHandle(internalUnsafeClass, "allocateMemory", long.class, long.class);
		freeMemory = Handles.findSpecialMethodHandle(internalUnsafeClass, "freeMemory", void.class, long.class);

		defineClass = Handles.findSpecialMethodHandle(internalUnsafeClass, "defineClass", Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
		allocateInstance = Handles.findSpecialMethodHandle(internalUnsafeClass, "allocateInstance", Object.class, Class.class);

		arrayBaseOffset = Handles.findSpecialMethodHandle(internalUnsafeClass, "arrayBaseOffset", int.class, Class.class);
		arrayIndexScale = Handles.findSpecialMethodHandle(internalUnsafeClass, "arrayIndexScale", int.class, Class.class);

		putReference = Handles.findSpecialMethodHandle(internalUnsafeClass, "putReference", void.class, Object.class, long.class, Object.class);
		getReference = Handles.findSpecialMethodHandle(internalUnsafeClass, "getReference", Object.class, Object.class, long.class);

		putByte = Handles.findSpecialMethodHandle(internalUnsafeClass, "putByte", void.class, Object.class, long.class, byte.class);
		getByte = Handles.findSpecialMethodHandle(internalUnsafeClass, "getByte", byte.class, Object.class, long.class);

		putChar = Handles.findSpecialMethodHandle(internalUnsafeClass, "putChar", void.class, Object.class, long.class, char.class);
		getChar = Handles.findSpecialMethodHandle(internalUnsafeClass, "getChar", char.class, Object.class, long.class);

		putBoolean = Handles.findSpecialMethodHandle(internalUnsafeClass, "putBoolean", void.class, Object.class, long.class, boolean.class);
		getBoolean = Handles.findSpecialMethodHandle(internalUnsafeClass, "getBoolean", boolean.class, Object.class, long.class);

		putShort = Handles.findSpecialMethodHandle(internalUnsafeClass, "putShort", void.class, Object.class, long.class, short.class);
		getShort = Handles.findSpecialMethodHandle(internalUnsafeClass, "getShort", short.class, Object.class, long.class);

		putInt = Handles.findSpecialMethodHandle(internalUnsafeClass, "putInt", void.class, Object.class, long.class, int.class);
		getInt = Handles.findSpecialMethodHandle(internalUnsafeClass, "getInt", int.class, Object.class, long.class);

		putLong = Handles.findSpecialMethodHandle(internalUnsafeClass, "putLong", void.class, Object.class, long.class, long.class);
		getLong = Handles.findSpecialMethodHandle(internalUnsafeClass, "getLong", long.class, Object.class, long.class);

		putFloat = Handles.findSpecialMethodHandle(internalUnsafeClass, "putFloat", void.class, Object.class, long.class, float.class);
		getFloat = Handles.findSpecialMethodHandle(internalUnsafeClass, "getFloat", float.class, Object.class, long.class);

		putDouble = Handles.findSpecialMethodHandle(internalUnsafeClass, "putDouble", void.class, Object.class, long.class, double.class);
		getDouble = Handles.findSpecialMethodHandle(internalUnsafeClass, "getDouble", double.class, Object.class, long.class);

		ADDRESS_SIZE = addressSize();
		ARRAY_OBJECT_BASE_OFFSET = arrayBaseOffset(Object[].class);
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
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_lONG;
	}

	public static long objectFieldOffset(Class<?> cls, String field_name) {
		try {
			return (long) objectFieldOffset$Class$String.invoke(internalUnsafe, cls, field_name);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_lONG;
	}

	public static Object staticFieldBase(Field field) {
		try {
			return staticFieldBase.invoke(internalUnsafe, field);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static long staticFieldOffset(Field field) {
		try {
			return (long) staticFieldOffset.invoke(internalUnsafe, field);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_lONG;
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
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return (T) UNREACHABLE_REFERENCE;
	}

	/**
	 * 内存地址操作
	 */
	public static void putAddress(Object o, long offset, long x) {
		try {
			putAddress.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static long getAddress(Object o, long offset) {
		try {
			return (long) getAddress.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_lONG;
	}

	/**
	 * 获取字段的内存地址
	 * 
	 * @param obj
	 * @param field
	 * @return
	 */
	public static long getAddress(Object obj, Field field) {
		if (Modifier.isStatic(field.getModifiers()))
			return getAddress(staticFieldBase(field), staticFieldOffset(field));
		else
			return getAddress(obj, objectFieldOffset(field));
	}

	/**
	 * 获取字段的内存地址
	 * 
	 * @param obj
	 * @param field
	 * @return
	 */
	public static long getAddress(Object obj, String field) {
		Field f = Reflection.getField(obj, field);
		if (Modifier.isStatic(f.getModifiers()))
			return getAddress(staticFieldBase(f), staticFieldOffset(f));
		else
			return getAddress(obj, objectFieldOffset(obj.getClass(), field));
	}

	public static int addressSize() {
		try {
			return (int) addressSize.invoke(internalUnsafe);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_INT;
	}

	public static Object getUncompressedObject(Object o, long offset) {
		try {
			return getUncompressedObject.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_REFERENCE;
	}

	public static long allocateMemory(long bytes) {
		try {
			return (long) allocateMemory.invoke(internalUnsafe, bytes);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_lONG;
	}

	public static void freeMemory(long address) {
		try {
			freeMemory.invoke(internalUnsafe, address);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static int arrayBaseOffset(Class<?> arrayClass) {
		try {
			return (int) arrayBaseOffset.invoke(internalUnsafe, arrayClass);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_INT;
	}

	public static int arrayIndexScale(Class<?> arrayClass) {
		try {
			return (int) arrayIndexScale.invoke(internalUnsafe, arrayClass);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_INT;
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
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static Object getReference(Object o, long offset) {
		try {
			return getReference.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_REFERENCE;
	}

	public static void putByte(Object o, long offset, byte x) {
		try {
			putByte.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static byte getByte(Object o, long offset) {
		try {
			return (byte) getByte.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_BYTE;
	}

	public static void putChar(Object o, long offset, char x) {
		try {
			putChar.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static char getChar(Object o, long offset) {
		try {
			return (char) getChar.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_CHAR;
	}

	public static void putBoolean(Object o, long offset, boolean x) {
		try {
			putBoolean.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static boolean getBoolean(Object o, long offset) {
		try {
			return (boolean) getBoolean.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_BOOLEAN;
	}

	public static void putShort(Object o, long offset, short x) {
		try {
			putShort.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static short getShort(Object o, long offset) {
		try {
			return (short) getShort.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_SHORT;
	}

	public static void putInt(Object o, long offset, int x) {
		try {
			putInt.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static int getInt(Object o, long offset) {
		try {
			return (int) getInt.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_INT;
	}

	public static void putLong(Object o, long offset, long x) {
		try {
			putLong.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static long getLong(Object o, long offset) {
		try {
			return (long) getLong.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_lONG;
	}

	public static void putDouble(Object o, long offset, double x) {
		try {
			putDouble.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static double getDouble(Object o, long offset) {
		try {
			return (double) getDouble.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return UNREACHABLE_DOUBLE;
	}

	public static void putFloat(Object o, long offset, float x) {
		try {
			putFloat.invoke(internalUnsafe, o, offset, x);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static float getFloat(Object o, long offset) {
		try {
			return (float) getFloat.invoke(internalUnsafe, o, offset);
		} catch (Throwable ex) {
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
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
