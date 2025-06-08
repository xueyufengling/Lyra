package lyra.internal;

import java.nio.ByteOrder;

import lyra.lang.InternalUnsafe;

public class memory {

	/**
	 * 字节序
	 */
	public static enum Endian {
		LITTLE, BIG;
	}

	public static final Endian LOCAL_ENDIAN;

	static {
		LOCAL_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? Endian.BIG : Endian.LITTLE;
	}

	public static final pointer malloc(long size) {
		return pointer.at(InternalUnsafe.allocateMemory(size));
	}

	public static final pointer malloc(long size, Class<?> type_cls) {
		return pointer.at(InternalUnsafe.allocateMemory(size * type.sizeof(type_cls)), type_cls);
	}

	public static final void free(pointer ptr) {
		InternalUnsafe.freeMemory(ptr.address());
	}
}
