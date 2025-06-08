package lyra.internal.oops;

import lyra.lang.InternalUnsafe;
import lyra.vm.base.VmBase;

/**
 * https://github.com/openjdk/jdk/blob/d7352559195b9e052c3eb24d773c0d6c10dc23ad/src/hotspot/share/oops/markWord.hpp#L77
 */
public class markWord {

	private static abstract class __obj_header_base {
		// public static final void
	}

	// @formatter:off
	/**
	* 对象头的结构<br>
	* Object Header由Mark Word和Klass Word组成<br>
	* ObjectHeader 32-bit JVM<br>
	* |----------------------------------------------------------------------------------------|--------------------|<br>
	* |                                    Object Header (64 bits)                             |        State       |<br>
	* |-------------------------------------------------------|--------------------------------|--------------------|<br>
	* |                  Mark Word (32 bits)                  |      Klass Word (32 bits)      |                    |<br>
	* |-------------------------------------------------------|--------------------------------|--------------------|<br>
	* | identity_hashcode:25 | age:4 | biased_lock:1 | lock:2 |      OOP to metadata object    |       Normal       |<br>
	* |-------------------------------------------------------|--------------------------------|--------------------|<br>
	* |  thread:23 | epoch:2 | age:4 | biased_lock:1 | lock:2 |      OOP to metadata object    |       Biased       |<br>
	* |-------------------------------------------------------|--------------------------------|--------------------|<br>
	* |               ptr_to_lock_record:30          | lock:2 |      OOP to metadata object    | Lightweight Locked |<br>
	* |-------------------------------------------------------|--------------------------------|--------------------|<br>
	* |               ptr_to_heavyweight_monitor:30  | lock:2 |      OOP to metadata object    | Heavyweight Locked |<br>
	* |-------------------------------------------------------|--------------------------------|--------------------|<br>
	* |                                              | lock:2 |      OOP to metadata object    |    Marked for GC   |<br>
	* |-------------------------------------------------------|--------------------------------|--------------------|<br>
	*/
	// @formatter:on
	@SuppressWarnings("unused")
	private static final class __32_bit extends __obj_header_base {
		// 32位JVM无OOP指针压缩
		public static final int HEADER_OFFSET = 0;
		public static final int HEADER_LENGTH = 64;

		public static final int MARKWORD_OFFSET = HEADER_OFFSET;
		public static final int MARKWORD_LENGTH = 32;
		public static final int KLASS_OFFSET = MARKWORD_OFFSET + MARKWORD_LENGTH;
		public static final int KLASS_LENGTH = 32;

		public static final int IDENTITY_HASHCODE_OFFSET = MARKWORD_OFFSET;
		public static final int IDENTITY_HASHCODE_LENGTH = 25;
		public static final int AGE_OFFSET = IDENTITY_HASHCODE_OFFSET + IDENTITY_HASHCODE_LENGTH;
		public static final int AGE_LENGTH = 4;
		public static final int BIASED_LOCK_OFFSET = AGE_OFFSET + AGE_LENGTH;
		public static final int BIASED_LOCK_LENGTH = 1;

		public static final int LOCK_OFFSET = BIASED_LOCK_OFFSET + BIASED_LOCK_LENGTH;
		public static final int LOCK_LENGTH = 2;

		public static final int THREAD_OFFSET = MARKWORD_OFFSET;
		public static final int THREAD_LENGTH = 23;
		public static final int EPOCH_OFFSET = THREAD_OFFSET + THREAD_LENGTH;
		public static final int EPOCH_LENGTH = 2;

		public static final int PTR_TO_LOCK_RECORD_OFFSET = MARKWORD_OFFSET;
		public static final int PTR_TO_LOCK_RECORD_LENGTH = 30;

		public static final int PTR_TO_HEAVYWEIGHT_MONITOR_OFFSET = MARKWORD_OFFSET;
		public static final int PTR_TO_HEAVYWEIGHT_MONITOR_LENGTH = 30;
	}

