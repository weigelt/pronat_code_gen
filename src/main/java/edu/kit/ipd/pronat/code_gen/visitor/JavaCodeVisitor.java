package edu.kit.ipd.pronat.code_gen.visitor;

import edu.kit.ipd.pronat.postpipelinedatamodel.ast.tree.ASTBlock;
import edu.kit.ipd.pronat.postpipelinedatamodel.ast.tree.ASTParallel;
import edu.kit.ipd.pronat.postpipelinedatamodel.ast.visitor.CStyleVisitor;

/**
 * @author Sebastian Weigelt
 * @author Viktor Kiesel
 */
public class JavaCodeVisitor extends CStyleVisitor {

	@Override
	public String getID() {
		return "JavaCodeVisitor";
	}

	private int threadNumber = 0;

	@Override
	public String toCode(ASTParallel node) {
		int threadIterator = 1;
		String threadJoins = "";
		String code = indent() + "//PARALLEL:\n";
		for (ASTBlock block : node.getSections()) {
			code += indent() + "//SECTION " + threadIterator++ + ":\n";
			code += indent() + "Thread t" + threadNumber + "= new Thread() { ";
			code += "public void run() { \n";
			indentUp();
			code += block.visit(this);
			indentDown();
			code += indent() + "}};\n";
			code += indent() + "t" + threadNumber + ".start(); \n";
			threadJoins += indent() + "t" + threadNumber++ + ".join();\n";
		}
		code += indent() + "//END PARALLEL\n";
		code += threadJoins;

		return code;
	}

}
