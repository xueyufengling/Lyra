package lyra.cxx;

import java.util.HashMap;

public class cxx_field {
	public static class offset_counter {
		private static final HashMap<String, offset_counter> counters = new HashMap<>();

		long current_offset = 0;

		private offset_counter(long start_offset) {
			current_offset = start_offset;
		}

		public long append(long typeSize) {
			return current_offset += typeSize;
		}

		public static offset_counter get(String typeName) {
			return counters.computeIfAbsent(typeName, (String name) -> new offset_counter(0));
		}
	}
}
