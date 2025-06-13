package lyra.internal.oops;

import lyra.cxx.cxx_field;
import lyra.cxx.cxx_stdtypes;
import lyra.cxx.cxx_type;

public class _ConstMethod {

	private static final cxx_type ConstMethod = cxx_type.define("ConstMethod", _Metadata.Metadata);

	public static final cxx_field _constMethod = ConstMethod.decl_field("_constMethod", cxx_stdtypes.pointer);
	public static final cxx_field _method_data = ConstMethod.decl_field("_method_data", cxx_stdtypes.pointer);
	public static final cxx_field _method_counters = ConstMethod.decl_field("_method_counters", cxx_stdtypes.pointer);
	public static final cxx_field _adapter = ConstMethod.decl_field("_adapter", cxx_stdtypes.pointer);
	public static final cxx_field _vtable_index = ConstMethod.decl_field("_vtable_index", cxx_stdtypes._int);
}
