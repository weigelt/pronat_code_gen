package edu.kit.ipd.parse.code_gen.visitor;

import edu.kit.ipd.parse.luna.data.ast.tree.ASTBlock;
import edu.kit.ipd.parse.luna.data.ast.tree.ASTParallel;
import edu.kit.ipd.parse.luna.data.ast.visitor.CStyleVisitor;

/**
 * @author Sebastian Weigelt
 * @author Viktor Kiesel
 */
public class CCodeVisitor extends CStyleVisitor {

	@Override
	public String getID() {
		return "CCodeVisitor";
	}

	@Override
	public String toCode(ASTParallel node) {
		String code = indent() + "#pragma omp parallel sections \n";
		code += indent() + "{\n";
		indentUp();
		for (ASTBlock block : node.getSections()) {
			code += indent() + "#pragma omp section \n";
			code += indent() + "{\n";
			indentUp();
			code += block.visit(this);
			indentDown();
			code += indent() + "}\n";
		}
		indentDown();
		code += indent() + "}";

		return code;
	}

}
