package lyra.internal.oops;

import lyra.cxx.cxx_type;
import lyra.cxx.pointer;
import lyra.vm.internal.VmBase;

/**
 * https://github.com/openjdk/jdk/blob/9586817cea3f1cad8a49d43e9106e25dafa04765/src/hotspot/share/oops/compressedOops.cpp#L49<br>
 * oop压缩相关常量。是否会运行时动态变更未知。<br>
 * oop压缩指将绝对地址取相对于堆base的偏移量并位移构成一个32位的oop。<br>
 * 对象头压缩/Klass压缩是指将对象头的Klass Word从64位压缩到32位的narrowKlass。<br>
 * 开启UseCompressedOops后，默认开启Klass压缩，但oop是否压缩取决于分配的堆内存大小。
 */
public class CompressedOops {
	/**
	 * 压缩模式
	 */
	public static enum Mode {
		UnscaledNarrowOop, // 无压缩
		ZeroBasedNarrowOop, // 压缩，基地址为0
		DisjointBaseNarrowOop, //
		HeapBasedNarrowOop;// 压缩，基地址非0
	};

	Mode mode;

	/**
	 * 最大堆内存大小
	 */
	public static final long max_heap_size;

	/**
	 * 堆内存末尾在内存中的绝对地址
	 */
	public static final long heap_space_end;

	/**
	 * 堆内存的起始地址
	 */
	public static final long base;

	/**
	 * 压缩oop时的位移
	 */
	public static final long shift;

	/**
	 * 堆内存相对地址范围
	 */
	public static final long heap_address_range;

	static {
		max_heap_size = VmBase.maxHeapSize();
		heap_space_end = VmBase.HeapBaseMinAddress + max_heap_size;// 这是最大的范围，实际范围可能只是其中一段区间，这种方法或许并不准确。
		if (heap_space_end > VmBase.UnscaledOopHeapMax) {// 实际堆内存大小大于不压缩oop时支持的最大地址，则需要压缩oop，哪怕没启用UseCompressedOops也会自动开启压缩。
			shift = VmBase.OOP_ENCODE_ADDRESS_SHIFT;
		} else if (VmBase.UseCompressedOops)// 指定了UseCompressedOops后则必定压缩。
			shift = VmBase.OOP_ENCODE_ADDRESS_SHIFT;
		else// 堆内存的末尾绝对地址小于4GB就不压缩
			shift = 0;
		if (heap_space_end <= VmBase.OopEncodingHeapMax) {
			base = 0;
		} else {
			base = pointer.nullptr.address();// 这对吗？
		}
		heap_address_range = heap_space_end - base;
	}

	/**
	 * 编码压缩oop<br>
	 * oop.encode_heap_oop_not_null
	 * 
	 * @param native_addr
	 * @return
	 */
	public static final int encode(long native_addr) {
		return (int) ((native_addr - base) >> shift);
	}

	public static final long pointer_delta(long native_addr) {
		return native_addr - base;
	}

	/**
	 * 解码压缩oop，位移可能为0，此时表示未压缩的相对于堆起始位置的相对地址.
	 * 
	 * @param oop_addr
	 * @return
	 */
	public static final long decode(int oop_addr) {
		return ((oop_addr & cxx_type.UINT32_T_MASK) << shift) + base;
	}
}
