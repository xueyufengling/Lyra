package lyra.klass;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lyra.filesystem.Packages;
import lyra.filesystem.UriPath;
import lyra.lang.Handles;
import lyra.lang.InternalUnsafe;
import lyra.lang.JavaLang;
import lyra.lang.Reflection;
import lyra.lang.base.ReflectionBase;
import lyra.object.ObjectManipulator;

public class KlassLoader {
	private static MethodHandle ClassLoader_m_defineClass;
	private static MethodHandle ClassLoader_m_findClass;
	/**
	 * final字段只能使用Unsafe更改，不可使用VarHandle。
	 */
	private static Field ClassLoader_f_parent;
	private static Field Class_f_classLoader;

	public static final String default_parent_field_name = "parent";

	static {
		ReflectionBase.noReflectionFieldFilter(() -> {
			ClassLoader_m_defineClass = Handles.findSpecialMethodHandle(ClassLoader.class, "defineClass", Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
			ClassLoader_m_findClass = Handles.findSpecialMethodHandle(ClassLoader.class, "findClass", Class.class, String.class);
			ClassLoader_f_parent = Reflection.getField(ClassLoader.class, "parent");
			Class_f_classLoader = Reflection.getField(Class.class, "classLoader");
		});
	}

	public static class Proxy extends ClassLoader {
		private ClassLoader son;
		private ByteCodeSource bytecodeSource;
		private boolean reverseLoading = false;// 记录是否已经开始向下查找，防止在整个加载链中都查找不到无限循环loadClass()

		public Proxy(ClassLoader dest, String dest_parent_field_name, ByteCodeSource source) {
			super(KlassLoader.getClassLoaderParent(dest, dest_parent_field_name));
			KlassLoader.setClassLoaderParent(dest, dest_parent_field_name, this);
			this.son = dest;
			this.bytecodeSource = source;
		}

		/**
		 * Proxy将插入双亲委托加载链的dest的上方
		 * 
		 * @param dest           目标ClassLoader
		 * @param undefinedKlass 要加载的字节码
		 */
		public Proxy(ClassLoader dest, String dest_parent_field_name, HashMap<String, byte[]> undefinedKlass) {
			this(dest, dest_parent_field_name, ByteCodeSource.Map.from(undefinedKlass));
		}

		public Proxy(ClassLoader dest, ByteCodeSource source) {
			this(dest, default_parent_field_name, source);
		}

		public Proxy(ClassLoader dest, HashMap<String, byte[]> undefinedKlass) {
			this(dest, default_parent_field_name, undefinedKlass);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			byte[] byte_code = bytecodeSource.genByteCode(name);
			// 代理及之上的类加载器找不到类定义时，则让子类加载器son去加载目标类，这样动态加载的类就可以引用son加载的类
			if (byte_code == null) {
				if (reverseLoading) {
					reverseLoading = false;
					return null;
				} else {
					reverseLoading = true;
					return son.loadClass(name);
				}
			}
			return defineClass(name, byte_code, 0, byte_code.length);
		}

		public final Class<?> load(String className) {
			try {
				return son.loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		public final ByteCodeSource byteCodeSource() {
			return bytecodeSource;
		}

		/**
		 * @param dest
		 * @param undefinedKlass
		 * @return
		 */
		public static final Proxy addFor(ClassLoader dest, HashMap<String, byte[]> undefinedKlass) {
			return new Proxy(dest, undefinedKlass);
		}

		public static final Proxy addFor(ClassLoader dest, String dest_parent_field_name, HashMap<String, byte[]> undefinedKlass) {
			return new Proxy(dest, dest_parent_field_name, undefinedKlass);
		}

		public static final Proxy addFor(ClassLoader dest, ByteCodeSource source) {
			return new Proxy(dest, source);
		}

		public static final Proxy addFor(ClassLoader dest, String dest_parent_field_name, ByteCodeSource source) {
			return new Proxy(dest, dest_parent_field_name, source);
		}
	}

	/**
	 * 将undefinedKlass委托给父类为loader的新自定义ClassLoader加载。<br>
	 * 注意，类加载的起点始终是手动调用的Class.forName(name,init,classLoader)、classLoader.loadClass(name)或直接使用该类型，例如直接在代码中使用{@code A a=new A();}的上下文的ClassLoader。<br>
	 * 类搜寻只会从起点开始，一直向上查找直到BootstrapClassLoader，而不会去查找起点ClassLoader的子代ClassLoader。<br>
	 * 因此，调用该方法返回的ClassLoader需要用户手动加载相关类，并且用反射使用加载的类，不能直接在代码中使用这些类型。<br>
	 * 
	 * @param loader
	 * @param undefinedKlass
	 * @return
	 */
	public static ClassLoader newClassLoaderFor(ClassLoader loader, HashMap<String, byte[]> undefinedKlass) {
		return new ClassLoader(loader) {
			private HashMap<String, byte[]> klassDefs = undefinedKlass;

			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				byte[] byte_code = klassDefs.get(name);
				if (byte_code == null)
					throw new ClassNotFoundException(name);
				return defineClass(name, byte_code, 0, byte_code.length);
			}
		};
	}

	public static ClassLoader newClassLoaderFor(HashMap<String, byte[]> undefinedKlass) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		return newClassLoaderFor(caller.getClassLoader(), undefinedKlass);
	}

	/**
	 * 解析父类加载器的字段
	 * 
	 * @param target
	 * @param dest_parent_field_name
	 * @return
	 */
	private static final Field resolveParentLoaderField(ClassLoader target, String dest_parent_field_name) {
		return dest_parent_field_name.equals(default_parent_field_name) ? ClassLoader_f_parent : ReflectionBase.fieldNoReflectionFilter(target.getClass(), dest_parent_field_name);
	}

	/**
	 * 为ClassLoader设置父类加载器
	 * 
	 * @param target                 目标ClassLoader
	 * @param dest_parent_field_name 目标ClassLoader使用的父类加载器的字段名，当没有使用ClassLoader.parent成员构建加载委托链时需要指定实际的字段名称
	 * @param parent
	 * @return
	 */
	public static ClassLoader setClassLoaderParent(ClassLoader target, String dest_parent_field_name, ClassLoader parent) {
		ObjectManipulator.setObject(target, resolveParentLoaderField(target, dest_parent_field_name), parent);
		return target;
	}

	public static ClassLoader setClassLoaderParent(ClassLoader target, ClassLoader parent) {
		return setClassLoaderParent(target, default_parent_field_name, parent);
	}

	/**
	 * 不经过安全检查直接获取parent
	 * 
	 * @param target
	 * @param parent
	 * @return
	 */
	public static ClassLoader getClassLoaderParent(ClassLoader target, String dest_parent_field_name) {
		return (ClassLoader) ObjectManipulator.access(target, resolveParentLoaderField(target, dest_parent_field_name));
	}

	public static ClassLoader getClassLoaderParent(ClassLoader target) {
		return getClassLoaderParent(target, default_parent_field_name);
	}

	/**
	 * 设置Class的classLoader变量
	 * 
	 * @param cls
	 * @param loader
	 * @return
	 */
	public static Class<?> setClassLoader(Class<?> cls, ClassLoader loader) {
		ObjectManipulator.setObject(cls, Class_f_classLoader, loader);
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
		return (ClassLoader) ObjectManipulator.access(cls, Class_f_classLoader);
	}

	/**
	 * 从指定的dClassLoader加载class
	 * 
	 * @param loader
	 * @param name
	 * @param b
	 * @param off
	 * @param len
	 * @param protectionDomain
	 * @return
	 * @throws ClassFormatError
	 */
	public static final Class<?> defineClass(ClassLoader loader, String name, byte[] b, int off, int len, ProtectionDomain protectionDomain) throws ClassFormatError {
		try {
			return (Class<?>) ClassLoader_m_defineClass.invokeExact(loader, name, b, off, len, protectionDomain);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return (Class<?>) InternalUnsafe.UNREACHABLE_REFERENCE;
	}

	/**
	 * 从指定stackSkip对应的类的ClassLoader加载class
	 * 
	 * @param stackSkip
	 * @param name
	 * @param b
	 * @param off
	 * @param len
	 * @param protectionDomain
	 * @return
	 * @throws ClassFormatError
	 */
	public static final Class<?> defineClass(int stackSkip, String name, byte[] b, int off, int len, ProtectionDomain protectionDomain) throws ClassFormatError {
		return defineClass(JavaLang.trackStackFrameClass(stackSkip).getClassLoader(), name, b, off, len, protectionDomain);
	}

	/**
	 * 从指定的dClassLoader加载class
	 * 
	 * @param loader
	 * @param name
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws ClassFormatError
	 */
	public static final Class<?> defineClass(ClassLoader loader, String name, byte[] b, int off, int len) throws ClassFormatError {
		return defineClass(loader, name, b, off, len, null);
	}

	public static final Class<?> defineClass(ClassLoader loader, String name, byte[] b) throws ClassFormatError {
		return defineClass(loader, name, b, 0, b.length);
	}

	/**
	 * 从指定stackSkip对应的类的ClassLoader加载class
	 * 
	 * @param stackSkip
	 * @param name
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws ClassFormatError
	 * @CallerSensitive
	 */
	public static final Class<?> defineClass(int stackSkip, String name, byte[] b, int off, int len) throws ClassFormatError {
		return defineClass(JavaLang.trackStackFrameClass(stackSkip).getClassLoader(), name, b, off, len);
	}

	public static final Class<?> defineClass(int stackSkip, String name, byte[] b) throws ClassFormatError {
		return defineClass(stackSkip, name, b, 0, b.length);
	}

	/**
	 * 查找类
	 * 
	 * @param stackSkip
	 * @param name
	 * @param b
	 * @return
	 * @throws ClassFormatError
	 */
	public static final Class<?> findClass(ClassLoader loader, String name) throws ClassNotFoundException {
		try {
			return (Class<?>) ClassLoader_m_findClass.invokeExact(loader, name);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return (Class<?>) InternalUnsafe.UNREACHABLE_REFERENCE;
	}

	public static ClassLoader getCallerClassLoader() {
		return JavaLang.getOuterCallerClass().getClassLoader();
	}

	public static ClassLoader getOuterCallerClassLoader() {
		return JavaLang.trackStackFrameClass(4).getClassLoader();
	}

	/**
	 * 加载类（包括未使用的类）并决定是否初始化
	 * 
	 * @param loader
	 * @param init
	 * @param class_names
	 */
	public static void loadKlass(ClassLoader loader, boolean init, String... class_names) {
		for (String cls : class_names)
			try {
				Class.forName(cls, init, loader);
			} catch (ClassNotFoundException ex) {
				System.err.println("Initialize class " + cls + " failed.");
				ex.printStackTrace();
			}
	}

	public static void loadKlass(boolean init, String... class_names) {
		ClassLoader loader = getOuterCallerClassLoader();
		loadKlass(loader, init, class_names);
	}

	/**
	 * 加载并初始化类
	 * 
	 * @param class_names
	 */
	public static void loadKlass(String... class_names) {
		ClassLoader loader = getOuterCallerClassLoader();
		loadKlass(loader, true, class_names);
	}

	public static void loadKlass(Class<?> any_class_in_package, UriPath.Resolver resolver, boolean init, String start_path, boolean include_subpackage) {
		List<String> class_names = Packages.getClassNamesInPackage(any_class_in_package, resolver, start_path, include_subpackage);
		ClassLoader loader = any_class_in_package.getClassLoader();
		for (String cls : class_names)
			try {
				Class.forName(cls, init, loader);
			} catch (ClassNotFoundException ex) {
				System.err.println("Initialize class " + cls + " failed.");
				ex.printStackTrace();
			}
	}

	public static void loadKlass(Class<?> any_class_in_package, boolean init, String start_path, boolean include_subpackage) {
		loadKlass(any_class_in_package, UriPath.Resolver.DEFAULT, init, start_path, include_subpackage);
	}

	public static void loadKlass(boolean init, String start_path, boolean include_subpackage) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		loadKlass(caller, init, start_path, include_subpackage);
	}

	/**
	 * 加载并初始化类
	 * 
	 * @param class_names
	 */
	public static void loadKlass(String start_path, boolean include_subpackage) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		loadKlass(caller, true, start_path, include_subpackage);
	}

	/**
	 * 获取一个类加载器加载的类，只包含它本身的类，不包含其委托给父类加载的类。<br>
	 * 注意，该数组原则上只有JVM内部使用，它不是线程安全的，该方法得到的引用不可直接访问或操作，否则会抛出并行修改错误。<br>
	 * 需要使用可以访问的数组，请使用{@code loadedClassesCopy()}
	 * 
	 * @param loader
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Class<?>> loadedClasses(ClassLoader loader) {
		if (loader != null)
			return (ArrayList<Class<?>>) ObjectManipulator.access(loader, "classes");
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<?>> loadedClassesCopy(ClassLoader loader) {
		return (ArrayList<Class<?>>) loadedClasses(loader).clone();
	}

	/**
	 * 获取已经加载的包列表
	 * 
	 * @return
	 */
	public static String[] getLoadedPackageNames(ClassLoader loader) {
		Package[] packages = loader.getDefinedPackages();// 获取调用该方法的类
		if (packages == null)
			return null;
		String[] package_names = new String[packages.length];
		for (int i = 0; i < packages.length; ++i) {
			package_names[i] = packages[i].getName();
		}
		return package_names;
	}

	public static String[] getLoadedPackageNames() {
		Class<?> caller = JavaLang.getOuterCallerClass();
		return getLoadedPackageNames(caller.getClassLoader());
	}

	/**
	 * 将类设置为系统类
	 * 
	 * @param cls
	 */
	public static void setAsSystemLoaded(Class<?> cls) {
		KlassLoader.setClassLoader(cls, null);// 将cls的类加载器设置为BootstrapClassLoader
	}

	public static void setAsSystemLoaded(Field f) {
		setAsSystemLoaded(f.getDeclaringClass());
	}

	public static void setAsSystemLoaded(Executable e) {
		setAsSystemLoaded(e.getDeclaringClass());
	}
}
