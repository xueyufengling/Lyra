package jvm.filesystem;

import java.util.List;

import jvm.filesystem.jar.JarFiles;
import jvm.lang.JavaLang;

public class Packages {
	public static List<String> getClassNamesInPackage(Class<?> any_class_in_package, UriPath.Resolver resolver, String package_name, boolean include_subpackage) {
		String path = KlassPath.getKlassPath(any_class_in_package, resolver);
		if (path.endsWith(JarFiles.JarExtensionName))
			return JarFiles.getClassNamesInJarPackage(any_class_in_package, resolver, package_name, include_subpackage);
		else
			return LocalFiles.getClassNamesInLocalPackage(any_class_in_package, resolver, package_name, include_subpackage);
	}

	public static List<String> getClassNamesInPackage(Class<?> any_class_in_package, String package_name, boolean include_subpackage) {
		return getClassNamesInPackage(any_class_in_package, UriPath.Resolver.DEFAULT, package_name, include_subpackage);
	}

	public static List<String> getClassNamesInPackage(String package_name, boolean include_subpackage) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		return getClassNamesInPackage(caller, package_name, include_subpackage);// 获取调用该方法的类
	}

	public static List<String> getClassNamesInPackage(Class<?> any_class_in_package, String package_name) {
		return getClassNamesInPackage(any_class_in_package, package_name, false);
	}

	public static List<String> getClassNamesInPackage(String package_name) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		return getClassNamesInPackage(caller, package_name);// 获取调用该方法的类
	}
}