	// @formatter:off
	/**
	* ObjectHeader 64-bit JVM<br>
	* |------------------------------------------------------------------------------------------------------------|--------------------|<br>
	* |                                            Object Header (128 bits)                                        |        State       |<br>
	* |------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* |                                  Mark Word (64 bits)                         |    Klass Word (64 bits)     |                    |<br>
	* |------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* | unused:25 | identity_hashcode:31 | unused:1 | age:4 | biased_lock:1 | lock:2 |    OOP to metadata object   |       Normal       |<br>
	* |------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* | thread:54 |       epoch:2        | unused:1 | age:4 | biased_lock:1 | lock:2 |    OOP to metadata object   |       Biased       |<br>
	* |------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* |                       ptr_to_lock_record:62                         | lock:2 |    OOP to metadata object   | Lightweight Locked |<br>
	* |------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* |                     ptr_to_heavyweight_monitor:62                   | lock:2 |    OOP to metadata object   | Heavyweight Locked |<br>
	* |------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* |                                                                     | lock:2 |    OOP to metadata object   |    Marked for GC   |<br>
	* |------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	*/
	// @formatter:on
	@SuppressWarnings("unused")
	private static final class __64_bit_no_UseCompressedOops extends __obj_header_base {

		// 64位JVM无OOP指针压缩
		public static final int HEADER_OFFSET = 0;
		public static final int HEADER_LENGTH = 128;

		public static final int MARKWORD_OFFSET = HEADER_OFFSET;
		public static final int MARKWORD_LENGTH = 64;
		public static final int KLASS_OFFSET = MARKWORD_OFFSET + MARKWORD_LENGTH;
		public static final int KLASS_LENGTH = 64;

		public static final int UNUSED_1_NORMAL_OFFSET = MARKWORD_OFFSET;
		public static final int UNUSED_1_NORMAL_LENGTH = 25;
		public static final int IDENTITY_HASHCODE_OFFSET = UNUSED_1_NORMAL_OFFSET + UNUSED_1_NORMAL_LENGTH;
		public static final int IDENTITY_HASHCODE_LENGTH = 31;
		public static final int UNUSED_2_NORMAL_OFFSET = IDENTITY_HASHCODE_OFFSET + IDENTITY_HASHCODE_LENGTH;
		public static final int UNUSED_2_NORMAL_LENGTH = 1;
		public static final int AGE_OFFSET = UNUSED_2_NORMAL_OFFSET + UNUSED_2_NORMAL_LENGTH;
		public static final int AGE_LENGTH = 4;
		public static final int BIASED_LOCK_OFFSET = AGE_OFFSET + AGE_LENGTH;
		public static final int BIASED_LOCK_LENGTH = 1;
		public static final int LOCK_OFFSET = BIASED_LOCK_OFFSET + BIASED_LOCK_LENGTH;
		public static final int LOCK_LENGTH = 2;

		public static final int THREAD_OFFSET = MARKWORD_OFFSET;
		public static final int THREAD_LENGTH = 54;
		public static final int EPOCH_OFFSET = THREAD_OFFSET + THREAD_LENGTH;
		public static final int EPOCH_LENGTH = 2;
		public static final int UNUSED_1_BIASED_OFFSET = EPOCH_OFFSET + EPOCH_LENGTH;
		public static final int UNUSED_1_BIASED_LENGTH = 1;

		public static final int PTR_TO_LOCK_RECORD_OFFSET = MARKWORD_OFFSET;
		public static final int PTR_TO_LOCK_RECORD_LENGTH = 62;

		public static final int PTR_TO_HEAVYWEIGHT_MONITOR_OFFSET = MARKWORD_OFFSET;
		public static final int PTR_TO_HEAVYWEIGHT_MONITOR_LENGTH = 62;
	}

