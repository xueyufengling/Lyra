package lyra.asm;

import org.objectweb.asm.MethodVisitor;

import lyra.asm.ByteCodeManipulator.MethodInfo;

public interface MethodOperator {
	public void modify(MethodInfo method_info, MethodVisitor method_visitor);
}
