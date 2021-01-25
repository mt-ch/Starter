package uk.ac.derby.ldi.starter.values;

import uk.ac.derby.ldi.starter.interpreter.ExceptionSemantic;
import uk.ac.derby.ldi.starter.interpreter.Parser;
import uk.ac.derby.ldi.starter.parser.ast.SimpleNode;

public class ValueBoolean extends ValueAbstract {

	private boolean internalValue;
	
	public ValueBoolean(boolean b) {
		internalValue = b;
	}
	
	public String getName() {
		return "boolean";
	}
	
	/** Convert this to a primitive boolean. */
	public boolean booleanValue() {
		return internalValue;
	}
	
	/** Convert this to a primitive string. */
	public String stringValue() {
		return (internalValue) ? "true" : "false";
	}
	
	public Value or(Value v) {
		return new ValueBoolean(internalValue || v.booleanValue());
	}

	public Value and(Value v) {
		return new ValueBoolean(internalValue && v.booleanValue());
	}

	public Value not() {
		return new ValueBoolean(!internalValue);
	}
	
	public Value Modulus(Value v) {
		throw new ExceptionSemantic("Invalid arguments. Modulus uses integer and double types.");
	}

	public int compare(Value v) {
		if (internalValue == v.booleanValue())
			return 0;
		else if (internalValue)
			return 1;
		else
			return -1;
	}
	
	public String toString() {
		return "" + internalValue;
	}
	
	public Boolean getRawValue() {
		return internalValue;
	}

	public Value dereference(SimpleNode node, Value v, int currChild, Parser p) {
		return null;
	}

	public Value power(Value v) {
		throw new ExceptionSemantic("Cannot power a boolean. Power only takes integer and double value types.");
	}
	
	public Value sin(Value v) {
		throw new ExceptionSemantic("Cannot Sin a boolean. Sin only takes integer and double value types.");
	}
	
	public Value cos(Value v) {
		throw new ExceptionSemantic("Cannot Cos a boolean. Cos only takes integer and double value types.");
	}
	
	public Value tan(Value v) {
		throw new ExceptionSemantic("Cannot Tan a boolean. tan only takes integer and double value types.");
	}
	

}
