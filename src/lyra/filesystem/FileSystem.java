package lyra.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;

import lyra.filesystem.jar.JarEntryData;
import lyra.filesystem.jar.JarEntryOperation;
import lyra.filesystem.jar.JarFiles;

public class FileSystem {

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	public static boolean isRootDir(String dir) {
		return dir == null || dir.equals("") || dir.equals("/");
	}

	public static boolean isWindowsOs() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	/**
	 * 标准化绝对路径
	 * 
	 * @param path
	 * @return
	 */
	public static String standardizedAbsPath(String path) {
		char ch = path.charAt(0);// 检查开头有没有多余的路径分隔符
		boolean isWindowsOs = isWindowsOs();
		if (ch == File.separatorChar || ch == '/') {
			if (isWindowsOs)
				path = path.substring(1);
		} else {
			if (!isWindowsOs)
				path = "/" + path;
		}
		ch = path.charAt(path.length() - 1);// 检查路径末尾有没有多余的路径分隔符
		if (ch == File.separatorChar || ch == '/')
			path = path.substring(0, path.length() - 1);
		path = path.replace('/', File.separatorChar);
		return path;
	}

	/**
	 * 标准化相对路径
	 * 
	 * @param path
	 * @return
	 */
	public static String standardizedRltPath(String path) {
		char ch = path.charAt(0);// 检查开头有没有多余的路径分隔符
		if (ch == File.separatorChar || ch == '/')
			path = path.substring(1);
		ch = path.charAt(path.length() - 1);// 检查路径末尾有没有多余的路径分隔符
		if (ch == File.separatorChar || ch == '/')
			path = path.substring(0, path.length() - 1);
		return path;
	}