	// @formatter:off
	/** <br>
	* ObjectHeader 64-bit JVM UseCompressedOops=true<br>
	* |--------------------------------------------------------------------------------------------------------------|--------------------|<br>
	* |                                            Object Header (96 bits)                                           |        State       |<br>
	* |--------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* |                                  Mark Word (64 bits)                           |    Klass Word (32 bits)     |                    |<br>
	* |--------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* | unused:25 | identity_hashcode:31 | cms_free:1 | age:4 | biased_lock:1 | lock:2 |    OOP to metadata object   |       Normal       |<br>
	* |--------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* | thread:54 |       epoch:2        | cms_free:1 | age:4 | biased_lock:1 | lock:2 |    OOP to metadata object   |       Biased       |<br>
	* |--------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* |                         ptr_to_lock_record                            | lock:2 |    OOP to metadata object   | Lightweight Locked |<br>
	* |--------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* |                     ptr_to_heavyweight_monitor                        | lock:2 |    OOP to metadata object   | Heavyweight Locked |<br>
	* |--------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	* |                                                                       | lock:2 |    OOP to metadata object   |    Marked for GC   |<br>
	* |--------------------------------------------------------------------------------|-----------------------------|--------------------|<br>
	 */
	// @formatter:on
	@SuppressWarnings("unused")
	private static final class __64_bit_with_UseCompressedOops extends __obj_header_base {
		// 64位JVM开启OOP指针压缩，JVM默认是开启的
		public static final int HEADER_OFFSET = 0;
		public static final int HEADER_LENGTH = 96;

		public static final int MARKWORD_OFFSET = HEADER_OFFSET;
		public static final int MARKWORD_LENGTH = 64;
		public static final int KLASS_OFFSET = MARKWORD_OFFSET + MARKWORD_LENGTH;
		public static final int KLASS_LENGTH = 32;

		public static final int UNUSED_1_NORMAL_OFFSET = MARKWORD_OFFSET;
		public static final int UNUSED_1_NORMAL_LENGTH = 25;
		public static final int IDENTITY_HASHCODE_OFFSET = UNUSED_1_NORMAL_OFFSET + UNUSED_1_NORMAL_LENGTH;
		public static final int IDENTITY_HASHCODE_LENGTH = 31;
		public static final int CMS_FREE_OFFSET = IDENTITY_HASHCODE_OFFSET + IDENTITY_HASHCODE_LENGTH;
		public static final int CMS_FREE_LENGTH = 1;
		public static final int AGE_OFFSET = CMS_FREE_OFFSET + CMS_FREE_LENGTH;
		public static final int AGE_LENGTH = 4;
		public static final int BIASED_LOCK_OFFSET = AGE_OFFSET + AGE_LENGTH;
		public static final int BIASED_LOCK_LENGTH = 1;
		public static final int LOCK_OFFSET = BIASED_LOCK_OFFSET + BIASED_LOCK_LENGTH;
		public static final int LOCK_LENGTH = 2;

		public static final int THREAD_OFFSET = MARKWORD_OFFSET;
		public static final int THREAD_LENGTH = 54;
		public static final int EPOCH_OFFSET = THREAD_OFFSET + THREAD_LENGTH;
		public static final int EPOCH_LENGTH = 2;

		public static final int PTR_TO_LOCK_RECORD_OFFSET = MARKWORD_OFFSET;
		public static final int PTR_TO_LOCK_RECORD_LENGTH = 62;

		public static final int PTR_TO_HEAVYWEIGHT_MONITOR_OFFSET = MARKWORD_OFFSET;
		public static final int PTR_TO_HEAVYWEIGHT_MONITOR_LENGTH = 62;

	}

	public static final int INVALID_OFFSET = -1;
	public static final int INVALID_LENGTH = -1;

	/**
	 * Mark Word的长度，单位bit
	 */
	public static final int MARKWORD_LENGTH;

	/**
	 * Klass Word的偏移量，单位bit
	 */
	public static final int KLASS_WORD_OFFSET;

