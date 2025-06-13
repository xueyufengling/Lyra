package lyra.lang;

import java.lang.reflect.Array;
import java.util.List;

public class Arrays {
	@SuppressWarnings("unchecked")
	public static final <T> T[] cat(T[]... arrays) {
		int total_len = 0;
		for (int idx = 0; idx < arrays.length; ++idx)
			total_len += arrays[idx].length;
		Object[] result = new Object[total_len];
		int ptr = 0;
		for (int idx = 0; idx < arrays.length; ptr += arrays[idx].length, ++idx)
			System.arraycopy(arrays[idx], 0, result, ptr, arrays[idx].length);
		return (T[]) result;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] toArray(Class<T> c, List<T> list) {
		T[] arr = (T[]) Array.newInstance(c, list.size());
		return list.toArray(arr);
	}
}
