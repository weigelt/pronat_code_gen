package edu.kit.ipd.pronat.code_gen.visitor;

import edu.kit.ipd.pronat.postpipelinedatamodel.ast.tree.*;
import edu.kit.ipd.pronat.postpipelinedatamodel.ast.visitor.IVisitor;
import edu.kit.ipd.pronat.postpipelinedatamodel.code.Method;
import edu.kit.ipd.pronat.postpipelinedatamodel.code.Parameter;

/**
 * @author Sebastian Weigelt
 * @author Viktor Kiesel
 */
public class PseudoCodeVisitor implements IVisitor {

	@Override
	public String getID() {
		return "PseudoCodeVisitor";
	}

	@Override
	public String toNewMethod(ASTRoot astRoot, Method method) {
		return generateHead(method) + ":\n" + astRoot.visit(this) + "\n";
	}

	@Override
	public String toCode(ASTRoot astRoot) {

		return toCode((ASTBlock) astRoot);
	}

	@Override
	public String toCode(ASTBlock node) {
		String s = "";

		for (ASTNode expression : node.getExpressions()) {
			s += expression.visit(this) + "\n";
		}
		return s;
	}

	@Override
	public String toCode(ASTBranch astCondition) {
		String s = "";
		s += "if (";
		for (ASTNode c : astCondition.getConditions().getExpressions()) {
			s += c.visit(this) + " & ";
		}
		if (!astCondition.getConditions().getExpressions().isEmpty()) {
			s = s.substring(0, s.length() - 3);
		}
		s += ")\n";
		s += "then {\n" + toCode(astCondition.getThenBlock()) + "}";
		s += " else {" + toCode(astCondition.getElseBlock()) + "}";
		return s;
	}

	@Override
	public String toCode(ASTExpression node) {
		if (node == null) {
			return "Error: Node is null";
		}
		return node.getChild().visit(this);
	}

	@Override
	public String toCode(ASTMethodCall astMethod) {
		String s = "";
		if (astMethod.getComment() != null) {
			s += toComment(astMethod.getComment()) + "\n";
		}
		Method method = astMethod.getMethod();
		s += ">:" + method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			s += para.getName() + ", ";
		}
		if (!method.getParameters().isEmpty()) {
			s = s.substring(0, s.length() - 2);
		}
		s += ")";
		if (!method.getReturnType().equals("void")) {
			s += " : " + method.getReturnType();
		}
		return s;
	}

	@Override
	public String toCode(ASTConditionCall astMethod) {
		String s = "";
		Method method = astMethod.getMethod();
		s += ":" + method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			s += para.getName() + ", ";
		}
		s = s.substring(0, s.length() - 2) + ")";
		if (!method.getReturnType().equals("void")) {
			s += " : " + method.getReturnType();
		}
		return s;
	}

	@Override
	public String toCode(ASTFor node) {
		String s = "";
		s += "for(" + node.getExpression().visit(this) + ";" + node.getCondition().visit(this) + ";" + node.getCounter().visit(this)
				+ ") {\n";
		s += node.getBlock().visit(this) + "}";
		return s;
	}

	@Override
	public String toCode(ASTDeclaration node) {
		String s = node.getType() + " " + node.getName() + "=" + node.getParameters();

		return s;
	}

	@Override
	public String toCode(ASTWhile node) {
		String s = "";
		if (node.isDo_while()) {
			s += "do_";
		}
		s += "while(";
		for (ASTNode c : node.getConditions().getExpressions()) {
			s += c.visit(this) + " & ";
		}
		if (node.getConditions().getExpressions().isEmpty()) {
			s = s.substring(0, s.length() - 3);
		}
		s += ") {\n";
		s += node.getBlock().visit(this) + "}";
		return s;
	}

	@Override
	public String toCode(ASTText astText) {
		return ">:" + astText.getText();
	}

	@Override
	public String toCode(ASTComperator node) {
		String s = node.getLeft() + " ";
		s += node.getSign().name();
		s += " " + node.getRight();
		return s;
	}

	@Override
	public String toCode(ASTParallel node) {
		String s = "PARALLEL:\n";
		for (ASTBlock block : node.getSections()) {
			s += "||" + block.visit(this).trim() + "|\n";
		}
		return s.trim();
	}

	public String toComment(String comment) {
		return "##" + comment + "#";
	}

	private String generateHead(Method method) {
		String s = "Define Method:" + method.getReturnType() + " ";
		s += method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			s += para.getName() + ", ";
		}
		s = s.substring(0, s.length() - 2) + ")";
		if (!method.getReturnType().equals("void")) {
			s += " : " + method.getReturnType();
		}
		return s;

	}

}
