package uk.ac.derby.ldi.starter.interpreter;

import uk.ac.derby.ldi.starter.parser.ast.*;

import java.util.stream.IntStream;

import java.util.Timer; 
import uk.ac.derby.ldi.starter.values.*;


import java.awt.font.NumericShaper.Range;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner; 
import java.util.TimerTask;

public class Parser implements StarterVisitor {
	
	// Scope display handler
	private Display scope = new Display();
	
	// Get the ith child of a given node.
	private static SimpleNode getChild(SimpleNode node, int childIndex) {
		return (SimpleNode)node.jjtGetChild(childIndex);
	}
	
	// Get the token value of the ith child of a given node.
	private static String getTokenOfChild(SimpleNode node, int childIndex) {
		return getChild(node, childIndex).tokenValue;
	}
	
	// Execute a given child of the given node
	private Object doChild(SimpleNode node, int childIndex, Object data) {
		return node.jjtGetChild(childIndex).jjtAccept(this, data);
	}
	
	// Execute a given child of a given node, and return its value as a Value.
	// This is used by the expression evaluation nodes.
	public Value doChild(SimpleNode node, int childIndex) {
		return (Value)doChild(node, childIndex, null);
	}
	
	// Execute all children of the given node
	Object doChildren(SimpleNode node, Object data) {
		return node.childrenAccept(this, data);
	}
	
	// Called if one of the following methods is missing...
	public Object visit(SimpleNode node, Object data) {
		System.out.println(node + ": acceptor not implemented in subclass?");
		return data;
	}
	
	// Execute a Starter program
	public Object visit(ASTCode node, Object data) {
		return doChildren(node, data);	
	}
	
	// Execute a statement
	public Object visit(ASTStatement node, Object data) {
		return doChildren(node, data);	
	}

	// Execute a block
	public Object visit(ASTBlock node, Object data) {
		return doChildren(node, data);	
	}

	// Function definition
	public Object visit(ASTFnDef node, Object data) {
		// Already defined?
		if (node.optimised != null)
			return data;
		// Child 0 - identifier (fn name)
		String fnname = getTokenOfChild(node, 0);
		if (scope.findFunctionInCurrentLevel(fnname) != null)
			throw new ExceptionSemantic("Function " + fnname + " already exists.");
		FunctionDefinition currentFunctionDefinition = new FunctionDefinition(fnname, scope.getLevel() + 1);
		// Child 1 - function definition parameter list
		doChild(node, 1, currentFunctionDefinition);
		// Add to available functions
		scope.addFunction(currentFunctionDefinition);
		// Child 2 - function body
		currentFunctionDefinition.setFunctionBody(getChild(node, 2));
		// Child 3 - optional return expression
		if (node.fnHasReturn)
			currentFunctionDefinition.setFunctionReturnExpression(getChild(node, 3));
		// Preserve this definition for future reference, and so we don't define
		// it every time this node is processed.
		node.optimised = currentFunctionDefinition;
		return data;
	}
	
	// Function definition parameter list
	public Object visit(ASTParmlist node, Object data) {
		FunctionDefinition currentDefinition = (FunctionDefinition)data;
		for (int i=0; i<node.jjtGetNumChildren(); i++)
			currentDefinition.defineParameter(getTokenOfChild(node, i));
		return data;
	}
	
	// Function body
	public Object visit(ASTFnBody node, Object data) {
		return doChildren(node, data);
	}
	
	// Function return expression
	public Object visit(ASTReturnExpression node, Object data) {
		return doChildren(node, data);
	}
	
	// Function call
	public Object visit(ASTCall node, Object data) {
		FunctionDefinition fndef;
		if (node.optimised == null) { 
			// Child 0 - identifier (fn name)
			String fnname = getTokenOfChild(node, 0);
			fndef = scope.findFunction(fnname);
			if (fndef == null)
				throw new ExceptionSemantic("Function " + fnname + " is undefined.");
			// Save it for next time
			node.optimised = fndef;
		} else
			fndef = (FunctionDefinition)node.optimised;
		FunctionInvocation newInvocation = new FunctionInvocation(fndef);
		// Child 1 - arglist
		doChild(node, 1, newInvocation);
		// Execute
		scope.execute(newInvocation, this);
		return data;
	}
	
