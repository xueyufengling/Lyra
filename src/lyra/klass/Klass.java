package lyra.klass;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;

import lyra.lang.Reflection;
import lyra.object.ObjectManipulator;

public class Klass {
	public static final class Definition {
		public final ClassLoader loader;
		public final String name;
		public final byte[] b;
		public final int off;
		public final int len;
		public final ProtectionDomain pd;
		public final String source;

		private Definition(ClassLoader loader, String name, byte[] b, int off, int len, ProtectionDomain pd, String source) {
			this.loader = loader;
			this.name = name;
			this.b = b;
			this.off = off;
			this.len = len;
			this.pd = pd;
			this.source = source;
		}

		public static final Definition of(ClassLoader loader, String name, byte[] b, int off, int len, ProtectionDomain pd, String source) {
			return new Definition(loader, name, b, off, len, pd, source);
		}
	}

	private static Field Class_$classLoader;

	static {
		Class_$classLoader = Reflection.getField(Class.class, "classLoader");
	}

	/**
	 * 设置Class的classLoader变量
	 * 
	 * @param cls
	 * @param loader
	 * @return
	 */
	public static Class<?> setClassLoader(Class<?> cls, ClassLoader loader) {
		ObjectManipulator.setObject(cls, Class_$classLoader, loader);
		return cls;
	}

	/**
	 * 不经过安全检查直接获取classLoader
	 * 
	 * @param target
	 * @param parent
	 * @return
	 */
	public static ClassLoader getClassLoader(Class<?> cls) {
		return (ClassLoader) ObjectManipulator.access(cls, Class_$classLoader);
	}

	/**
	 * 将类设置为系统类
	 * 
	 * @param cls
	 */
	public static void setAsBootstrap(Class<?> cls) {
		setClassLoader(cls, null);// 将cls的类加载器设置为BootstrapClassLoader
	}

	public static void setAsBootstrap(Field f) {
		setAsBootstrap(f.getDeclaringClass());
	}

	public static void setAsBootstrap(Executable e) {
		setAsBootstrap(e.getDeclaringClass());
	}
}
