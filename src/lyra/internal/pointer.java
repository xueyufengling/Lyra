package lyra.internal;

import java.lang.reflect.Field;
import java.util.Objects;

import lyra.internal.oops.markWord;
import lyra.lang.InternalUnsafe;
import lyra.lang.Reflection;

/**
 * C++指针，实际上是JVM使用的相对内存地址
 */
public class pointer {
	/**
	 * C++层的指针转换为(void*)(uint64_t)addr
	 */
	private long addr;
	private Class<?> ptr_type;

	/**
	 * 指针算术运算的步长，与类型有关，以byte为单位
	 */
	private long stride;

	/**
	 * 缓存指针类型的klass word，每次cast()的时候更新值
	 */
	private long ptr_type_klass_word;

	static final Class<?> void_ptr_type = void.class;

	/**
	 * 仅指针算术运算使用！
	 * 
	 * @param addr
	 * @param type
	 * @param stride
	 * @param ptr_type_klass_word
	 */
	private pointer(long addr, Class<?> type, long stride, long ptr_type_klass_word) {
		this.addr = addr;
		this.ptr_type = type;
		this.stride = stride;
		this.ptr_type_klass_word = ptr_type_klass_word;
	}

	/**
	 * 仅拷贝构造指针使用！
	 * 
	 * @param addr
	 * @param type
	 * @param stride
	 * @param ptr_type_klass_word
	 */
	private pointer(pointer ptr) {
		this.addr = ptr.addr;
		this.ptr_type = ptr.ptr_type;
		this.stride = ptr.stride;
		this.ptr_type_klass_word = ptr.ptr_type_klass_word;
	}

	private pointer(long addr, Class<?> type) {
		this.addr = addr;
		cast(type);
	}

	private pointer(long addr) {
		this(addr, void_ptr_type);
	}

	private pointer(String hex, Class<?> type) {
		this.addr = Long.decode(hex.strip().toLowerCase());
		cast(type);
	}

	private pointer(String hex) {
		this(hex, void_ptr_type);
	}

	public static final pointer nullptr;

	public long address() {
		return addr;
	}

	public Class<?> type() {
		return ptr_type;
	}

	public boolean is_nullptr() {
		return addr == 0;
	}

	public boolean is_void_ptr_type() {
		return ptr_type == void_ptr_type;
	}

	/**
	 * 十六进制地址
	 */
	@Override
	public String toString() {
		return "0x" + Long.toHexString(addr);
	}

	/**
	 * 判断两个指针是否相同，只比较地址不比较类型。
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		return (obj instanceof pointer ptr) && this.addr == ptr.addr;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(addr) ^ Objects.hashCode(ptr_type);
	}

	/**
	 * 把给定的地址和类型包装为指针
	 * 
	 * @param addr
	 * @param type
	 * @return
	 */
	public static final pointer at(long addr, Class<?> type) {
		return new pointer(addr, type);
	}

	public static final pointer at(long addr) {
		return new pointer(addr);
	}

	/**
	 * 将给定的十六进制地址和类型包装为指针
	 * 
	 * @param hex
	 * @param type
	 * @return
	 */
	public static final pointer at(String hex, Class<?> type) {
		return new pointer(hex, type);
	}

	public static final pointer at(String hex) {
		return pointer.at(0);
	}

	public pointer copy() {
		return new pointer(this);
	}

	/**
	 * 强制转换指针
	 * 
	 * @param destType
	 * @return
	 */
	public pointer cast(Class<?> destType) {
		this.ptr_type = destType;
		this.stride = type.sizeof(destType);
		if (!type.is_primitive(destType)) {
			// 每次cast()的时候更新目标对象的类型
			ptr_type_klass_word = markWord.get_klass_word(destType);
		}
		return this;
	}

	/**
	 * 指针赋值，只赋地址不赋类型。类型依然是原指针的类型
	 */
	public pointer assign(pointer ptr) {
		this.addr = ptr.addr;
		return this;
	}

	public pointer assign(long addr) {
		this.addr = addr;
		return this;
	}

	public pointer assign(String hex) {
		this.addr = Long.decode(hex.strip().toLowerCase());
		return this;
	}

	/**
	 * 指针加法，返回一个新指针
	 * 
	 * @param step
	 * @return
	 */
	public pointer add(long step) {
		return new pointer(addr + stride * step, ptr_type, stride, ptr_type_klass_word);
	}

	/**
	 * 指针的自增运算，改变的是指针自身的地址，并不返回新的指针拷贝
	 * 
	 * @param step
	 * @return
	 */
	public pointer inc(long step) {
		this.addr += stride * step;
		return this;
	}

	public pointer inc() {
		this.addr += stride;
		return this;
	}

	public pointer sub(long step) {
		return new pointer(addr - stride * step, ptr_type, stride, ptr_type_klass_word);
	}