	// Function invocation in an expression
	public Object visit(ASTFnInvoke node, Object data) {
		FunctionDefinition fndef;
		if (node.optimised == null) { 
			// Child 0 - identifier (fn name)
			String fnname = getTokenOfChild(node, 0);
			fndef = scope.findFunction(fnname);
			if (fndef == null)
				throw new ExceptionSemantic("Function " + fnname + " is undefined.");
			if (!fndef.hasReturn())
				throw new ExceptionSemantic("Function " + fnname + " is being invoked in an expression but does not have a return value.");
			// Save it for next time
			node.optimised = fndef;
		} else
			fndef = (FunctionDefinition)node.optimised;
		FunctionInvocation newInvocation = new FunctionInvocation(fndef);
		// Child 1 - arglist
		doChild(node, 1, newInvocation);
		// Execute
		return scope.execute(newInvocation, this);
	}
	
	// Function invocation argument list.
	public Object visit(ASTArgList node, Object data) {
		FunctionInvocation newInvocation = (FunctionInvocation)data;
		for (int i=0; i<node.jjtGetNumChildren(); i++)
			newInvocation.setArgument(doChild(node, i));
		newInvocation.checkArgumentCount();
		return data;
	}
	
	// Execute an IF 
	public Object visit(ASTIfStatement node, Object data) {
		// evaluate boolean expression
		Value hopefullyValueBoolean = doChild(node, 0);
		if (!(hopefullyValueBoolean instanceof ValueBoolean))
			throw new ExceptionSemantic("The test expression of an if statement must be boolean.");
		if (((ValueBoolean)hopefullyValueBoolean).booleanValue())
			doChild(node, 1);							// if(true), therefore do 'if' statement
		else if (node.ifHasElse)						// does it have an else statement?
			doChild(node, 2);							// if(false), therefore do 'else' statement
		return data;
	}
	
	// Execute a FOR loop
	public Object visit(ASTForLoop node, Object data) {
		// loop initialisation
		doChild(node, 0);
		while (true) {
			// evaluate loop test
			Value hopefullyValueBoolean = doChild(node, 1);
			if (!(hopefullyValueBoolean instanceof ValueBoolean))
				throw new ExceptionSemantic("The test expression of a for loop must be boolean.");
			if (!((ValueBoolean)hopefullyValueBoolean).booleanValue())
				break;
			// do loop statement
			doChild(node, 3);
			// assign loop increment
			doChild(node, 2);
		}
		return data;
	}
	
	// Process an identifier
	// This doesn't do anything, but needs to be here because we need an ASTIdentifier node.
	public Object visit(ASTIdentifier node, Object data) {
		return data;
	}
	
	// Execute the print statement
	public Object visit(ASTPrint node, Object data) {
		final int numOfItems = node.jjtGetNumChildren();
		for (int i = 0; i < numOfItems; i++) {
			System.out.print(doChild(node, i));
		}
		System.out.println();
		return data;
	}
	
	// Execute an assignment statement.
	public Object visit(ASTAssignment node, Object data) {
		Display.Reference reference;
		if (node.optimised == null) {
			String name = getTokenOfChild(node, 0);
			reference = scope.findReference(name);
			if (reference == null)
				reference = scope.defineVariable(name);
			node.optimised = reference;
		} else
			reference = (Display.Reference)node.optimised;
		reference.setValue(doChild(node, 1));
		return data;
	}

	// OR
	public Object visit(ASTOr node, Object data) {
		return doChild(node, 0).or(doChild(node, 1));
	}

	// AND
	public Object visit(ASTAnd node, Object data) {
		return doChild(node, 0).and(doChild(node, 1));
	}

	// ==
	public Object visit(ASTCompEqual node, Object data) {
		return doChild(node, 0).eq(doChild(node, 1));
	}

	// !=
	public Object visit(ASTCompNequal node, Object data) {
		return doChild(node, 0).neq(doChild(node, 1));
	}

	// >=
	public Object visit(ASTCompGTE node, Object data) {
		return doChild(node, 0).gte(doChild(node, 1));
	}

	// <=
	public Object visit(ASTCompLTE node, Object data) {
		return doChild(node, 0).lte(doChild(node, 1));
	}

	// >
	public Object visit(ASTCompGT node, Object data) {
		return doChild(node, 0).gt(doChild(node, 1));
	}

