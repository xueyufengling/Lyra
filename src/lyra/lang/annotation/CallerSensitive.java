package lyra.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.VarHandle;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import lyra.klass.special.MirrorAnnotation;
import lyra.lang.Reflection;
import lyra.lang.base.HandleBase;

/**
 * 运行时的该注解将转为jdk.internal.reflect.CallerSensitive。<br>
 * 检测逻辑具体实现在jdk.internal.reflect.Reflection.isCallerSensitive(Method m)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface CallerSensitive {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static class CallerSensitiveMirrorImpl implements MirrorAnnotation<CallerSensitive, CallerSensitiveMirrorImpl, Annotation> {
		/**
		 * 未缓存CallerSensitive标志位，将调用jdk.internal.reflect.Reflection.isCallerSensitive(Method m)获取标志位
		 */
		static final byte not_initialized = 0;

		/**
		 * 已初始化标志位，该方法是CallerSensitive的
		 */
		static final byte initialized_cs = 1;

		/**
		 * 已初始化标志位，该方法不是CallerSensitive的
		 */
		static final byte initialized_not_cs = -1;

		/**
		 * 标志位缓存状态<br>
		 * 该字段被标记为{@code @Stable}，其行为是改变一次值后和static final等价，可能会被优化内联，修改该值可能无效。<br>
		 * 这将导致修改无法变更方法的callerSensitive状态，即该方法没有被JVM视为CallerSensitive。
		 */
		private static final VarHandle Method_callerSensitive;

		static final Class CallerSensitiveClass = Reflection.forName("jdk.internal.reflect.CallerSensitive");

		/**
		 * CallerSensitiveInstance的实际类型为class com.sun.proxy.jdk.proxy1.$Proxy51
		 */
		static final Annotation CallerSensitiveInstance;

		static final CallerSensitiveMirrorImpl mirrorInstance;

		static {
			Method_callerSensitive = HandleBase.internalFindVarHandle(Method.class, "callerSensitive", byte.class);
			Method m = Reflection.getDeclaredMethod(Class.class, "forName", String.class);
			CallerSensitiveInstance = m.getAnnotation(CallerSensitiveClass);
			mirrorInstance = new CallerSensitiveMirrorImpl();
		}

		/**
		 * 擦除缓存标志位
		 * 
		 * @param m
		 */
		private final void eraseCallerSensitiveFlagCache(Method m) {
			Method_callerSensitive.set(m, not_initialized);
		}

		@Override
		public void operate(AnnotatedElement ae) {
			eraseCallerSensitiveFlagCache((Method) ae);
		}

		@Override
		public Annotation destAnnotationInstance() {
			return CallerSensitiveInstance;
		}

		@Override
		public Class<CallerSensitive> mirrorAnnotationClass() {
			return CallerSensitive.class;
		}
	}
}
