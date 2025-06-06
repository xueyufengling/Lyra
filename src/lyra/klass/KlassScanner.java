package lyra.klass;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;

public class KlassScanner {
	/**
	 * 扫描所有被注解的元素，包括Class，Field，Method，Constructor等<br>
	 * 
	 * @param loader
	 * @param annotation
	 * @return
	 */
	public static ArrayList<AnnotatedElement> scanAnnotatedElements(ClassLoader loader) {
		ArrayList<AnnotatedElement> annotated = new ArrayList<>();
		ArrayList<Class<?>> classes = KlassLoader.loadedClassesCopy(loader);
		for (Class<?> cls : classes) {
			if (cls.getAnnotations().length > 0)
				annotated.add(cls);
			KlassWalker.walkAccessibleObjects(cls, (AccessibleObject ao, boolean isStatic) -> {
				if (ao.getAnnotations().length > 0)
					annotated.add(ao);
			});
		}
		return annotated;
	}

	/**
	 * 扫描指定注解的元素
	 * 
	 * @param <T>
	 * @param loader
	 * @param annotationCls
	 * @return
	 */
	public static <T extends Annotation> ArrayList<AnnotatedElement> scanAnnotatedElements(ClassLoader loader, Class<T> annotationCls) {
		ArrayList<AnnotatedElement> annotated = new ArrayList<>();
		ArrayList<Class<?>> classes = KlassLoader.loadedClassesCopy(loader);
		for (Class<?> cls : classes) {
			if (cls.getAnnotation(annotationCls) != null)
				annotated.add(cls);
			KlassWalker.walkAccessibleObjects(cls, (AccessibleObject ao, boolean isStatic) -> {
				if (ao.getAnnotation(annotationCls) != null)
					annotated.add(ao);
			});
		}
		return annotated;
	}
}