	// <
	public Object visit(ASTCompLT node, Object data) {
		return doChild(node, 0).lt(doChild(node, 1));
	}

	// +
	public Object visit(ASTAdd node, Object data) {
		return doChild(node, 0).add(doChild(node, 1));
	}

	// -
	public Object visit(ASTSubtract node, Object data) {
		return doChild(node, 0).subtract(doChild(node, 1));
	}

	// *
	public Object visit(ASTTimes node, Object data) {
		return doChild(node, 0).mult(doChild(node, 1));
	}

	// /
	public Object visit(ASTDivide node, Object data) {
		return doChild(node, 0).div(doChild(node, 1));
	}

	// Modulus 
	public Object visit(ASTModulus node, Object data) {
		return doChild(node, 0).Modulus(doChild(node, 1));
	}
	
	// NOT
	public Object visit(ASTUnaryNot node, Object data) {
		return doChild(node, 0).not();
	}

	// + (unary)
	public Object visit(ASTUnaryPlus node, Object data) {
		return doChild(node, 0).unary_plus();
	}

	// - (unary)
	public Object visit(ASTUnaryMinus node, Object data) {
		return doChild(node, 0).unary_minus();
	}
	
	// ^ 
	public Object visit(ASTPower node, Object data) {
		return doChild(node, 0).power(doChild(node, 1));
	}
	
	//sine
	public Object visit(ASTsin node, Object data) {
		return doChild(node, 0).sin(null);
	}
	
	//cos
	public Object visit(ASTcos node, Object data) {
		return doChild(node, 0).cos(null);
	}
		
	//tan
	public Object visit(ASTtan node, Object data) {
		return doChild(node, 0).tan(null);
	}
	
	// Return string literal
	public Object visit(ASTCharacter node, Object data) {
		if (node.optimised == null)
			node.optimised = ValueString.stripDelimited(node.tokenValue);
		return node.optimised;
	}

	// Return integer literal
	public Object visit(ASTInteger node, Object data) {
		if (node.optimised == null)
			node.optimised = new ValueInteger(Long.parseLong(node.tokenValue));
		return node.optimised;
	}

	// Return floating point literal
	public Object visit(ASTRational node, Object data) {
		if (node.optimised == null)
			node.optimised = new ValueRational(Double.parseDouble(node.tokenValue));
		return node.optimised;
	}

	// Return true literal
	public Object visit(ASTTrue node, Object data) {
		if (node.optimised == null)
			node.optimised = new ValueBoolean(true);
		return node.optimised;
	}

	// Return false literal
	public Object visit(ASTFalse node, Object data) {
		if (node.optimised == null)
			node.optimised = new ValueBoolean(false);
		return node.optimised;
	}
	
//	Returns a random value between specified min and max values
//	child0 = min value
//	child1 = max value
	public Object visit(ASTRandom node, Object data) {
		final Value min = doChild(node, 0);
		final Value max = doChild(node, 1);

		// Check the arguments are integer or rational
		if ((min instanceof ValueInteger || min instanceof ValueRational)
				&& ( max instanceof ValueInteger || max instanceof ValueRational)) {
			final double minDouble = Double.parseDouble(min.stringValue());
			final double maxDouble = Double.parseDouble(max.stringValue());
			final double result = ThreadLocalRandom.current().nextDouble(minDouble,
					maxDouble);
			// Return as a double value
			return NumberUtils.tryInt(result);
		}
		else
			throw new ExceptionSemantic("Random(" + min + ", " + max + ") is not a valid argument. \n Random() function requires the arguments to be integer or double type.");
	}
	
	// Execute while loop
	public Object visit(ASTWhile node, Object data) {
		while (true) {
			final Value loopTest = doChild(node, 0);
			if (!(loopTest instanceof ValueBoolean))
				throw new ExceptionSemantic(
						"The test expression of a while loop must be boolean.");

			if (!((ValueBoolean) loopTest).getRawValue()) 
				break;

			doChild(node, 1); // Do the loop statement
		}

		return data;
	}
	
