package lyra.klass;

import lyra.object.ObjectManipulator;

public interface Mirror {
	@SuppressWarnings("unchecked")
	public default <T> T cast(T destTypeObj) {
		return (T) ObjectManipulator.cast(this, destTypeObj);
	}
}
