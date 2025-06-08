package lyra.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.Map;

import lyra.klass.special.Mirror;
import lyra.klass.special.MirrorAnnotation;
import lyra.lang.Handles;
import lyra.lang.Reflection;
import lyra.object.ObjectManipulator;

public class Annotations {
	private static VarHandle AnnotationData_annotations;
	private static VarHandle AnnotationData_declaredAnnotations;
	private static MethodHandle Class_annotationData;
	private static MethodHandle Field_declaredAnnotations;
	private static MethodHandle Executable_declaredAnnotations;

	static {
		Class<?> AnnotationDataClass = Reflection.forName("java.lang.Class$AnnotationData");
		Class_annotationData = Handles.findSpecialMethodHandle(Class.class, Class.class, "annotationData", AnnotationDataClass);
		AnnotationData_annotations = Handles.findVarHandle(AnnotationDataClass, "annotations", Map.class);
		AnnotationData_declaredAnnotations = Handles.findVarHandle(AnnotationDataClass, "declaredAnnotations", Map.class);
		Field_declaredAnnotations = Handles.findSpecialMethodHandle(Field.class, "declaredAnnotations", Map.class);
		Executable_declaredAnnotations = Handles.findSpecialMethodHandle(Executable.class, Executable.class, "declaredAnnotations", Map.class);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final void enableMirrorAnnotations(String staticMirrorInstanceField, Class<? extends MirrorAnnotation>... mirrorAnnotationImplClasses) {
		for (Class<?> cls : mirrorAnnotationImplClasses) {
			Reflection.forName(cls.getName(), true);
			MirrorAnnotation mirrorInstance = (MirrorAnnotation) ObjectManipulator.access(cls, staticMirrorInstanceField);
			mirrorInstance.castAllAnnotations();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final void enableMirrorAnnotations(Class<? extends MirrorAnnotation>... mirrorAnnotationImplClasses) {
		enableMirrorAnnotations(MirrorAnnotation.STATIC_MIRROR_INSTANCE_FIELD, mirrorAnnotationImplClasses);
	}

	@SuppressWarnings("unchecked")
	public static void enableIntrinsicAnnotations() {
		enableMirrorAnnotations(CallerSensitive.CallerSensitiveMirrorImpl.class);
	}

	/**
	 * 获取类缓存的注解数据，Class.getAnnotation()获取的注解都是此处缓存的注解数据
	 * 
	 * @param cls
	 * @return
	 */
	public static Map<Class<? extends Annotation>, Annotation> cachedAnnotations(Class<?> cls) {
		return (Map<Class<? extends Annotation>, Annotation>) AnnotationData_annotations.get(cls);
	}

	/**
	 * 获取声明注解Map，对于Class而言，使用的是缓存注解而非该声明注解。
	 * 
	 * @param e
	 * @return
	 */
	public static Map<Class<? extends Annotation>, Annotation> declaredAnnotations(AnnotatedElement ae) {
		try {
			if (ae instanceof Class cls)
				return (Map<Class<? extends Annotation>, Annotation>) AnnotationData_declaredAnnotations.get(Class_annotationData.invokeExact(cls));
			else if (ae instanceof Field f)
				return (Map<Class<? extends Annotation>, Annotation>) Field_declaredAnnotations.invokeExact(f);
			else if (ae instanceof Executable e)
				return (Map<Class<? extends Annotation>, Annotation>) Executable_declaredAnnotations.invokeExact(e);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 运行时实际被使用的注解数据
	 * 
	 * @param ae
	 * @return
	 */
	public static Map<Class<? extends Annotation>, Annotation> actualUsedAnnotations(AnnotatedElement ae) {
		if (ae instanceof Class cls)
			return cachedAnnotations(cls);
		else
			return declaredAnnotations(ae);
	}

	/**
	 * 获取被注解的元素所在的类
	 * 
	 * @param ae
	 * @return
	 */
	public static Class<?> getDeclaringClass(AnnotatedElement ae) {
		if (ae instanceof Class cls)
			return cls;
		else if (ae instanceof Field f)
			return f.getDeclaringClass();
		else if (ae instanceof Executable e)
			return e.getDeclaringClass();
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void castAnnotation(AnnotatedElement ae, Class<? extends Annotation> targetAnnotationCls, Class<?> destAnnotationCls, Annotation destAnnotation) {
		// 判断一个AnnotatedElement是否有某个注解，实际是判断缓存的注解map是否存在指定注解Class<?>的key
		Map<Class<? extends Annotation>, Annotation> annoMap = Annotations.actualUsedAnnotations(ae);
		Annotation mirror_annotation = annoMap.remove(targetAnnotationCls);// 移除镜像注解的key
		Mirror.cast(mirror_annotation, destAnnotation);
		annoMap.put((Class<? extends Annotation>) destAnnotationCls, mirror_annotation);// 填入目标注解
	}

	public static void castAnnotation(AnnotatedElement ae, Class<? extends Annotation> targetAnnotationCls, Annotation destAnnotation) {
		castAnnotation(ae, targetAnnotationCls, destAnnotation.annotationType(), destAnnotation);
	}

	/**
	 * 如果目标注解的类型和实际获取的对象annotationType()类型不一致，那么需要手动传入目标注解类型。<br>
	 * 
	 * @param ae
	 * @param targetAnnotationCls
	 * @param destAnnotationCls
	 * @param destAnnotation
	 */
	@SuppressWarnings("unchecked")
	public static void replaceAnnotation(AnnotatedElement ae, Class<? extends Annotation> targetAnnotationCls, Class<?> destAnnotationCls, Annotation destAnnotation) {
		// 判断一个AnnotatedElement是否有某个注解，实际是判断缓存的注解map是否存在指定注解Class<?>的key
		Map<Class<? extends Annotation>, Annotation> annoMap = Annotations.actualUsedAnnotations(ae);
		annoMap.remove(targetAnnotationCls);// 移除镜像注解的key
		annoMap.put((Class<? extends Annotation>) destAnnotationCls, destAnnotation);// 填入目标注解
	}

	public static void replaceAnnotation(AnnotatedElement ae, Class<? extends Annotation> targetAnnotationCls, Annotation destAnnotation) {
		replaceAnnotation(ae, targetAnnotationCls, destAnnotation.annotationType(), destAnnotation);
	}

}
