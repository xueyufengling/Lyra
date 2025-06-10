package lyra.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import lyra.internal.oops.markWord;
import lyra.lang.InternalUnsafe;
import lyra.lang.Reflection;

/**
 * C++风格的引用，无需担心GC移动对象导致指针失效。
 */
public class reference {
	Object ref_base;
	long offset;

	Class<?> ref_type;// 可以是基本类型
	long ref_type_klass_word;

	private reference(Object jobject, long offset, Class<?> ref_type) {
		this.ref_base = jobject;
		this.offset = offset;
		this.ref_type = ref_type;
	}

	long address_of_reference() {
		return pointer.address_of_object(ref_base) + offset;
	}

	public Object value() {
		if (ref_type == byte.class)
			return InternalUnsafe.getByte(null, offset);
		else if (ref_type == char.class)
			return InternalUnsafe.getChar(null, offset);
		else if (ref_type == boolean.class)
			return InternalUnsafe.getBoolean(null, offset);
		else if (ref_type == short.class)
			return InternalUnsafe.getShort(null, offset);
		else if (ref_type == int.class)
			return InternalUnsafe.getInt(null, offset);
		else if (ref_type == float.class)
			return InternalUnsafe.getFloat(null, offset);
		else if (ref_type == long.class)
			return InternalUnsafe.getLong(null, offset);
		else if (ref_type == double.class)
			return InternalUnsafe.getDouble(null, offset);
		else {
			Object deref_obj = pointer.dereference_object(address_of_reference());
			markWord.set_klass_word(deref_obj, ref_type_klass_word);
			return deref_obj;
		}
	}

	public Class<?> type() {
		return ref_type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		return (obj instanceof reference ref) && (this.ref_base == ref.ref_base) && (this.offset == ref.offset);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(ref_base) ^ Objects.hashCode(offset) ^ Objects.hashCode(ref_type);
	}

	/**
	 * 强制转引用类型
	 * 
	 * @param destType
	 * @return
	 */
	public reference cast(Class<?> destType) {
		this.ref_type = destType;
		if (!type.is_primitive(destType)) {
			// 每次cast()的时候更新目标对象的类型
			ref_type_klass_word = markWord.get_klass_word(destType);
		}
		return this;
	}

	/**
	 * 获取对象的引用，不能对ref_base取引用。
	 * 
	 * @param jobject
	 * @return
	 */
	public static final reference reference_of(Object jobject) {
		return new reference(jobject, 0, jobject.getClass());
	}

	/**
	 * 获取字段的引用，包括静态字段的引用
	 * 
	 * @param jobject
	 * @param field
	 * @return
	 */
	public static final reference reference_of(Object jobject, Field field) {
		if (Modifier.isStatic(field.getModifiers()))
			return new reference(InternalUnsafe.staticFieldBase(field), InternalUnsafe.staticFieldOffset(field), field.getType());
		else
			return new reference(jobject, InternalUnsafe.objectFieldOffset(field), field.getType());
	}

	public static final reference reference_of(Object jobject, String field) {
		return reference_of(jobject, Reflection.getField(jobject, field));
	}

	/**
	 * 为引用的对象赋值，如果目标为对象，则只赋值其字段，不改变对象头。
	 * 
	 * @param v
	 * @return
	 */
	public reference assign(Object v) {
		if (ref_type == byte.class)
			InternalUnsafe.putByte(ref_base, offset, type.byte_value(v));
		else if (ref_type == char.class)
			InternalUnsafe.putChar(ref_base, offset, type.char_value(v));
		else if (ref_type == boolean.class)
			InternalUnsafe.putBoolean(ref_base, offset, type.boolean_value(v));
		else if (ref_type == short.class)
			InternalUnsafe.putShort(ref_base, offset, type.short_value(v));
		else if (ref_type == int.class)
			InternalUnsafe.putInt(ref_base, offset, type.int_value(v));
		else if (ref_type == float.class)
			InternalUnsafe.putFloat(ref_base, offset, type.float_value(v));
		else if (ref_type == long.class)
			InternalUnsafe.putLong(ref_base, offset, type.long_value(v));
		else if (ref_type == double.class)
			InternalUnsafe.putDouble(ref_base, offset, type.double_value(v));
		else
			InternalUnsafe.copyMemory0(v, markWord.HEADER_BYTE_LENGTH, ref_base, markWord.HEADER_BYTE_LENGTH, type.sizeof_object(v.getClass()) - markWord.HEADER_BYTE_LENGTH);// 只拷贝字段，不覆盖对象头
		return this;
	}
}
