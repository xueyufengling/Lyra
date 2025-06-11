package lyra.vm;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import lyra.lang.Handles;
import lyra.object.ObjectManipulator;
import lyra.vm.internal.VmBase;

/**
 * 管理JVM的相关功能
 */
public class Vm extends VmBase {
	/**
	 * 实际类型为sun.management.RuntimeImpl
	 */
	private static final Object RuntimeMXBean;
	/**
	 * JVM的管理类，实现是sun.management.VMManagementImpl，是sun.management.RuntimeImpl的成员jvm
	 */
	private static final Object VMManagement;
	public static final Class<?> VMManagementClass;// 在HotSpot虚拟机中是sun.management.VMManagementImpl

	private static MethodHandle VMManagementImpl_getProcessId;

	/**
	 * 系统属性System.props
	 */
	private static final Properties Properties;

	private static final Class<?> ClassLoaders;

	/**
	 * JVM参数 com.sun.management.internal.Flag
	 */
	private static final Class<?> FlagClass;
	private static MethodHandle Flag_getFlag;
	private static MethodHandle Flag_getValue;
	private static MethodHandle Flag_setLongValue;
	private static MethodHandle Flag_setDoubleValue;
	private static MethodHandle Flag_setBooleanValue;
	private static MethodHandle Flag_setStringValue;

	static {
		RuntimeMXBean = ManagementFactory.getRuntimeMXBean();
		Object vmManagement = null;
		Class<?> vmManagementClass = null;
		Properties properties = null;
		Class<?> classLoaders = null;
		Class<?> flagClass = null;
		try {
			vmManagement = ObjectManipulator.access(RuntimeMXBean, "jvm");// 获取JVM管理类
			vmManagementClass = vmManagement.getClass();
			VMManagementImpl_getProcessId = Handles.findSpecialMethodHandle(vmManagementClass, vmManagementClass, "getProcessId", int.class);// 获取进程ID的native方法
			// 获取系统属性引用
			properties = (Properties) ObjectManipulator.access(System.class, "props");
			// 获取所有系统ClassLoaders
			classLoaders = Class.forName("jdk.internal.loader.ClassLoaders");
			// 虚拟机参数Flag类及其成员方法
			flagClass = Class.forName("com.sun.management.internal.Flag");
			Flag_getFlag = Handles.findStaticMethodHandle(flagClass, "getFlag", flagClass, String.class);
			Flag_setLongValue = Handles.findStaticMethodHandle(flagClass, "setLongValue", void.class, String.class, long.class);
			Flag_setDoubleValue = Handles.findStaticMethodHandle(flagClass, "setDoubleValue", void.class, String.class, double.class);
			Flag_setBooleanValue = Handles.findStaticMethodHandle(flagClass, "setBooleanValue", void.class, String.class, boolean.class);
			Flag_setStringValue = Handles.findStaticMethodHandle(flagClass, "setStringValue", void.class, String.class, String.class);
			Flag_getValue = Handles.findSpecialMethodHandle(flagClass, flagClass, "getValue", Object.class);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		VMManagement = vmManagement;
		VMManagementClass = vmManagementClass;
		Properties = properties;
		ClassLoaders = classLoaders;
		FlagClass = flagClass;
	}

	/**
	 * 使用反射获取系统的内建ClassLoader
	 * 
	 * @param class_loader_name
	 * @return
	 */
	public static Object accessBuiltinClassLoaders(String class_loader_name) {
		try {
			return ObjectManipulator.access(ClassLoaders, class_loader_name);
		} catch (SecurityException | IllegalArgumentException ex) {
			System.err.println("Class loader name can only be BOOT_LOADER | PLATFORM_LOADER | APP_LOADER");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<URL> getBuiltinClassLoaderClassPath(String class_loader_name) {
		try {
			Object class_loader = accessBuiltinClassLoaders(class_loader_name);
			Object url_classpath = ObjectManipulator.access(class_loader, "ucp");// jdk.internal.loader.URLClassPath
			if (url_classpath != null)// BOOT_LOADER的ucp为null
				return (ArrayList<URL>) ObjectManipulator.access(url_classpath, "path");
		} catch (SecurityException | IllegalArgumentException ex) {
			System.err.println("Cannot get class loader classpath");
		}
		return null;
	}

	/**
	 * 无视权限获取系统属性
	 * 
	 * @param key
	 * @return
	 */
	public static String getSystemProperty(String key) {
		return Properties.getProperty(key);
	}

	/**
	 * 无视权限设置系统属性
	 * 
	 * @param key
	 * @param value
	 */
	public static void setSystemProperty(String key, String value) {
		Properties.setProperty(key, value);
	}

	/**
	 * 无视操作权限设置JVM的参数设置，必须自己确保value的类型与JVM的参数类型一致！调用的是native方法，但不是所有参数都支持运行时修改。大部分标志无法成功设置，因为检测可写标志在native方法内，无法干涉
	 * 
	 * @param name
	 * @param value
	 */
	public static void setVmOption(String name, Object value) {
		try {
			Object flag = Flag_getFlag.invoke(name);
			Object v = Flag_getValue.invoke(FlagClass.cast(flag));
			VarHandle writeable = Handles.findVarHandle(FlagClass, "writeable", boolean.class);
			writeable.set(flag, true);
			if (v instanceof Long lv)
				Flag_setLongValue.invokeExact(name, lv.longValue());
			if (v instanceof Double dv)
				Flag_setDoubleValue.invokeExact(name, dv.doubleValue());
			if (v instanceof Boolean bv)
				Flag_setBooleanValue.invokeExact(name, bv.booleanValue());
			if (v instanceof String sv)
				Flag_setStringValue.invokeExact(name, (String) sv);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 获取当前JVM的进程ID（也称PID）
	 * 
	 * @return
	 */
	public static int getProcessId() {
		try {
			return (int) VMManagementImpl_getProcessId.invoke(VMManagement);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return -1;
	}
}
