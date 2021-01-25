package uk.ac.derby.ldi.starter.interpreter;

import uk.ac.derby.ldi.starter.parser.ast.ASTCode;
import uk.ac.derby.ldi.starter.parser.ast.Starter;
import uk.ac.derby.ldi.starter.parser.ast.StarterVisitor;

public class Interpreter {
	
	private static void usage() {
		System.out.println("Usage: starter [-d1] < <source>");
		System.out.println("          -d1 -- output AST");
	}
	
	public static void main(String args[]) {
		boolean debugAST = false;
		if (args.length == 1) {
			if (args[0].equals("-d1"))
				debugAST = true;
			else {
				usage();
				return;
			}
		}
		Starter language = new Starter(System.in);
		try {
			ASTCode parser = language.code();
			StarterVisitor nodeVisitor;
			if (debugAST)
				nodeVisitor = new ParserDebugger();
			else
				nodeVisitor = new Parser();
			parser.jjtAccept(nodeVisitor, null);
		} catch (Throwable e) {
			System.out.println(e.getMessage());
		}
	}
}
