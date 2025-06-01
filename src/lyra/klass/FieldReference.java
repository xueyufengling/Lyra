package lyra.klass;

import java.lang.reflect.Field;

import lyra.lang.Reflection;

/**
 * 用于重定向字段引用的封装工具类
 */
public class FieldReference {
	private final Field field;
	public final Object refObjBase;
	public final String refName;
	public Object redirectRefValue;
	/**
	 * 原始值
	 */
	private Object primaryValue;

	private FieldReference(Object refObjBase, String refName, Object redirectRefValue) {
		this.refObjBase = refObjBase;
		this.refName = refName;
		this.redirectRefValue = redirectRefValue;
		this.field = Reflection.getField(refObjBase, refName);
		this.asPrimary();
	}

	private FieldReference(Object refObjBase, String refName) {
		this(refObjBase, refName, null);
	}

	public final FieldReference redirect(Object redirectRefValue) {
		ObjectManipulator.setObject(refObjBase, field, redirectRefValue);
		return this;
	}

	public final FieldReference redirect() {
		return redirect(redirectRefValue);
	}

	/**
	 * 恢复到最开始的值
	 * 
	 * @return
	 */
	public final FieldReference recovery() {
		return redirect(primaryValue);
	}

	/**
	 * 将当前值设置为原先值，可提供recovery()恢复到该值
	 * 
	 * @return
	 */
	public final FieldReference asPrimary() {
		primaryValue = ObjectManipulator.access(refObjBase, field);
		return this;
	}

	public final Object value() {
		return ObjectManipulator.access(refObjBase, field);
	}

	public static final FieldReference of(Object refObjBase, String refName, Object redirectRefValue) {
		if (refObjBase != null && refName != null)
			return new FieldReference(refObjBase, refName, redirectRefValue);
		return null;
	}

	public static final FieldReference of(Object refObjBase, String refName) {
		return of(refObjBase, refName, null);
	}
}
