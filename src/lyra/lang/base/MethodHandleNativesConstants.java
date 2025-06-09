package lyra.lang.base;

import lyra.object.ObjectManipulator;

/**
 * java.lang.invoke.MethodHandleNatives.Constants定义的常数<br>
 * 主要用于MemberName
 */
public class MethodHandleNativesConstants {

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
			System.err.println("Get java.lang.invoke.MethodHandleNatives.Constants failed.");
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
