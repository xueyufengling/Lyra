package lyra.vm.base;

import java.lang.management.ManagementFactory;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;

/**
 * JVM的相关参数获取，没有依赖任何lyra.vm.base外部类。
 */
public abstract class VmBase {
	public static final int NATIVE_JVM_BIT_VERSION;// 64或32
	public static final boolean NATIVE_JVM_HOTSPOT;// JVM是否是HotSpot，如果是才能获取JVM参数进而判断指针是否压缩
	public static final boolean UseCompressedOops;

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
		boolean hotspot = false;
		boolean compressed_oops = false;
		hotSpotDiagnosticMXBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
		if (hotSpotDiagnosticMXBean != null) {
			hotspot = true;// 获取HotSpotDiagnosticMXBean的getVMOption()方法
			if (NATIVE_JVM_BIT_VERSION == 64) // 64位JVM需要检查是否启用了指针压缩
				compressed_oops = getBooleanOption("UseCompressedOops");
		}
		NATIVE_JVM_HOTSPOT = hotspot;
		UseCompressedOops = compressed_oops;
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
