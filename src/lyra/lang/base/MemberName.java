package lyra.lang.base;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles.Lookup;

import lyra.lang.Handles;
import lyra.object.ObjectManipulator;

/**
 * java.lang.invoke.MemberName缓存MethodHandle的相关metadata。<br>
 * JVM的MethodHandle执行检查依赖于该类，修改目标MethodHandle的MemberName可以绕过检查。
 */
public class MemberName {

	private static MethodHandle matchingFlagsSet;
	private static MethodHandle allFlagsSet;
	private static MethodHandle anyFlagSet;

	// unofficial modifier flags, used by HotSpot:
	public static final int BRIDGE;
	public static final int VARARGS;
	public static final int SYNTHETIC;
	public static final int ANNOTATION;
	public static final int ENUM;

	private static MethodHandle isBridge;
	private static MethodHandle isVarargs;
	private static MethodHandle isSynthetic;

	static final String CONSTRUCTOR_NAME; // the ever-popular

	// modifiers exported by the JVM:
	static final int RECOGNIZED_MODIFIERS;

	// private flags, not part of RECOGNIZED_MODIFIERS:
	static final int IS_METHOD, // method (not constructor)
			IS_CONSTRUCTOR, // constructor
			IS_FIELD, // field
			IS_TYPE, // nested type
			CALLER_SENSITIVE, // @CallerSensitive annotation detected
			TRUSTED_FINAL; // trusted final field

	static final int ALL_ACCESS;
	static final int ALL_KINDS;
	static final int IS_INVOCABLE;

	private static MethodHandle isInvocable;
	private static MethodHandle isMethod;
	private static MethodHandle isConstructor;
	private static MethodHandle isField;
	private static MethodHandle isType;
	private static MethodHandle isPackage;
	private static MethodHandle isCallerSensitive;
	private static MethodHandle isTrustedFinalField;

	static Class<?> java_lang_invoke_MemberName;
	static Class<?> java_lang_invoke_DirectMethodHandle_Constructor;