	public static byte[] read(Path path) {
		try {
			return Files.readAllBytes(path);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// -------------------------------------------------------- forEach Operations --------------------------------------------------------------------

	// foreach函数族
	/**
	 * 遍历每个JarEntry
	 * 
	 * @param jar
	 * @param op
	 */
	@SuppressWarnings("deprecation")
	public static void foreachEntries(InputStream jar, JarEntryOperation op) {
		try (JarInputStream jar_stream = new JarInputStream(jar)) {
			JarEntry entry = null;
			while ((entry = jar_stream.getNextJarEntry()) != null) {
				op.operate(entry, JarFiles.getJarEntryBytes(jar_stream, DEFAULT_BUFFER_SIZE));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 遍历本地文件系统
	 * 
	 * @param start_path
	 * @param op
	 */
	public static void foreachEntries(String start_path, FileEntryOperation op) {
		Path root = Paths.get(start_path);
		try (Stream<Path> paths = Files.walk(root)) {
			paths.forEach(file -> {
				op.operate(start_path, file);
			});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 遍历每个文件
	 * 
	 * @param jar
	 * @param op
	 */
	public static void foreachFiles(InputStream jar, JarEntryOperation.File op) {
		foreachEntries(jar, new JarEntryOperation() {
			@Override
			public boolean operate(JarEntry entry, ByteArrayOutputStream bytes) {
				if (!entry.isDirectory()) {
					String path = entry.getName();
					int sep = path.lastIndexOf('/');
					op.operate(sep == -1 ? "/" : path.substring(0, sep), path.substring(sep + 1), entry, bytes);
				}
				return true;
			}
		});
	}

	public static void foreachFiles(String start_path, FileEntryOperation.File op) {
		foreachFiles(start_path, true, op);
	}

	/**
	 * 从指定目录开始遍历每个文件
	 * 
	 * @param jar
	 * @param start_path         开始遍历的目录
	 * @param include_subpackage 是否遍历子目录
	 * @param op
	 */
	public static void foreachFiles(InputStream jar, String start_path, boolean include_subpackage, JarEntryOperation.File op) {
		foreachFiles(jar, new JarEntryOperation.File() {
			@Override
			public boolean operate(String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) {
				if (isRootDir(start_path)) {
					if (isRootDir(file_dir))
						op.operate(file_dir, file_name, entry, bytes);
				} else {
					if (!isRootDir(file_dir) && file_dir.startsWith(start_path))
						op.operate(file_dir, file_name, entry, bytes);
				}
				return true;
			}
		});
	}

	public static void foreachFiles(String start_path, boolean include_subpackage, FileEntryOperation.File op) {
		String std_start_path = standardizedAbsPath(start_path);
		Path root = Paths.get(std_start_path);
		try (Stream<Path> paths = Files.walk(root, include_subpackage ? Integer.MAX_VALUE : 1)) {
			paths.filter(Files::isRegularFile)
					.forEach(file -> {
						op.operate(std_start_path, standardizedRltPath(standardizedAbsPath(file.getParent().toString()).replace(std_start_path, "")), file.getFileName().toString(), file);
					});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// collect函数族

	/**
	 * 按照条件收集Entry
	 * 
	 * @param jar
	 * @param condition 条件，返回true则代表收集，false代表不收集
	 * @return
	 */
	public static List<JarEntryData> collectFiles(InputStream jar, JarEntryOperation.File condition) {
		List<JarEntryData> entries = new ArrayList<>();
		foreachFiles(jar, new JarEntryOperation.File() {
			@Override
			public boolean operate(String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) {
				boolean reserved = condition.operate(file_dir, file_name, entry, bytes);
				if (reserved)
					entries.add(JarEntryData.from(file_dir, file_name, entry, bytes.toByteArray()));
				return reserved;
			}
		});
		return entries;
	}

	public static List<Path> collectFiles(String start_path, FileEntryOperation.File condition) {
		return collectFiles(start_path, true, condition);
	}

	/**
	 * 遍历子包收集文件
	 * 
	 * @param jar
	 * @param start_path
	 * @param include_subpackage
	 * @param condition
	 * @return
	 */
	public static List<JarEntryData> collectFiles(InputStream jar, String start_path, boolean include_subpackage, JarEntryOperation.File condition) {
		List<JarEntryData> entries = new ArrayList<>();
		foreachFiles(jar, start_path, include_subpackage, new JarEntryOperation.File() {
			@Override
			public boolean operate(String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) {
				boolean reserved = condition.operate(file_dir, file_name, entry, bytes);
				if (reserved)
					entries.add(JarEntryData.from(file_dir, file_name, entry, bytes.toByteArray()));
				return reserved;
			}
		});
		return entries;
	}

	public static List<Path> collectFiles(String start_path, boolean include_subpackage, FileEntryOperation.File condition) {
		List<Path> entries = new ArrayList<>();
		foreachFiles(start_path, include_subpackage, new FileEntryOperation.File() {
			@Override
			public boolean operate(String start_root_path, String relative_file_dir, String file_name, Path entry) {
				boolean reserved = condition.operate(start_root_path, relative_file_dir, file_name, entry);
				if (reserved)
					entries.add(entry);
				return reserved;
			}
		});
		return entries;
	}

	// collect函数的具体实现
	/**
	 * 收集jar中的文件名匹配正则表达式的文件
	 * 
	 * @param jar
	 * @param regex
	 * @return
	 */
	public static List<JarEntryData> collectFilesByRegex(InputStream jar, String regex) {
		return collectFiles(jar, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			return file_name.matches(regex);
		});
	}

	public static List<Path> collectFilesByRegex(String start_path, String regex) {
		return collectFiles(start_path, (String start_root_path, String relative_file_dir, String file_name, Path entry) -> {
			return file_name.matches(regex);
		});
	}

	public static List<JarEntryData> collectFilesByRegex(InputStream jar, String start_path, boolean include_subpackage, String regex) {
		return collectFiles(jar, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			return file_name.matches(regex);
		});
	}

	public static List<Path> collectFilesByRegex(String start_path, boolean include_subpackage, String regex) {
		return collectFiles(start_path, include_subpackage, (String start_root_path, String relative_file_dir, String file_name, Path entry) -> {
			return file_name.matches(regex);
		});
	}

	public static List<JarEntryData> collectFilesByType(InputStream jar, String type) {
		return collectFiles(jar, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			return file_name.endsWith(type);
		});
	}

	public static List<Path> collectFilesByType(String start_path, String type) {
		return collectFiles(start_path, (String start_root_path, String relative_file_dir, String file_name, Path entry) -> {
			return file_name.endsWith(type);
		});
	}

	public static List<JarEntryData> collectFilesByType(InputStream jar, String start_path, boolean include_subpackage, String file_type) {
		return collectFiles(jar, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			return file_name.endsWith(file_type);
		});
	}

	public static List<Path> collectFilesByType(String start_path, boolean include_subpackage, String file_type) {
		return collectFiles(start_path, include_subpackage, (String start_root_path, String relative_file_dir, String file_name, Path entry) -> {
			return file_name.endsWith(file_type);
		});
	}

	// filter函数族
	/**
	 * 遍历指定条件的文件并执行操作
	 * 
	 * @param jar
	 * @param condition
	 * @param op
	 */
	public static void filterFiles(InputStream jar, JarEntryOperation.File condition, JarEntryOperation.File op) {
		foreachFiles(jar, new JarEntryOperation.File() {
			@Override
			public boolean operate(String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) {
				if (condition.operate(file_dir, file_name, entry, bytes))
					op.operate(file_dir, file_name, entry, bytes);
				return true;
			}
		});
	}

	public static void filterFiles(String start_path, FileEntryOperation.File condition, FileEntryOperation.File op) {
		filterFiles(start_path, true, condition, op);
	}

	// ---------
	public static void filterFiles(InputStream jar, String start_path, boolean include_subpackage, JarEntryOperation.File condition, JarEntryOperation.File op) {
		foreachFiles(jar, start_path, include_subpackage, new JarEntryOperation.File() {
			@Override
			public boolean operate(String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) {
				if (condition.operate(file_dir, file_name, entry, bytes))
					op.operate(file_dir, file_name, entry, bytes);
				return true;
			}
		});
	}

	public static void filterFiles(String start_path, boolean include_subpackage, FileEntryOperation.File condition, FileEntryOperation.File op) {
		foreachFiles(start_path, include_subpackage, new FileEntryOperation.File() {
			@Override
			public boolean operate(String start_root_path, String relative_file_dir, String file_name, Path entry) {
				if (condition.operate(start_root_path, relative_file_dir, file_name, entry))
					op.operate(start_root_path, relative_file_dir, file_name, entry);
				return true;
			}
		});
	}

	// -----------
	public static void filterFilesByRegex(InputStream jar, String regex, JarEntryOperation.File op) {
		filterFiles(jar, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			return file_name.matches(regex);
		}, op);
	}

	public static void filterFilesByRegex(String start_path, String regex, FileEntryOperation.File op) {
		filterFilesByRegex(start_path, true, regex, op);
	}

	// ------------
	public static void filterFilesByRegex(InputStream jar, String start_path, boolean include_subpackage, String regex, JarEntryOperation.File op) {
		filterFiles(jar, start_path, include_subpackage, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			return file_name.matches(regex);
		}, op);
	}

	public static void filterFilesByRegex(String start_path, boolean include_subpackage, String regex, FileEntryOperation.File op) {
		filterFiles(start_path, include_subpackage, (String start_root_path, String relative_file_dir, String file_name, Path entry) -> {
			return file_name.matches(regex);
		}, op);
	}

	// ------------
	public static void filterFilesByType(InputStream jar, String file_type, JarEntryOperation.File op) {
		filterFiles(jar, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			return file_name.endsWith(file_type);
		}, op);
	}

	public static void filterFilesByType(String start_path, String file_type, FileEntryOperation.File op) {
		filterFilesByType(start_path, true, file_type, op);
	}

	// ------------
	public static void filterFilesByType(InputStream jar, String start_path, boolean include_subpackage, String file_type, JarEntryOperation.File op) {
		filterFiles(jar, start_path, include_subpackage, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			return file_name.endsWith(file_type);
		}, op);
	}

	public static void filterFilesByType(String start_path, boolean include_subpackage, String file_type, FileEntryOperation.File op) {
		filterFiles(start_path, include_subpackage, (String start_root_path, String relative_file_dir, String file_name, Path entry) -> {
			return file_name.endsWith(file_type);
		}, op);
	}

	// ----------------------------------------------------------------- Class --------------------------------------------------------------------------
	public static void filterClass(InputStream jar, JarEntryOperation.Class op) {
		filterFilesByType(jar, KlassPath.ClassExtensionName, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			String full_path = entry.getName();
			op.operate(full_path.substring(0, full_path.length() - KlassPath.ClassExtensionName.length()).replace('/', '.'), entry, bytes);
			return true;
		});
	}

