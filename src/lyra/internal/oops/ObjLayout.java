package lyra.internal.oops;

import lyra.vm.internal.VmBase;

public class ObjLayout {

	public static enum Mode {
		/**
		 * JDK 24+<br>
		 * 开启对象头压缩，包含压缩klass pointer，即+UseCompressedClassPointers<br>
		 * +UseCompactObjectHeaders
		 */
		Compact,
		/**
		 * 压缩klass pointer，但不压缩对象头<br>
		 * +UseCompressedClassPointers -UseCompactObjectHeaders
		 */
		Compressed,
		/**
		 * 未压缩klass pointer，也未压缩对象头<br>
		 * -UseCompressedClassPointers -UseCompactObjectHeaders
		 */
		Uncompressed,
		/**
		 * 未定义
		 */
		Undefined
	}

	public static final Mode _klass_mode;
	public static final long _oop_base_offset_in_bytes;
	public static final boolean _oop_has_klass_gap;

	static {
		if (VmBase.getBooleanOption("UseCompactObjectHeaders")) {
			_klass_mode = Mode.Compact;
			_oop_has_klass_gap = false;
		} else {
			if (VmBase.getBooleanOption("UseCompressedClassPointers")) {
				_klass_mode = Mode.Compressed;
				_oop_has_klass_gap = true;
			} else {
				_klass_mode = Mode.Uncompressed;
				_oop_has_klass_gap = false;
			}
		}
		_oop_base_offset_in_bytes = markWord.HEADER_BYTE_LENGTH;
	}
}
