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
}
