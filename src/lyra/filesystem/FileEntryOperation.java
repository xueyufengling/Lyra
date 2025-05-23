package jvm.filesystem;

import java.nio.file.Path;

@FunctionalInterface
public interface FileEntryOperation {
	/**
	 * 遍历处理JarEntry
	 * 
	 * @param entry
	 * @param bytes
	 * @return 在collect时是否收集该Entry
	 */
	public boolean operate(String start_path, Path entry);

	@FunctionalInterface
	public static interface File {
		/**
		 * 单个文件处理
		 * 
		 * @param start_path        开始遍历的路径
		 * @param relative_file_dir 相对于开始路径的文件所在文件夹路径
		 * @param file_name         文件名称
		 * @param entry
		 */
		public boolean operate(String start_path, String relative_file_dir, String file_name, Path entry);
	}

	@FunctionalInterface
	public static interface Class {
		/**
		 * 单个文件处理
		 * 
		 * @param class_full_name
		 * @param entry
		 * @param bytes
		 */
		public boolean operate(String class_full_name, Path entry);
	}
}
