package lyra.klass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 遍历类的工具，无视访问修饰符和反射过滤<br>
 * 提供原始Field、Method、Constructor等，对其进行修改会导致反射获取到的所有副本都被修改
 */
public class KlassWalker {
	@FunctionalInterface
	public static interface FieldOperation {
		/**
		 * 遍历每个字段
		 * 
		 * @param f
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 */
		public void operate(Field f, boolean isStatic, Object value);
	}

	/**
	 * 字段如果是静态的，则op()中形参value为字段值；如果是非静态字段则传入null
	 * 
	 * @param cls
	 * @param op
	 */
	public static void walkFields(Class<?> cls, FieldOperation op) {
		Field[] fields = ObjectManipulator.getDeclaredFields(cls);
		for (Field f : fields) {
			boolean isStatic = Modifier.isStatic(f.getModifiers());
			op.operate(f, isStatic, isStatic ? ObjectManipulator.access(cls, f) : null);
		}
	}

	/**
	 * op()中形参value为字段值
	 * 
	 * @param obj
	 * @param op
	 */
	public static void walkFields(Object obj, FieldOperation op) {
		Field[] fields = ObjectManipulator.getDeclaredFields(obj.getClass());
		for (Field f : fields) {
			op.operate(f, Modifier.isStatic(f.getModifiers()), ObjectManipulator.access(obj, f));
		}
	}

	@FunctionalInterface
	public static interface FieldAnnotationOperation<T extends Annotation> {
		/**
		 * 遍历每个具有某注解的字段
		 * 
		 * @param f
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 */
		public void operate(Field f, boolean isStatic, Object value, T annotation);
	}

	/**
	 * 遍历含有某个注解的全部字段
	 * 
	 * @param cls
	 * @param annotation
	 * @param op
	 */
	public static <T extends Annotation> void walkFields(Class<?> cls, Class<T> annotationCls, FieldAnnotationOperation<T> op) {
		walkFields(cls, (Field f, boolean isStatic, Object value) -> {
			T annotation = f.getAnnotation(annotationCls);
			if (annotation != null)
				op.operate(f, isStatic, value, annotation);
		});
	}

	public static <T extends Annotation> void walkFields(Object obj, Class<T> annotationCls, FieldAnnotationOperation<T> op) {
		walkFields(obj, (Field f, boolean isStatic, Object value) -> {
			T annotation = f.getAnnotation(annotationCls);
			if (annotation != null)
				op.operate(f, isStatic, value, annotation);
		});
	}
}
