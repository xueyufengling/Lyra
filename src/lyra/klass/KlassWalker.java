package lyra.klass;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import lyra.lang.Reflection;
import lyra.object.ObjectManipulator;

/**
 * 遍历类的工具，无视访问修饰符和反射过滤<br>
 * 提供原始Field、Method、Constructor等，对其进行修改会导致反射获取到的所有副本都被修改
 */
public class KlassWalker {
	/**
	 * 字段操作不可限定泛型，这是因为欲访问的字段实际类型不一定是期望的类型，此时如果强制使用泛型限定，则会在强制转换时报错。
	 */
	@FunctionalInterface
	public static interface FieldOperation {
		/**
		 * 遍历每个字段，处理的是root原对象，即反射缓存的对象。
		 * 
		 * @param f
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 * @return 是否继续迭代，返回true代表继续迭代，false则终止迭代
		 */
		public boolean operate(Field f, boolean isStatic, Object value);
	}

	@FunctionalInterface
	public static interface SimpleFieldOperation {
		/**
		 * 遍历每个字段，处理的是root原对象，即反射缓存的对象。
		 * 
		 * @param f
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 */
		public boolean operate(String field_name, Class<?> field_type, boolean isStatic, Object value);
	}

	/**
	 * 字段如果是静态的，则op()中形参value为字段值；如果是非静态字段则传入null
	 * 
	 * @param cls
	 * @param op
	 */
	public static void walkFields(Class<?> cls, FieldOperation op) {
		Field[] fields = Reflection.getDeclaredFields(cls);
		for (Field f : fields) {
			boolean isStatic = Modifier.isStatic(f.getModifiers());
			if (!op.operate(f, isStatic, isStatic ? ObjectManipulator.access(cls, f) : null))
				return;
		}
	}

	public static void walkFields(Class<?> cls, SimpleFieldOperation op) {
		walkFields(cls, (Field f, boolean isStatic, Object value) -> {
			return op.operate(f.getName(), f.getType(), isStatic, value);
		});
	}

	/**
	 * op()中形参value为字段值
	 * 
	 * @param obj
	 * @param op
	 */
	public static void walkFields(Object obj, FieldOperation op) {
		Field[] fields = Reflection.getDeclaredFields(obj.getClass());
		for (Field f : fields) {
			if (!op.operate(f, Modifier.isStatic(f.getModifiers()), ObjectManipulator.access(obj, f)))
				return;
		}
	}

	public static void walkFields(Object obj, SimpleFieldOperation op) {
		walkFields(obj, (Field f, boolean isStatic, Object value) -> {
			return op.operate(f.getName(), f.getType(), isStatic, value);
		});
	}

	@FunctionalInterface
	public static interface AnnotatedFieldOperation<T extends Annotation> {
		/**
		 * 遍历每个具有某注解的字段
		 * 
		 * @param f
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 */
		public boolean operate(Field f, boolean isStatic, Object value, T annotation);
	}

	@FunctionalInterface
	public static interface SimpleAnnotatedFieldOperation<T extends Annotation> {
		/**
		 * 遍历每个具有某注解的字段
		 * 
		 * @param f
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 */
		public boolean operate(String field_name, Class<?> field_type, boolean isStatic, Object value, T annotation);
	}

	/**
	 * 遍历含有某个注解的全部字段
	 * 
	 * @param cls
	 * @param annotation
	 * @param op
	 */
	public static <T extends Annotation> void walkAnnotatedFields(Class<?> cls, Class<T> annotationCls, AnnotatedFieldOperation<T> op) {
		walkFields(cls, (Field f, boolean isStatic, Object value) -> {
			T annotation = f.getAnnotation(annotationCls);
			if (annotation != null)
				return op.operate(f, isStatic, value, annotation);
			return true;
		});
	}

	public static <T extends Annotation> void walkAnnotatedFields(Class<?> cls, Class<T> annotationCls, SimpleAnnotatedFieldOperation<T> op) {
		walkAnnotatedFields(cls, annotationCls, (Field f, boolean isStatic, Object value, T annotation) -> {
			return op.operate(f.getName(), f.getType(), isStatic, value, annotation);
		});
	}

	public static <T extends Annotation> void walkAnnotatedFields(Object obj, Class<T> annotationCls, AnnotatedFieldOperation<T> op) {
		walkFields(obj, (Field f, boolean isStatic, Object value) -> {
			T annotation = f.getAnnotation(annotationCls);
			if (annotation != null)
				return op.operate(f, isStatic, value, annotation);
			return true;
		});
	}

	public static <T extends Annotation> void walkAnnotatedFields(Object obj, Class<T> annotationCls, SimpleAnnotatedFieldOperation<T> op) {
		walkAnnotatedFields(obj, annotationCls, (Field f, boolean isStatic, Object value, T annotation) -> {
			return op.operate(f.getName(), f.getType(), isStatic, value, annotation);
		});
	}

	@FunctionalInterface
	public static interface TypeFieldOperation<T> {
		/**
		 * 遍历每个具有某注解的字段
		 * 
		 * @param f
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 */
		public boolean operate(Field f, boolean isStatic, T value);
	}

	/**
	 * 遍历指定类的目标类型或其子类的字段
	 * 
	 * @param <T>
	 * @param cls
	 * @param targetType
	 * @param op
	 */
	@SuppressWarnings("unchecked")
	public static <T> void walkTypeFields(Class<?> cls, Class<T> targetType, TypeFieldOperation<T> op) {
		walkFields(cls, (Field f, boolean isStatic, Object value) -> {
			if (Reflection.is(f, targetType))
				return op.operate(f, isStatic, (T) value);
			return true;
		});
	}