	public static void filterClass(String start_path, FileEntryOperation.Class op) {
		filterClass(start_path, true, op);
	}

	// ------------
	public static void filterClass(InputStream jar, String start_path, boolean include_subpackage, JarEntryOperation.Class op) {
		filterFilesByType(jar, start_path, include_subpackage, KlassPath.ClassExtensionName, (String file_dir, String file_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
			String full_path = entry.getName();
			op.operate(full_path.substring(0, full_path.length() - KlassPath.ClassExtensionName.length()).replace('/', '.'), entry, bytes);
			return true;
		});
	}

	public static void filterClass(String start_path, boolean include_subpackage, FileEntryOperation.Class op) {
		filterFilesByType(start_path, include_subpackage, KlassPath.ClassExtensionName, (String start_root_path, String relative_file_dir, String file_name, Path entry) -> {
			String relative_bin_path = relative_file_dir.replace(File.separatorChar, '.') + '.' + file_name;// 例如pkg.example.A.class
			op.operate(relative_bin_path.substring(0, relative_bin_path.length() - KlassPath.ClassExtensionName.length()), entry);
			return true;
		});
	}

	// ------------
	public static HashMap<String, byte[]> collectClass(InputStream... jars) {
		HashMap<String, byte[]> classDefs = new HashMap<>();
		for (InputStream jar : jars)
			filterClass(jar, (String class_full_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
				classDefs.put(class_full_name, bytes.toByteArray());
				return true;
			});
		return classDefs;
	}

	public static HashMap<String, byte[]> collectClass(String... start_paths) {
		HashMap<String, byte[]> classDefs = new HashMap<>();
		for (String start_path : start_paths)
			filterClass(start_path, (String class_full_name, Path entry) -> {
				classDefs.put(class_full_name, read(entry));
				return true;
			});
		return classDefs;
	}

	// ------------
	public static HashMap<String, byte[]> collectClass(String start_path, boolean include_subpackage, InputStream... jars) {
		HashMap<String, byte[]> classDefs = new HashMap<>();
		for (InputStream jar : jars)
			filterClass(jar, start_path, include_subpackage, (String class_full_name, JarEntry entry, ByteArrayOutputStream bytes) -> {
				classDefs.put(class_full_name, bytes.toByteArray());
				return true;
			});
		return classDefs;
	}

	public static HashMap<String, byte[]> collectClass(boolean include_subpackage, String... start_paths) {
		HashMap<String, byte[]> classDefs = new HashMap<>();
		for (String start_path : start_paths)
			filterClass(start_path, include_subpackage, (String class_full_name, Path entry) -> {
				classDefs.put(class_full_name, read(entry));
				return true;
			});
		return classDefs;
	}
}
