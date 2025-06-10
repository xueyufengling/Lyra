package lyra.vm.base;

import java.lang.management.ManagementFactory;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;

/**
 * JVM的相关参数获取，没有依赖任何lyra.vm.base外部类。
 */
public abstract class VmBase {
	public static final boolean UseCompressedOops;
	public static final int ObjectAlignmentInBytes;// 对象字节对齐，默认为8,必须是2的幂

	public static final int NATIVE_JVM_BIT_VERSION;// 64或32
	public static final boolean ON_64_BIT_JVM;// 是否运行在64位JVM，该变量为缓存值，用于指针的快速条件判断
	public static final boolean NATIVE_JVM_HOTSPOT;// JVM是否是HotSpot，如果是才能获取JVM参数进而判断指针是否压缩
	public static final int NATIVE_ADDRESS_SHIFT;// 64位JVM开启UseCompressedOops的情况下，oop被压缩，指向的地址有按位偏移。NATIVE_ADDRESS_SHIFT=log2(ObjectAlignmentInBytes)

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
		boolean hotspot = false;
		boolean compressed_oops = false;
		int align_bytes = 8;
		hotSpotDiagnosticMXBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
		if (hotSpotDiagnosticMXBean != null) {
			hotspot = true;// 获取HotSpotDiagnosticMXBean的getVMOption()方法
			if (NATIVE_JVM_BIT_VERSION == 64) // 64位JVM需要检查是否启用了指针压缩
				compressed_oops = getBooleanOption("UseCompressedOops");
			align_bytes = getIntOption("ObjectAlignmentInBytes");
		}
		NATIVE_JVM_HOTSPOT = hotspot;
		UseCompressedOops = compressed_oops;
		ObjectAlignmentInBytes = align_bytes;
		NATIVE_ADDRESS_SHIFT = uint32_log2(ObjectAlignmentInBytes);
	}

	/**
	 * 求2为底的对数，用于2的整数次幂的快速算法
	 * 
	 * @param num
	 * @return -1为无效结果
	 */
	public static int uint32_log2(int uint32) {
		if (uint32 == 0)// 非法值
			return -1;
		int power = 0;
		int i = 0x01;
		while (i != uint32) {
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
}
