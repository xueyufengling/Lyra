package lyra.ntv;

public class LibLyra {
	private static boolean loadedLibrary = false;

	public static final void loadLibrary() {
		if (loadedLibrary)
			return;
		loadedLibrary = true;
		System.loadLibrary("Lyra");
	}
}
