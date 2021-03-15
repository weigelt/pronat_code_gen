package edu.kit.ipd.pronat.code_gen.visitor;

import edu.kit.ipd.pronat.postpipelinedatamodel.ast.tree.*;
import edu.kit.ipd.pronat.postpipelinedatamodel.ast.visitor.AbstractVisitor;
import edu.kit.ipd.pronat.postpipelinedatamodel.code.Method;
import edu.kit.ipd.pronat.postpipelinedatamodel.code.Parameter;

/**
 * @author Sebastian Weigelt
 * @author Viktor Kiesel
 */
public class PythonCodeVisitor extends AbstractVisitor {

	@Override
	public String getID() {
		return "PythonCodeVisitor";
	}

	@Override
	public String toNewMethod(ASTRoot astRoot, Method method) {

		indentUp();
		String code = indent() + generateHead(method) + "\n";
		indentUp();
		code += toCode((ASTBlock) astRoot) + "\n";
		indentDown();
		indentDown();
		return code;

	}

	@Override
	public String toCode(ASTBranch astCondition) {
		String s = "";
		s += indent() + "if (";
		for (ASTNode c : astCondition.getConditions().getExpressions()) {
			s += c.visit(this) + " && ";
		}
		if (!astCondition.getConditions().getExpressions().isEmpty()) {
			s = s.substring(0, s.length() - 4);
		}
		s += "):\n";
		indentUp();
		s += toCode(astCondition.getThenBlock());
		indentDown();
		s += indent() + "else :\n";
		indentUp();
		s += toCode(astCondition.getElseBlock());
		indentDown();
		return s;
	}

	@Override
	public String toCode(ASTMethodCall astMethod) {
		String s = "";
		if (astMethod.getComment() != null) {
			s += indent() + "# " + astMethod.getComment() + "\n";
		}
		Method method = astMethod.getMethod();
		s += indent() + method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			s += para.getName() + ", ";
		}
		if (!method.getParameters().isEmpty()) {
			s = s.substring(0, s.length() - 2);
		}
		s += ")";
		//		if (!method.getReturnType().equals("void"))
		//			s += " : " + method.getReturnType();
		return s;
	}

	@Override
	public String toCode(ASTConditionCall astMethod) {
		String s = "";
		//		if (astMethod.getComment() != null)
		//			s += indent() + "# " + astMethod.getComment() + "\n";
		Method method = astMethod.getMethod();
		s += method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			s += para.getName() + ", ";
		}
		if (!method.getParameters().isEmpty()) {
			s = s.substring(0, s.length() - 2);
		}
		s += ")";
		//		if (!method.getReturnType().equals("void"))
		//			s += " : " + method.getReturnType();
		return s;
	}

	@Override
	public String toCode(ASTFor node) {
		String code = "";
		code += indent() + "for " + node.getVariableName() + " in range(" + node.getIterations() + "):\n";
		indentUp();
		code += node.getBlock().visit(this);
		indentDown();
		return code;
	}

	@Override
	public String toCode(ASTDeclaration node) {
		String s = node.getType() + " " + node.getName() + "=" + node.getParameters();
		return s;
	}

	@Override
	public String toCode(ASTWhile node) {
		String s = "";

		// simulate do while by runing it once
		if (node.isDo_while()) {
			s += node.getBlock().visit(this) + "";
		}

		s += indent() + "while(";
		for (ASTNode c : node.getConditions().getExpressions()) {
			if (node.isNegated()) {
				s += "!";
			}
			s += c.visit(this) + " && ";
		}
		if (!node.getConditions().getExpressions().isEmpty()) {
			s = s.substring(0, s.length() - 4);
		}
		s += "): \n";
		indentUp();
		s += node.getBlock().visit(this) + "";
		indentDown();
		return s;
	}

	@Override
	public String toCode(ASTText astText) {
		String s = "";
		if (astText.isComment()) {
			s += indent() + "#";
		}
		s += astText.getText();
		return s;
	}

	@Override
	public String toCode(ASTComperator node) {
		String s = node.getLeft() + " ";
		s += toComparatorSign(node.getSign());
		s += " " + node.getRight();
		return s;
	}

	@Override
	public String toCode(ASTParallel node) {
		int threadIterator = 1;
		String s = "#PARALLEL:\n";
		for (ASTBlock block : node.getSections()) {
			//			s += "//||" + block.visit(this).trim() + "|\n";
			s += indent() + "#SECTION " + threadIterator++ + ":\n";
			s += indent() + block.visit(this) + "\n";
		}
		s += "#END PARALLEL (No implicit join)\n";

		return s;
	}

	private String generateHead(Method method) {
		String s = indent() + method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			s += para.getName() + ", ";
		}
		if (!method.getParameters().isEmpty()) {
			s = s.substring(0, s.length() - 2);
		}
		s += ");";
		return "def " + s + ":\n";
	}

	public String toComment(String comment) {
		return "#" + comment;
	}

}
