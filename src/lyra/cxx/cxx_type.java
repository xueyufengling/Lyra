package lyra.cxx;

import java.util.HashMap;

public class cxx_type {
	private static final HashMap<String, cxx_type> definedTypes = new HashMap<>();

	public static final cxx_type get(String type) {
		return definedTypes.computeIfAbsent(type, (String t) -> new cxx_type());
	}

	private long length;

	public long size() {
		return length;
	}

	/**
	 * 用于将signed int类型储存的unsigned int值转换为unsigned long值。<br>
	 * 用法：{@code uint64_t addr = (int32_t) & UINT32_T_MASK;}
	 */
	public static final long UINT32_T_MASK = 0xFFFFFFFFL;

	public static final long uint_ptr(int oop_addr) {
		return oop_addr & UINT32_T_MASK;
	}

	public static final long UINT16_T_MASK = 0xFFFFL;

	public static final long uint_ptr(short s) {
		return s & UINT16_T_MASK;
	}

	public static final long UINT8_T_MASK = 0xFFL;

	public static final long uint_ptr(byte b) {
		return b & UINT8_T_MASK;
	}

	public static final long uint_ptr(char c) {
		return c & UINT8_T_MASK;
	}

	public static final int UINT8_T_MASK_I = 0xFF;

	public static final int uint8_t(byte b) {
		return b & UINT8_T_MASK_I;
	}

	public static final int uint8_t(char c) {
		return c & UINT8_T_MASK_I;
	}
}
