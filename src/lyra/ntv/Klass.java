package lyra.ntv;

public class Klass {
	static {
		LibLyra.loadLibrary();
	}

	public static final native long klassWord(Object obj);

	public static final native long klassWord(Class<?> klass);

	public static final native long klassWord(String klassName);
}
