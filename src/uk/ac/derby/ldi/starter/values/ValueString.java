package uk.ac.derby.ldi.starter.values;

import uk.ac.derby.ldi.starter.interpreter.ExceptionSemantic;
import uk.ac.derby.ldi.starter.interpreter.Parser;
import uk.ac.derby.ldi.starter.parser.ast.SimpleNode;


public class ValueString extends ValueAbstract {
	
	private String internalValue;
	
	/** Return a ValueString given a quote-delimited source string. */
	public static ValueString stripDelimited(String b) {
		return new ValueString(b.substring(1, b.length() - 1));
	}
	
	public ValueString(String b) {
		internalValue = b;
	}
	
	public String getName() {
		return "string";
	}
	
	/** Convert this to a String. */
	public String stringValue() {
		return internalValue;		
	}
	
	public static ValueString returnString(String a) {
		return new ValueString(a);
	}
	
	public int compare(Value v) {
		return internalValue.compareTo(v.stringValue());
	}
	
	/** Add performs string concatenation. */
	public Value add(Value v) {
		return new ValueString(internalValue + v.stringValue());
	}
	
	public String toString() {
		return internalValue;
	}
	
	public String getRawValue() {
		return internalValue;
	}
	
	public Value Modulus(Value v) {
		throw new ExceptionSemantic("Invalid arguments. Modulus uses integer and double types.");
	}


	 //Dereferences a value in a nested expression.
	@Override
	public Value dereference(SimpleNode node, Value v, int currChild, Parser p) {
		final ValueString valueString = (ValueString) v;
		final int index = (int) ((ValueInteger) p.doChild(node, currChild)).getRawValue();
		final String str = "" + valueString.stringValue().charAt(index);
		return new ValueString(str);
	}
	
	@Override
	public Value power(Value v) {
		throw new ExceptionSemantic("Cannot Power a string. Power only takes integer and double value types.");
	}
	
	public Value sin(Value v) {
		throw new ExceptionSemantic("Cannot Sin a string. Sin only takes integer and double value types.");
	}
	
	public Value cos(Value v) {
		throw new ExceptionSemantic("Cannot Cos a string. Cos only takes integer and double value types.");
	}
	
	public Value tan(Value v) {
		throw new ExceptionSemantic("Cannot Tan a string. tan only takes integer and double value types.");
	}
		
}