	/**
	 * Klass Word的长度，单位bit
	 */
	public static final int KLASS_WORD_LENGTH;

	public static final int HEADER_LENGTH;

	/**
	 * Mark Word的长度，单位byte
	 */
	public static final int MARKWORD_BYTE_LENGTH;

	/**
	 * Klass Word的偏移量，单位byte
	 */
	public static final int KLASS_WORD_BYTE_OFFSET;

	/**
	 * Klass Word的长度，单位byte
	 */
	public static final int KLASS_WORD_BYTE_LENGTH;

	/**
	 * header总长度
	 */
	public static final int HEADER_BYTE_LENGTH;

	static {
		if (VmBase.NATIVE_JVM_BIT_VERSION == 32) {
			MARKWORD_LENGTH = __32_bit.MARKWORD_LENGTH;
			KLASS_WORD_OFFSET = __32_bit.KLASS_OFFSET;
			KLASS_WORD_LENGTH = __32_bit.KLASS_LENGTH;
			HEADER_LENGTH = __32_bit.HEADER_LENGTH;
		} else if (VmBase.NATIVE_JVM_BIT_VERSION == 64) {
			if (VmBase.UseCompressedOops) {
				MARKWORD_LENGTH = __64_bit_with_UseCompressedOops.MARKWORD_LENGTH;
				KLASS_WORD_OFFSET = __64_bit_with_UseCompressedOops.KLASS_OFFSET;
				KLASS_WORD_LENGTH = __64_bit_with_UseCompressedOops.KLASS_LENGTH;
				HEADER_LENGTH = __64_bit_with_UseCompressedOops.HEADER_LENGTH;
			} else {
				MARKWORD_LENGTH = __64_bit_no_UseCompressedOops.MARKWORD_LENGTH;
				KLASS_WORD_OFFSET = __64_bit_no_UseCompressedOops.KLASS_OFFSET;
				KLASS_WORD_LENGTH = __64_bit_no_UseCompressedOops.KLASS_LENGTH;
				HEADER_LENGTH = __64_bit_no_UseCompressedOops.HEADER_LENGTH;
			}
		} else {
			MARKWORD_LENGTH = INVALID_LENGTH;
			KLASS_WORD_OFFSET = INVALID_OFFSET;
			KLASS_WORD_LENGTH = INVALID_LENGTH;
			HEADER_LENGTH = INVALID_LENGTH;
		}
		MARKWORD_BYTE_LENGTH = MARKWORD_LENGTH / 8;
		KLASS_WORD_BYTE_OFFSET = KLASS_WORD_OFFSET / 8;
		KLASS_WORD_BYTE_LENGTH = KLASS_WORD_LENGTH / 8;
		HEADER_BYTE_LENGTH = HEADER_LENGTH / 8;
	}

	public static final long get_klass_word(Class<?> c) {
		return get_klass_word(InternalUnsafe.allocateInstance(c));
	}

	/**
	 * 获取对象头
	 * 
	 * @param obj
	 * @return
	 */
	public static final long get_klass_word(Object obj) {
		if (KLASS_WORD_LENGTH == 32)
			return InternalUnsafe.getInt(obj, KLASS_WORD_BYTE_OFFSET);
		else if (KLASS_WORD_LENGTH == 64)
			return InternalUnsafe.getLong(obj, KLASS_WORD_BYTE_OFFSET);
		else
			return 0;
	}

	/**
	 * 强制改写对象头
	 * 
	 * @param obj
	 * @param klassWord
	 * @return
	 */
	public static final boolean set_klass_word(Object obj, long klassWord) {
		if (KLASS_WORD_LENGTH == 32) {
			InternalUnsafe.putInt(obj, KLASS_WORD_BYTE_OFFSET, (int) klassWord);
			return true;
		} else if (KLASS_WORD_LENGTH == 64) {
			InternalUnsafe.putLong(obj, KLASS_WORD_BYTE_OFFSET, klassWord);
			return true;
		}
		return false;
	}
}