	/**
	 * 遍历指定对象的目标类型或其子类的字段
	 * 
	 * @param <T>
	 * @param obj
	 * @param targetType
	 * @param op
	 */
	@SuppressWarnings("unchecked")
	public static <T> void walkTypeFields(Object obj, Class<T> targetType, TypeFieldOperation<T> op) {
		walkFields(obj, (Field f, boolean isStatic, Object value) -> {
			if (Reflection.is(f, targetType))
				return op.operate(f, isStatic, (T) value);
			return true;
		});
	}

	@FunctionalInterface
	public static interface MethodOperation<M> {
		/**
		 * 遍历每个方法，处理的是root原对象，即反射缓存的对象。
		 * 
		 * @param m
		 * @param isStatic 目标字段是否是静态的
		 * @param value    方法所属对象实例，静态方法则为null
		 */
		public boolean operate(Method m, boolean isStatic, M obj);
	}

	@FunctionalInterface
	public static interface AnnotatedMethodOperation<M, T extends Annotation> {
		/**
		 * 遍历每个具有某注解的方法
		 * 
		 * @param m
		 * @param isStatic 目标字段是否是静态的
		 * @param value    方法所属对象实例，静态方法则为null
		 */
		public boolean operate(Method m, boolean isStatic, M obj, T annotation);
	}

	public static <M> void walkMethods(Class<M> cls, MethodOperation<M> op) {
		Method[] methods = Reflection.getDeclaredMethods(cls);
		for (Method m : methods) {
			if (!op.operate(m, Modifier.isStatic(m.getModifiers()), null))
				return;
		}
	}

	public static <M> void walkMethods(M obj, MethodOperation<M> op) {
		Method[] methods = Reflection.getDeclaredMethods(obj.getClass());
		for (Method m : methods) {
			if (!op.operate(m, Modifier.isStatic(m.getModifiers()), obj))
				return;
		}
	}

	public static <M, T extends Annotation> void walkAnnotatedMethods(Class<M> cls, Class<T> annotationCls, AnnotatedMethodOperation<M, T> op) {
		walkMethods(cls, (Method m, boolean isStatic, M obj) -> {
			T annotation = m.getAnnotation(annotationCls);
			if (annotation != null)
				return op.operate(m, isStatic, obj, annotation);
			return true;
		});
	}

	public static <M, T extends Annotation> void walkAnnotatedMethods(M o, Class<T> annotationCls, AnnotatedMethodOperation<M, T> op) {
		walkMethods(o, (Method m, boolean isStatic, M obj) -> {
			T annotation = m.getAnnotation(annotationCls);
			if (annotation != null)
				return op.operate(m, isStatic, obj, annotation);
			return true;
		});
	}

	@FunctionalInterface
	public static interface ConstructorOperation<C> {
		/**
		 * 遍历每个构造函数，处理的是root原对象，即反射缓存的对象。
		 * 
		 * @param c
		 */
		public boolean operate(Constructor<C> c);
	}

	@FunctionalInterface
	public static interface AnnotatedConstructorOperation<C, T extends Annotation> {
		/**
		 * 遍历每个具有某注解的构造函数
		 * 
		 * @param c
		 * @param annotation
		 */
		public boolean operate(Constructor<C> c, T annotation);
	}

	public static <C> void walkConstructors(Class<C> cls, ConstructorOperation<C> op) {
		Constructor<C>[] constructors = Reflection.getDeclaredConstructors(cls);
		for (Constructor<C> c : constructors) {
			if (!op.operate(c))
				return;
		}
	}

	public static <C, T extends Annotation> void walkAnnotatedConstructors(Class<C> cls, Class<T> annotationCls, AnnotatedConstructorOperation<C, T> op) {
		walkConstructors(cls, (Constructor<C> c) -> {
			T annotation = c.getAnnotation(annotationCls);
			if (annotation != null)
				return op.operate(c, annotation);
			return true;
		});
	}

	@FunctionalInterface
	public static interface ExecutableOperation {
		/**
		 * 遍历每个方法或构造函数，处理的是root原对象，即反射缓存的对象。
		 * 
		 * @param e
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 */
		public boolean operate(Executable e, boolean isStatic);
	}

	public static void walkExecutables(Class<?> cls, ExecutableOperation op) {
		Method[] methods = Reflection.getDeclaredMethods(cls);
		Constructor<?>[] constructors = Reflection.getDeclaredConstructors(cls);
		for (Constructor<?> c : constructors) {
			if (!op.operate(c, Modifier.isStatic(c.getModifiers())))
				return;
		}
		for (Method m : methods) {
			if (!op.operate(m, Modifier.isStatic(m.getModifiers())))
				return;
		}
	}

	@FunctionalInterface
	public static interface AccessibleObjectOperation {
		/**
		 * 遍历每个字段，处理的是root原对象，即反射缓存的对象。
		 * 
		 * @param ao
		 * @param isStatic 目标字段是否是静态的
		 * @param value    字段值，无效则为null
		 */
		public boolean operate(AccessibleObject ao, boolean isStatic);
	}

	public static void walkAccessibleObjects(Class<?> cls, AccessibleObjectOperation op) {
		Field[] fields = Reflection.getDeclaredFields(cls);
		Method[] methods = Reflection.getDeclaredMethods(cls);
		Constructor<?>[] constructors = Reflection.getDeclaredConstructors(cls);
		for (Field f : fields) {
			if (!op.operate(f, Modifier.isStatic(f.getModifiers())))
				return;
		}
		for (Constructor<?> c : constructors) {
			if (!op.operate(c, Modifier.isStatic(c.getModifiers())))
				return;
		}
		for (Method m : methods) {
			if (!op.operate(m, Modifier.isStatic(m.getModifiers())))
				return;
		}
	}

}
