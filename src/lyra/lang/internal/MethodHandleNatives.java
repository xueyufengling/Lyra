package lyra.lang.internal;

import java.lang.invoke.MethodHandle;

import lyra.cxx.pointer;
import lyra.lang.Handles;
import lyra.object.ObjectManipulator;

/**
 * java.lang.invoke.MethodHandleNatives.Constants定义的常数<br>
 * 主要用于MemberName
 */
public class MethodHandleNatives {

	static Class<?> java_lang_invoke_MethodHandleNatives;

	static {
		try {
			java_lang_invoke_MethodHandleNatives = Class.forName("java.lang.invoke.MethodHandleNatives");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public static final class Constants {

		static Class<?> java_lang_invoke_MethodHandleNatives_Constants;

		public static final int MN_IS_METHOD, // method (not constructor)
				MN_IS_CONSTRUCTOR, // constructor
				MN_IS_FIELD, // field
				MN_IS_TYPE, // nested type
				MN_CALLER_SENSITIVE, // @CallerSensitive annotation detected
				MN_TRUSTED_FINAL, // trusted final field
				MN_REFERENCE_KIND_SHIFT, // refKind
				MN_REFERENCE_KIND_MASK;

		/**
		 * Constant pool reference-kind codes, as used by CONSTANT_MethodHandle CP entries.
		 */
		public static final byte REF_NONE, // null value
				REF_getField,
				REF_getStatic,
				REF_putField,
				REF_putStatic,
				REF_invokeVirtual,
				REF_invokeStatic,
				REF_invokeSpecial,
				REF_newInvokeSpecial,
				REF_invokeInterface,
				REF_LIMIT;

		/**
		 * Flags for Lookup.ClassOptions
		 */
		public static final int NESTMATE_CLASS,
				HIDDEN_CLASS,
				STRONG_LOADER_LINK,
				ACCESS_VM_ANNOTATIONS;

		/**
		 * Lookup modes
		 */
		public static final int LM_MODULE,
				LM_UNCONDITIONAL,
				LM_TRUSTED;

		static {
			try {
				java_lang_invoke_MethodHandleNatives_Constants = Class.forName("java.lang.invoke.MethodHandleNatives$Constants");
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			MN_IS_METHOD = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "MN_IS_METHOD");
			MN_IS_CONSTRUCTOR = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "MN_IS_CONSTRUCTOR");
			MN_IS_FIELD = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "MN_IS_FIELD");
			MN_IS_TYPE = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "MN_IS_TYPE");
			MN_CALLER_SENSITIVE = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "MN_CALLER_SENSITIVE");
			MN_TRUSTED_FINAL = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "MN_TRUSTED_FINAL");
			MN_REFERENCE_KIND_SHIFT = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "MN_REFERENCE_KIND_SHIFT");
			MN_REFERENCE_KIND_MASK = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "MN_REFERENCE_KIND_MASK");

			REF_NONE = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_NONE");
			REF_getField = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_getField");
			REF_getStatic = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_getStatic");
			REF_putField = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_putField");
			REF_putStatic = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_putStatic");
			REF_invokeVirtual = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_invokeVirtual");
			REF_invokeStatic = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_invokeStatic");
			REF_invokeSpecial = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_invokeSpecial");
			REF_newInvokeSpecial = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_newInvokeSpecial");
			REF_invokeInterface = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_invokeInterface");
			REF_LIMIT = (byte) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "REF_LIMIT");

			NESTMATE_CLASS = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "NESTMATE_CLASS");
			HIDDEN_CLASS = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "HIDDEN_CLASS");
			STRONG_LOADER_LINK = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "STRONG_LOADER_LINK");
			ACCESS_VM_ANNOTATIONS = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "ACCESS_VM_ANNOTATIONS");

			LM_MODULE = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "LM_MODULE");
			LM_UNCONDITIONAL = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "LM_UNCONDITIONAL");
			LM_TRUSTED = (int) ObjectManipulator.access(java_lang_invoke_MethodHandleNatives_Constants, "LM_TRUSTED");
		}
	}

	private static MethodHandle getMemberVMInfo;

	static {
		getMemberVMInfo = Handles.findStaticMethodHandle(java_lang_invoke_MethodHandleNatives, "getMemberVMInfo", Object.class, MemberName.java_lang_invoke_MemberName);
	}

	/**
	 * 目标成员字段或方法的信息。<br>
	 */
	public static final class MemberVMInfo {
		/**
		 * 成员的偏移量，相对于class或interface起始。<br>
		 * {@link https://github.com/openjdk/jdk/blob/3ff83ec49e561c44dd99508364b8ba068274b63a/src/hotspot/share/classfile/javaClasses.hpp#L1310}
		 */
		public final long offset;

		/**
		 * 目标成员字段或方法的信息。<br>
		 * 若是字段，则target_ptr为字段所属类型Class<?> oop。<br>
		 * 若是方法，则target_ptr为其在C++层的Method*指针。根据Method*可以进一步获取constMethod*<br>
		 */
		public final Object target_ptr;

		private MemberVMInfo(long vmindex, Object vmtarget) {
			this.offset = vmindex;
			this.target_ptr = vmtarget;
		}

		/**
		 * 获取InstanceMirrorKlass。<br>
		 * 这个对象实际就是staticFieldBase().<br>
		 * 
		 * @return
		 */
		public final pointer InstanceMirrorKlass() {
			return pointer.address_of(target_ptr).cast(Class.class);
		}

		public final pointer Method() {
			return pointer.address_of(target_ptr).cast(byte.class);
		}
	}

	/**
	 * 获取成员的底层信息
	 * 
	 * @param memberName
	 * @return
	 */
	public static MemberVMInfo getMemberVMInfo(Object memberName) {
		Object[] vminfo = null;
		try {
			vminfo = (Object[]) getMemberVMInfo.invoke(memberName);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return new MemberVMInfo((Long) vminfo[0], vminfo[1]);
	}

	public static MemberVMInfo getMemberVMInfo(Class<?> target, String method_name, Class<?>... arg_types) {
		Object memberName = MemberName.memberNameOf(Handles.findMethodHandle(target, method_name, arg_types));
		return getMemberVMInfo(memberName);
	}
}