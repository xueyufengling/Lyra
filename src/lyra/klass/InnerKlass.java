package lyra.klass;

import java.lang.reflect.Field;

import lyra.lang.Reflection;
import lyra.object.ObjectManipulator;

public abstract class InnerKlass {
	/**
	 * 获取指定类型的外部类引用
	 * 
	 * @param <T>
	 * @param target 要获取的外部类类型
	 * @param obj    要获取外部类引用的对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getEnclosingClassInstance(Class<T> target, Object obj) {
		if (target == null || obj == null) {
			return null;
		}
		Class<?> cls = obj.getClass();
		Class<?> outerCls = cls.getEnclosingClass();
		if (outerCls == null) {
			return null;// obj没有外部类
		}
		T outerObj = null;
		Field[] fields = Reflection.getDeclaredFields(cls);
		for (Field field : fields) {
			if (field.getType() == outerCls && field.getName().startsWith("this$")) {// 找到上一层外部类的实例引用字段
				outerObj = (T) ObjectManipulator.access(obj, field);
				if (outerCls == target)
					return outerObj;
				else
					return getEnclosingClassInstance(target, outerObj);
			}
		}
		return null;
	}

	/**
	 * 获取当前对象上一层的外部类引用
	 * 
	 * @param obj
	 * @return
	 */
	public static Object getEnclosingClassInstance(Object obj) {
		return obj == null ? null : getEnclosingClassInstance(obj.getClass().getEnclosingClass(), obj);
	}
}