	// Declaration of a variable
	public Object visit(ASTDeclaration node, Object data) {
		final String name = getTokenOfChild(node, 0);

		if (node.defType == "constant")
			throw new ExceptionSemantic("Constants must be initialised."
					+ " Change the \"const\" keyword before \"" + name
					+ "\" to \"let\".");

		if (scope.findReference(name) != null)
			throw new ExceptionSemantic("Variable \"" + name + "\" already exists.");

		// Define the variable and get the reference.
		final Display.Reference ref = scope.defineVariable(name);

		if (node.isArrayWithCap == true) {
			final int capacity = (int) doChild(node, 1).getRawValue();
			ref.setValue(new ValueArray(capacity));
		}

		else if (node.isArrayDeclaration == true)
			ref.setValue(new ValueArray(0));

		return data;
	}
	
	// Defining a variable using store
	public Object visit(ASTDefinition node, Object data) {
		final Node initialisation = node.jjtGetChild(0);
		final String name = getTokenOfChild((SimpleNode) initialisation, 0);

		if (scope.findReference(name) == null
				&& scope.findReference("constant" + name) == null) {
			switch (node.defType) {
			case "variable":
				scope.defineVariable(name);
				break;
			case "constant":
				scope.defineConstant(name);
			}
			initialisation.jjtAccept(this, data); // Do the initialisation.
		} else
			throw new ExceptionSemantic("The defined variable \"" + name + "\" already exists in this scope.");
		return data;
	}
	
	//Const
	public Object visit(ASTConstInit node, Object data) {
		final String name = getTokenOfChild(node, 0);
		final Display.Reference reference = scope.findReference("constant" + name);
		final Value rightVal = doChild(node, 1);
		if (rightVal == null)
			throw new ExceptionSemantic(
					"Right value of the constant's initialisation cannot resolve to null.");

		reference.setValue(rightVal);
		return data;
	}

	//Array
	public Object visit(ASTArrayInit node, Object data) {
		final String name = getTokenOfChild(node, 0);
		Display.Reference reference = scope.findReference(name);
		// If the array is being defined as a constant...
		if (reference == null)
			reference = scope.findReference("constant" + name);

		int initValNum = -1;
		int capacity = -1;
		int currChild = -1;
		if (node.isArrayWithCap == true) {
			// Get the number of values in the initialisation.
			// -2 -- the first two children are identifier() and capacity.
			initValNum = node.jjtGetNumChildren() - 2;

			// Get the array's initial capacity from the explicitly specified
			// capacity (child index 1),
			capacity = (int) doChild(node, 1).getRawValue();

			// currChild := 2 -- the values start from the third child.
			currChild = 2;
		} else {
			// -1 -- the first child is an identifier().
			initValNum = node.jjtGetNumChildren() - 1;

			// Capacity is implicitly set with the number of values on initialisation.
			capacity = initValNum;

			// currChild := 1 -- the values start from the second child.
			currChild = 1;
		}

		// If there's more values than the array can store...
		if (initValNum > capacity)
			throw new ExceptionSemantic(
					"There is more initial values for \"" + name + "\" array ("
							+ initValNum + ") than its capacity (" + capacity + ").");

		// Initialise an empty array with the specified capacity.
		final ValueArray valueArray = new ValueArray(capacity);

		// Add all the values to the array.
		Value currentValue;
		for (; currChild < node.jjtGetNumChildren(); currChild++) {
			currentValue = doChild(node, currChild);
			valueArray.append(currentValue);
		}

		reference.setValue(valueArray);
		return data;
	}
	
	//Dereference node
	public Object visit(ASTDereference node, Object data) {
		Display.Reference reference;

		if (node.optimised == null) {
			final String name = node.tokenValue;
			reference = scope.findReference(name);
			if (reference == null)
				reference = scope.findReference("constant" + name);
			if (reference == null)
				throw new ExceptionSemantic(
						"Variable or parameter \"" + name + "\" is undefined.");
			node.optimised = reference;
		} else
			reference = (Display.Reference) node.optimised;

		final int numChildren = node.jjtGetNumChildren();
		if (numChildren > 0) { 
			int currChild = 0; 
			Value value = reference.getValue();

			for (; currChild < numChildren; currChild++)
				value = value.dereference(node, value, currChild, this);

			return value;
		}

		return reference.getValue();
	}
	
	//Input node
	public Object visit(ASTInput node, Object data) {
		final Scanner obj = new Scanner(System.in);
		final String input = obj.nextLine();
		
		return ValueString.returnString(input);
	}
}
	