	public pointer dec(long step) {
		this.addr -= stride * step;
		return this;
	}

	public pointer dec() {
		this.addr -= stride;
		return this;
	}

	/**
	 * 利用Object[]的元素为oop指针的事实来间接取指针。<br>
	 * 在32位和未启用UseCompressedOops的64位JVM上，取的地址直接就是绝对地址。<br>
	 * 在开启UseCompressedOops的64位JVM上，取的地址是相对偏移量，需要除以8（字节对其）或者右移3位+相对偏移量为0的基地址（即nullptr的绝对地址）才是绝对地址。
	 */
	private static Object[] __ref_fetch;

	static {
		__ref_fetch = new Object[1];
		nullptr = address_of(null);
	}

	/**
	 * 获取对象的地址，返回long
	 * 
	 * @param jobject
	 * @return
	 */
	private static final long address_of_object(Object jobject) {
		__ref_fetch[0] = jobject;
		return InternalUnsafe.getInt(__ref_fetch, InternalUnsafe.ARRAY_OBJECT_BASE_OFFSET);
	}

	/**
	 * 取对象地址，即andress_of_object()。基本类型都是by value传递参数入栈，取地址没意义，因此只能取对象的地址。<br>
	 * 如果要取对象的字段（可能是基本类型）的地址，使用本方法的其他重载方法。
	 * 
	 * @param jobject
	 * @return
	 */
	public static final pointer address_of(Object jobject) {
		return pointer.at(address_of_object(jobject), jobject == null ? void_ptr_type : jobject.getClass());
	}

	/**
	 * 因为基本类型都是by value传递参数入栈，取地址没意义，因此只能取类的字段地址
	 * 
	 * @param jobject
	 * @param field
	 * @return
	 */
	public static final pointer address_of(Object jobject, Field field) {
		return pointer.at(InternalUnsafe.getAddress(jobject, field), field.getType());
	}

	public static final pointer address_of(Object jobject, String field) {
		return address_of(jobject, Reflection.getField(jobject, field));
	}

	/**
	 * 对一个对象指针取引用
	 * 
	 * @param addr
	 * @return
	 */
	private static final Object dereference_object(long addr) {
		InternalUnsafe.putInt(__ref_fetch, InternalUnsafe.ARRAY_OBJECT_BASE_OFFSET, (int) addr);
		return __ref_fetch[0];
	}

	/**
	 * 取引用值
	 * 
	 * @return
	 */
	public Object dereference() {
		// 不可对void*类型的指针取值
		if (is_void_ptr_type())
			throw new RuntimeException("Cannot dereference a void* pointer at " + this.toString());
		if (ptr_type == byte.class)
			return InternalUnsafe.getByte(null, addr);
		else if (ptr_type == char.class)
			return InternalUnsafe.getChar(null, addr);
		else if (ptr_type == boolean.class)
			return InternalUnsafe.getBoolean(null, addr);
		else if (ptr_type == short.class)
			return InternalUnsafe.getShort(null, addr);
		else if (ptr_type == int.class)
			return InternalUnsafe.getInt(null, addr);
		else if (ptr_type == float.class)
			return InternalUnsafe.getFloat(null, addr);
		else if (ptr_type == long.class)
			return InternalUnsafe.getLong(null, addr);
		else if (ptr_type == double.class)
			return InternalUnsafe.getDouble(null, addr);
		else {
			Object deref_obj = dereference_object(addr);
			markWord.set_klass_word(deref_obj, ptr_type_klass_word);
			return deref_obj;
		}
	}

	/**
	 * 设置指针指向的地址的值
	 * 
	 * @param v
	 * @return 返回指针本身
	 */
	public pointer dereference_assign(Object v) {
		// 不可对void*类型的指针取值
		if (is_void_ptr_type())
			throw new RuntimeException("Cannot dereference a void* pointer at " + this.toString());
		if (ptr_type == byte.class)
			InternalUnsafe.putByte(null, addr, type.byte_value(v));
		else if (ptr_type == char.class)
			InternalUnsafe.putChar(null, addr, type.char_value(v));
		else if (ptr_type == boolean.class)
			InternalUnsafe.putBoolean(null, addr, type.boolean_value(v));
		else if (ptr_type == short.class)
			InternalUnsafe.putShort(null, addr, type.short_value(v));
		else if (ptr_type == int.class)
			InternalUnsafe.putInt(null, addr, type.int_value(v));
		else if (ptr_type == float.class)
			InternalUnsafe.putFloat(null, addr, type.float_value(v));
		else if (ptr_type == long.class)
			InternalUnsafe.putLong(null, addr, type.long_value(v));
		else if (ptr_type == double.class)
			InternalUnsafe.putDouble(null, addr, type.double_value(v));
		else
			InternalUnsafe.putAddress(null, addr, address_of_object(v));
		return this;
	}
}
