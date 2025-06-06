package lyra.klass.special;

/**
 * 在父类中使用子类Class<?>对象
 * 
 * @param <Derived>
 */
public interface CRTP<Derived> {
	public abstract Class<Derived> getDerivedClass();
}
