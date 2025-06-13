package lyra.cxx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * C++对象内存布局计算
 */
public class cxx_type {
	/**
	 * 所有定义的类型
	 */
	private static final HashMap<String, cxx_type> definedTypes = new HashMap<>();

	@Override
	public cxx_type clone() {
		try {
			return (cxx_type) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		return (obj instanceof cxx_type type) && this.name.equals(type.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name) ^ Objects.hashCode(size) ^ Objects.hashCode(base_types) ^ Objects.hashCode(fields);
	}

	@Override
	public String toString() {
		return name;
	}

	private String name;
	/**
	 * 类型的长度，不含padding
	 */
	private long size;

	/**
	 * 父类
	 */
	private cxx_type[] base_types;

	/**
	 * 所有直接或间接基类的字段全部展开存放的数组
	 */
	private ArrayList<cxx_field> base_fields;

	/**
	 * 子类，即本类的对象顶，是本类第一个字段的偏移量
	 */
	private long derived_top;

	/**
	 * 计算基类字段偏移
	 * 
	 * @param arr            字段数组
	 * @param current_offset 计算开始前的偏移量
	 * @return 计算完成后的末尾偏移量
	 */
	private long resolve_base_fields(ArrayList<cxx_field> arr, long current_offset) {
		for (int i = 0; i < base_types.length; ++i)// 基类字段放在最前方
			current_offset = base_types[i].resolve_base_fields(arr, current_offset);
		for (int i = 0; i < fields.size(); ++i) {
			cxx_field current_field = fields.get(i).clone();
			current_field.offset = current_offset;
			current_offset += current_field.type().size();
			arr.add(current_field);
		}
		return current_offset;
	}

	/**
	 * 本身的字段
	 */
	private ArrayList<cxx_field> fields;

	/**
	 * 更新size的标记
	 */
	boolean dirty_flag;

	/**
	 * 定义一个类型，禁止递归继承，且基类必须已经解析完成，即基类的size和字段不再变化。
	 * 
	 * @param type
	 * @param base_types
	 * @return
	 */
	public static final cxx_type define(String type, cxx_type... base_types) {
		return definedTypes.computeIfAbsent(type, (String t) -> new cxx_type(type, base_types));
	}

	/**
	 * 仅用于定义基本类型
	 * 
	 * @param name
	 * @param size
	 */
	private cxx_type(String name, long size) {
		this.name = name;
		this.size = size;
		this.dirty_flag = false;
	}

	private cxx_type(String name, cxx_type... base_types) {
		this.name = name;
		this.size = 0;
		this.fields = new ArrayList<>();
		this.base_types = base_types;
		this.base_fields = new ArrayList<>();
		this.derived_top = resolve_base_fields(base_fields, 0);
		this.dirty_flag = true;
	}

	/**
	 * 定义一个原生类型的长度，原生类型没有字段
	 * 
	 * @param type
	 * @return
	 */
	static final cxx_type define_primitive(String type, long size) {
		cxx_type new_type = new cxx_type(type, size);
		definedTypes.put(type, new_type);
		return new_type;
	}

	/**
	 * 是否是基本类型
	 * 
	 * @return
	 */
	public final boolean is_primitive() {
		return fields == null;
	}

	/**
	 * 添加字段，禁止添加类本身。
	 * 
	 * @param field
	 * @return
	 */
	public cxx_field decl_field(cxx_field field) {
		if (fields == null || field.type().equals(this))// 禁止给原生类型添加字段，或添加类本身作为字段
			throw new RuntimeException("Cannot append field \"" + field + "\" to " + this + ". append fields to primitive types or append self as a field are not allowed.");
		fields.add(field);
		field.decl_type = this;
		this.dirty_flag = true;// 标记size更新
		return field;
	}

	public cxx_field decl_field(String name, cxx_type type) {
		return decl_field(cxx_field.define(name, type));
	}

	/**
	 * 更新字段偏移量和本类型的大小
	 * 
	 * @return
	 */
	private long resolve_offset_and_size() {
		if (base_types.length == 0 && fields.isEmpty())
			size = 1;
		else {
			long current_offset = derived_top;
			for (int idx = 0; idx < fields.size(); ++idx) {
				cxx_field f = fields.get(idx);
				f.offset = current_offset;
				current_offset += f.type().size();
			}
			size = current_offset;
		}
		return size;
	}

	/**
	 * 获取该类型的大小
	 * 
	 * @return
	 */
	public long size() {
		if (fields == null || !dirty_flag) {
			return size;
		} else {
			dirty_flag = false;
			return resolve_offset_and_size();
		}
	}

	/**
	 * 获取类型名称
	 * 
	 * @return
	 */
	public String name() {
		return name;
	}

	/**
	 * 获取本类声明的指定索引字段
	 * 
	 * @param idx
	 * @return
	 */
	public cxx_field field_at(int idx) {
		return fields == null ? null : fields.get(idx);
	}

	/**
	 * 获取本类声明的指定名称的字段偏移量
	 * 
	 * @param field_name
	 * @return
	 */
	public int field_index(String field_name) {
		if (fields != null)
			for (int idx = 0; idx < fields.size(); ++idx)
				if (fields.get(idx).name().equals(field_name))
					return idx;
		return -1;
	}

	/**
	 * 获取本类声明的指定名称的字段
	 * 
	 * @param field_name
	 * @return
	 */
	public cxx_field field(String field_name) {
		int idx = field_index(field_name);
		if (idx == -1)
			return null;
		return fields.get(idx);
	}
}
