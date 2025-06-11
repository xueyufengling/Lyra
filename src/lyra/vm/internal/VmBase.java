package lyra.vm.internal;

import java.lang.management.ManagementFactory;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;

/**
 * JVM的相关参数获取，没有依赖任何lyra.vm.base外部类。
 */
public abstract class VmBase {
	/**
	 * 是否开启oop压缩，默认顺带开启对象头的klass word压缩（UseCompressedClassPointers）。
	 */
	public static final boolean UseCompressedOops;

	/**
	 * 对象字节对齐，默认为8,必须是2的幂，一般来说是机器的数据字大小，即int类型大小。
	 */
	public static final long ObjectAlignmentInBytes;

	/**
	 * 堆内存base的最小地址。
	 */
	public static final long HeapBaseMinAddress;

	/**
	 * 64或32，JVM有没有16位的还真不清楚
	 */
	public static final int NATIVE_JVM_BIT_VERSION;

	/**
	 * 是否运行在64位JVM，该变量为缓存值，用于指针的快速条件判断
	 */
	public static final boolean ON_64_BIT_JVM;

	/**
	 * JVM是否是HotSpot，如果是才能使用HotSpotDiagnostic获取JVM参数
	 */
	public static final boolean NATIVE_JVM_HOTSPOT;

	/**
	 * 64位JVM开启UseCompressedOops的情况下，如果oop被压缩时，指向的地址有按位偏移。NATIVE_ADDRESS_SHIFT=log2(ObjectAlignmentInBytes)
	 */
	public static final int OOP_ENCODE_ADDRESS_SHIFT;

	/**
	 * 机器CPU的数据字长度，也是native指针长度。根据JVM位数判断。
	 */
	public static final int NATIVE_WORD_SIZE;

	/**
	 * uint32_t的最大值，用于掩码和计算32位机器最大寻址地址。
	 */
	public static final long MAX_JUINT = 0xFFFFFFFFL;

	/**
	 * JVM中未压缩oop时支持的最大堆内存大小，类型为uint，实际上是4G
	 */
	public static final long UnscaledOopHeapMax;

	/**
	 * JVM中压缩了oop时支持的最大堆内存大小，类型为ulong，实际上是32G
	 */
	public static final long OopEncodingHeapMax;

	/**
	 * HotSpotDiagnosticMXBean的实现类是 com.sun.management.internal.HotSpotDiagnostic
	 */
	public static final HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean;

	static {
		String bit_version = System.getProperty("sun.arch.data.model");
		if (bit_version != null && bit_version.contains("64"))
			NATIVE_JVM_BIT_VERSION = 64;
		else {
			String arch = System.getProperty("os.arch");
			if (arch != null && arch.contains("64"))
				NATIVE_JVM_BIT_VERSION = 64;
			else
				NATIVE_JVM_BIT_VERSION = 32;
		}
		if (NATIVE_JVM_BIT_VERSION == 64)
			ON_64_BIT_JVM = true;
		else
			ON_64_BIT_JVM = false;
		NATIVE_WORD_SIZE = NATIVE_JVM_BIT_VERSION / 8;// 实际上还有16位机器，此时数据字长度只有2字节，但我们这无法获取本地机器的数据字长度，只能根据JVM位数去判断。
		boolean hotspot = false;
		boolean compressed_oops = false;
		long align_bytes = 8;
		long heap_base_min_addr = 0;
		hotSpotDiagnosticMXBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
		if (hotSpotDiagnosticMXBean != null) {
			hotspot = true;// 获取HotSpotDiagnosticMXBean的getVMOption()方法
			if (NATIVE_JVM_BIT_VERSION == 64) // 64位JVM需要检查是否启用了指针压缩
				compressed_oops = getBooleanOption("UseCompressedOops");
			align_bytes = getLongOption("ObjectAlignmentInBytes");
			heap_base_min_addr = getLongOption("HeapBaseMinAddress");
		}
		NATIVE_JVM_HOTSPOT = hotspot;

		UseCompressedOops = compressed_oops;
		ObjectAlignmentInBytes = align_bytes;
		HeapBaseMinAddress = heap_base_min_addr;

		OOP_ENCODE_ADDRESS_SHIFT = uint64_log2(ObjectAlignmentInBytes);

		UnscaledOopHeapMax = MAX_JUINT + 1;// 2^32
		OopEncodingHeapMax = UnscaledOopHeapMax << OOP_ENCODE_ADDRESS_SHIFT;// 使用OOP压缩编码后支持的最大的堆内存
	}

	/**
	 * 求2为底的对数，用于2的整数次幂的快速算法
	 * 
	 * @param num
	 * @return -1为无效结果
	 */
	public static int uint64_log2(long uint64) {
		if (uint64 == 0)// 非法值
			return -1;
		int power = 0;
		long i = 0x01;
		while (i != uint64) {
			++power;
			if (i == 0)// 溢出
				return -1;
			i <<= 1;
		}
		return power;
	}

	public static VMOption getVmOption(String name) {
		return hotSpotDiagnosticMXBean.getVMOption(name);
	}

	/**
	 * 获取指定的boolean类型的VM参数
	 * 
	 * @param option_name 参数名称，例如UseCompressedOops
	 * @return
	 */
	public static boolean getBooleanOption(String option_name) {
		return Boolean.parseBoolean(getVmOption(option_name).getValue().toString());
	}

	public static int getIntOption(String option_name) {
		return Integer.parseInt(getVmOption(option_name).getValue().toString());
	}

	public static long getLongOption(String option_name) {
		return Long.parseLong(getVmOption(option_name).getValue().toString());
	}

	public static void dumpHeap(String fileName, boolean live) {
		try {
			hotSpotDiagnosticMXBean.dumpHeap(fileName, live);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean isPrimitiveBoxingType(Object obj) {
		Class<?> cls = obj.getClass();
		return cls == Character.class || cls == Boolean.class || (obj instanceof Number);
	}

	/**
	 * 堆内存的最大大小
	 * 
	 * @return
	 */
	public static long maxHeapSize() {
		return Runtime.getRuntime().maxMemory();
	}

	/**
	 * 堆内存的当前大小
	 * 
	 * @return
	 */
	public static long currentHeapSize() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * 堆内存的当前空闲空间，创建新对象时空闲空间减小，GC后空闲空间增加
	 * 
	 * @return
	 */
	public static long freeHeapSize() {
		return Runtime.getRuntime().freeMemory();
	}
}
