package lyra.klass.special;

import lyra.klass.GenericTypes;

public interface RuntimeGenericType {
	@SuppressWarnings("deprecation")
	public default Class<?>[] genericTypes() {
		return GenericTypes.classes(this.getClass());
	}

	public default Class<?> genericType(int idx) {
		return genericTypes()[idx];
	}

	public default Class<?> genericType() {
		return genericTypes()[0];
	}
}
