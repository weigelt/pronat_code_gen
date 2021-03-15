package edu.kit.ipd.pronat.code_gen.visitor;

import edu.kit.ipd.pronat.postpipelinedatamodel.ast.tree.*;
import edu.kit.ipd.pronat.postpipelinedatamodel.ast.visitor.IVisitor;
import edu.kit.ipd.pronat.postpipelinedatamodel.code.Method;
import edu.kit.ipd.pronat.postpipelinedatamodel.code.Parameter;

/**
 * @author Sebastian Weigelt
 * @author Viktor Kiesel
 */
public class PlantUMLCodeVisitor implements IVisitor {

	boolean extendedParameters = false;

	@Override
	public String getID() {
		return "PlantUMLCodeVisitor";

	}

	@Override
	public String toNewMethod(ASTRoot astRoot, Method method) {

		String code = "";
		code += generateHead(method) + "{\n";
		code += toCode((ASTBlock) astRoot);
		code += "}\n";
		return code;

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
			s += c.visit(this) + " ";
		}
		if (!astCondition.getConditions().getExpressions().isEmpty()) {
			s = s.substring(0, s.length() - 1);
		}
		s += ") ";
		s += "then (True)\n" + toCode(astCondition.getThenBlock()) + "\n";
		s += "else (False)\n" + toCode(astCondition.getElseBlock()) + "endif\n";
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
		Method method = astMethod.getMethod();
		s += ":" + method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			if (extendedParameters) {
				s += para.getType() + "<-";
			}
			s += para.getName() + ", ";
		}
		if (!method.getParameters().isEmpty()) {
			s = s.substring(0, s.length() - 2);
		}
		s += ");";
		if (astMethod.getComment() != null) {
			s += "\n" + toComment(astMethod.getComment()) + "\n";
		}
		return s;
	}

	@Override
	public String toCode(ASTConditionCall astConditionCall) {
		String s = "";
		Method method = astConditionCall.getMethod();
		s += "" + method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			s += para.getName() + ", ";
		}
		if (!method.getParameters().isEmpty()) {
			s = s.substring(0, s.length() - 2);
		}
		s += ")";
		return s;
	}

	@Override
	public String toCode(ASTFor node) {
		String s = "";
		s += "while ( for " + node.getIterations() + " times ) \n";
		s += node.getBlock().visit(this) + "endwhile\n";
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
			s += "repeat\n";
			s += node.getBlock().visit(this);
			s += "repeat while (";
			for (ASTNode c : node.getConditions().getExpressions()) {
				s += c.visit(this) + " ";
			}
			if (!node.getConditions().getExpressions().isEmpty()) {
				s = s.substring(0, s.length() - 1);
			}
			if (node.isNegated()) {
				s += ") is (False)\n->True;";
			} else {
				s += ") is (True)\n->False;";
			}

		} else {
			s += "while (";
			for (ASTNode c : node.getConditions().getExpressions()) {
				s += c.visit(this) + " ";
			}
			if (!node.getConditions().getExpressions().isEmpty()) {
				s = s.substring(0, s.length() - 1);
			}
			if (node.isNegated()) {
				s += ") is (False)";
			} else {
				s += ") is (True)";
			}
			s += "\n";
			s += node.getBlock().visit(this) + "endwhile ";
			if (node.isNegated()) {
				s += "(True)";
			} else {
				s += "(False)";
			}

		}
		return s;
	}

	@Override
	public String toCode(ASTText astText) {
		String s = ":";
		s += astText.getText() + ";";
		return s;
	}

	@Override
	public String toCode(ASTComperator node) {
		String s = node.getLeft() + " ";

		switch (node.getSign()) {
		case EQ:
			s += "==";
			break;
		case GE:
			s += ">=";
			break;
		case GT:
			s += ">";
			break;
		case LE:
			s += "<=";
			break;
		case LT:
			s += "<";
			break;
		case NE:
			s += "!=";
			break;
		default:
			break;
		}
		s += " " + node.getRight();
		return s;
	}

	@Override
	public String toCode(ASTParallel node) {
		String s = "";
		boolean again = false;
		for (ASTBlock block : node.getSections()) {
			s += "fork";
			if (again) {
				s += " again";
			} else {
				again = true;
			}
			s += "\n";
			s += block.visit(this);
		}
		if (node.getSections().isEmpty()) // TODO: ERROR if Empty
		{
			s += "fork\n";
		}
		s += "end fork\n";
		return s.trim();
	}

	private String generateHead(Method method) {
		String s = "partition " + method.getReturnType() + "_";
		s += method.getName() + "(";
		for (Parameter para : method.getParameters()) {
			s += para.getType() + "->" + para.getName() + ",_";
		}
		if (!method.getParameters().isEmpty()) {
			s = s.substring(0, s.length() - 2);
		}
		s += ")";
		return s;
	}

	public String toComment(String comment) {
		// TODO Auto-generated method stub
		return "floating note left: " + comment;
	}

}