package lyra.lang.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles.Lookup;

import lyra.lang.Handles;
import lyra.lang.InternalUnsafe;
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

	public static final String INITIALIZER_NAME = "<cinit>";
	public static final String CONSTRUCTOR_NAME; // the ever-popular

	// modifiers exported by the JVM:
	public static final int RECOGNIZED_MODIFIERS;

	// private flags, not part of RECOGNIZED_MODIFIERS:
	public static final int IS_METHOD, // method (not constructor)
			IS_CONSTRUCTOR, // constructor
			IS_FIELD, // field
			IS_TYPE, // nested type
			CALLER_SENSITIVE, // @CallerSensitive annotation detected
			TRUSTED_FINAL; // trusted final field

	public static final int ALL_ACCESS;
	public static final int ALL_KINDS;
	public static final int IS_INVOCABLE;

	private static MethodHandle isInvocable;
	private static MethodHandle isMethod;
	private static MethodHandle isConstructor;
	private static MethodHandle isField;
	private static MethodHandle isType;
	private static MethodHandle isPackage;
	private static MethodHandle isCallerSensitive;
	private static MethodHandle isTrustedFinalField;

	static Class<?> java_lang_invoke_MemberName;
	static Class<?> java_lang_invoke_DirectMethodHandle;
	static Class<?> java_lang_invoke_DirectMethodHandle$Constructor;

	static {
		try {
			java_lang_invoke_MemberName = Class.forName("java.lang.invoke.MemberName");
			java_lang_invoke_DirectMethodHandle = Class.forName("java.lang.invoke.DirectMethodHandle");
			java_lang_invoke_DirectMethodHandle$Constructor = Class.forName("java.lang.invoke.DirectMethodHandle$Constructor");

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

		IS_METHOD = MethodHandleNatives.Constants.MN_IS_METHOD; // method (not constructor)
		IS_CONSTRUCTOR = MethodHandleNatives.Constants.MN_IS_CONSTRUCTOR; // constructor
		IS_FIELD = MethodHandleNatives.Constants.MN_IS_FIELD; // field
		IS_TYPE = MethodHandleNatives.Constants.MN_IS_TYPE; // nested type
		CALLER_SENSITIVE = MethodHandleNatives.Constants.MN_CALLER_SENSITIVE; // @CallerSensitive annotation detected
		TRUSTED_FINAL = MethodHandleNatives.Constants.MN_TRUSTED_FINAL; // trusted final field

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
	private static VarHandle java_lang_invoke_DirectMethodHandle$Constructor_initMethod;
	private static VarHandle java_lang_invoke_DirectMethodHandle_member;

	static {
		java_lang_invoke_DirectMethodHandle$Constructor_initMethod = HandleBase.internalFindVarHandle(java_lang_invoke_DirectMethodHandle$Constructor, "initMethod", java_lang_invoke_MemberName);
		java_lang_invoke_DirectMethodHandle_member = HandleBase.internalFindVarHandle(java_lang_invoke_DirectMethodHandle, "member", java_lang_invoke_MemberName);
	}

	/**
	 * 获取一个Callable的MemberName
	 * 
	 * @param m
	 * @return
	 */
	public static final Object memberNameOf(MethodHandle m) {
		if (java_lang_invoke_DirectMethodHandle$Constructor.isInstance(m))
			return java_lang_invoke_DirectMethodHandle$Constructor_initMethod.get(m);
		else if (java_lang_invoke_DirectMethodHandle.isInstance(m))
			return java_lang_invoke_DirectMethodHandle_member.get(m);
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

	private static MethodHandle getReferenceKind;

	static {
		getReferenceKind = Handles.findVirtualMethodHandle(java_lang_invoke_MemberName, "getReferenceKind", byte.class);
	}

	/**
	 * 获取指定memberName的调用字节码
	 * 
	 * @param memberName
	 * @return
	 */
	public static byte getReferenceKind(Object memberName) {
		try {
			return (byte) getReferenceKind.invoke(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_BYTE;
	}

	private static MethodHandle init;

	static {
		init = Handles.findVirtualMethodHandle(java_lang_invoke_MemberName, "init", void.class, Class.class, String.class, Object.class, int.class);
	}

	/**
	 * 在一个对象上进行初始化
	 * 
	 * @param memberName
	 * @param defClass
	 * @param name
	 * @param type       Class<?>或MethodType
	 * @param flags
	 * @return
	 */
	public static final Object init(Object memberName, Class<?> defClass, String name, Object type, int flags) {
		try {
			return (Object) init.invoke(memberName, defClass, name, flags);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return HandleBase.UNREACHABLE_REFERENCE;
	}

	/**
	 * 构建一个字段或方法的名称
	 * 
	 * @param defClass
	 * @param name
	 * @param type
	 * @param flags
	 * @return
	 */
	public static final Object allocate(Class<?> defClass, String name, Object type, int flags) {
		return init(InternalUnsafe.allocateInstance(java_lang_invoke_MemberName), defClass, name, type, flags);
	}
}
