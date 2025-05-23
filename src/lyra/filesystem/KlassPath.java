package lyra.filesystem;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import lyra.lang.JavaLang;

public class KlassPath {
	public static final String ClassExtensionName = ".class";

	/**
	 * 获取class的URL location
	 * 
	 * @param clazz
	 * @return
	 */
	@Deprecated
	public static String klassCodeSourceLocation(Class<?> clazz) {
		URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
		if (location != null) {
			try {
				return URLDecoder.decode(location.getPath(), StandardCharsets.UTF_8.name());
			} catch (UnsupportedEncodingException ex) {
				throw new AssertionError("UTF-8 not supported", ex);
			}
		}
		return null;
	}

	/**
	 * 获取指定class的文件所在目录URI，可能是本地class文件，也可能是jar打包的class文件<br>
	 * 例如Minecraft模组BlueArchive: Rendezvous的模组jar中主类URI路径为/D:/JavaProjects/testClient/.minecraft/mods/ba-1.0.0.jar#191!/ba<br>
	 * 其中!为Java URL的分隔符，前面是jar包路径，后面是jar包内的路径。<br>
	 * #191为FML的ClassLoader自行添加的标识符，不同框架添加的标识符可能不一样，也可能不额外添加标识符<br>
	 * 
	 * @param any_class
	 * @return
	 */
	public static String localKlassLocationUri(Class<?> any_class) {
		String path = null;
		if (any_class.getResource(any_class.getSimpleName() + ClassExtensionName) == null)// 目标class的文件找不到
			return null;
		try {
			if (any_class.getClassLoader() == null)
				path = ClassLoader.getSystemResource("").toURI().getPath();
			else
				path = any_class.getResource("").toURI().getPath();
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
		}
		return path;
	}

	/**
	 * 传入jar中的一个类，获取对应的jar绝对路径，包括BootstrapClassLoader加载的jar
	 * 
	 * @param any_class_in_jar jar内的任意一个类
	 * @return class的本地文件路径；当class在jar内时返回jar的绝对路径
	 */
	public static String localKlassLocation(Class<?> any_class, UriPath.Resolver resolver) {
		return UriPath.resolve(localKlassLocationUri(any_class), resolver).filesystem_path;
	}

	public static String localKlassLocation(Class<?> any_class) {
		return localKlassLocation(any_class, UriPath.Resolver.DEFAULT);
	}

	/**
	 * 获取any_class所在jar的路径或者未打包的类文件夹
	 * 
	 * @param any_class
	 * @param resolver  路径解析器，根据URI分割为合法的文件系统路径和Entry路径
	 * @return
	 */
	public static String getKlassPath(Class<?> any_class, UriPath.Resolver resolver) {
		return localKlassLocation(any_class, resolver);
	}

	public static String getKlassPath(UriPath.Resolver resolver) {
		Class<?> caller = JavaLang.getOuterCallerClass();// 获取调用该方法的类
		return getKlassPath(caller, resolver);
	}

	public static String getKlassPath() {
		Class<?> caller = JavaLang.getOuterCallerClass();// 获取调用该方法的类
		return getKlassPath(caller, UriPath.Resolver.DEFAULT);
	}
}