	static {
		try {
			java_lang_invoke_MemberName = Class.forName("java.lang.invoke.MemberName");
			java_lang_invoke_DirectMethodHandle_Constructor = Class.forName("java.lang.invoke.DirectMethodHandle$Constructor");

		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		BRIDGE = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "BRIDGE");
		VARARGS = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "VARARGS");
		SYNTHETIC = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "SYNTHETIC");
		ANNOTATION = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "ANNOTATION");
		ENUM = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "ENUM");

		CONSTRUCTOR_NAME = (String) ObjectManipulator.access(java_lang_invoke_MemberName, "CONSTRUCTOR_NAME");
		RECOGNIZED_MODIFIERS = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "RECOGNIZED_MODIFIERS");

		IS_METHOD = MethodHandleNativesConstants.MN_IS_METHOD; // method (not constructor)
		IS_CONSTRUCTOR = MethodHandleNativesConstants.MN_IS_CONSTRUCTOR; // constructor
		IS_FIELD = MethodHandleNativesConstants.MN_IS_FIELD; // field
		IS_TYPE = MethodHandleNativesConstants.MN_IS_TYPE; // nested type
		CALLER_SENSITIVE = MethodHandleNativesConstants.MN_CALLER_SENSITIVE; // @CallerSensitive annotation detected
		TRUSTED_FINAL = MethodHandleNativesConstants.MN_TRUSTED_FINAL; // trusted final field

		ALL_ACCESS = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "ALL_ACCESS");
		ALL_KINDS = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "ALL_KINDS");
		IS_INVOCABLE = (int) ObjectManipulator.access(java_lang_invoke_MemberName, "IS_INVOCABLE");

		matchingFlagsSet = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "matchingFlagsSet", boolean.class, int.class, int.class);
		allFlagsSet = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "allFlagsSet", boolean.class, int.class);
		anyFlagSet = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "anyFlagSet", boolean.class, int.class);

		isBridge = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isBridge", boolean.class);
		isVarargs = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isVarargs", boolean.class);
		isSynthetic = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isSynthetic", boolean.class);

		isInvocable = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isInvocable", boolean.class);
		isMethod = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isMethod", boolean.class);
		isConstructor = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isConstructor", boolean.class);
		isField = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isField", boolean.class);
		isType = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isType", boolean.class);
		isPackage = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isPackage", boolean.class);
		isCallerSensitive = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isCallerSensitive", boolean.class);
		isTrustedFinalField = Handles.findSpecialMethodHandle(java_lang_invoke_MemberName, "isTrustedFinalField", boolean.class);
	}

	public static boolean matchingFlagsSet(Object memberName, int mask, int flags) {
		try {
			return (boolean) matchingFlagsSet.invokeExact(memberName, mask, flags);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean allFlagsSet(Object memberName, int flags) {
		try {
			return (boolean) allFlagsSet.invokeExact(memberName, flags);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean anyFlagSet(Object memberName, int flags) {
		try {
			return (boolean) anyFlagSet.invokeExact(memberName, flags);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isBridge(Object memberName) {
		try {
			return (boolean) isBridge.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isVarargs(Object memberName) {
		try {
			return (boolean) isVarargs.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isSynthetic(Object memberName) {
		try {
			return (boolean) isSynthetic.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isInvocable(Object memberName) {
		try {
			return (boolean) isInvocable.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isMethod(Object memberName) {
		try {
			return (boolean) isMethod.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isConstructor(Object memberName) {
		try {
			return (boolean) isConstructor.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isField(Object memberName) {
		try {
			return (boolean) isField.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isType(Object memberName) {
		try {
			return (boolean) isType.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isPackage(Object memberName) {
		try {
			return (boolean) isPackage.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isCallerSensitive(Object memberName) {
		try {
			return (boolean) isCallerSensitive.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	public static boolean isTrustedFinalField(Object memberName) {
		try {
			return (boolean) isTrustedFinalField.invokeExact(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BOOLEAN;
	}

	/**
	 * DirectMethodHandle$Constructor的MemberName对象
	 */
	private static VarHandle java_lang_invoke_DirectMethodHandle_Constructor_initMethod;

	static {
		java_lang_invoke_DirectMethodHandle_Constructor_initMethod = HandleBase.internalFindVarHandle(java_lang_invoke_DirectMethodHandle_Constructor, "initMethod", java_lang_invoke_MemberName);
	}

	/**
	 * 获取一个Callable的MemberName
	 * 
	 * @param m
	 * @return
	 */
	public static final Object memberNameOf(MethodHandle m) {
		if (java_lang_invoke_DirectMethodHandle_Constructor.isInstance(m))
			return java_lang_invoke_DirectMethodHandle_Constructor_initMethod.get(m);
		return null;
	}

	private static VarHandle java_lang_invoke_MemberName_flags;

	static {
		java_lang_invoke_MemberName_flags = HandleBase.internalFindVarHandle(java_lang_invoke_MemberName, "flags", int.class);
	}

	/**
	 * 获取一个MemberName的标志
	 * 
	 * @param memberName
	 * @return
	 */
	public static int getMemberNameFlags(Object memberName) {
		return (int) java_lang_invoke_MemberName_flags.get(memberName);
	}

	/**
	 * 设置一个MemberName的标志
	 * 
	 * @param memberName
	 * @param flags
	 * @return
	 */
	public static void setMemberNameFlags(Object memberName, int flags) {
		java_lang_invoke_MemberName_flags.set(memberName, flags);
	}

	/**
	 * 设置flags中的标志flag是否启用，可通过该方法为flags增加或删除flag。
	 * 
	 * @param flags
	 * @param flag
	 * @param mark
	 * @return
	 */
	public static int setFlag(int flags, int flag, boolean mark) {
		return mark ? flags | flag : flags & (~flag);
	}

	private static MethodHandle getDirectMethod;

	static {
		getDirectMethod = Handles.findSpecialMethodHandle(MethodHandles.Lookup.class, "getDirectMethod", MethodHandle.class, byte.class, Class.class, java_lang_invoke_MemberName, MethodHandles.Lookup.class);
	}

	/**
	 * 将MemberName包装为MethodHandle
	 * 
	 * @param refKind      调用类型，实际上是字节码，从MethodHandleNativesConstants中查看，例如类的非静态成员方法是invokeVirtual。
	 * @param refc         调用者的所属类，即这个方法属于哪个类
	 * @param method
	 * @param callerLookup
	 * @return
	 */
	public static MethodHandle getDirectMethod(byte refKind, Class<?> refc, Object method, Lookup callerLookup) {
		try {
			return (MethodHandle) getDirectMethod.invoke(Handles.IMPL_LOOKUP, refKind, refc, method, callerLookup);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return (MethodHandle) HandleBase.UNREACHABLE_REFERENCE;
	}

	public static MethodHandle getDirectMethod(byte refKind, Class<?> refc, Object method) {
		return getDirectMethod(refKind, refc, method, Handles.IMPL_LOOKUP);
	}

	/**
	 * 获取一个可以被当作实例方法调用的构造函数。
	 * 
	 * @param target_type
	 * @param arg_types
	 * @return
	 */
	public static MethodHandle invokeVirtualConstructor(Class<?> target_type, Class<?>... arg_types) {
		Object memberName = memberNameOf(HandleBase.internalFindConstructor(target_type, arg_types));
		int flags = getMemberNameFlags(memberName);
		flags = setFlag(flags, IS_CONSTRUCTOR, false);// 取消构造函数标志
		flags = setFlag(flags, IS_METHOD, true);// 添加普通方法标志
		setMemberNameFlags(memberName, flags);
		return getDirectMethod(MethodHandleNativesConstants.REF_invokeVirtual, target_type, memberName);
	}
}
