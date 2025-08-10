package lyra.klass;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;

public class KlassScanner {
	@FunctionalInterface
	public static interface Filter {
		/**
		 * 过滤AnnotatedElement的条件。
		 * 
		 * @param scannedCls
		 * @return 返回为true才收集该元素
		 */
		public boolean condition(AnnotatedElement scannedAE);

		public static final Filter RESERVE_ALL = (AnnotatedElement scannedAE) -> true;

		@FunctionalInterface
		public static interface Klass {
			/**
			 * 过滤扫描到的类，只有返回true的类才被保留。
			 * 
			 * @param scannedCls
			 * @return
			 */
			public boolean condition(Class<?> scannedCls);

			public static final Klass RESERVE_ALL = (Class<?> scannedCls) -> true;
		}
	}

	/**
	 * 扫描所有被注解的元素，包括Class，Field，Method，Constructor等<br>
	 * 
	 * @param loader
	 * @param filter
	 * @return
	 */
	public static ArrayList<AnnotatedElement> scanAnnotatedElements(ClassLoader loader, Filter filter) {
		ArrayList<AnnotatedElement> annotated = new ArrayList<>();
		ArrayList<Class<?>> classes = KlassLoader.loadedClassesCopy(loader);
		for (Class<?> cls : classes) {
			if (cls.getAnnotations().length > 0) {
				if (filter.condition(cls))
					annotated.add(cls);
			}
			KlassWalker.walkAccessibleObjects(cls, (AccessibleObject ao, boolean isStatic, Object obj) -> {
				if (ao.getAnnotations().length > 0) {
					if (filter.condition(ao))
						annotated.add(ao);
				}
				return true;
			});
		}
		return annotated;
	}

	public static ArrayList<AnnotatedElement> scanAnnotatedElements(ClassLoader loader) {
		return scanAnnotatedElements(loader, Filter.RESERVE_ALL);
	}

	/**
	 * 扫描指定ClassLoader的指定注解的元素
	 * 
	 * @param <T>
	 * @param loader        要扫描的ClassLoader
	 * @param annotationCls 注解类
	 * @param filter        过滤条件
	 * @return
	 */
	public static <T extends Annotation> ArrayList<AnnotatedElement> scanAnnotatedElements(ClassLoader loader, Class<T> annotationCls, Filter filter) {
		ArrayList<AnnotatedElement> annotated = new ArrayList<>();
		ArrayList<Class<?>> classes = KlassLoader.loadedClassesCopy(loader);
		for (Class<?> cls : classes) {
			if (cls.isAnnotationPresent(annotationCls)) {
				if (filter.condition(cls))// 不满足条件的AnnotatedElement不放入结果
					annotated.add(cls);
			}
			KlassWalker.walkAccessibleObjects(cls, (AccessibleObject ao, boolean isStatic, Object obj) -> {
				if (ao.isAnnotationPresent(annotationCls)) {
					if (filter.condition(ao))
						annotated.add(ao);
				}
				return true;
			});
		}
		return annotated;
	}

	public static <T extends Annotation> ArrayList<AnnotatedElement> scanAnnotatedElements(ClassLoader loader, Class<T> annotationCls) {
		return scanAnnotatedElements(loader, annotationCls, Filter.RESERVE_ALL);
	}

	public static <T extends Annotation> ArrayList<AnnotatedElement> scanAnnotatedElements(ClassLoader loader, Class<T> annotationCls, Filter.Klass filter) {
		ArrayList<AnnotatedElement> annotated = new ArrayList<>();
		ArrayList<Class<?>> classes = KlassLoader.loadedClassesCopy(loader);
		for (Class<?> cls : classes) {
			// 不满足条件的类直接略过
			if (!filter.condition(cls))
				continue;
			if (cls.isAnnotationPresent(annotationCls))
				annotated.add(cls);
			KlassWalker.walkAccessibleObjects(cls, (AccessibleObject ao, boolean isStatic, Object obj) -> {
				if (ao.isAnnotationPresent(annotationCls))
					annotated.add(ao);
				return true;
			});
		}
		return annotated;
	}

	public static <T extends Annotation> ArrayList<AnnotatedElement> scanAnnotatedElementsFilterClass(ClassLoader loader, Class<T> annotationCls) {
		return scanAnnotatedElements(loader, annotationCls, Filter.Klass.RESERVE_ALL);
	}
}
