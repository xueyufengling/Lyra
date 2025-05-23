package jvm.filesystem;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import jvm.lang.JavaLang;

public class LocalFiles {
	public static List<String> getClassNamesInLocalPackage(Class<?> any_class_in_package, UriPath.Resolver resolver, String package_name, boolean include_subpackage) {
		List<String> class_names = new ArrayList<>();
		FileSystem.filterClass(KlassPath.getKlassPath(any_class_in_package, resolver), include_subpackage, (String class_full_name, Path entry) -> {
			if (class_full_name.startsWith(package_name))
				class_names.add(class_full_name);
			return true;
		});
		return class_names;
	}

	public static List<String> getClassNamesInLocalPackage(Class<?> any_class_in_package, String package_name, boolean include_subpackage) {
		return getClassNamesInLocalPackage(any_class_in_package, UriPath.Resolver.DEFAULT, package_name, include_subpackage);
	}

	public static List<String> getClassNamesInLocalPackage(String package_name, boolean include_subpackage) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		return getClassNamesInLocalPackage(caller, package_name, include_subpackage);// 获取调用该方法的类
	}

	public static List<String> getClassNamesInLocalPackage(Class<?> any_class_in_package, String package_name) {
		return getClassNamesInLocalPackage(any_class_in_package, package_name, false);
	}

	public static List<String> getClassNamesInLocalPackage(String package_name) {
		Class<?> caller = JavaLang.getOuterCallerClass();
		return getClassNamesInLocalPackage(caller, package_name);// 获取调用该方法的类
	}
}
